USE vcampus;

-- 插入用户数据
INSERT INTO user (id, pwd, role) VALUES
('3000001', '$2a$10$1pI0ukptgmpJL.E/5enwiOKi2iQoXq08SuZ4BC7RIukQwHgn9Zbxq', 3),   -- 管理员
('1000001', '$2a$10$/OcJ2hobg.pe7tBEiYKk0O6RnotFMKVQR.6793UrHyRNRNoZ1jjaq', 1), -- 学生
('2000001', '$2a$10$/QmXoduwL7IsTG7akzp.KO1UsxySbH9NtFe8g4fZAhRhLlpTDcvKu', 2); -- 教师
