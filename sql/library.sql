CREATE DATABASE library;
USE library;

CREATE TABLE books (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       type VARCHAR(50) NOT NULL,
                       is_favorite BOOLEAN DEFAULT FALSE
);

CREATE TABLE members (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         username VARCHAR(50) UNIQUE NOT NULL,
                         password VARCHAR(255) NOT NULL,
                         full_name VARCHAR(255) NOT NULL,
                         role ENUM('ADMIN', 'MEMBER') NOT NULL
);

CREATE TABLE loans (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       book_id INT NOT NULL,
                       member_id INT NOT NULL,
                       borrow_date DATE NOT NULL,
                       return_date DATE,
                       due_date DATE NOT NULL,
                       overdue_fee DECIMAL(10,2) DEFAULT 0.0,
                       FOREIGN KEY (book_id) REFERENCES books(id),
                       FOREIGN KEY (member_id) REFERENCES members(id)
);

-- Insert default admin account
INSERT INTO members (username, password, full_name, role)
VALUES ('admin', 'admin123', 'Administrator', 'ADMIN');