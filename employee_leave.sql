
CREATE DATABASE IF NOT EXISTS employee_leave_db;
USE employee_leave_db;

CREATE TABLE IF NOT EXISTS employees (
    emp_id INT PRIMARY KEY AUTO_INCREMENT,
    emp_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    joining_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS leave_balance (
    balance_id INT PRIMARY KEY AUTO_INCREMENT,
    emp_id INT NOT NULL UNIQUE,
    total_leaves INT NOT NULL DEFAULT 12,
    used_leaves INT NOT NULL DEFAULT 0,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id),
    CHECK (total_leaves >= 0),
    CHECK (used_leaves >= 0 AND used_leaves <= total_leaves)
);

CREATE TABLE IF NOT EXISTS leave_requests (
    leave_id INT PRIMARY KEY AUTO_INCREMENT,
    emp_id INT NOT NULL,
    leave_type ENUM('Casual', 'Sick', 'Earned') NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status ENUM('Pending', 'Approved', 'Rejected')
        NOT NULL DEFAULT 'Pending',
    applied_on DATE NOT NULL,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id),
    CHECK (days > 0),
    CHECK (end_date >= start_date)
);

INSERT INTO employees
(emp_name, email, department, joining_date)
VALUES
('Ravi Kumar', 'ravi@gmail.com', 'IT', '2024-01-15'),
('Priya Sharma', 'priya@gmail.com', 'HR', '2023-06-10'),
('Kiran Reddy', 'kiran@gmail.com', 'Finance', '2024-03-20'),
('Sneha Rao', 'sneha@gmail.com', 'Testing', '2022-11-05'),
('Arun Kumar', 'arun@gmail.com', 'Development', '2025-02-01'),
('Divya Sri', 'divya@gmail.com', 'Marketing', '2023-08-12'),
('Rahul Verma', 'rahul@gmail.com', 'IT', '2024-07-18'),
('Anjali Devi', 'anjali@gmail.com', 'HR', '2025-01-10'),
('Suresh Babu', 'suresh@gmail.com', 'Finance', '2022-09-25'),
('Pooja Rani', 'pooja@gmail.com', 'Testing', '2025-04-14');

INSERT INTO leave_balance
(emp_id, total_leaves, used_leaves)
VALUES
(1, 12, 2),
(2, 12, 3),
(3, 12, 1),
(4, 12, 4),
(5, 12, 0),
(6, 12, 2),
(7, 12, 5),
(8, 12, 1),
(9, 12, 3),
(10, 12, 0);

INSERT INTO leave_requests
(emp_id, leave_type, start_date, end_date, days,
 reason, status, applied_on)
VALUES
(1, 'Casual', '2026-10-20', '2026-10-21', 2,
 'Personal work', 'Pending', '2026-10-18'),
(2, 'Sick', '2026-10-19', '2026-10-20', 2,
 'Fever and cold', 'Approved', '2026-10-17'),
(3, 'Earned', '2026-10-24', '2026-10-26', 3,
 'Family vacation', 'Pending', '2026-10-18'),
(4, 'Casual', '2026-10-15', '2026-10-16', 2,
 'Family function', 'Rejected', '2026-10-12'),
(5, 'Sick', '2026-10-22', '2026-10-22', 1,
 'Doctor appointment', 'Pending', '2026-10-18'),
(6, 'Earned', '2026-10-25', '2026-10-27', 3,
 'Personal trip', 'Approved', '2026-10-15'),
(7, 'Casual', '2026-10-28', '2026-10-29', 2,
 'Personal reasons', 'Pending', '2026-10-18'),
(8, 'Sick', '2026-10-16', '2026-10-17', 2,
 'Health issue', 'Rejected', '2026-10-14'),
(9, 'Earned', '2026-11-02', '2026-11-04', 3,
 'Family trip', 'Pending', '2026-10-18'),
(10, 'Casual', '2026-11-05', '2026-11-06', 2,
 'Personal work', 'Pending', '2026-10-18');
 
 SELECT * FROM employees;

SELECT * FROM leave_balance;

SELECT * FROM leave_requests;

SELECT
    e.emp_id,
    e.emp_name,
    e.department,
    lb.total_leaves,
    lb.used_leaves,
    (lb.total_leaves - lb.used_leaves) AS remaining_leaves
FROM employees e
JOIN leave_balance lb
    ON e.emp_id = lb.emp_id;
    
SELECT
    lr.leave_id,
    e.emp_name,
    lr.leave_type,
    lr.start_date,
    lr.end_date,
    lr.days,
    lr.reason,
    lr.status,
    lr.applied_on
FROM leave_requests lr
JOIN employees e
    ON lr.emp_id = e.emp_id
ORDER BY lr.leave_id;

