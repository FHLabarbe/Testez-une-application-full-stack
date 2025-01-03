INSERT INTO `TEACHERS` (first_name, last_name) VALUES
('Margot', 'DELAHAYE'),
('Hélène', 'THIERCELIN');

INSERT INTO `USERS` (email, last_name, first_name, password, admin) VALUES
('yoga@studio.com', 'Admin', 'Admin', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq', true),
('john.doe@example.com', 'Doe', 'John', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq', false),
('jane.smith@example.com', 'Smith', 'Jane', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq', false),
('test.user@example.com', 'User', 'Test', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq', true);

INSERT INTO `SESSIONS` (name, description, date, teacher_id) VALUES
('Yoga Session 1', 'Beginner Yoga Session', '2024-12-12 10:00:00', 1),
('Yoga Session 2', 'Advanced Yoga Session', '2024-12-13 14:00:00', 2);

INSERT INTO `PARTICIPATE` (session_id, user_id) VALUES
(1, 2), -- John Doe participe à Yoga Session 1
(2, 3); -- Jane Smith participe à Yoga Session 2
