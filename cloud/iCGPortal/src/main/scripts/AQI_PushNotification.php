#! /usr/local/bin/php
<?php
define('SYSPATH','1');

// Set below parameters($test_mode & $local) to true if executing the code in localhost.
$test_mode = false; //True will not execute UPDATE queries
$local = false; //True to run the script in local setup

$config_file_path = $functions_file_path = '';
$nl = '';

if ($local) {
	$nl = "<br><br>";
	$proxy = '';
	$path = 'D:/SarathData/softwares/xampp/htdocs/sarath/liteon/';
	//$lockfile = fopen($path . "includes/apnlockliteon", "w");
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
	//$lockfile = fopen($path . "includes/apnlockliteon", "w");
}

if (empty ( $config_file_path )) {
	echo 'Config file not found at: ' . $config_file_path . $nl;
	exit ();
}
if (empty ( $functions_file_path )) {
	echo 'Config file not found at: ' . $functions_file_path . $nl;
	exit ();
}

// Make sure only one instance of this job runs at a time.

/*if (!$local && !flock($lockfile,LOCK_EX | LOCK_NB)) {
	echo "cant get lock";
	exit();
}*/

class AQI_PushNotification {  
    protected $aqi_api_url; 
    protected $aqi_allergies; 
    protected $standard_aqi;
	protected $uf;
	
	var $area_aqi=array();
	
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
		
