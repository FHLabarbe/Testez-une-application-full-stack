INSERT INTO USERS (email, last_name, first_name, password, admin) VALUES
('admin@example.com', 'Admin', 'User', 'encodedpassword', true),
('user1@example.com', 'Doe', 'John', 'encodedpassword', false),
('user2@example.com', 'Smith', 'Jane', 'encodedpassword', false);

INSERT INTO SESSIONS (name, description, date) VALUES
('Yoga Session 1', 'Beginner Yoga Session', '2024-12-12 10:00:00'),
('Yoga Session 2', 'Advanced Yoga Session', '2024-12-13 14:00:00');

INSERT INTO PARTICIPATE (session_id, user_id) VALUES
(1, 2), -- John Doe participe à Yoga Session 1
(2, 3); -- Jane Smith participe à Yoga Session 2
