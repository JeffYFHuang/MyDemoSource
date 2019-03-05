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
	$lockfile = fopen($path . "includes/apnlockliteon", "w");
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
	$lockfile = fopen($path . "includes/apnlockliteon", "w");
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

if (!$local && !flock($lockfile,LOCK_EX | LOCK_NB)) {
	echo "cant get lock";
	exit();
}

class Repeated_PushNotification {  
    protected $repeate_count; 
	protected $uf;
	
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
		
		if (array_key_exists ( 'repeate_count', $config )){
			$this->repeate_count = $config ['repeate_count'];
		} else {
			$this->uf->send_mail("Not Found: repeate_count setting for resending Push Notification", "From: Repeated_PushNotification \n Reason: repeate_count setting for resending Push Notification");
			$this->uf->print_error ( "Not Found: repeate_count setting for resending push notification !!!!" );
		}
	}
	
    public function createPushMessages(){
		$messages = array();
		/* Connect to database for read-only */
		$this->uf->dbConnect($uf->dbReadConnection,READONLYCONNECTION);
		if($uf->dbReadConnection){
			$sql = "SELECT `device_events_queue`.`queue_id`, `device_events_queue`.`device_event_id`, `device_events_queue`.`user_id`, `users`.`android_app_token`, `users`.`iphone_app_token`,`users`.`role_type`, `device_events`.`event_occured_date`, `device_events`.`event_id`, `supported_events`.`event_name`, `supported_events`.`event_description`, `device_events`.`uuid`, `students`.`name` AS student_name
			FROM `device_events_queue`, `users`, `device_events`, `supported_events`, `device_students`, `students` 
			WHERE `users`.`user_id` = `device_events_queue`.`user_id` 
			AND `device_events_queue`.`device_event_id` = `device_events`.`device_event_id` 
			AND `device_events`.`event_id` = `supported_events`.`event_id` 
            AND `device_students`.`device_uuid` = `device_events`.`uuid` 
			AND `device_students`.`status` = 'active'
            AND `students`.`student_id` = `device_students`.`student_id`
			AND `device_events_queue`.`queue_sent` < $this->repeate_count 
			AND `device_events_queue`.`isEliminated` = 'n'
			ORDER BY `users`.`role_type`";
			
			$result = $this->uf->e_mysql_query($sql,$uf->dbReadConnection);
			if($result){
				while ($row = mysqli_fetch_array($result)){
					if(!empty($row['android_app_token'])){
						$messages[] = array(
							'token' =>$row['android_app_token'],
							'title' => $row['event_name'] . ': ' . $row['student_name'],
							'body' => $row['event_name'] . ' Occured At: ' . $row['event_occured_date'],
							'eventid'=> $row['event_id'],
							'queueid'=> $row['queue_id'],
							'deviceeventid'=> $row['device_event_id'],
							'uuid'=> $row['uuid']
						);
					}
					if(!empty($row['iphone_app_token'])){
						$messages[] = array(
							'token' =>$row['iphone_app_token'],
							'title' => $row['event_name'] . ': ' . $row['student_name'],
							'body' => $row['event_name'] . ' Occured At: ' . $row['event_occured_date'],
							'eventid'=> $row['event_id'],
							'queueid'=> $row['queue_id'],
							'deviceeventid'=> $row['device_event_id'],
							'uuid'=> $row['uuid']
						);
					}
				}
			}
			$this->uf->dbClose($uf->dbReadConnection); 
		} else {
			$this->uf->updateActivityLog('IWPS', "Not connected to the write database. Unable to update messages", 'error', 'PushNotification');
			$this->uf->send_mail("Not connected to the write database", "From: Repeated_PushNotification \n API URL: $this->aqi_api_url \n Reason: Not connected to the write database. Unable to update messages. Need to check config.php file for Database Configurations.");
			$this->uf->print_error ( "Not connected to the write database. Unable to update messages" );
		}
		return $messages;
    }
	
    public function sendMessages() {
		$messages = $this->createPushMessages();
		$messages_failed = array();
		
		/* get all messages from queue, if any */
		if($messages){
			/* Connect to database for write */
			$this->uf->dbConnect($uf->dbConnection);
		
			/* if connected push all messages to APN server */
			foreach($messages as $key=>$message){
				$result = $this->uf->sendNotification($message);
				if (!empty ( $result['RESPONSE']['results'][0]['error'] )) {
					array_push($messages_failed, $messages[$key]);
				}else{
					//Success Case - Update queue_sent Count & queue_sent_date
					if($uf->dbConnection){
						$sql  = "UPDATE `device_events_queue` SET `queue_sent_date` = NOW(), `queue_sent` = `queue_sent`+1 WHERE `queue_id` = " . $message['queueid'];

						$this->uf->e_mysql_query($sql,$this->dbConnection);
					}else{
						$this->uf->print_error ( "Not connected to the write database. Unable to update queue" );
					}
				}
			}
			/* Close database for write */
			$this->uf->dbClose($uf->dbConnection);
		
			if(!empty($messages_failed)) $this->uf->updateActivityLog('event_queue', $messages_failed, 'error', 'PushNotification');
		} else {
			$this->uf->updateActivityLog('event_queue', "No Pending Queue Found To Send Push Notification", 'info', 'PushNotification');
			$this->uf->send_mail("No Pending Queue To Send Repeated Push Notification", "From: Repeated_PushNotification \n Reason: No Pending Queue To Send Repeated Push Notification.");
			$this->uf->print_debug("No Pending Queue To Send Repeated Push Notification");
		}
    }	
}

$read_Repeated_PushNotification = new Repeated_PushNotification();
$read_Repeated_PushNotification->sendMessages();

// Let the next process run this job
if(!$local){
	flock($lockfile,LOCK_UN);
	fclose($lockfile);

}
?>
