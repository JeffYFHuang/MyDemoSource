-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 20, 2017 at 03:22 PM
-- Server version: 10.1.21-MariaDB
-- PHP Version: 7.1.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `liteon`
--

-- Uncomment and change the Schema Name below line before importing the script to whichever schema you need data for.

-- USE `liteon`;

--
-- Dumping data for table `accounts`
--

INSERT INTO `accounts` (`account_id`, `account_name`, `account_type`, `account_active`, `created_date`, `updated_date`) VALUES
 (1, 'Super Admin', 'admin', 'y', NOW(), NOW());

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `account_id`, `role_type`, `name`, `username`, `password`,`openid_username`, `user_active`, `mobile_number`, `app_token`, `app_type`, `password_activation_code`, `password_activation_code_expiry`,`signup_activation_code`,`mobile_session_id`,`session_id`, `session_expiry`, `lastlogin_date`,`created_date`,`updated_date`) VALUES

(1, 1, 'super_admin', 'Super Admin', 'super@admin.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1',NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL);

--
-- Dumping data for table `system_configuration`
--

INSERT INTO `system_configuration` (`system_configuration_id`, `iwps_sync_hours`, `web_session_validity_minutes`, `wearable_session_validity_minutes`, `password_reset_validity_minutes`, `student_allergies`, `source_date_format`, `source_datetime_format`, `db_date_format`, `db_datetime_format`) VALUES
(1,5,60,60,60, 'sneezing, stuffiness,runny nose,itchiness, sun burn,itchy rash,prickly heat','YYYY-MM-DD','YYYY-MM-DD HH:MM:SS','YYYY-MM-DD','YYYY-MM-DD HH:MM:SS');

--
-- Dumping data for table `supported_events`
--

INSERT INTO `supported_events` (`event_id`, `event_name`, `event_description`, `event_default`, `generated_by`, `notify_staff`, `supported_fields` ,`notify_parent`,`notify_teacher`,`parent_unsubscribe`, `notify_member`) VALUES
(1, 'School Enter', 'Strudent Entered into School', 'yes', 'device', 'no', 'gps_data_code,gps_location_data,event_occured_date','yes','yes','yes','yes'),
(2, 'School Exit', 'Strudent Exited from School', 'yes', 'device', 'no', 'gps_data_code,gps_location_data,event_occured_date','yes','yes','yes','yes'),
(3, 'Geofence Entry ', 'School Created Geofence Entry', 'yes', 'device', 'no', 'gps_data_code,gps_location_data,event_occured_date','yes','yes','yes','no'),
(4, 'Geofence Exit', 'School Created Geofence Exit', 'yes', 'device', 'no', 'gps_data_code,gps_location_data,event_occured_date','yes','yes','yes','no'),
(5, 'Class Reminders', 'Reminders from Teachers for a class', 'yes', 'system', 'no', '','yes','no','no','no'),
(6, 'Class Teacher Rewards', 'ClassTeacher  Rewards', 'no', 'system', 'no', '','no','no','no','no'),
(7, 'School Announcements', 'Announcements from school', 'no', 'system', 'no', '','no','no','no','no'),
(8, 'Fitness & Activity Monitor', 'Fitness & Activity Monitor (PFI; Step Count; Activity: Walk, Run, Cycle; Heart Rate; Calorie Count; Sleep; Concentration Level; Stress Level)', 'no', 'system', 'no', '','no','no','no','no'),
(9, 'Initiative Warm Push Services', 'Initiative Warm Push Services', 'yes', 'system', 'no', '','yes','no','no','no'),
(10, 'Geozone Entry', 'Parent Created Geozone Entry', 'yes', 'system', 'no', '','yes','no','no','no'),
(11, 'Geozone Exit', 'Parent Created Geozone Exit', 'yes', 'system', 'no', '','yes','no','no','no'),
(12, 'Report Summary', 'Report Summary', 'yes', 'system', 'no', '','no','no','yes','no'),
(13, 'SOS Alert', 'SOS Alert', 'yes', 'device', 'yes', 'gps_data_code,gps_location_data,event_occured_date','yes','yes','yes','yes'),
(14, 'SOS Removing - No repetetive notification, no report required.', 'SOS Removing - No repetetive notification, no r eport required.', 'no', 'device', 'no', 'event_occured_date','no','no','no','no'),
(15, 'Fall Detection', 'Fall Detection','yes', 'device', 'yes', 'gps_data_code,gps_location_data,event_occured_date','yes','yes','no','no'),
(16, 'Abnormal Vital Sign', 'Abnormal Vital Sign', 'yes', 'device', 'no', 'vital_sign_type,vital_sign_value,event_occured_date','yes','yes','yes','no'),
(17, 'Sensor Malfunction', 'Sensor Malfunction', 'yes', 'device', 'no', 'sensor_type_code,event_occured_date','yes','yes','no','no'),
(18, 'Low Battery level Notification', 'Low Battery Level  Notification','yes', 'device', 'no', 'battery_level_value,event_occured_date','yes','no','no','no'),
(19, 'GPS Location ', 'GPS Location ', 'no', 'device', 'no', 'gps_data_code,gps_location_data,event_occured_date','no','no','no','no'),
(20, 'Band Removal Alert', 'Band Removal Alert', 'yes', 'device', 'no', 'gps_data_code,gps_location_data,event_occured_date','yes','yes','yes','yes'),
(21, 'Band Back Alert', 'Band Back Alert', 'no', 'device', 'no', 'event_occured_date','no','no','no','no');


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

