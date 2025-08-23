-- Initialize roles
INSERT INTO roles (name, description) VALUES 
('LIBRARIAN', 'Librarian - Can manage book data'),
('MEMBER', 'General user - Can search books, borrow and return')
ON CONFLICT (name) DO NOTHING;

-- Initialize libraries
INSERT INTO libraries (name, address, phone) VALUES 
('Main Library', 'Taipei City', '02-2361-9132'),
('Branch A', 'Taipei City', '02-2707-1008'),
('Branch B', 'Taipei City', '02-2720-8889')
ON CONFLICT (name) DO NOTHING;
