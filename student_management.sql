CREATE DATABASE student_management;
USE student_management;

CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    student_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    age INT,
    city VARCHAR(50)
);

INSERT INTO students
(student_name, email, phone, age, city)
VALUES
('Ravi', 'ravi@gmail.com', '9876543210', 21, 'Nellore'),
('Priya', 'priya@gmail.com', '9876543211', 20, 'Kavali'),
('Kiran', 'kiran@gmail.com', '9876543212', 22, 'Chennai'),
('Sneha', 'sneha@gmail.com', '9876543213', 21, 'Hyderabad'),
('Arjun', 'arjun@gmail.com', '9876543214', 23, 'Bangalore');

CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    duration_months INT NOT NULL,
    fee DECIMAL(10,2) NOT NULL
);

INSERT INTO courses
(course_name, duration_months, fee)
VALUES
('Java Full Stack', 6, 25000),
('Python Full Stack', 6, 22000),
('Web Development', 3, 12000),
('Data Analytics', 4, 18000);

CREATE TABLE enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATE NOT NULL,
    FOREIGN KEY (student_id)
        REFERENCES students(student_id),
    FOREIGN KEY (course_id)
        REFERENCES courses(course_id),
    UNIQUE (student_id, course_id)
);

INSERT INTO enrollments
(student_id, course_id, enrollment_date)
VALUES
(1, 1, '2026-09-01'),
(2, 2, '2026-09-02'),
(3, 1, '2026-09-03'),
(4, 3, '2026-09-04'),
(5, 4, '2026-09-05'),
(1, 3, '2026-09-06');

CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('Present', 'Absent') NOT NULL,
    FOREIGN KEY (enrollment_id)
        REFERENCES enrollments(enrollment_id),
    UNIQUE (enrollment_id, attendance_date)
);

INSERT INTO attendance
(enrollment_id, attendance_date, status)
VALUES
(1, '2026-09-10', 'Present'),
(1, '2026-09-11', 'Present'),
(1, '2026-09-12', 'Absent'),
(2, '2026-09-10', 'Present'),
(2, '2026-09-11', 'Absent'),
(3, '2026-09-10', 'Present'),
(4, '2026-09-10', 'Present'),
(5, '2026-09-10', 'Absent');

CREATE TABLE marks (
    mark_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT NOT NULL,
    subject_name VARCHAR(100) NOT NULL,
    total_marks INT NOT NULL DEFAULT 100,
    obtained_marks INT NOT NULL,
    FOREIGN KEY (enrollment_id)
        REFERENCES enrollments(enrollment_id),
    UNIQUE (enrollment_id, subject_name),
    CHECK (total_marks > 0),
    CHECK (obtained_marks >= 0
           AND obtained_marks <= total_marks)
);

INSERT INTO marks
(enrollment_id, subject_name, total_marks, obtained_marks)
VALUES
(1, 'Java', 100, 85),
(1, 'SQL', 100, 90),
(1, 'HTML CSS', 100, 78),
(2, 'Python', 100, 88),
(2, 'SQL', 100, 76),
(3, 'Java', 100, 65),
(3, 'SQL', 100, 72),
(4, 'HTML CSS', 100, 92),
(5, 'Statistics', 100, 81);

select * from students;
select * from courses;
select * from enrollments;
select * from attendance;
select * from marks;





