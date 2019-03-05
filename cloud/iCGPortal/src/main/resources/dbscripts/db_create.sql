-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Sep 07, 2017 at 09:46 AM
-- Server version: 10.1.13-MariaDB
-- PHP Version: 7.0.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `liteon`
--

-- DROP DATABASE IF EXISTS `liteon`;
-- CREATE DATABASE IF NOT EXISTS `liteon` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
-- USE `liteon`;


-- --------------------------------------------------------

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `account_id` int(11) NOT NULL,
  `account_name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `account_type` enum('parent','school','admin') COLLATE utf8_bin NOT NULL,
  `account_active` enum('y','n') COLLATE utf8_bin DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `activity_log`
--

DROP TABLE IF EXISTS `activity_log`;
CREATE TABLE `activity_log` (
  `activity_log_id` int(11) NOT NULL,
  `name` varchar(45) COLLATE utf8_bin NOT NULL,
  `username` varchar(145) COLLATE utf8_bin NOT NULL,
  `user_role` enum('parent_admin','parent_member','school_admin','school_teacher','school_staff','system_admin','support_staff','super_admin','Wearable','Beacon','IWPS','event_queue') COLLATE utf8_bin NOT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `level` enum('info','error','critical') COLLATE utf8_bin NOT NULL,
  `action` enum('UserLogin','UserLogout','Update','Create','Delete','PushNotification','EmailNotification','Submit','BeaconLogin','DatabaseBackup') COLLATE utf8_bin NOT NULL,
  `summary` varchar(2000) COLLATE utf8_bin NOT NULL,
  `ipaddress` varchar(45) COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `announcement_id` int(11) NOT NULL,
  `school_id` int(11) NOT NULL,
  `name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `class_grade`
--

