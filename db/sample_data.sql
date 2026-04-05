USE ewaste_db;

-- password for seeded users: password
INSERT INTO users (id, name, email, password, phone, address, role, status, location)
VALUES
(1, 'Super Admin', 'superadmin@ewaste.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1R8R9L6byN1Nsx3Rp3XIanFkFJx2K', '+1-555-0001', 'Head Office', 'SUPER_ADMIN', 'ACTIVE', NULL),
(2, 'NY Collector', 'ny.collector@ewaste.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1R8R9L6byN1Nsx3Rp3XIanFkFJx2K', '+1-555-0002', 'NGO Office NY', 'LOCAL_ADMIN', 'ACTIVE', 'New York'),
(3, 'LA Applicant', 'la.collector@ewaste.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1R8R9L6byN1Nsx3Rp3XIanFkFJx2K', '+1-555-0003', 'NGO Office LA', 'LOCAL_ADMIN', 'PENDING', 'Los Angeles'),
(4, 'John Doe', 'john@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1R8R9L6byN1Nsx3Rp3XIanFkFJx2K', '+1-555-1001', '123 Main St, NY', 'USER', 'ACTIVE', NULL)
ON DUPLICATE KEY UPDATE email=email;

INSERT INTO requests (user_id, admin_id, device_type, quantity, description, pickup_address, location, pickup_date, status)
VALUES
(4, 2, 'Laptop', 2, 'Old office laptops', '123 Main St, NY', 'New York', CURDATE() + INTERVAL 2 DAY, 'APPROVED'),
(4, NULL, 'Battery', 10, 'Mixed batteries', '123 Main St, NY', 'New York', CURDATE() + INTERVAL 3 DAY, 'PENDING')
ON DUPLICATE KEY UPDATE id=id;
