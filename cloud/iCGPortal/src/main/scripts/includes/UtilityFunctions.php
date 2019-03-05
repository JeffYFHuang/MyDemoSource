<?php

define('READONLYCONNECTION','ReadOnlyConnection1');

class UtilityFunctions {  
    protected $dbMasterHost;
    protected $dbSlaveHost;
    protected $dbPort;
    protected $dbUser;
    protected $dbDatabase;
    protected $dbPassword;
    protected $dbConnection;
    protected $dbReadConnection;
	
    protected $debug_enabled;
    protected $http_api_retry_count;
    protected $api_google_fcm;
    protected $notification_server_key;
    protected $admin_email;
	
    function __construct(){
    	global $config;
    	global $nl;
		$this->initConfig ();
    }
    
	private function initConfig() {
		global $config_file_path;
		
		require ($config_file_path);
				
		if (array_key_exists ( 'master', $config )) {
			if (array_key_exists ( 'host', $config ['master'] ))
				$this->dbMasterHost = $config ['master'] ['host'];
			if (array_key_exists ( 'port', $config ['master'] ))
				$this->dbPort = $config ['master'] ['port'];
			if (array_key_exists ( 'user', $config ['master'] ))
				$this->dbUser = $config ['master'] ['user'];
			if (array_key_exists ( 'database', $config ['master'] ))
				$this->dbDatabase = $config ['master'] ['database'];
			if (array_key_exists ( 'pass', $config ['master'] ))
				$this->dbPassword = $config ['master'] ['pass'];
		} else {
			$this->callUtilityFunctions->print_error ( "Master Database Configurations Not Found !!!!" );
		}
		
		if (array_key_exists ( 'ReadOnlyConnectionHosts', $config )) {
			$this->dbSlaveHost = $config ['ReadOnlyConnectionHosts'];
		} else {
			$this->callUtilityFunctions->print_error ( "Readonly Database Configurations Not Found !!!!" );
		}
			
		if (array_key_exists ( 'http_api_retry_count', $config )){
			$this->http_api_retry_count = $config ['http_api_retry_count'];
		}else{
			$this->http_api_retry_count = 1;
		}
			
		if (array_key_exists ( 'api_google_fcm', $config )){
			$this->api_google_fcm = $config ['api_google_fcm'];
		} else {
			$this->callUtilityFunctions->print_error ( "Not Found: api_google_fcm used for Push Notification !!!!" );
		}	
			
		if (array_key_exists ( 'admin_email', $config )){
			$this->admin_email = $config ['admin_email'];
		} else {
			$this->callUtilityFunctions->print_error ( "Not Found: admin_email used for Email Notification !!!!" );
		}
			
		if (array_key_exists ( 'notification_server_key', $config )){
			$this->notification_server_key = $config ['notification_server_key'];
		} else {
			$this->callUtilityFunctions->print_error ( "Not Found: notification_server_key used for Push Notification !!!!" );
		}				
		
		if (array_key_exists ( 'debug_enabled', $config )){
			$this->debug_enabled = $config ['debug_enabled'];
		}
	}
	