DROP TABLE IF EXISTS `class_grade`;
CREATE TABLE `class_grade` (
  `class_grade_id` int(11) NOT NULL,
  `school_id` int(11) NOT NULL,
  `teacher_id` int(11) DEFAULT NULL,
  `grade` varchar(3) COLLATE utf8_bin NOT NULL,
  `class`varchar(3) COLLATE utf8_bin NOT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `closed_device_events`
--

DROP TABLE IF EXISTS `closed_device_events`;
CREATE TABLE `closed_device_events` (
  `id` int(11) NOT NULL,
  `device_event_id` int(11) DEFAULT 0 ,
  `parent_id` int(11) DEFAULT NULL,
  `admin_id` int(11) DEFAULT NULL,
  `staff_id` int(11) DEFAULT NULL,
  `parent_closed_date` datetime DEFAULT NULL,
  `admin_closed_date` datetime DEFAULT NULL,
  `staff_closed_date` datetime DEFAULT NULL,
  `duration` int(11) DEFAULT 0,
  `is_sos_abnormal` enum('yes','no','') COLLATE utf8_bin DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `county_areas`
--

DROP TABLE IF EXISTS `county_areas`;
CREATE TABLE `county_areas` (
  `county_area_id` int(11) NOT NULL,
  `area_cn` varchar(45) CHARACTER SET utf8 NOT NULL,
  `area_en` varchar(45) CHARACTER SET utf8 NOT NULL,
  `county_cn` varchar(45) CHARACTER SET utf8 NOT NULL,
  `county_en` varchar(45) CHARACTER SET utf8 NOT NULL,
  `county_district_cn` varchar(45) CHARACTER SET utf8 NOT NULL,
  `county_district_en` varchar(45) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Taiwan Area and county mapping for iCG IWPS feature' ROW_FORMAT=COMPACT;

-- --------------------------------------------------------

--
-- Table structure for table `devices`
--

DROP TABLE IF EXISTS `devices`;
CREATE TABLE `devices` (
  `device_id` int(11) NOT NULL,
  `device_configuration_id` int(11) NOT NULL,
  `school_id` int(11) NOT NULL,
  `uuid` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `session_id` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `session_expiry` datetime DEFAULT NULL,
  `status` enum('broken','returned','assigned','') COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `temp_device_analytics_output`
--

DROP TABLE IF EXISTS `temp_device_analytics_output`;
CREATE TABLE `temp_device_analytics_output` (
  `id` int(11) NOT NULL,
  `device_id` int(11) NOT NULL,
  `physical_fitness_index` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `sleep_quality_index` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `concentration_level_index` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `device_configurations`
--

DROP TABLE IF EXISTS `device_configurations`;
CREATE TABLE `device_configurations` (
  `device_configuration_id` int(11) NOT NULL,
  `device_model` varchar(45) COLLATE utf8_bin NOT NULL,
  `firmware_name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `firmware_version` varchar(45) COLLATE utf8_bin NOT NULL,
  `description` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `firmware_size` double DEFAULT NULL,
  `firmware_file` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `low_battery` int(11) DEFAULT NULL,
  `gps_report_frequency` int(11) DEFAULT NULL,
  `device_self_testing_version` int(11) DEFAULT NULL,
  `wearable_sync_frequency` int(11) DEFAULT NULL,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `device_events`
--

DROP TABLE IF EXISTS `device_events`;
CREATE TABLE `device_events` (
  `device_event_id` int(11) NOT NULL,
  `uuid` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `event_id` int(11) NOT NULL,
  `gps_data_code` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `gps_location_data` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `sensor_type_code` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `sensor_error_code` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `vital_sign_type` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `vital_sign_value` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `abnormal_code` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `battery_level_value` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `in_time` time DEFAULT NULL,
  `out_time` time DEFAULT NULL,
  `is_entry_exit_abnormal` enum('yes','no','') COLLATE utf8_bin DEFAULT NULL,
  `abnormal_reason` varchar(150) COLLATE utf8_bin DEFAULT NULL,
  `geozone_id` int(11) DEFAULT NULL,
  `ips_receiver_zone_id` int(11) DEFAULT NULL,
   `event_occured_date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `device_events_queue`
--

DROP TABLE IF EXISTS `device_events_queue`;
CREATE TABLE `device_events_queue` (
  `queue_id` int(11) NOT NULL,
  `device_event_id` int(11) NOT NULL,
-- `notification_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `app_type` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `app_token` varchar(300) COLLATE utf8_bin DEFAULT NULL,
  `queue_sent` int(11) DEFAULT NULL,
  `queue_sent_date` datetime DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `isEliminated` enum('y','n') COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `device_students`
--

DROP TABLE IF EXISTS `device_students`;
CREATE TABLE `device_students` (
  `device_students_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `device_uuid` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `status` enum('active','inactive') COLLATE utf8_bin DEFAULT NULL,
  `start_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `end_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `event_subscriptions`
--

DROP TABLE IF EXISTS `event_subscriptions`;
CREATE TABLE `event_subscriptions` (
  `notification_id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL DEFAULT '0',
  `student_id` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `geozones`
--

DROP TABLE IF EXISTS `geozones`;
CREATE TABLE `geozones` (
  `geozone_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `uuid` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `zone_radius` float(20) NOT NULL,
  `zone_details` varchar(400) COLLATE utf8_bin DEFAULT NULL,
  `zone_name` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `zone_entry_alert` enum('yes','no') COLLATE utf8_bin DEFAULT NULL,
  `zone_exit_alert` enum('yes','no') COLLATE utf8_bin DEFAULT NULL,
  `zone_description` varchar(145) COLLATE utf8_bin DEFAULT NULL,
  `frequency_minutes` int(2) DEFAULT NULL,
  `valid_till` datetime DEFAULT NULL,
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- --------------------------------------------------------

--
-- Table structure for table `parent_kids`
--

DROP TABLE IF EXISTS `parent_kids`;
CREATE TABLE `parent_kids` (
  `parent_kid_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `pet_details`
--

DROP TABLE IF EXISTS `pet_details`;
CREATE TABLE `pet_details` (
  `pet_details_id` int(11) NOT NULL,
  `uuid` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `pet_type` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `pet_name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `size_level` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `shape_level` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `ornament_level` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `gladness_level` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `vigor_level` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `created_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `reminders`
--

DROP TABLE IF EXISTS `reminders`;
CREATE TABLE `reminders` (
  `reminder_id` int(11) NOT NULL,
  `class_grade_id` int(11) NOT NULL,
  `comments` varchar(245) COLLATE utf8_bin DEFAULT NULL,
  `image_name` varchar(250) COLLATE utf8_bin DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `rewards`
--

DROP TABLE IF EXISTS `rewards`;
CREATE TABLE `rewards` (
  `reward_id` int(11) NOT NULL,
  `rewards_category_id` int(11) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `reward_icon_url` varchar(60) COLLATE utf8_bin DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `rewards_category`
--

DROP TABLE IF EXISTS `rewards_category`;
CREATE TABLE `rewards_category` (
  `rewards_category_id` int(11) NOT NULL,
  `school_id` int(11) NOT NULL,
  `category_name` varchar(245) CHARACTER SET utf8 DEFAULT NULL,
  `category_icon_url` varchar(60) COLLATE utf8_bin DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

-- --------------------------------------------------------

--
-- Table structure for table `rewards_students`
--

DROP TABLE IF EXISTS `rewards_students`;
CREATE TABLE `rewards_students` (
  `id` int(11) NOT NULL,
  `reward_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `received_count` int(11) NOT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `school_calendar`
--

DROP TABLE IF EXISTS `school_calendar`;
CREATE TABLE `school_calendar` (
  `school_calendar_id` int(11) NOT NULL,
  `school_id` int(11) NOT NULL,
  `name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `date_close` date NOT NULL,
  `date_reopen` date DEFAULT NULL,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `school_details`
--

DROP TABLE IF EXISTS `school_details`;
CREATE TABLE `school_details` (
  `school_details_id` int(11) NOT NULL,
  `school_id` int(11) NOT NULL,
  `school_in_start` time DEFAULT NULL,
  `school_in_end` time DEFAULT NULL,
  `school_out_start` time DEFAULT NULL,
  `school_out_end` time DEFAULT NULL,
  `mobile_number` varchar(15) COLLATE utf8_bin DEFAULT NULL,
  `city` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `state` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `zipcode` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `county` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `country` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `address` varchar(145) COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- --------------------------------------------------------

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
CREATE TABLE `students` (
  `student_id` int(11) NOT NULL,
  `registartion_no` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `roll_no` varchar(5) COLLATE utf8_bin DEFAULT NULL,
  `class_grade_id` int(11) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `nickname` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `emergency_contact` varchar(15) COLLATE utf8_bin DEFAULT NULL,
  `gender` enum('MALE','FEMALE') COLLATE utf8_bin DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `height` float DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `allergies` varchar(245) COLLATE utf8_bin DEFAULT NULL,
  `allow_sleep_data` enum('yes','no') COLLATE utf8_bin NOT NULL DEFAULT 'no',
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `supported_events`
--

DROP TABLE IF EXISTS `supported_events`;
CREATE TABLE `supported_events` (
  `event_id` int(11) NOT NULL,
  `event_name` varchar(45) COLLATE utf8_bin NOT NULL,
  `event_description` varchar(245) COLLATE utf8_bin DEFAULT NULL,
  `event_default` enum('yes','no') COLLATE utf8_bin NOT NULL DEFAULT 'no',
  `generated_by` enum('device','system') COLLATE utf8_bin NOT NULL DEFAULT 'device',
  `notify_parent` enum('yes','no') COLLATE utf8_bin NOT NULL DEFAULT 'no',
  `notify_member` enum('yes','no') COLLATE utf8_bin NOT NULL DEFAULT 'no',
  `notify_staff` enum('yes','no') COLLATE utf8_bin DEFAULT 'no',
  `notify_teacher` enum('yes','no') COLLATE utf8_bin NOT NULL DEFAULT 'no',
  `supported_fields` varchar(145) COLLATE utf8_bin DEFAULT '',
  `parent_unsubscribe` enum('yes','no') COLLATE utf8_bin NOT NULL DEFAULT 'no'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `system_configuration`
--

DROP TABLE IF EXISTS `system_configuration`;
CREATE TABLE `system_configuration` (
  `system_configuration_id` int(11) NOT NULL,
  `iwps_sync_hours` int(11) NOT NULL,
  `web_session_validity_minutes` int(11) NOT NULL,
  `wearable_session_validity_minutes` int(11) NOT NULL,
  `password_reset_validity_minutes` int(11) NOT NULL,
  `student_allergies` varchar(545) COLLATE utf8_bin NOT NULL,
  `source_date_format` varchar(10) COLLATE utf8_bin NOT NULL,
  `source_datetime_format` varchar(20) COLLATE utf8_bin NOT NULL,
  `db_date_format` varchar(10) COLLATE utf8_bin NOT NULL,
  `db_datetime_format` varchar(20) COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `timetable`
--

DROP TABLE IF EXISTS `timetable`;
CREATE TABLE `timetable` (
  `timetable_id` int(11) NOT NULL,
  `class_grade_id` int(11) NOT NULL,
  `subject_one` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `subject_two` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `subject_three` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `subject_four` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `subject_five` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `subject_six` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `subject_seven` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `subject_eight` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `week_day` enum('MON','TUE','WED','THU','FRI') COLLATE utf8_bin NOT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `account_id` int(11) NOT NULL,
  `role_type` enum('parent_admin','parent_member','school_admin','school_teacher','school_staff','system_admin','support_staff','super_admin') COLLATE utf8_bin NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `username` varchar(145) COLLATE utf8_bin DEFAULT NULL,
  `password` varchar(245) COLLATE utf8_bin DEFAULT NULL,
  `openid_username` varchar(150) COLLATE utf8_bin DEFAULT NULL,
  `user_active` enum('y','n') COLLATE utf8_bin NOT NULL,
  `mobile_number` varchar(15) COLLATE utf8_bin DEFAULT NULL,
  `app_token` varchar(300) COLLATE utf8_bin DEFAULT NULL COMMENT 'Token used for push notification',
  `app_type` enum('android','iphone','') COLLATE utf8_bin DEFAULT '',
  `password_activation_code` varchar(145) COLLATE utf8_bin DEFAULT NULL,
  `password_activation_code_expiry` datetime DEFAULT NULL,
  `signup_activation_code` varchar(145) COLLATE utf8_bin DEFAULT NULL,
  `mobile_session_id` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `session_id` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `session_expiry` datetime DEFAULT NULL,
  `lastlogin_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------


--
-- Table structure for table `ips_receiver`
--

CREATE TABLE `ips_receiver` (
  `ips_receiver_id` int(11) NOT NULL,
  `ips_receiver_mac` varchar(20) COLLATE utf8_bin NOT NULL,
  `receiver_name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `school_id` int(11) NOT NULL,
  `receiver_version` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `receiver_status` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `session_id` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `session_expiry` datetime DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `ips_receiver_zone`
--

CREATE TABLE `ips_receiver_zone` (
  `ips_receiver_zone_id` int(11) NOT NULL,
  `ips_receiver_id` int(11) NOT NULL,
  `zone_name` varchar(50) COLLATE utf8_bin NOT NULL,
  `map_type` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `building_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `floor_number` varchar(11) COLLATE utf8_bin DEFAULT NULL,  
  `map_filename` varchar(150) COLLATE utf8_bin DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `ips_receiver_device`
--

CREATE TABLE `ips_receiver_device` (
  `ips_receiver_device_id` int(11) NOT NULL,
  `ips_receiver_zone_id` int(11) NULL,
  `ips_receiver_id` int(11) NOT NULL,
  `device_uuid` varchar(45) COLLATE utf8_bin NOT NULL,
  `firmware_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `firmware_version` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `device_model` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(15) COLLATE utf8_bin DEFAULT NULL,
  `status_description` varchar(1500) COLLATE utf8_bin DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`account_id`);

--
-- Indexes for table `activity_log`
--
ALTER TABLE `activity_log`
  ADD PRIMARY KEY (`activity_log_id`),
  ADD KEY `activity_log_username` (`username`),
  ADD KEY `activity_log_user_role` (`user_role`),
  ADD KEY `activity_log_create_date` (`created_date`),
  ADD KEY `activity_log_level` (`level`),
  ADD KEY `activity_log_action` (`action`);

--
-- Indexes for table `announcement`
--
ALTER TABLE `announcement`
  ADD PRIMARY KEY (`announcement_id`),
  ADD KEY `announcement_school_id` (`school_id`);
  

--
-- Indexes for table `class_grade`
--
ALTER TABLE `class_grade`
  ADD PRIMARY KEY (`class_grade_id`),
  ADD UNIQUE KEY `school_class_grade_teacher` (`school_id`,`class`,`grade`) USING BTREE,
  ADD UNIQUE KEY `class_teachers` (`teacher_id`) USING BTREE,
  ADD UNIQUE KEY `cl_teacher_id` (`teacher_id`) USING BTREE,
  ADD KEY `class_grade_teacher_id` (`teacher_id`),
  ADD KEY `class_grade_school_id` (`school_id`);

--
-- Indexes for table `closed_device_events`
--
ALTER TABLE `closed_device_events`
  -- ADD PRIMARY KEY (`device_event_id`),
  ADD KEY `closed_device_events_parent_id` (`parent_id`),
  ADD KEY `closed_device_events_admin_id` (`admin_id`),
  ADD KEY `closed_device_events_staff_id` (`staff_id`);

--
-- Indexes for table `county_areas`
--
ALTER TABLE `county_areas`
  ADD PRIMARY KEY (`county_area_id`),
  ADD KEY `county_areas_area_cn` (`area_cn`),
  ADD KEY `county_areas_county_en` (`county_en`);

--
-- Indexes for table `devices`
--
ALTER TABLE `devices`
  ADD PRIMARY KEY (`device_id`),
  ADD KEY `devices_device_configuration_id` (`device_configuration_id`) USING BTREE,
  ADD KEY `devices_school_id` (`school_id`);

--
-- Indexes for table `device_analytics_output`
--
ALTER TABLE `temp_device_analytics_output`
  ADD PRIMARY KEY (`id`);
 

--
-- Indexes for table `device_configurations`
--
ALTER TABLE `device_configurations`
  ADD PRIMARY KEY (`device_configuration_id`),
  ADD UNIQUE KEY `device_configurations_firmwarefile` (`firmware_file`) USING BTREE;

--
-- Indexes for table `device_events`
--
ALTER TABLE `device_events`
  ADD PRIMARY KEY (`device_event_id`),
  ADD KEY `device_events_event_id` (`event_id`),
  ADD KEY `device_events_geozone_id` (`geozone_id`);

--
-- Indexes for table `device_events_queue`
--
ALTER TABLE `device_events_queue`
  ADD PRIMARY KEY (`queue_id`),
  ADD KEY `device_events_queue_device_event_id` (`device_event_id`),
  -- ADD KEY `device_events_queue_notification_id` (`notification_id`),
  ADD KEY `device_events_queue_user_id` (`user_id`);

--
-- Indexes for table `device_students`
--
ALTER TABLE `device_students`
  ADD PRIMARY KEY (`device_students_id`),
  ADD KEY `device_student_id` (`student_id`),
  ADD INDEX(`device_uuid`);
  
--
-- Indexes for table `event_subscriptions`
--
ALTER TABLE `event_subscriptions`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `event_subscriptions_event_id` (`event_id`),
  ADD KEY `event_subscriptions_student_id` (`student_id`),
  ADD KEY `event_subscriptions_user_id` (`user_id`);

--
-- Indexes for table `geozones`
--
ALTER TABLE `geozones`
  ADD PRIMARY KEY (`geozone_id`),
  ADD KEY `geozones_user_id` (`user_id`);

--
-- Indexes for table `parent_kids`
--
ALTER TABLE `parent_kids`
  ADD PRIMARY KEY (`parent_kid_id`),
  ADD KEY `member_students_user_id` (`user_id`),
  ADD KEY `member_students_student_id` (`student_id`);
  
--
-- Indexes for table `pet_details`
--
ALTER TABLE `pet_details`
  ADD PRIMARY KEY (`pet_details_id`); 

--

--
-- Indexes for table `reminders`
--
ALTER TABLE `reminders`
  ADD PRIMARY KEY (`reminder_id`);

--
-- Indexes for table `rewards`
--
ALTER TABLE `rewards`
  ADD PRIMARY KEY (`reward_id`),
  ADD KEY `rewards_rewards_category_id` (`rewards_category_id`);

--
-- Indexes for table `rewards_category`
--
ALTER TABLE `rewards_category`
  ADD PRIMARY KEY (`rewards_category_id`);

--
-- Indexes for table `rewards_students`
--
ALTER TABLE `rewards_students`
  ADD PRIMARY KEY (`id`),
  ADD KEY `rewards_students_reward_id` (`reward_id`),
  ADD KEY `rewards_students_student_id` (`student_id`),
  ADD KEY `rewards_students_teacher_id` (`teacher_id`);

--
-- Indexes for table `school_calendar`
--
ALTER TABLE `school_calendar`
  ADD PRIMARY KEY (`school_calendar_id`),
  ADD KEY `school_calendar_school_id` (`school_id`);

--
-- Indexes for table `school_details`
--
ALTER TABLE `school_details`
  ADD PRIMARY KEY (`school_details_id`),
  ADD KEY `school_details_school_id` (`school_id`);


--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`student_id`),
  ADD UNIQUE KEY `registartion_no` (`registartion_no`),
  ADD KEY `students_class_grade_id` (`class_grade_id`);
 

--
-- Indexes for table `supported_events`
--
ALTER TABLE `supported_events`
  ADD PRIMARY KEY (`event_id`);

--
-- Indexes for table `system_configuration`
--
ALTER TABLE `system_configuration`
  ADD PRIMARY KEY (`system_configuration_id`);

--
-- Indexes for table `timetable`
--
ALTER TABLE `timetable`
  ADD PRIMARY KEY (`timetable_id`),
  ADD UNIQUE KEY `timetable_class_grade_id` (`class_grade_id`, `week_day`) USING BTREE;

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `users_msession_id` (`mobile_session_id`),
  ADD UNIQUE KEY `users_session_id` (`session_id`),
  ADD UNIQUE KEY `users_username` (`username`),
  ADD UNIQUE KEY `users_openid_username` (`openid_username`),
  ADD KEY `users_account_id` (`account_id`),
  ADD KEY `users_role_type` (`role_type`);

--
-- Indexes for table `ips_receiver`
--
ALTER TABLE `ips_receiver`
  ADD PRIMARY KEY (`ips_receiver_id`),
  ADD UNIQUE KEY `school_id` (`school_id`),
  ADD UNIQUE KEY `ips_receiver_mac` (`ips_receiver_mac`);
  
--
-- Indexes for table `ips_receiver_zone`
--
ALTER TABLE `ips_receiver_zone`
  ADD PRIMARY KEY (`ips_receiver_zone_id`),
  ADD KEY `ips_receiver_zone_ips_receiver_id` (`ips_receiver_id`);
  
--
-- Indexes for table `ips_receiver_device`
--
ALTER TABLE `ips_receiver_device`
  ADD PRIMARY KEY (`ips_receiver_device_id`),
  ADD UNIQUE KEY `device_uuid` (`device_uuid`),
  ADD KEY `ips_receiver_device_ips_receiver_zone_id` (`ips_receiver_zone_id`) USING BTREE,
   ADD KEY `ips_receiver_device_ips_receiver_id` (`ips_receiver_id`);
  
--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `accounts`
--
ALTER TABLE `accounts`
  MODIFY `account_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `activity_log`
--
ALTER TABLE `activity_log`
  MODIFY `activity_log_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `announcement`
--
ALTER TABLE `announcement`
  MODIFY `announcement_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `class_grade`
--
ALTER TABLE `class_grade`
  MODIFY `class_grade_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `county_areas`
--
ALTER TABLE `county_areas`
  MODIFY `county_area_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `devices`
--
ALTER TABLE `devices`
  MODIFY `device_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `temp_device_analytics_output`
--
ALTER TABLE `temp_device_analytics_output`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `device_configurations`
--
ALTER TABLE `device_configurations`
  MODIFY `device_configuration_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `device_events`
--
ALTER TABLE `device_events`
  MODIFY `device_event_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `device_events_queue`
--
ALTER TABLE `device_events_queue`
  MODIFY `queue_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `device_students`
--
ALTER TABLE `device_students`
  MODIFY `device_students_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `event_subscriptions`
--
ALTER TABLE `event_subscriptions`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `geozones`
--
ALTER TABLE `geozones`
  MODIFY `geozone_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `parent_kids`
--
ALTER TABLE `parent_kids`
  MODIFY `parent_kid_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `pet_details`
--
ALTER TABLE `pet_details`
  MODIFY `pet_details_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `reminders`
--
ALTER TABLE `reminders`
  MODIFY `reminder_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `rewards`
--
ALTER TABLE `rewards`
  MODIFY `reward_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `rewards_category`
--
ALTER TABLE `rewards_category`
  MODIFY `rewards_category_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `rewards_students`
--
ALTER TABLE `rewards_students`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `school_calendar`
--
ALTER TABLE `school_calendar`
  MODIFY `school_calendar_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `school_details`
--
ALTER TABLE `school_details`
  MODIFY `school_details_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `student_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `supported_events`
--
ALTER TABLE `supported_events`
  MODIFY `event_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `system_configuration`
--
ALTER TABLE `system_configuration`
  MODIFY `system_configuration_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `timetable`
--
ALTER TABLE `timetable`
  MODIFY `timetable_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT;
  
  
  
--
-- AUTO_INCREMENT for table `closed_device_events`
--
ALTER TABLE `closed_device_events`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
  
--
-- AUTO_INCREMENT for table `ips_receiver`
--
ALTER TABLE `ips_receiver`
  MODIFY `ips_receiver_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ips_receiver_zone`
--
ALTER TABLE `ips_receiver_zone`
  MODIFY `ips_receiver_zone_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ips_receiver_device`
--
ALTER TABLE `ips_receiver_device`
  MODIFY `ips_receiver_device_id` int(11) NOT NULL AUTO_INCREMENT;
  
  
--
-- Constraints for dumped tables
--

--
-- Constraints for table `announcement`
--
ALTER TABLE `announcement`
 -- ADD CONSTRAINT `announcement_class` FOREIGN KEY (`school_id`,`class`) REFERENCES `class_grade` (`school_id`, `class`) ON UPDATE NO ACTION,
  ADD CONSTRAINT `announcement_school_id` FOREIGN KEY (`school_id`) REFERENCES `accounts` (`account_id`) ON UPDATE NO ACTION;
 -- ADD CONSTRAINT `announcement_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON UPDATE NO ACTION;

--
-- Constraints for table `class_grade`
--
ALTER TABLE `class_grade`
  ADD CONSTRAINT `class_grade_school_id` FOREIGN KEY (`school_id`) REFERENCES `accounts` (`account_id`) ON UPDATE NO ACTION,
  ADD CONSTRAINT `class_grade_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`user_id`) ON UPDATE NO ACTION;

--
-- Constraints for table `closed_device_events`
--
ALTER TABLE `closed_device_events`
  ADD CONSTRAINT `closed_device_events_admin_id` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  -- ADD CONSTRAINT `closed_device_events_device_event_id` FOREIGN KEY (`device_event_id`) REFERENCES `device_events` (`device_event_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `closed_device_events_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `closed_device_events_staff_id` FOREIGN KEY (`staff_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `devices`
--
ALTER TABLE `devices`
  ADD CONSTRAINT `devices_device_configuration_id` FOREIGN KEY (`device_configuration_id`) REFERENCES `device_configurations` (`device_configuration_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
	ADD CONSTRAINT `devices_school_id` FOREIGN KEY (`school_id`) REFERENCES `accounts` (`account_id`) ON UPDATE NO ACTION;

--
-- Constraints for table `device_events`
--
ALTER TABLE `device_events`
  ADD CONSTRAINT `device_events_event_id` FOREIGN KEY (`event_id`) REFERENCES `supported_events` (`event_id`) ON UPDATE NO ACTION,
   ADD CONSTRAINT `device_events_geozone_id` FOREIGN KEY (`geozone_id`) REFERENCES `geozones` (`geozone_id`) ON UPDATE NO ACTION;

--
-- Constraints for table `device_events_queue`
--
ALTER TABLE `device_events_queue`
  ADD CONSTRAINT `device_events_queue_device_event_id` FOREIGN KEY (`device_event_id`) REFERENCES `device_events` (`device_event_id`) ON UPDATE NO ACTION,
--  ADD CONSTRAINT `device_events_queue_notification_id` FOREIGN KEY (`notification_id`) REFERENCES `event_subscriptions` (`notification_id`) ON UPDATE NO ACTION,
  ADD CONSTRAINT `device_events_queue_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON UPDATE NO ACTION;

--
-- Constraints for table `device_students`
--
ALTER TABLE `device_students`
  ADD CONSTRAINT `device_students_student_id` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON UPDATE NO ACTION;

--
-- Constraints for table `event_subscriptions`
--
ALTER TABLE `event_subscriptions`
  ADD CONSTRAINT `event_subscriptions_student_id` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON UPDATE NO ACTION,
  ADD CONSTRAINT `event_subscriptions_event_id` FOREIGN KEY (`event_id`) REFERENCES `supported_events` (`event_id`) ON UPDATE NO ACTION,
  ADD CONSTRAINT `event_subscriptions_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON UPDATE NO ACTION;

--
-- Constraints for table `geozones`
--
ALTER TABLE `geozones`
  ADD CONSTRAINT `geozones_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON UPDATE NO ACTION;

--
-- Constraints for table `parent_kids`
--
ALTER TABLE `parent_kids`
  ADD CONSTRAINT `member_students_student_id` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON UPDATE NO ACTION,
  ADD CONSTRAINT `member_students_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON UPDATE NO ACTION;

--
-- Constraints for table `reminders`
--
ALTER TABLE `reminders`
  ADD CONSTRAINT `reminders_class_grade_id` FOREIGN KEY (`class_grade_id`) REFERENCES `class_grade` (`class_grade_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `rewards`
--
ALTER TABLE `rewards`
  ADD CONSTRAINT `rewards_rewards_category_id` FOREIGN KEY (`rewards_category_id`) REFERENCES `rewards_category` (`rewards_category_id`) ON UPDATE NO ACTION;


--
-- Constraints for table `rewards_students`
--
ALTER TABLE `rewards_students`
  ADD CONSTRAINT `rewards_students_reward_id` FOREIGN KEY (`reward_id`) REFERENCES `rewards` (`reward_id`) ON UPDATE NO ACTION,
  ADD CONSTRAINT `rewards_students_student_id` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON UPDATE NO ACTION,
  ADD CONSTRAINT `rewards_students_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`user_id`) ON UPDATE NO ACTION;

--
-- Constraints for table `school_calendar`
--
ALTER TABLE `school_calendar`
  ADD CONSTRAINT `school_calendar_school_id` FOREIGN KEY (`school_id`) REFERENCES `accounts` (`account_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;
  
  --
-- Constraints for table `school_details`
--
ALTER TABLE `school_details`
  ADD CONSTRAINT `school_details_school_id` FOREIGN KEY (`school_id`) REFERENCES `accounts` (`account_id`);


--
-- Constraints for table `students`
--
ALTER TABLE `students`
  ADD CONSTRAINT `students_class_grade_id` FOREIGN KEY (`class_grade_id`) REFERENCES `class_grade` (`class_grade_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `timetable`
--
ALTER TABLE `timetable`
  ADD CONSTRAINT `timetable_class_grade_id` FOREIGN KEY (`class_grade_id`) REFERENCES `class_grade` (`class_grade_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_account_id` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`);
  
--
-- Constraints for table `ips_receiver`
--
ALTER TABLE `ips_receiver`
  ADD CONSTRAINT `ips_receiver_school_id` FOREIGN KEY (`school_id`) REFERENCES `accounts` (`account_id`) ON UPDATE NO ACTION;

--
-- Constraints for table `ips_receiver_zone`
--
ALTER TABLE `ips_receiver_zone`
  ADD CONSTRAINT `ips_receiver_zone_ips_receiver_id` FOREIGN KEY (`ips_receiver_id`) REFERENCES `ips_receiver` (`ips_receiver_id`) ON UPDATE NO ACTION;  
  
--
-- Constraints for table `ips_receiver_device`
--
ALTER TABLE `ips_receiver_device`
  ADD CONSTRAINT `ips_receiver_device_ips_receiver_zone_id` FOREIGN KEY (`ips_receiver_zone_id`) REFERENCES `ips_receiver_zone` (`ips_receiver_zone_id`) ON UPDATE NO ACTION,
   ADD CONSTRAINT `ips_receiver_device_ips_receiver_id` FOREIGN KEY (`ips_receiver_id`) REFERENCES `ips_receiver` (`ips_receiver_id`) ON UPDATE NO ACTION;


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;