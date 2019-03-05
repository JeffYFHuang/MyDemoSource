<?php defined('SYSPATH') OR die('No direct access allowed.');

// Database Configuration
$config['master'] = array
(
	'user'     => 'root',
	'pass'     => 'MySq&23DF1$%@#^!',
	'host'     => '10.10.10.11',
	'port'     => 3306,
	'database' => 'ext_icgcloud'
);

// List of Read Only Database Connection Host IP Addresses
$config['ReadOnlyConnectionHosts'] = array('10.10.10.11');

// Set database backup folder path
$config['db_backup_path'] = '/home/ubuntu/cronscripts/database_backups';

/* Config Air Quality Index (AQI) & Ultraviolet Index (UVI) API URL's*/
$config['aqi_api_url'] = 'http://opendata2.epa.gov.tw/AQI.json';
//'https://opendata.epa.gov.tw/webapi/api/rest/datastore/355000000I-000259/?format=json&token=5tad11A4zUi49wSbIGZn2A'; 

$config['uvi_api_url'] = 'http://opendata2.epa.gov.tw/UV/UV.json';
//'https://opendata.epa.gov.tw/webapi/api/rest/datastore/355000000I-000005/?format=json&token=5tad11A4zUi49wSbIGZn2A'; 

$config['aqi_allergies'] = 'sneezing, runny nose'; //AQI Allergies, comma seperated text
$config['standard_aqi'] = '101'; //AQI Standard Value
$config['uvi_allergies'] = 'sun burn, prickly heat'; //UVI Allergies, comma seperated text
$config['standard_uvi'] = '6'; //UVI Standard Value

// API access key from Google API's Console
$config['api_google_fcm'] = 'https://fcm.googleapis.com/fcm/send';
$config['notification_server_key'] = 'AAAAqESU66E:APA91bGoSkRj6zJbyrlAfECrlGtD3396-hgaz_KaXxisTlLWxMPPcjCP4z9psE79ULSI1umGY7TP1vzfY9meNokJZIsYdH5jBZDpctR31o_eg135DgpUXb2x40Sp2oWfpXo3PQuMMFr-';

//Maximum number of times to call External API's incase of failure
$config['http_api_retry_count'] = 10;//5;

//Maximum number of times to send repeated push notifications for device events
$config['repeate_count'] = 5;

// Set comma seperated list of email address to receive email at the end of cron job execution 
$config['admin_email'] = 'premkumar.chandramohan@aricent.com, Peggy.PH.Lin@liteon.com';

// Set 1 to capture debug logs for the script else 0
$config['debug_enabled'] = 1;

// Geozone Entry & Exit Event Codes
$config['geozone_entry'] = 10;
$config['geozone_exit'] = 11;
?>
