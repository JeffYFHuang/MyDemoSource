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
(1, 'Super Admin', 'admin', 'y', NOW(), NOW()),
(2, 'Taipei American School', 'school',  'y', NOW(), NOW() ),
(3, 'Parent of Chia', 'parent', 'y', NOW(), NOW()),
(4, 'Parent of Chih', 'parent', 'y', NOW(), NOW()),
(5, 'Taipei International Christian Academy', 'school',  'y', NOW(), NOW() ),
(6, 'Parent of Yan', 'parent', 'y', NOW(), NOW() ),
(7, 'Parent of Chun', 'parent', 'y', NOW(), NOW()),
(8, 'Parent of Chieh', 'parent',  'y', NOW(), NOW() ),
(9, 'Parent of Ming', 'parent',  'y', NOW(), NOW() ),
(10, 'Taipei Municipal Jianguo High School', 'school',  'y', NOW(), NOW() ),
(11, 'National San Chung Senior High School', 'school',  'y', NOW(), NOW() ),
(12, 'Taipei Private Yan Ping High School', 'school',  'y', NOW(), NOW() ),
(13, 'Dominican International School', 'school',  'y', NOW(), NOW() ),
(14, 'Parent of Yan 1', 'parent', 'y', NOW(), NOW() ),
(15, 'Parent of Chun 1', 'parent', 'y', NOW(), NOW()),
(16, 'Parent of Chieh 1', 'parent',  'y', NOW(), NOW() ),
(17, 'Parent of Ming 1', 'parent',  'y', NOW(), NOW() ),
(18, 'Parent of Yan 2', 'parent', 'y', NOW(), NOW() ),
(19, 'Parent of Chun 2', 'parent', 'y', NOW(), NOW()),
(20, 'Parent of Chieh 2', 'parent',  'y', NOW(), NOW() ),
(21, 'Parent of Ming 3', 'parent',  'y', NOW(), NOW() );

--
-- Dumping data for table `school_details`
--
INSERT INTO `school_details` (`school_details_id`, `school_id`, `school_in_start`, `school_in_end`, `school_out_start`, `school_out_end`, `mobile_number`, `city`, `state`, `zipcode`, `county`, `country`) VALUES
(1, 2, '07:15:00', '08:00:00', '15:15:00', '16:00:00', '0911111111', 'Zhubei City', NULL, NULL, 'Hsinchu County', 'Taiwan'),
(2, 5, '07:30:00', '08:15:00', '15:15:00', '16:00:00', '0922222222', 'Yuanlin City', NULL, NULL, 'Changhua County', 'Taiwan'),
(3, 10, '07:30:00', '08:15:00', '15:15:00', '16:00:00', '0933333333', 'Taipei City', NULL, NULL, 'Zhongzheng', 'Taiwan'),
(4, 11, '07:30:00', '08:15:00', '15:15:00', '16:00:00', '0944444444', 'Yuanlin City', NULL, NULL, 'Changhua County', 'Taiwan'),
(5, 12, '07:30:00', '08:15:00', '15:15:00', '16:00:00', '0955555555', 'Yuanlin City', NULL, NULL, 'Changhua County', 'Taiwan'),
(6, 13, '07:30:00', '08:15:00', '15:15:00', '16:00:00', '0966666666', 'Yuanlin City', NULL, NULL, 'Changhua County', 'Taiwan');

--
-- Dumping data for table `school_calendar`
--
INSERT INTO `school_calendar` (`school_calendar_id`, `school_id`, `name`,`date_close`) VALUES
(NULL, 2,'Mid-Autumn Festival','2017-10-04'),
(NULL, 2,' National Day/Double Tenth Day','2017-10-10'),
(NULL, 2,' Taiwan’s Retrocession Day','2017-10-25'),
(NULL, 2,' Double Ninth Day','2017-10-28'),
(NULL, 2,' Constitution Day','2017-12-25'),
(NULL, 2,'New Years Day','2018-01-01'),
(NULL, 2,'Chinese New Years Eve','2018-02-15'),
(NULL, 2,'Chinese New Years','2018-02-16'),
(NULL, 2,'Chinese New Years Holiday','2018-02-17'),
(NULL, 2,'Chinese New Years Holiday','2018-02-18'),
(NULL, 2,'Chinese New Years Holiday','2018-02-19'),
(NULL, 2,'Chinese New Years Holiday','2018-02-20'),
(NULL, 2,'Peace Memorial Day','2018-02-28'),
(NULL, 2,'Lantern Festival','2018-03-02'),
(NULL, 2,'Childrens Day','2018-04-04'),
(NULL, 2,'Tomb Sweeping Day','2018-04-05'),
(NULL, 2,'Tomb Sweeping Day Holiday','2018-04-06'),
(NULL, 2,'Mothers Day','2018-05-13'),
(NULL, 2,'Dragon Boat Festival','2018-06-18'),
(NULL, 2,'Fathers Day','2018-08-08'),
(NULL, 2,'Mid Autumn Festival','2018-09-24'),
(NULL, 2,'National Day','2018-10-10'),
(NULL, 2,'Double Ninth Day','2018-10-17'),
(NULL, 2,'Republic Day Holiday','2018-12-31'),
(NULL, 5,' National Day/Double Tenth Day','2017-10-10'),
(NULL, 5,' Taiwan’s Retrocession Day','2017-10-25'),
(NULL, 5,' Double Ninth Day','2017-10-28'),
(NULL, 5,' Constitution Day','2017-12-25'),
(NULL, 5,'New Years Day','2018-01-01'),
(NULL, 5,'Chinese New Years Eve','2018-02-15'),
(NULL, 5,'Chinese New Years','2018-02-16'),
(NULL, 5,'Chinese New Years Holiday','2018-02-17'),
(NULL, 5,'Chinese New Years Holiday','2018-02-18'),
(NULL, 5,'Chinese New Years Holiday','2018-02-19'),
(NULL, 5,'Chinese New Years Holiday','2018-02-20'),
(NULL, 5,'Peace Memorial Day','2018-02-28'),
(NULL, 5,'Lantern Festival','2018-03-02'),
(NULL, 5,'Childrens Day','2018-04-04'),
(NULL, 10,'Tomb Sweeping Day','2018-04-05'),
(NULL, 10,'Tomb Sweeping Day Holiday','2018-04-06'),
(NULL, 10,'Mothers Day','2018-05-13'),
(NULL, 10,'Dragon Boat Festival','2018-06-18'),
(NULL, 10,'Fathers Day','2018-08-08'),
(NULL, 10,'Mid Autumn Festival','2018-09-24'),
(NULL, 10,'National Day','2018-10-10'),
(NULL, 10,'Double Ninth Day','2018-10-17'),
(NULL, 10,'Republic Day Holiday','2018-12-31');

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `account_id`, `role_type`, `name`, `username`, `password`,`openid_username`, `user_active`, `mobile_number`, `app_token`, `app_type`, `password_activation_code`, `password_activation_code_expiry`,`signup_activation_code`,`mobile_session_id`,`session_id`, `session_expiry`, `lastlogin_date`,`created_date`,`updated_date`) VALUES

