DELETE FROM `event_subscriptions` WHERE `user_id` IN (SELECT user_id FROM `users` WHERE role_type = 'parent_member' AND user_id NOT IN (SELECT DISTINCT user_id FROM parent_kids));

DELETE FROM `device_events_queue` WHERE `user_id` IN (SELECT user_id FROM `users` WHERE role_type = 'parent_member' AND user_id NOT IN (SELECT DISTINCT user_id FROM parent_kids));

DELETE FROM `users` WHERE role_type = 'parent_member' AND user_id NOT IN (SELECT DISTINCT user_id FROM parent_kids);