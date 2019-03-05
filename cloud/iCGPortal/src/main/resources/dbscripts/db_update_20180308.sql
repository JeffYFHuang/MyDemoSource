ALTER TABLE `users` ADD `android_app_token` VARCHAR(300) NULL AFTER `user_active`, ADD `iphone_app_token` VARCHAR(300) NULL AFTER `android_app_token`;
ALTER TABLE `users` DROP `app_token`, DROP `app_type`;

ALTER TABLE `device_events_queue` ADD `android_app_token` VARCHAR(300) NULL AFTER `user_id`, ADD `iphone_app_token` VARCHAR(300) NULL AFTER `android_app_token`;
ALTER TABLE `device_events_queue` DROP `app_type`, DROP `app_token`;

ALTER TABLE `device_events` ADD `ips_receiver_id` INT  DEFAULT NULL AFTER `ips_receiver_zone_id`;