(1, 1, 'super_admin', 'Super Admin', 'super@admin.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1',NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(2, 2, 'school_admin', 'Taipei American School Admin', 'ifdemo@liteon.com', '1000:2d4b6154ec3ccbc439934e5e6dc2be1e:1243c33ae3ed8358a0b69b7fa6a31743f44f8bdf88f7872184df70ef1cec3bcdfee5fea3ff62de48198ba3851a4e50c358a02e2b58bdf03ffcb77dfc6f14a802', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(3, 2, 'school_teacher', 'Taipei American School Teacher 1', 'dps_teacher1@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00',' ss23454', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(4, 3, 'parent_admin', 'Dev Parent User', 'admin1@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(5, 5, 'school_admin', 'Taipei International Christian Academy School', 'nps_admin@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1',NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00','ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(6, 5, 'school_teacher', 'Taipei International Christian Academy Teache', 'nps_teacher1@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(7, 2, 'school_teacher', 'Taipei American School Teacher 2', 'dps_teacher2@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(8, 2, 'school_staff', 'Taipei American School Staff 1', 'dps_staff1@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(9, 3, 'parent_member', 'Dev Guardian User', 'member1@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(10, 4, 'parent_admin', 'QA Parent User', 'admin2@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(11, 4, 'parent_member', 'QA Guardian User', 'member2@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(12, 6, 'parent_admin', 'LO Parent User', 'admin3@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00','ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(13, 6, 'parent_member', 'LO Guardian User', 'member3@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(14, 7, 'parent_admin', 'Dev Parent User', 'admin4@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00','ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(15, 8, 'parent_admin', 'LO Parent User', 'admin5@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(16, 9, 'parent_admin', 'QA Parent User', 'admin6@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(17, 10, 'school_admin', 'Taipei International Christian Academy School', 'cas_admin@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1',NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00','ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(18, 10, 'school_teacher', 'Taipei International Christian Academy Teache', 'cas_teacher1@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(19, 10, 'school_teacher', 'Taipei American School Teacher 2', 'cas_teacher2@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(20, 10, 'school_teacher', 'Taipei American School Teacher 2', 'cas_teacher3@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(21, 10, 'school_teacher', 'Taipei American School Teacher 2', 'cas_teacher4@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(22, 10, 'school_teacher', 'Taipei American School Teacher 2', 'cas_teacher5@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(23, 10, 'school_staff', 'Taipei American School Staff 1', 'cas_staff1@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(24, 10, 'school_staff', 'Taipei American School Staff 1', 'cas_staff2@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(25, 10, 'school_staff', 'Taipei American School Staff 1', 'cas_staff3@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(26, 10, 'school_staff', 'Taipei American School Staff 1', 'cas_staff4@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(27, 11, 'school_admin', 'National San Chung Senior High School', 'shs_admin@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1',NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00','ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(28, 11, 'school_teacher', 'National San Chung Senior High School Teache', 'shs_teacher1@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(29, 11, 'school_teacher', 'National San Chung Senior High SchoolTeacher 2', 'shs_teacher2@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(30, 11, 'school_teacher', 'National San Chung Senior High SchoolTeacher 2', 'shs_teacher3@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(31, 11, 'school_teacher', 'National San Chung Senior High SchoolTeacher 2', 'shs_teacher4@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(32, 11, 'school_teacher', 'National San Chung Senior High SchoolTeacher 2', 'shs_teacher5@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(33, 11, 'school_staff', 'National San Chung Senior High SchoolStaff 1', 'shs_staff1@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(34, 11, 'school_staff', 'National San Chung Senior High SchoolStaff 1', 'shs_staff2@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(35, 11, 'school_staff', 'National San Chung Senior High SchoolStaff 1', 'shs_staff3@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(36, 11, 'school_staff', 'National San Chung Senior High SchoolStaff 1', 'shs_staff4@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(37, 12, 'school_admin', 'Taipei Private Yan Ping High School', 'phs_admin@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1',NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00','ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(38, 12, 'school_teacher', 'Taipei Private Yan Ping High School Teache', 'phs_teacher1@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(39, 12, 'school_teacher', 'Taipei Private Yan Ping High SchoolTeacher 2', 'phs_teacher2@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(40, 12, 'school_teacher', 'Taipei Private Yan Ping High SchoolTeacher 2', 'phs_teacher3@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(41, 12, 'school_teacher', 'Taipei Private Yan Ping High SchoolTeacher 2', 'phs_teacher4@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(42, 12, 'school_teacher', 'Taipei Private Yan Ping High SchoolTeacher 2', 'phs_teacher5@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(43, 12, 'school_staff', 'Taipei Private Yan Ping High SchoolStaff 1', 'phs_staff1@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(44, 12, 'school_staff', 'Taipei Private Yan Ping High SchoolStaff 1', 'phs_staff2@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(45, 12, 'school_staff', 'Taipei Private Yan Ping High SchoolStaff 1', 'phs_staff3@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(46, 12, 'school_staff', 'Taipei Private Yan Ping High SchoolStaff 1', 'phs_staff4@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(47, 13, 'school_admin', 'Dominican International School', 'dis_admin@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1',NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00','ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(48, 13, 'school_teacher', 'Dominican International School Teache', 'dis_teacher1@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(49, 13, 'school_teacher', 'Dominican International SchoolTeacher 2', 'dis_teacher2@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(50, 13, 'school_teacher', 'Dominican International SchoolTeacher 2', 'dis_teacher3@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(51, 13, 'school_teacher', 'Dominican International SchoolTeacher 2', 'dis_teacher4@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(52, 13, 'school_teacher', 'Dominican International SchoolTeacher 2', 'dis_teacher5@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(53, 13, 'school_staff', 'Dominican International SchoolStaff 1', 'dis_staff1@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(54, 13, 'school_staff', 'Dominican International SchoolStaff 1', 'dis_staff2@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(55, 13, 'school_staff', 'Dominican International SchoolStaff 1', 'dis_staff3@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(56, 13, 'school_staff', 'Dominican International SchoolStaff 1', 'dis_staff4@school.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(57, 14, 'parent_admin', 'QA Parent User', 'admin7@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(58, 14, 'parent_member', 'Dev Guardian User', 'member4@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(59, 14, 'parent_member', 'QA Guardian User', 'member5@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(60, 15, 'parent_admin', 'LO Parent User', 'admin8@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00','ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(61, 15, 'parent_member', 'LO Guardian User', 'member6@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(62, 16, 'parent_admin', 'Dev Parent User', 'admin9@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00','ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(63, 17, 'parent_admin', 'LO Parent User', 'admin10@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'iphone', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(64, 18, 'parent_admin', 'QA Parent User', 'admin11@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(65, 19, 'parent_admin', 'QA Parent User', 'admin12@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(66, 20, 'parent_admin', 'QA Parent User', 'admin13@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL),
(67, 21, 'parent_admin', 'QA Parent User', 'admin14@parent.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', NULL, 'y', '9030008893', '359092050465370', 'android', 'xxyy1234','0000-00-00 00:00:00', 'ss23455', NULL, NULL,NULL, '0000-00-00 00:00:00', NULL, NULL);
INSERT INTO `users` (`account_id`, `role_type`, `name`, `username`, `password`, `user_active`, `mobile_number`) VALUES ('1', 'system_admin', 'System Admin', 'system1@admin.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', 'y', '9030008893');
INSERT INTO `users` (`account_id`, `role_type`, `name`, `username`, `password`, `user_active`, `mobile_number`) VALUES ('1', 'support_staff', 'Support Staff', 'support1@admin.com', '1000:2519ab53a31ceae60143e80b283629da:8c48649c45b8fce9cf196c890b897131fdd95cc2d8fa47fdb8238def25b75ac6803f3865aea46f9578357a4125a0d425bc3abcdd39e0d7a6394a047a620b1ee1', 'y', '9030008893');

INSERT INTO `class_grade` (`class_grade_id`,`school_id`,`teacher_id`,`grade`,`class`,`created_date`,`updated_date`) VALUES 
(1,2,3,'PK',101,NOW(), NULL),
(2,5,6,'KG',102,NOW(), NULL),
(3,2,7,'UG',201,NOW(), NULL),
(4,10,18,'G1',101,NOW(), NULL),
(5,10,19,'G2',201,NOW(), NULL),
(6,10,20,'G3',301,NOW(), NULL),
(7,10,21,'G1',102,NOW(), NULL),
(8,10,22,'G2',202,NOW(), NULL),
(9,11,28,'G1',101,NOW(), NULL),
(10,11,29,'G2',201,NOW(), NULL),
(11,12,38,'G3',301,NOW(), NULL),
(12,12,39,'G1',102,NOW(), NULL),
(13,13,48,'G2',202,NOW(), NULL),
(14,13,49,'UG',201,NOW(), NULL);

INSERT INTO `class_grade` (`school_id`, `grade`, `class`) VALUES ('5', 'UG', '222');
INSERT INTO `class_grade` (`school_id`, `grade`, `class`) VALUES ('5', 'PK', '223');
INSERT INTO `class_grade` (`school_id`, `grade`, `class`) VALUES ('5', 'G1', '224');
INSERT INTO `class_grade` (`school_id`, `grade`, `class`) VALUES ('5', 'G2', '225');
INSERT INTO `class_grade` (`school_id`, `grade`, `class`) VALUES ('5', 'G3', '226');
INSERT INTO `class_grade` (`school_id`, `grade`, `class`) VALUES ('2', 'G1', '228');
INSERT INTO `class_grade` (`school_id`, `grade`, `class`) VALUES ('2', 'G2', '229');
INSERT INTO `class_grade` (`school_id`, `grade`, `class`) VALUES ('2', 'G3', '330');
INSERT INTO `class_grade` (`school_id`, `grade`, `class`) VALUES ('2', 'PK', '332');
INSERT INTO `class_grade` (`school_id`, `grade`, `class`) VALUES ('2', 'UG', '333');



--
-- Dumping data for table `students` 
--

INSERT INTO `students` (`student_id`,  `registartion_no`,`roll_no`, `class_grade_id`,`name`, `nickname`,`emergency_contact`,`gender`, `dob`,`height`, `weight`, `allergies`, `created_date`, `updated_date`) VALUES
(1,  '10' ,'1100',1,'Chia-hua', 'Hui-ting', '9030442233',NULL,  NULL, NULL, NULL, 'sneezing, stuffiness,runny nose,itchiness, sun burn,itchy rash,prickly heat', NOW() , NULL),
(2,  '11','1101',2,'Chia-hui', 'Hui-ju', '9030442233',NULL,  NULL, NULL, NULL,'sneezing, stuffiness,prickly heat', NOW() , NULL),
(3,  '12', '1102',3,'Chia-ling', 'Hui-chun', '9030442233',NULL,  NULL, NULL, NULL,'runny nose,itchiness,prickly heat', NOW() , NULL),
(4, '13', '1103', 1,'Tsung-han', 'Kuan-ting', '9030442233',NULL,  NULL, NULL, NULL,'runny nose', NOW() , NULL),
(5, '123', '1104',2,'Chih-wei', 'Kuan-yu', '9030442233',NULL,  NULL, NULL, NULL,'sneezing', NOW() , NULL),
(6,  '14', '1105',3,'Chih-hung', 'Chia-hao', '9030442233',NULL,  NULL, NULL, NULL,'itchiness', NOW() , NULL),
(7, '15',  '1106',1,'Chih-hao', 'Chia-ming', '9030442233',NULL,  NULL, NULL, NULL,'itchiness', NOW() , NULL),
(8, '16' ,  '1107',2,'Che-wei', 'Chien-hung', '9030442233',NULL,  NULL, NULL, NULL,'sneezing', NOW() , NULL),
(9,  '17' , '1108',3,'Yu-hsuan', 'Chia-wei', '9030442233',NULL,  NULL, NULL, NULL,'itchy rash', NOW() , NULL),
(10, '18',  '1109',1,'Yan-ting', 'Chun-hung','9030442233',NULL,  NULL, NULL, NULL,'prickly heat', NOW() , NULL),
(11,'19',   '1111',2,'Hsin-hung', 'Chun-chieh','9030442233',NULL,  NULL, NULL, NULL,'prickly heat', NOW() , NULL),
(12,  '20', '1112',3,'Wei-ting', 'Chun-hsien', '9030442233',NULL,  NULL, NULL, NULL,'sun burn', NOW() , NULL),
(13, '102', '1102',3,'Chia-ling', 'Hui-chun', '0930442233',NULL,  NULL, NULL, NULL,'runny nose,itchiness,prickly heat', NOW() , NULL),
(14, '103', '1103', 1,'Tsung-han', 'Kuan-ting', '0930442233',NULL,  NULL, NULL, NULL,'runny nose', NOW() , NULL),
(15, '113', '1104',2,'Chih-wei', 'Kuan-yu', '0930442233',NULL,  NULL, NULL, NULL,'sneezing', NOW() , NULL),
(16, '114', '1105',3,'Chih-hung', 'Chia-hao', '0930442233',NULL,  NULL, NULL, NULL,'itchiness', NOW() , NULL),
(17, '115',  '1106',1,'Chih-hao', 'Chia-ming', '0990442233',NULL,  NULL, NULL, NULL,'itchiness', NOW() , NULL),
(18, '126' ,  '1107',2,'Che-wei', 'Chien-hung', '0930442233',NULL,  NULL, NULL, NULL,'sneezing', NOW() , NULL),
(19, '137' , '1108',3,'Yu-hsuan', 'Chia-wei', '0930442233',NULL,  NULL, NULL, NULL,'itchy rash', NOW() , NULL),
(20, '128',  '1109',1,'Yan-ting', 'Chun-hung','0930442233',NULL,  NULL, NULL, NULL,'prickly heat', NOW() , NULL),
(21, '119',   '1111',2,'Hsin-hung', 'Chun-chieh','0930442233',NULL,  NULL, NULL, NULL,'prickly heat', NOW() , NULL),
(22, '210', '1112',3,'Wei-ting', 'Chun-hsien', '0930442233',NULL,  NULL, NULL, NULL,'sun burn', NOW() , NULL),
(23, '202', '2102',3,'Chia-ling', 'Hui-chun', '0930442233',NULL,  NULL, NULL, NULL,'runny nose,itchiness,prickly heat', NOW() , NULL),
(24, '203', '2103', 1,'Tsung-han', 'Kuan-ting', '0930442233',NULL,  NULL, NULL, NULL,'runny nose', NOW() , NULL),
(25, '213', '2104',2,'Chih-wei', 'Kuan-yu', '0930442233',NULL,  NULL, NULL, NULL,'sneezing', NOW() , NULL),
(26, '214', '2105',3,'Chih-hung', 'Chia-hao', '0930442233',NULL,  NULL, NULL, NULL,'itchiness', NOW() , NULL),
(27, '215',  '2106',1,'Chih-hao', 'Chia-ming', '0990442233',NULL,  NULL, NULL, NULL,'itchiness', NOW() , NULL),
(28, '226' ,  '2107',2,'Che-wei', 'Chien-hung', '0930442233',NULL,  NULL, NULL, NULL,'sneezing', NOW() , NULL),
(29, '237' , '2108',3,'Yu-hsuan', 'Chia-wei', '0930442233',NULL,  NULL, NULL, NULL,'itchy rash', NOW() , NULL),
(30, '228',  '2109',1,'Yan-ting', 'Chun-hung','0930442233',NULL,  NULL, NULL, NULL,'prickly heat', NOW() , NULL),
(31, '219',   '2111',2,'Hsin-hung', 'Chun-chieh','0930442233',NULL,  NULL, NULL, NULL,'prickly heat', NOW() , NULL),
(32, '310', '2112',3,'Wei-ting', 'Chun-hsien', '0930442233',NULL,  NULL, NULL, NULL,'sun burn', NOW() , NULL);


--
-- Dumping data for table `system_configuration`
--

INSERT INTO `system_configuration` (`system_configuration_id`, `iwps_sync_hours`, `web_session_validity_minutes`, `wearable_session_validity_minutes`, `password_reset_validity_minutes`, `student_allergies`, `source_date_format`, `source_datetime_format`, `db_date_format`, `db_datetime_format`) VALUES
(1,5,60,60,60, 'sneezing, stuffiness,runny nose,itchiness, sun burn,itchy rash,prickly heat','YYYY-MM-DD','YYYY-MM-DD HH:MM:SS','YYYY-MM-DD','YYYY-MM-DD HH:MM:SS');

--
-- Dumping data for table `device_configurations`
--

INSERT INTO `device_configurations` (`device_configuration_id`, `device_model`, `firmware_name`,`firmware_version`,`description`,`firmware_size`,`firmware_file`,`low_battery`, `gps_report_frequency`,`device_self_testing_version`,`wearable_sync_frequency`, `created_date`,`updated_date`) VALUES
(1001,'model1','Model1 Firmware 1','xw.v5.1.1','Sample',12, 'nrf52832.zip',1,1,10,1, NOW()+INTERVAL 10 DAY_MINUTE, NOW()+INTERVAL 600 DAY_MINUTE),
(1002, 'model1','Model1 Firmware 2','xw.v5.1.2','Sample',8, 'nrf52832_xxaa.zip',1,1,10,1, NOW()+INTERVAL 700 DAY_MINUTE, NOW()+INTERVAL 800 DAY_MINUTE),
(1003, 'model3','Model3 Firmware 1','xw.v5.3.1','Sample',7, 'nrf52832_xx.zip',1,1,10,1, NOW()+INTERVAL 60 DAY_MINUTE, NOW()+INTERVAL 120 DAY_MINUTE);

--
-- Dumping data for table `devices`
--
INSERT INTO `devices` (`device_id`, `device_configuration_id`, `school_id`,`uuid`, `session_id`, `session_expiry`, `status`) VALUES 
 (1, 1001, 5, '63026db6-cf0f-4fb2-aefd-f297e451a346', NULL, NULL, 'assigned'),
 (2, 1001, 2, '3e1ad934-04a9-4cb9-87f1-b10cf6ce9a76', NULL, NULL, 'assigned'),
 (3, 1003, 5, '1b12dec0-ab84-48d6-95f7-92bf119770bd', NULL, NULL, 'assigned'),
 (4, 1001, 2, 'dc4888ce-5995-432b-ac55-671107eff9eb', NULL, NULL, 'assigned'),
 (5, 1001, 2, 'a9999cc6-7dab-4176-b344-770e917f2c32', NULL, NULL, 'assigned'),
 (6, 1001, 2, 'ce11418b-b068-455a-8edd-c6d656500519', NULL, NULL, 'assigned'),
 (7, 1003, 2, '0cfba685-9ba7-4013-81ca-e3d8ab3a1b39', NULL, NULL, 'assigned'),
 (8, 1001, 5, 'aebe58c1-9697-453d-bdeb-8a4ae44b258f', NULL, NULL, 'assigned'),
 (9, 1001, 2, '7ae8833d-dccd-42f2-b410-b91312c3b336', NULL, NULL, 'assigned'),
 (10, 1001, 2, '2e52bb6a-774d-4064-8dbf-4c8e0ca5fe4d', NULL, NULL, 'assigned'),
 (11, 1003, 5, '9f7ad649-ae9c-4dca-86fa-821c447625eb', NULL, NULL, 'broken'),
 (12, 1001, 2, '562da6df-c403-49df-83d7-a564513c178f', NULL, NULL, 'assigned'),
 (13, 1001, 2, '3e947738-8cc4-4449-acf9-4dfafa749e36', NULL, NULL, 'assigned'),
 (14, 1003, 2, '508650c7-8646-426a-a0ea-29143780f0aa', NULL, NULL, 'assigned'),
 (15, 1001, 5, 'ca3ab781-346c-4dae-a330-4eccd5b8fde7', NULL, NULL, 'assigned'),
 (16, 1001, 2, '24412b05-a20a-413f-9ab4-bcef1ba96b78', NULL, NULL, 'assigned'),
 (17, 1001, 2, '28d0691d-d56c-4c6d-8d8c-88be73273f10', NULL, NULL, 'assigned'),
 (18, 1001, 5, '66ce63c3-7e6a-41bb-bd29-1a620d829ea4', NULL, NULL, 'assigned'),
 (19, 1003, 2, 'ca2ad182-4616-417b-84cb-1c492cc39dc3', NULL, NULL, 'assigned'),
 (20, 1001, 2, 'e7b6946e-74a4-4cc0-ad27-f48a7fb165ae', NULL, NULL, 'unassigned'),
 (21, 1003, 5, 'a81f9087-286c-4130-98b8-51d3769cd951', NULL, NULL, 'unassigned'),
 (22, 1001, 2, 'e2a2774f-2f72-493e-89ea-f1d880ec06a4', NULL, NULL, 'unassigned'),
 (23, 1003, 2, '60396210-e3be-4dd0-ab17-093d7ab417d1', NULL, NULL, 'unassigned'),
 (24, 1001, 2, 'a8e2a3c2-c299-4f71-833b-c83dbbb53520', NULL, NULL, 'unassigned'),
 (25, 1001, 2, 'b217cb97-826a-46dc-9881-76f6307e332e', NULL, NULL, 'unassigned'),
 (26, 1001, 2, 'bf528a18-e059-456d-9371-9e8131a01cbb', NULL, NULL, 'unassigned'),
 (27, 1001, 2, '89b11e6d-d0b9-49e8-a538-d8e154ddea2e', NULL, NULL, 'unassigned'),
 (28, 1001, 2, 'e99dd5e8-7bea-42a2-8404-bd26e50a9eed', NULL, NULL, 'unassigned'),
 (29, 1001, 2, '4232efe7-5241-4048-a47f-78c7759a0a81', NULL, NULL, 'unassigned'),
 (30, 1001, 2, 'f6dd92d2-4a03-4fe7-a209-67a29a59108d', NULL, NULL, 'unassigned'),
 (31, 1001, 5, '336781f8-09da-49de-b6de-70b598920be2', NULL, NULL, 'unassigned'),
 (32, 1001, 2, '65758d67-4fca-42d9-af49-412b04e7cc88', NULL, NULL, 'unassigned'),
 (33, 1001, 2, '96dc1fd4-e801-43aa-8699-2314e7059e82', NULL, NULL, 'unassigned'),
 (34, 1003, 2, 'b5a7079c-3107-40fa-ac6e-ef55e86408c3', NULL, NULL, 'unassigned'),
 (35, 1001, 2, '638ddcdd-6c13-4c8d-8a6c-5c814524c627', NULL, NULL, 'unassigned'),
 (36, 1003, 2, '5849d6bb-512e-49f3-bc96-e943d53db9b9', NULL, NULL, 'unassigned'),
 (37, 1001, 2, '5d0a5a85-b970-440c-ac88-27323a4d6b6e', NULL, NULL, 'unassigned');

--
-- Dumping data for table `device_students`
--

INSERT INTO device_students (`device_students_id`,`student_id`,`device_uuid`,`status`,`start_date`,`end_date`) VALUES 
(NULL, 1, '63026db6-cf0f-4fb2-aefd-f297e451a346', 'inactive',NOW()+INTERVAL -32 DAY ,NOW()+INTERVAL -20 DAY),
(NULL, 1, '3e1ad934-04a9-4cb9-87f1-b10cf6ce9a76', 'active',NOW()+INTERVAL -21 DAY ,NULL),
(NULL, 2, '63026db6-cf0f-4fb2-aefd-f297e451a346', 'active',NOW()+INTERVAL -21 DAY ,NULL),
(NULL, 3, 'dc4888ce-5995-432b-ac55-671107eff9eb', 'active',NOW()+INTERVAL -12 DAY ,NULL),
(NULL, 4, 'a9999cc6-7dab-4176-b344-770e917f2c32', 'inactive',NOW()+INTERVAL -31 DAY ,NOW()+INTERVAL -22 DAY),
(NULL, 4, 'ce11418b-b068-455a-8edd-c6d656500519', 'inactive',NOW()+INTERVAL -23 DAY ,NOW()+INTERVAL -9 DAY),
(NULL, 4, '0cfba685-9ba7-4013-81ca-e3d8ab3a1b39', 'active',FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) ,NULL),
(NULL, 5, '1b12dec0-ab84-48d6-95f7-92bf119770bd', 'active',FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) ,NULL),
(NULL, 6, 'a9999cc6-7dab-4176-b344-770e917f2c32', 'active',NOW()+INTERVAL -25 DAY ,NULL),
(NULL, 7, 'ce11418b-b068-455a-8edd-c6d656500519', 'active',FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) ,NULL),
(NULL, 8, 'aebe58c1-9697-453d-bdeb-8a4ae44b258f', 'active',FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) ,NULL),
(NULL, 9, '7ae8833d-dccd-42f2-b410-b91312c3b336', 'active',FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) ,NULL),
(NULL, 10, '2e52bb6a-774d-4064-8dbf-4c8e0ca5fe4d', 'active',FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) ,NULL),
(NULL, 11, '9f7ad649-ae9c-4dca-86fa-821c447625eb', 'inactive',FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) ,NULL),
(NULL, 12, '562da6df-c403-49df-83d7-a564513c178f', 'active',FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) ,NULL),
(NULL, 13, '3e947738-8cc4-4449-acf9-4dfafa749e36', 'active', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NULL),
(NULL, 14, '508650c7-8646-426a-a0ea-29143780f0aa', 'active', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NULL),
(NULL, 15, 'ca3ab781-346c-4dae-a330-4eccd5b8fde7', 'active', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NULL),
(NULL, 16, '24412b05-a20a-413f-9ab4-bcef1ba96b78', 'active', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NULL),
(NULL, 17, '28d0691d-d56c-4c6d-8d8c-88be73273f10', 'active', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NULL),
(NULL, 18, '66ce63c3-7e6a-41bb-bd29-1a620d829ea4', 'active', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NULL),
(NULL, 19, 'ca2ad182-4616-417b-84cb-1c492cc39dc3', 'active', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -8 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NULL);

--
-- Dumping data for table `rewards_category`
--

INSERT INTO `rewards_category` (`rewards_category_id`, `school_id`, `category_name`, `category_icon_url`) VALUES
(1, 2, 'Scientist', 'blackcloud.png'),
(2, 2, 'Atheletics', 'price.jpg'),
(3, 2, 'Artist', 'computer.jpg'),
(4, 2, 'Acadmemics', 'computer.jpg'),
(5, 5, 'Scientist', 'blackcloud.png'),
(6, 5, 'Atheletics', 'price.jpg'),
(7, 5, 'Leadership', 'computer.jpg');

--
-- Dumping data for table `rewards`
--

INSERT INTO `rewards` (`reward_id`, `rewards_category_id`, `name`, `reward_icon_url`) VALUES
(NULL, 1, 'New Ideas', 'blackcloud.png'),
(NULL, 2, 'Team Works', 'price.jpg'),
(NULL, 3, 'Innovative Idea', 'computer.jpg'),
(NULL, 4, 'Best in Sceince', 'computer.jpg'),
(NULL, 4, 'Class Topper', 'computer.jpg'),
(NULL, 5, 'Class Topper', 'computer.jpg'),
(NULL, 6, 'New Ideas', 'blackcloud.png'),
(NULL, 7, 'Innovative Idea', 'computer.jpg');

--
-- Dumping data for table `rewards_students`
--

INSERT INTO `rewards_students` (`reward_id`, `student_id`, `teacher_id`, `received_count`) 
SELECT `rewards`.`reward_id`, `students`.`student_id`, `class_grade`.`teacher_id`, FLOOR( 1 + RAND( ) * 20 ) FROM students, `class_grade`, `rewards`, `rewards_category`
WHERE `class_grade`.`class_grade_id` = `students`.`class_grade_id` AND `class_grade`.`school_id` = `rewards_category`.`school_id` AND `rewards_category`.`rewards_category_id` = `rewards`.`rewards_category_id`;

--
-- Dumping data for table `rewards_category`
--

INSERT INTO `rewards_category` (`rewards_category_id`, `school_id`, `category_name`, `category_icon_url`) VALUES
(8, 2, 'Leadership', 'blackcloud.png'),
(9, 2, 'Initiative', 'price.jpg'),
(10, 5, 'Sports', 'blackcloud.png'),
(11, 5, 'Artist', 'price.jpg'),
(12, 5, 'Acadmemics', 'computer.jpg');

--
-- Dumping data for table `rewards`
--

INSERT INTO `rewards` (`reward_id`, `rewards_category_id`, `name`, `reward_icon_url`) VALUES
(NULL, 8,  'New Ideas', 'blackcloud.png'),
(NULL, 8,  'Team Works', 'price.jpg'),
(NULL, 9,  'Innovative Idea', 'computer.jpg'),
(NULL, 9,  'Best in Sceince', 'computer.jpg'),
(NULL, 10, 'Class Topper', 'computer.jpg'),
(NULL, 10, 'Class Topper', 'computer.jpg'),
(NULL, 11, 'New Ideas', 'blackcloud.png'),
(NULL, 11, 'Innovative Idea', 'computer.jpg'),
(NULL, 12, 'New Ideas', 'blackcloud.png'),
(NULL, 12, 'Innovative Idea', 'computer.jpg');

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

--
-- Dumping data for table `device_events` - Call 0
--

-- INSERT INTO `device_events`(`device_event_id`, `uuid`, `event_id`, `gps_data_code`, `gps_location_data`, `sensor_type_code`, `sensor_error_code`, `vital_sign_type`, `vital_sign_value`, `abnormal_code`, `battery_level_value`, `event_occured_date`) (SELECT NULL, ds.device_uuid, se.event_id, 'L2C' AS gps_data_code, CONCAT(( 22.00417 + RAND( ) * (25.12825-22.00417)), ',', ( 118.31833 + RAND( ) * (121.753-118.31833))) AS gps_location_data, 'CCD' AS sensor_type_code, NULL, NULL, NULL, NULL, NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -5 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) AS event_date FROM `supported_events` AS se, device_students AS ds, students AS s WHERE `generated_by` = 'device' AND se.event_id NOT IN (1, 2));

--
-- Dumping data for table `announcement`
--
INSERT INTO `announcement` (`announcement_id`, `school_id`, `name`, `description`, `created_date`, `updated_date`) VALUES
(NULL, 2, 'Kids shoes needs to be replaced', 'New Announcement', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -5 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW()),
(NULL, 2,  'Color Kid Photo Save Date Announcement', 'Foto Save', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -5 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW()),
(NULL, 2,  'Recent Parents Meet', 'Parents Meet', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -5 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW()),
(NULL, 2,  'Picnic Announcement', 'Picnic Announcement',FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -5 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW()),
(NULL, 5,  'Health Awarenes Announcement', 'Health Announcement', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -5 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW()),
(NULL, 5,  'Safety Awareness Announcements', 'Safety Announcement',FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -5 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW()),
(NULL, 5,  'Health Awarenes Announcement', 'Health Announcement', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -5 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW()),
(NULL, 5,  'Safety Awareness Announcements', 'Safety Announcement', FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -5 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW());

--
-- Dumping data for table `device_events` - Call 1
--

-- INSERT INTO `device_events`(`device_event_id`, `uuid`, `event_id`, `gps_data_code`, `gps_location_data`, `sensor_type_code`, `sensor_error_code`, `vital_sign_type`, `vital_sign_value`, `abnormal_code`, `battery_level_value`, `event_occured_date`) (SELECT NULL, ds.device_uuid, se.event_id, 'L2C' AS gps_data_code, CONCAT(( 22.00417 + RAND( ) * (25.12825-22.00417)), ',', ( 118.31833 + RAND( ) * (121.753-118.31833))) AS gps_location_data, 'CCD' AS sensor_type_code, NULL, NULL, NULL, NULL, NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -4 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) AS event_date FROM `supported_events` AS se, device_students AS ds, students AS s WHERE `generated_by` = 'device' AND se.event_id NOT IN (1, 2));

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '1', '42', '55', '58', NOW() + INTERVAL 6 DAY_HOUR);

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '1', '85', '78', '58', NOW() + INTERVAL 10 DAY_HOUR);

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '1', '98', '85', '75', NOW() + INTERVAL 16 DAY_HOUR);

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '5', '40', '58', '75', '2017-07-05 04:39:33');

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '5', '55', '65', '88', '2017-07-06 05:38:19');

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '5', '85', '65', '55', '2017-07-06 14:41:49');

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '7', '88', '78', '89', '2017-07-06 05:12:15');

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '7', '85', '89', '87', '2017-07-06 04:15:41');

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '7', '78', '45', '77', '2017-07-06 07:23:44');

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '9', '85', '58', '52', '2017-07-06 07:28:37');

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '9', '85', '85', '88', '2017-07-06 03:11:37');

INSERT INTO `temp_device_analytics_output` (`id`, `device_id`, `physical_fitness_index`, `sleep_quality_index`, `concentration_level_index`, `created_date`) VALUES (NULL, '9', '87', '78', '45', '2017-07-06 00:00:00');

--
-- Dumping data for table `device_events` - Call 2
--

-- INSERT INTO `device_events`(`device_event_id`, `uuid`, `event_id`, `gps_data_code`, `gps_location_data`, `sensor_type_code`, `sensor_error_code`, `vital_sign_type`, `vital_sign_value`, `abnormal_code`, `battery_level_value`, `event_occured_date`) (SELECT NULL, ds.device_uuid, se.event_id, 'L2C' AS gps_data_code, CONCAT(( 22.00417 + RAND( ) * (25.12825-22.00417)), ',', ( 118.31833 + RAND( ) * (121.753-118.31833))) AS gps_location_data, 'CCD' AS sensor_type_code, NULL, NULL, NULL, NULL, NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -3 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) AS event_date FROM `supported_events` AS se, device_students AS ds, students AS s WHERE `generated_by` = 'device' AND se.event_id NOT IN (1, 2));

--
-- Dumping data for table `reminders`
--
INSERT INTO `reminders` (`reminder_id`, `class_grade_id`, `comments`, `image_name`, `created_date`, `updated_date`) VALUES
 (1, 1 , 'Get supplied', NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -10 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW() + INTERVAL 10 DAY),
 (2, 2, 'Dress for success', NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -10 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW() + INTERVAL 10 DAY),
 (3, 3, 'Check for good health', NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -10 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW() + INTERVAL 10 DAY),
 (4, 1, 'Make a lunch plan', NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -10 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW() + INTERVAL 10 DAY),
 (5, 2, 'Early to bed and early to rise', NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -10 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW() + INTERVAL 10 DAY),
 (6, 3, 'Set Up or Update your Calendar', NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -10 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW() + INTERVAL 10 DAY),
 (7, 1, 'If your child is starting Kindergarten or a new school or a sports program and needs a physical, schedule their appointment today.', NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -10 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW() + INTERVAL 10 DAY),
 (8, 2 ,'Will your child be eating lunch provided by the school?  If so, how much will it cost?  How will you pay? ', NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -10 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW() + INTERVAL 10 DAY),
 (9, 3, 'Summer bedtimes are generally more relaxed, but as children head back to school, they must prepare their bodies with more sleep.', NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -10 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))), NOW() + INTERVAL 10 DAY);

-- Dumping data for table `device_events` - Call 3
--

-- INSERT INTO `device_events`(`device_event_id`, `uuid`, `event_id`, `gps_data_code`, `gps_location_data`, `sensor_type_code`, `sensor_error_code`, `vital_sign_type`, `vital_sign_value`, `abnormal_code`, `battery_level_value`, `event_occured_date`) (SELECT NULL, ds.device_uuid, se.event_id, 'L2C' AS gps_data_code, CONCAT(( 22.00417 + RAND( ) * (25.12825-22.00417)), ',', ( 118.31833 + RAND( ) * (121.753-118.31833))) AS gps_location_data, 'CCD' AS sensor_type_code, NULL, NULL, NULL, NULL, NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -2 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) AS event_date FROM `supported_events` AS se, device_students AS ds, students AS s WHERE `generated_by` = 'device' AND se.event_id NOT IN (1, 2));

--
-- Dumping data for table `parent_kids`
--
INSERT INTO `parent_kids` (`parent_kid_id`,`user_id`,`student_id`,`created_date`) VALUES 
(NULL, 4, 1, NOW()), (NULL, 4, 2, NOW()), (NULL, 4, 3, NOW()), (NULL, 9, 1, NOW()),
(NULL, 9, 3, NOW()), (NULL, 10, 3, NOW()), (NULL, 10, 4, NOW()), (NULL, 11, 3, NOW()), (NULL, 11,4, NOW()), (NULL, 12, 5, NOW()), (NULL, 12, 6, NOW()), (NULL, 13, 5, NOW());

--
-- Dumping data for table `device_events` - Call 4
--

-- INSERT INTO `device_events`(`device_event_id`, `uuid`, `event_id`, `gps_data_code`, `gps_location_data`, `sensor_type_code`, `sensor_error_code`, `vital_sign_type`, `vital_sign_value`, `abnormal_code`, `battery_level_value`, `event_occured_date`) (SELECT NULL, ds.device_uuid, se.event_id, 'L2C' AS gps_data_code, CONCAT(( 22.00417 + RAND( ) * (25.12825-22.00417)), ',', ( 118.31833 + RAND( ) * (121.753-118.31833))) AS gps_location_data, 'CCD' AS sensor_type_code, NULL, NULL, NULL, NULL, NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL -1 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) AS event_date FROM `supported_events` AS se, device_students AS ds, students AS s WHERE `generated_by` = 'device' AND se.event_id NOT IN (1, 2));

--
-- Dumping data for table `event_subscriptions`
--
INSERT INTO `event_subscriptions` (SELECT NULL, se.event_id, ds.student_id, u.user_id, NOW() 
FROM `supported_events` AS se , `device_students` AS ds 
LEFT JOIN students AS s ON s.student_id = ds.student_id 
LEFT JOIN parent_kids pk ON pk.student_id= s.student_id 
LEFT JOIN users AS u ON u.user_id = pk.user_id 
WHERE u.role_type = 'parent_admin' AND se.parent_unsubscribe = 'yes' AND ds.status = 'active');
INSERT INTO `event_subscriptions` (SELECT NULL, se.event_id, ds.student_id, u.user_id, NOW() FROM `supported_events` AS se , `device_students` AS ds 
LEFT JOIN students AS s ON s.student_id = ds.student_id 
LEFT JOIN parent_kids pk ON pk.student_id= s.student_id 
LEFT JOIN users AS u ON u.user_id = pk.user_id 
WHERE u.role_type = 'parent_member' AND se.notify_member = 'yes' AND ds.status = 'active');

--
-- Dumping data for table `timetable`
--

INSERT INTO `timetable` (`timetable_id`,`class_grade_id`,`subject_one`,`subject_two`,`subject_three`,`subject_four`,`subject_five`,`subject_six`,`subject_seven`,`subject_eight`,`week_day`,`created_date`) VALUES 
(1,1,'Reading','FienArts','Language','Math','History','P.H','Scout','English','MON',NOW()),
(2,1,'Reading','FienArts','Language','Math','History','P.H','Scout','English','TUE',NOW()),
(3,1,'Reading','FienArts','Language','Math','History','P.H','Scout','English','WED',NOW()),
(4,1,'Reading','FienArts','Language','Math','History','P.H','Scout','English','THU',NOW()),
(5,1,'Reading','FienArts','Language','Math','History','P.H','Scout','English','FRI',NOW());

--
-- Dumping data for table `device_events` - Call 5
--

-- INSERT INTO `device_events`(`device_event_id`, `uuid`, `event_id`, `gps_data_code`, `gps_location_data`, `sensor_type_code`, `sensor_error_code`, `vital_sign_type`, `vital_sign_value`, `abnormal_code`, `battery_level_value`, `event_occured_date`) (SELECT NULL, ds.device_uuid, se.event_id, 'L2C' AS gps_data_code, CONCAT(( 22.00417 + RAND( ) * (25.12825-22.00417)), ',', ( 118.31833 + RAND( ) * (121.753-118.31833))) AS gps_location_data, 'CCD' AS sensor_type_code, NULL, NULL, NULL, NULL, NULL, (NOW()) AS event_date FROM `supported_events` AS se, device_students AS ds, students AS s WHERE `generated_by` = 'device' AND se.event_id NOT IN (1, 2));

--
-- Dumping data for table `ips_receiver`
--

INSERT INTO `ips_receiver` (`ips_receiver_id`, `ips_receiver_mac`, `receiver_name`,`school_id`, `receiver_version`,`receiver_status`, `session_id`, `session_expiry`, `created_date`, `updated_date`) VALUES
(1, '00:A0:C9:14:C8:29','beacon1', 2, '1.1release', 'active',NULL, NULL, '2017-10-17 12:45:47', '2017-10-17 12:45:47'),
(2, '08:56:27:6f:2b:9c', 'beacon2', 5, '2.0 version','active', NULL, NULL, '2017-10-17 12:46:47', '2017-10-17 12:46:47');

--
-- Dumping data for table `ips_receiver_zone`
--

INSERT INTO `ips_receiver_zone` (`ips_receiver_zone_id`, `ips_receiver_id`, `zone_name`,`map_type`, `building_name`,`floor_number`,`map_filename`, `created_date`, `updated_date`) VALUES
(1, 1, 'zone1','full','rmz',NULL,'zone1.png', '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(2, 1, 'zone2','partial','rmz','1st', 'zone2.png', '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(3, 1, 'zone3','partial','rmz','2nd', 'zone3.png', '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(4, 1, 'zone4', 'partial','rmz','3rd', 'zone4.png', '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(5, 1, 'zone5', 'partial','rmz','4th','zone5.png', '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(6, 1, 'zone6',  'partial','rmz','5th',NULL, '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(7, 1, 'zone7','partial','rmz','6th',  NULL, '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(8, 1, 'zone8', 'partial','rmz','7th', NULL, '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(9, 1, 'zone9', 'partial','rmz','8th', NULL, '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(10, 1, 'zone10','partial','rmz','9th', NULL, '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(11, 2, 'zone11','full','ecospace',NULL, 'zone11.png', '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(12, 2, 'zone12','partial','ecospace','1st', 'zone12.png', '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(13, 2, 'zone13','partial','ecospace','2nd', 'zone13.png', '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(14, 2, 'zone14','partial','ecospace','3rd', NULL, '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(15, 2, 'zone15','partial','ecospace','4th', NULL, '2017-10-17 07:17:50', '2017-10-17 07:17:50'),
(16, 2, 'zone16','partial','ecospace','5th', NULL, '2017-10-17 07:17:50', '2017-10-17 07:17:50');

--
-- Dumping data for table `ips_receiver_device`
--

INSERT INTO `ips_receiver_device` (`ips_receiver_device_id`, `ips_receiver_zone_id`, `ips_receiver_id`,`device_uuid`, `firmware_name`, `firmware_version`,`device_model`, `status`, `status_description`, `created_date`,`updated_date`) VALUES
(1, 3, 1,'5d7fae93-96fd-4c4249-90969d-deviceuuid1', 'Ubiquiti Firmware', 'release 1.4.5', 'ICG_17_model','active','up and running', '2017-10-17 07:18:29', '2017-10-17 07:18:29'),
(2, 4, 1,'5d7fae93-96fd-4c4249-90969d-deviceuuid2', 'Ubiquiti Firmware', 'release 1.4.5', 'ICG_17_model', 'disable', 'disabled', '2017-10-17 07:18:29', '2017-10-17 07:18:29'),
(3, 5, 1,'5d7fae93-96fd-4c4249-90969d-deviceuuid3', 'Ubiquiti Firmware', 'release 1.4.5', 'ICG_17_model', 'down', 'down and inactive', '2017-10-17 07:18:29', '2017-10-17 07:18:29'),
(4, 12,2, '5d7fae93-96fd-4c4249-90969d-deviceuuid4', 'Ubiquiti Firmware', 'release 1.4.5', 'ICG_17_model', 'up', 'up and running', '2017-10-17 07:18:29', '2017-10-17 07:18:29'),
(5, 13, 2,'5d7fae93-96fd-4c4249-90969d-deviceuuid5', 'Ubiquiti Firmware', 'release 1.4.5', 'ICG_17_model', 'down', 'down and inactive', '2017-10-17 07:18:29', '2017-10-17 07:18:29'),
(6, NULL,2, '5d7fae93-96fd-4c4249-90969d-deviceuuid6', 'Ubiquiti Firmware', 'release 1.4.5', 'ICG_17_model', 'up', 'up and running', '2017-10-17 07:18:29', '2017-10-17 07:18:29');

--
-- Dumping data for table `device_events` - Call 6
--

-- INSERT INTO `device_events`(`device_event_id`, `uuid`, `event_id`, `gps_data_code`, `gps_location_data`, `sensor_type_code`, `sensor_error_code`, `vital_sign_type`, `vital_sign_value`, `abnormal_code`, `battery_level_value`, `event_occured_date`) (SELECT NULL, ds.device_uuid, se.event_id, 'L2C' AS gps_data_code, CONCAT(( 22.00417 + RAND( ) * (25.12825-22.00417)), ',', ( 118.31833 + RAND( ) * (121.753-118.31833))) AS gps_location_data, 'CCD' AS sensor_type_code, NULL, NULL, NULL, NULL, NULL, FROM_UNIXTIME(UNIX_TIMESTAMP((NOW() + INTERVAL 1 DAY + INTERVAL FLOOR(0 + (RAND() * 86400)) DAY_SECOND))) AS event_date FROM `supported_events` AS se, device_students AS ds, students AS s WHERE `generated_by` = 'device');

--
-- Updating data for table `device_events` - in_time, out-time & is_entry_exit_abnormal
--

UPDATE `device_events` SET `in_time` = DATE_FORMAT(`event_occured_date`,'%H:%i:%s') WHERE `event_id` = 1 ;
UPDATE `device_events` SET `out_time` = DATE_FORMAT(`event_occured_date`,'%H:%i:%s') WHERE `event_id` = 2;
UPDATE `device_events` SET `is_entry_exit_abnormal` = 'yes' WHERE DATE_FORMAT(`event_occured_date`,'%H:%i:%s') BETWEEN '08:00:00' AND '16:00:00';
UPDATE `device_events` SET `is_entry_exit_abnormal` = 'yes' WHERE `in_time` NOT BETWEEN '08:00:00' AND '14:00:00' OR `out_time` NOT BETWEEN '08:00:00' AND '16:00:00';

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