		if (array_key_exists ( 'aqi_api_url', $config )){
			$this->aqi_api_url = $config ['aqi_api_url'];
		} else {
			$this->uf->send_mail("Not Found: aqi_api_url setting for reading AQI data", "From: AQI_PushNotification \n Reason: aqi_api_url setting for reading AQI data is empty");
			$this->uf->print_error ( "Not Found: aqi_api_url setting for reading AQI data !!!!" );
		}
		if (array_key_exists ( 'aqi_allergies', $config )){
			$this->aqi_allergies = $config ['aqi_allergies'];
		} else {
			$this->uf->send_mail("Not Found: aqi_allergies setting for reading AQI data", "From: AQI_PushNotification \n Reason: aqi_allergies setting for reading AQI data is empty");
			$this->uf->print_error ( "Not Found: aqi_allergies setting for reading AQI data !!!!" );
		}
		if (array_key_exists ( 'standard_aqi', $config )){
			$this->standard_aqi = $config ['standard_aqi'];
		} else {
			$this->uf->send_mail("Not Found: standard_aqi setting for reading AQI data", "From: AQI_PushNotification \n Reason: standard_aqi setting for reading AQI data is empty");
			$this->uf->print_error ( "Not Found: standard_aqi setting for reading AQI data !!!!" );
		}
	}
	
	public function readAPI(){
		if(!empty($this->aqi_api_url)){
			$j = 0;
			$tomorrow = date('Y-m-d', strtotime("+1 days"));
			$aqi_records = $this->uf->makeCURLRequest($this->aqi_api_url);
			$no_of_records = sizeof($aqi_records['RESPONSE']);
			if($no_of_records > 0){
				$this->uf->print_debug("Number of records found:" . $no_of_records);
				for($i = 0; $i < $no_of_records; $i++){
					$aqi_record = $aqi_records['RESPONSE'][$i];
					if(!empty($aqi_record['AQI']) && $aqi_record['AQI'] >= $this->standard_aqi){
						$j++;
						$this->area_aqi[$aqi_record['County']] = $aqi_record['AQI'];
					}
				}
			} else {
				$this->uf->updateActivityLog('IWPS', "No AQI Data Received From API", 'error', 'PushNotification');
				$this->uf->send_mail("No AQI Data Received From API", "From: AQI_PushNotification \n API URL: $this->aqi_api_url \n Reason: Either no data received from API or Data Output is changed or API URL is not reacheable. \n" . $aqi_records['ERROR'] . "\n Information regarding this API Call:  \n" . $aqi_records['INFO']);
				$this->uf->print_error("No AQI Data Received From API");
			}
		}
	}

    public function createPushMessages(){
		$this->readAPI();		
		$messages = array();
		
		if(!empty(($this->area_aqi)) && count($this->area_aqi) > 0){
			/* Connect to database for read-only */
			$this->uf->dbConnect($uf->dbReadConnection);
			if($uf->dbReadConnection){
				foreach ($this->area_aqi as $county => $aqi) {
					if (strpos($this->aqi_allergies, ',') !== false) {
						$aqiAllergies = str_replace(',', '|', $this->aqi_allergies);
						$allergies = ' AND (CONCAT(",", `allergies`, ",") REGEXP ",(' . $aqiAllergies . '),")';
					}else{
						$allergies = ' AND (allergies LIKE "%' . $this->aqi_allergies . '%")';
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
					//$result = mysqli_query ( $uf->dbReadConnection, $sql );
					$result = $this->uf->e_mysql_query($sql,$uf->dbReadConnection);
					if($result){
						$rowcount=mysqli_num_rows($result);
						$this->uf->print_debug("\nNumber of records found (for county):" . $rowcount . '(' . $county . ')');
						while ($row = mysqli_fetch_array($result, MYSQLI_ASSOC)){
							$this->uf->print_debug("\nInside DB Query Results Loop");
							$alert_txt = 'Air Quality Index Alert';
							$this->uf->print_debug("Android app token: ". $row['android_app_token']);
							$this->uf->print_debug("iPhone app token: ". $row['iphone_app_token']);
							$this->uf->print_debug("Student Name: ". $row['student_name']);
							if(!empty($row['android_app_token'])){
								$messages[] = array(
									'token' =>$row['android_app_token'],
									'title' => $alert_txt . ': ' . $row['student_name'],
									'body' => $alert_txt . ': ' . $aqi . ' is the AQI Value at ' . $county . '. The Air Quality Index today is high in your city, children with respiratory disease should limit prolonged outdoor exertion.'
								);
							}
							if(!empty($row['iphone_app_token'])){
								$messages[] = array(
									'token' =>$row['iphone_app_token'],
									'title' => $alert_txt . ': ' . $row['student_name'],
									'body' => $alert_txt . ': ' . $aqi . ' is the AQI Value at ' . $county . '. The Air Quality Index today is high in your city, children with respiratory disease should limit prolonged outdoor exertion.'
								);
							}
						}
					}
				}
				$this->uf->dbClose($uf->dbReadConnection); 
			} else {
				$this->uf->updateActivityLog('IWPS', "Not connected to the write database. Unable to update messages", 'error', 'PushNotification');
				$this->uf->send_mail("Not connected to the write database", "From: AQI_PushNotification \n API URL: $this->aqi_api_url \n Reason: Not connected to the write database. Unable to update messages. Need to check config.php file for Database Configurations.");
				$this->uf->print_error ( "Not connected to the write database. Unable to update messages" );
			}
		}
		$this->uf->print_debug("messages array count: ".count($messages));
		return $messages;
    }
	
    public function sendMessages() {
		$messages = $this->createPushMessages();
		$messages_failed = array();
		
		/* get all messages from queue, if any */
		if($messages){
			/* if connected push all messages to APN server */
			foreach($messages as $key=>$message){
				$result = $this->uf->sendNotification($message);
				if (!empty ( $result['RESPONSE']['results'][0]['error'] )) {
					$this->uf->print_error("Push Notification failed");
					array_push($messages_failed, $messages[$key]);
				}
			}
			
			if(!empty($messages_failed)) $this->uf->updateActivityLog('IWPS', $messages_failed, 'error', 'PushNotification');
		} else {
			$this->uf->updateActivityLog('IWPS', "No Matching AQI Data Found To Send Push Notification", 'error', 'PushNotification');
			$this->uf->send_mail("No Matching AQI Data Found To Send Push Notification", "From: AQI_PushNotification \n API URL: $this->aqi_api_url \n Reason: Either no data received from API or Data Output is changed or API URL is not reacheable.");
			$this->uf->print_error("No Matching AQI Data Found To Send Push Notification");
		}
    }	
}

$read_AQI_PushNotification = new AQI_PushNotification();
$read_AQI_PushNotification->sendMessages();

?>
