#! /usr/local/bin/php
<?php
define('SYSPATH','1');

// Set below parameters($test_mode & $local) to true if executing the code in localhost.
$test_mode = true; //True will not execute UPDATE queries
$local = false; //True to run the script in local setup

$config_file_path = $functions_file_path = '';
$nl = '';

if ($local) {
	$nl = "<br><br>";
	$proxy = '';
	$path = 'D:/SarathData/softwares/xampp/htdocs/sarath/liteon/';
	if (file_exists ( $path . "includes/config.php" )) {
		$config_file_path = $path . "includes/config.php";
	}
	if (file_exists ( $path . "includes/UtilityFunctions.php" )) {
		$functions_file_path = $path . "includes/UtilityFunctions.php";
	}
} else {
	$nl = "\n\n";
	$proxy = '';
	$path = '/home/ubuntu/cronscripts/';
	if (file_exists ( $path . "includes/config.php" )) {
		$config_file_path = $path . "includes/config.php";
	}
	if (file_exists ( $path . "includes/UtilityFunctions.php" )) {
		$functions_file_path = $path . "includes/UtilityFunctions.php";
	}
}

if (empty ( $config_file_path )) {
	echo 'Config file not found at: ' . $config_file_path . $nl;
	exit ();
}
if (empty ( $functions_file_path )) {
	echo 'Config file not found at: ' . $functions_file_path . $nl;
	exit ();
}

class UVI_PushNotification {
    protected $uvi_api_url;
    protected $uvi_record_type;
	protected $uf;
    protected $uvi_allergies; 
    protected $standard_uvi;
	
    var $county_uvi=array();
	
    function __construct(){
    	global $config_file_path;
    	global $functions_file_path;
    	global $config;
    	
		require ($functions_file_path);
		$this->uf = new UtilityFunctions();
    	$this->initConfig ();
    }
	private function initConfig() {
		global $config_file_path;
		require ($config_file_path);
							
		if (array_key_exists ( 'uvi_api_url', $config )){
			$this->uvi_api_url = $config ['uvi_api_url'];
		} else {
			$this->uf->send_mail("Not Found: uvi_api_url setting for reading UVI data", "From: UVI_PushNotification \n Reason: uvi_api_url setting for reading UVI data is empty");
			$this->uf->print_error ( "Not Found: uvi_api_url setting for reading UVI data !!!!" );
		}
			
		if (array_key_exists ( 'uvi_allergies', $config )){
			$this->uvi_allergies = $config ['uvi_allergies'];
		} else {
			$this->uf->send_mail("Not Found: uvi_allergies setting for reading UVI data", "From: UVI_PushNotification \n Reason: uvi_allergies setting for reading UVI data is empty");
			$this->uf->print_error ( "Not Found: uvi_allergies setting for reading UVI data !!!!" );
		}
		if (array_key_exists ( 'standard_uvi', $config )){
			$this->standard_uvi = $config ['standard_uvi'];
		} else {
			$this->uf->send_mail("Not Found: standard_uvi setting for reading UVI data", "From: UVI_PushNotification \n Reason: standard_uvi setting for reading UVI data is empty");
			$this->uf->print_error ( "Not Found: standard_uvi setting for reading UVI data !!!!" );
		}	
	}
	
	public function readAPI(){
		if(!empty($this->uvi_api_url)){
			$j = 0;
			$uvi_records = $this->uf->makeCURLRequest($this->uvi_api_url);
			$no_of_records = sizeof($uvi_records['RESPONSE']);
			if($no_of_records > 0){
				$this->uf->print_debug("Number of records found:" . $no_of_records . ", standard_uvi:" . $this->standard_uvi);
				for($i = 0; $i < $no_of_records; $i++){
					$uvi_record = $uvi_records['RESPONSE'][$i];
					$this->uf->print_debug("$j: County, UVI >> " . $uvi_record['County'] . ', ' . $uvi_record['UVI']);
					if(!empty($uvi_record['UVI']) && $uvi_record['UVI'] >= $this->standard_uvi){
						$j++;
						$this->county_uvi[$uvi_record['County']] = $uvi_record['UVI'];
					}
				}
			} else {
				$this->uf->updateActivityLog('IWPS', "No UVI Data Received From API", 'error', 'PushNotification');
				$this->uf->send_mail("No UVI Data Received From API", "From: UVI_PushNotification \n API URL: $this->uvi_api_url \n Reason: Either no data received from API or Data Output is changed or API URL is not reacheable. \n" . $uvi_records['ERROR'] . "\n Information regarding this API Call:  \n" . $uvi_records['INFO']);
				$this->uf->print_error("No UVI Data Received From API");
			}
		}
	}