	public function sendNotification($message){
		$msgNotification = array
		(
			'title' => $message['title'],
			'body' => $message['body']		 
		);
		if(!empty($message['eventid']) && $message['eventid'] > 0){
			$this->print_debug("inside if");
			$msgBody = array
			(
				'eventid' => $message['eventid'],
				'queueid' => $message['queueid'],
				'deviceeventid' => $message['deviceeventid'],
				'uuid' => $message['uuid']		 
			);
			
			$fields = array
			(
				'registration_ids' => Array($message['token']),
				'data'			=> $msgBody,
				'content_available'    => true,
				'priority'              => 'high',
				'notification' => $msgNotification
			);
		}else{
			$this->print_debug("inside else");
			$fields = array
			(
				'registration_ids' => Array($message['token']),
				'notification' => $msgNotification
			);
		}
		
		$headers = array
		(
			'Authorization: key=' . $this->notification_server_key,
			'Content-Type: application/json'
		);
		
		$ch = curl_init();
		curl_setopt( $ch, CURLOPT_URL, $this->api_google_fcm );
		curl_setopt( $ch, CURLOPT_POST, true );
		curl_setopt( $ch, CURLOPT_TIMEOUT, 6000); // the timeout in seconds
		curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers );
		curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
		curl_setopt( $ch, CURLOPT_SSL_VERIFYHOST, 0 );
		curl_setopt( $ch, CURLOPT_SSL_VERIFYPEER, false );
		curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $fields ) );
		$this->print_debug( "Notification message: ". json_encode($fields) );
		$result ['RESPONSE'] = json_decode(curl_exec ( $ch ), true);
		$result ['INFO'] = curl_getinfo ( $ch );
		$result ['ERROR'] = curl_error ( $ch );
		$http_status_code = curl_getinfo ( $ch, CURLINFO_HTTP_CODE );
		
		$this->print_debug( "http_status_code: ". $http_status_code );
		curl_close ( $ch );
		
		if ($http_status_code == 404) {
			$this->print_error ( "Server/URL requested is not found !!!" );
		}
		if (!empty ( $result ['ERROR'] )) {
			$this->print_debug("Error returned for:: [URL:$this->api_google_fcm] [Error: " . $result ['ERROR'] . "]");
		}
		if (!empty ( $result ['INFO'] )) {
			$this->print_debug("The information regarding:: [URL:$this->api_google_fcm] " . print_r($result ['INFO'], true));
		}
		if (!empty ( $result ['RESPONSE'] )) {
			$this->print_debug("Response as received for:: [URL:$this->api_google_fcm] " . print_r($result ['RESPONSE'], true));
		}
		
		return $result;
	}
	
    public function updateActivityLog($user_role, $messages, $level, $action){
		$this->print_debug("Inside updateActivityLog");
		/* Connect to database for write */
		$this->dbConnect($this->dbConnection);
    	if($this->dbConnection){
			if(is_array($messages)){
				foreach($messages as $key=>$message){
					$sql  = "INSERT INTO `activity_log` (`activity_log_id`, `name`, `username`, `user_role`, `created_date`, `level`, `action`, `summary`, `ipaddress`) VALUES (NULL, 'CRON Jobs', 'auto', '" . $user_role . "', NOW(), '" . $level . "', '" . $action . "', '" . http_build_query($message, '', '&amp;') . "', '')";

					$this->e_mysql_query($sql,$this->dbConnection);
				}
			}else if(!empty($messages)){
				$sql  = "INSERT INTO `activity_log` (`activity_log_id`, `name`, `username`, `user_role`, `created_date`, `level`, `action`, `summary`, `ipaddress`) VALUES (NULL, 'CRON Jobs', 'auto', '" . $user_role . "', NOW(), '" . $level . "', '" . $action . "', '" . $messages . "', '')";

				$this->e_mysql_query($sql,$this->dbConnection);
			}    		
    	}else{
    		$this->print_error ( "Not connected to the write database. Unable to update messages" );
    	}
		/* Close database for write */
	    $this->dbClose($this->dbConnection);
    }

    function fileGetContents($url){
    	$response = file_get_contents($url);
	//$result ['RESPONSE'] = $response;
	return $response;
    }

    /**
     * Method to mke CURL requests and process the response. Return will be an array contianing
     * RESPONSE - The actual CURL response as received from the url
     * INFO - The information regarding a specific CURL request
     * ERROR - The error message or empty string if no error occurred
     *
     * @param string $url to request 
	 * @param string/json $query value to send 
     * @throws nop_exception If the string is not a boolean
     */
	function makeCURLRequest($url) {
		$retry_count = 1;
		while($retry_count < $this->http_api_retry_count){
			$ch = curl_init ();
			curl_setopt ( $ch, CURLOPT_URL, $url );
			curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, 1 );
			curl_setopt ( $ch, CURLOPT_CONNECTTIMEOUT, 5 );
			curl_setopt( $ch, CURLOPT_TIMEOUT, 6000); // the timeout in seconds
			if(!empty($proxy)) curl_setopt ( $ch, CURLOPT_PROXY, $proxy );
			if(!empty($query)) curl_setopt($ch, CURLOPT_POSTFIELDS, $query);
			
			$result ['RESPONSE'] = json_decode(curl_exec ( $ch ), true);
			$result ['INFO'] = curl_getinfo ( $ch );
			$result ['ERROR'] = curl_error ( $ch );
			$http_status_code = curl_getinfo ( $ch, CURLINFO_HTTP_CODE );
			
			curl_close ( $ch );
			
			/*if (!empty ( $result ) && empty ( $result ['ERROR'] )) {
				$retry_count = $this->http_api_retry_count;
			}else{
				$retry_count++;
				$this->print_debug("current retry count: ". $retry_count );
			}
			*/
			if ($http_status_code != 200) {
				$retry_count++;
				$this->print_debug( "Retrying again for AQI/UVI data");
				sleep(60);
			} else {
				$retry_count = $this->http_api_retry_count;
			}
			//$this->print_debug ( "Inside: makeCURLRequest, current retry count: " . $retry_count . "\n Error response for previous attempt: " . $result ['ERROR']);
		}
		
		if ($http_status_code == 404) {
			$this->print_error ( "Server/URL requested is not found !!!" );
		}
		if (!empty ( $result ['ERROR'] )) {
			$this->print_debug("Error returned for:: [URL:$url] [Error: " . $result ['ERROR'] . "]");
		}
		/*if (!empty ( $result ['INFO'] )) {
			$this->print_debug("The information regarding:: [URL:$url] " . print_r($result ['INFO'], true));
		}*/
		if (!empty ( $result ['RESPONSE'] )) {
			$this->print_debug("Response as received for:: [URL:$url] " . print_r($result ['RESPONSE'], true));
		}else{
			$this->print_debug("No Response received for:: [URL:$url] ");
		}
		
		return $result;
	}
	
    /**
	 * Open a database connection based in preconfigured credentials
	 * 
	 * @param Integer $db represents the index of the database to use
	 * @param Object  $connection database object connection
	 */
    function dbConnect(&$connection, $db = 'default'){
    	//if($db == 'default'){
    		$hostToConnect = $this->dbMasterHost;
    	/*} else{
    		$randomIndex = rand ( 0, sizeof ( $this->dbSlaveHost ) - 1 );
    		$hostToConnect = $this->dbSlaveHost [$randomIndex];
    	}*/
    	
    	$connection = mysqli_connect ( $hostToConnect . ':' . $this->dbPort, $this->dbUser, $this->dbPassword );
        if($connection) {
            if(!mysqli_select_db($connection, $this->dbDatabase)){
                $this->print_error ( 'Can\'t select table' );
                $this->dbClose($connection);
                unset($connection);
            }
            $this->print_debug("Connected to database: " . $this->dbDatabase);
	    //$connection->set_charset("utf8");
	    if (!$connection->set_charset("utf8")) {
      		printf("Error loading character set utf8: %s\n", $connection->error);
  	    } 
	    printf("Current character set: %s\n", $connection->character_set_name());
        } else {
        	$this->print_error("Error connecting to database " . $this->dbDatabase);
        }
    }
    
    /**
	 * Close database connection
	 * 
	 * @param Object $connection database object connection
	 */
    function dbClose(&$connection){
        if($connection){
            mysqli_close($connection);
            $this->print_debug("Closed connection to database");
        }
    }

    function e_mysql_query($query, $link_identifier = NULL) {
    	global $nl;
		global $test_mode;
		$result = '';
		
    	if ($link_identifier == NULL) {
    		throw new Exception ( " link_identifier is null." );
    	}
		
	$result = mysqli_query ( $link_identifier, $query );
	if (! $result) {
		$error = mysqli_error ($link_identifier) . $nl . $query;
		throw new Exception ( $error );
	}
	if($test_mode) $this->print_debug('test_mode is true >> ' . $query);
		
    	return $result;
    }

    function print_error($error){
        global $nl;
		global $local;
		if($local) echo '<font color="red">';
		echo ($nl . date ( "D M d h:i:s Y" ) . ": " . $error . $nl);
		if($local) echo '</font>';
		exit;
    }
    
    function print_debug($msg){
    	global $nl;
		if ($this->debug_enabled > 0) {
			echo ($nl . date ( "D M d h:i:s Y" ) . ": " . $msg . $nl);
		}
    }
    
    function send_mail($subject = 'iCG Wearable CRON JOB', $message = 'iCG Wearable CRON JOB'){
		$headers = "From:  $this->admin_email \r\n" .
			'X-Mailer: PHP/' . phpversion();

		mail($this->admin_email, $subject, $message, $headers);
    }
}

?>
