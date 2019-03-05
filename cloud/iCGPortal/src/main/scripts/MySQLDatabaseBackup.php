#! /usr/local/bin/php
<?php
define('SYSPATH','1');

$local = false; //True to run the script in local setup

$config_file_path = $functions_file_path = '';
$nl = $path = '';

if ($local) {
	$nl = "<br><br>";
	$path = 'D:/SarathData/softwares/xampp/htdocs/sarath/liteon/';
	if (file_exists ( $path . "includes/config.php" )) {
		$config_file_path = $path . "includes/config.php";
	}
	if (file_exists ( $path . "includes/UtilityFunctions.php" )) {
		$functions_file_path = $path . "includes/UtilityFunctions.php";
	}
} else {
	$nl = "\n\n";
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

class DatabaseBackup {
    protected $uvi_api_url;
    protected $uvi_record_type;
    protected $db_backup_path;
    protected $uf;
    protected $dbHost;
    protected $dbPort;
    protected $dbUser;
    protected $dbDatabase;
    protected $dbPassword;
	
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
		
		if (array_key_exists ( 'master', $config )) {
			if (array_key_exists ( 'host', $config ['master'] ))
				$this->dbHost = $config ['master'] ['host'];
			if (array_key_exists ( 'port', $config ['master'] ))
				$this->dbPort = $config ['master'] ['port'];
			if (array_key_exists ( 'user', $config ['master'] ))
				$this->dbUser = $config ['master'] ['user'];
			if (array_key_exists ( 'database', $config ['master'] ))
				$this->dbDatabase = $config ['master'] ['database'];
			if (array_key_exists ( 'pass', $config ['master'] ))
				$this->dbPassword = $config ['master'] ['pass'];
		} else {
			$this->uf->print_error ( "Master Database Configurations Not Found !!!!" );
		}
			
		if (array_key_exists ( 'db_backup_path', $config )){
			$this->db_backup_path = $config ['db_backup_path'];
		} else {
			$this->uf->send_mail("Not Found: db_backup_path used for Database Backup", "From: DatabaseBackup \n Reason: Not Found: db_backup_path used for Database Backup");
			$this->uf->print_error ( "Not Found: db_backup_path used for Database Backup !!!!" );
		}	
	}
	
	public function EXPORT_TABLES(){
		$filename = $this->db_backup_path . '/'.$this->dbDatabase . '-' . time().'.sql.gz';
		$type = 'info';
		$command = "/usr/bin/mysqldump --user=$this->dbUser --password='$this->dbPassword' --host=$this->dbHost $this->dbDatabase | gzip > $filename";

		system($command);
		
		$bytes = $this->get_filesize($filename);
		$message = 'Database backup file:' . $filename . ', ' .	'File size:' . $bytes;
		
		if(!$bytes) { 
			$type = 'error';
			$subject = "Could not run mysql dump command";
			$message =  "From: DatabaseBackup \n Command: $command \n Reason: Non zero $return_var exec return values or zero $bytes bytes of $message is an error case";
			$this->uf->send_mail($subject, $message);
		}
		
		$this->uf->updateActivityLog('super_admin', $message, $type, 'DatabaseBackup');
		
	}
	
	/**
	 * Gets the filesize
	*/
	public function get_filesize($filename) {
		$bytes = filesize($filename);
			if ($bytes >= 1073741824)
			{
				$bytes = number_format($bytes / 1073741824, 2) . ' GB';
			}
			elseif ($bytes >= 1048576)
			{
				$bytes = number_format($bytes / 1048576, 2) . ' MB';
			}
			elseif ($bytes >= 1024)
			{
				$bytes = number_format($bytes / 1024, 2) . ' KB';
			}
			elseif ($bytes > 1)
			{
				$bytes = $bytes . ' bytes';
			}
			elseif ($bytes == 1)
			{
				$bytes = $bytes . ' byte';
			}
			else
			{
				$bytes = '0 bytes';
			}
			return $bytes;
	}

}

$takeDatabaseBackup = new DatabaseBackup();
$takeDatabaseBackup->EXPORT_TABLES();
?>