    public function createPushMessages(){
		$this->readAPI();
		$messages = array();
		
		if(!empty(($this->county_uvi)) && count($this->county_uvi) > 0){
			/* Connect to database for read-only */
			$this->uf->dbConnect($uf->dbReadConnection,READONLYCONNECTION);
			
			if($uf->dbReadConnection){
				foreach ($this->county_uvi as $county => $uvi) {
					if (strpos($this->uvi_allergies, ',') !== false) {
						$uviAllergies = str_replace(',', '|', $this->uvi_allergies);
						$allergies = ' AND (CONCAT(",", `allergies`, ",") REGEXP ",(' . $uviAllergies . '),")';
					}else{
						$allergies = ' AND (allergies LIKE "%' . $this->uvi_allergies . '%")';
					}
					$sql = "SELECT DISTINCT parent.user_id, parent.account_id, parent.name, parent.iphone_app_token, parent.android_app_token, kids.`student_id`, kids.`name` AS student_name
							FROM `county_areas` AS areas
							LEFT JOIN `school_details` AS sd ON sd.`county` = areas.`county_en`
							LEFT JOIN `accounts` AS schools ON schools.`account_id` = sd.`school_id`
							LEFT JOIN `class_grade` AS cg ON cg.`school_id` = schools.`account_id`
							LEFT JOIN `students` AS kids ON kids.`class_grade_id` = cg.`class_grade_id` $allergies
							LEFT JOIN `parent_kids` AS pk ON pk.`student_id` = kids.`student_id`
							JOIN `users` AS parent ON parent.`user_id` = pk.`user_id` 
							AND (parent.`iphone_app_token` IS NOT NULL OR parent.`android_app_token` IS NOT NULL)
							AND parent.`role_type` = 'parent_admin'
							WHERE areas.`county_cn` = '$county'
							";
					
					$result = $this->uf->e_mysql_query($sql,$uf->dbReadConnection);
					if($result){
						$this->uf->print_debug("\nNumber of records found:" . mysqli_num_rows($result));
						while ($row = mysqli_fetch_array($result)){
							$this->uf->print_debug("\nInside DB Query Loop");
							$alert_txt = 'Ultra Violet Index Alert';
							if(!empty($row['android_app_token'])){
								$messages[] = array(
									'token' =>$row['android_app_token'],
									'title' => $alert_txt . ': ' . $row['student_name'],
									'body' => $alert_txt . ': ' . $uvi . ' is the UV Value at ' . $county . '. The UV Index today is high in your city, protection against sunburn is needed.'
								);
							}
							if(!empty($row['iphone_app_token'])){
								$messages[] = array(
									'token' =>$row['iphone_app_token'],
									'title' => $alert_txt . ': ' . $row['student_name'],
									'body' => $alert_txt . ': ' . $uvi . ' is the UV Value at ' . $county . '. The UV Index today is high in your city, protection against sunburn is needed.'
								);
							}
						}
					}
				}
				$this->uf->dbClose($uf->dbReadConnection); 
			} else {
				$this->uf->updateActivityLog('IWPS', "Not connected to the write database. Unable to update messages", 'error', 'PushNotification');
				$this->uf->send_mail("Not connected to the write database", "From: UVI_PushNotification \n API URL: $this->uvi_api_url \n Reason: Not connected to the write database. Unable to update messages. Need to check config.php file for Database Configurations.");
				$this->uf->print_error ( "Not connected to the write database. Unable to update messages" );
			}
		}
		return $messages;
    }
	
    public function sendMessages() { 
		$messages = $this->createPushMessages();
		$messages_failed = array();
		
		/* get all messages from queue, if any */
		if($messages){
			/* if connected push all messages to APN server */
			foreach($messages as $key=>$message){
				$result = $this->uf->sendNotification($message, 'parent');
				if (!empty ( $result['RESPONSE']['results'][0]['error'] )) {
					array_push($messages_failed, $messages[$key]);
				}
			}
			
			if(!empty($messages_failed)) $this->uf->updateActivityLog('IWPS', $messages_failed, 'error', 'PushNotification');
		} else {
			$this->uf->updateActivityLog('IWPS', "No Matching UVI Data Found To Send Push Notification", 'info', 'PushNotification');
			$this->uf->send_mail("No Matching UVI Data Found To Send Push Notification", "From: UVI_PushNotification \n API URL: $this->uvi_api_url \n Reason: Either no data received from API or Data Output is changed or API URL is not reacheable.");
			$this->uf->print_error("No Matching UVI Data Found To Send Push Notification");
		}
    }
}

$read_UVI_PushNotification = new UVI_PushNotification();
$read_UVI_PushNotification->sendMessages();

?>
