-- ======================================================================================
-- 1. USERS (Idempotent: if they exist, it does nothing)
-- ======================================================================================

-- Admin (To manage statuses)
INSERT INTO users (email, password)
VALUES ('admin@cityfix.com', '$2a$10$EF/nJocSJLRlil5Kqjbrrelctx3RT2bsgbRtfuf6wMdCnepZ/TzEe')
ON CONFLICT (email)
DO UPDATE SET password = EXCLUDED.password;

-- Citizen (To report issues)
INSERT INTO users (email, password)
VALUES ('citizen@cityfix.com', '$2a$10$EF/nJocSJLRlil5Kqjbrrelctx3RT2bsgbRtfuf6wMdCnepZ/TzEe')
ON CONFLICT (email)
DO UPDATE SET password = EXCLUDED.password;


-- ======================================================================================
-- 2. ROLES (Linking roles to dynamically retrieved IDs)
-- ======================================================================================

-- ROLE_ADMIN for admin@cityfix.com
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM users WHERE email = 'admin@cityfix.com'
EXCEPT SELECT user_id, role FROM user_roles;

-- ROLE_USER for admin@cityfix.com (optional, if you want them to have both)
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_USER' FROM users WHERE email = 'admin@cityfix.com'
EXCEPT SELECT user_id, role FROM user_roles;

-- ROLE_USER for citizen@cityfix.com
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_USER' FROM users WHERE email = 'citizen@cityfix.com'
EXCEPT SELECT user_id, role FROM user_roles;


-- ======================================================================================
-- 3. ISSUES (Variety of statuses and locations in Barcelona)
-- ======================================================================================

-- NOTE: We use subqueries to get the reporter's ID (citizen@cityfix.com)
-- Table: urban_issues (matches IssueEntity)
-- Fields: title, latitude, longitude, status, category, reporter_id

-- Issue 1: REPORTED (Newly created) - Near Sagrada Familia
-- Category: LIGHTING (Traffic light)
INSERT INTO urban_issues (title, latitude, longitude, status, category, reporter_id)
SELECT 'Broken traffic light at Sagrada Familia', 41.4036, 2.1744, 'REPORTED', 'LIGHTING', id
FROM users WHERE email = 'citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Broken traffic light at Sagrada Familia');

-- Issue 2: IN_PROGRESS (Under repair) - Plaza Cataluña
-- Category: ROAD (Pothole)
INSERT INTO urban_issues (title, latitude, longitude, status, category, reporter_id)
SELECT 'Dangerous pothole in bike lane', 41.3870, 2.1700, 'IN_PROGRESS', 'ROAD', id
FROM users WHERE email = 'citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Dangerous pothole in bike lane');

-- Issue 3: RESOLVED (Fixed) - Park Güell
-- Category: OTHER (Bench/Furniture)
INSERT INTO urban_issues (title, latitude, longitude, status, category, reporter_id)
SELECT 'Broken bench at viewpoint', 41.4145, 2.1527, 'RESOLVED', 'OTHER', id
FROM users WHERE email = 'citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Broken bench at viewpoint');

-- Issue 4: REPORTED (Another new one) - Barceloneta Beach
-- Category: OTHER (Public facility)
INSERT INTO urban_issues (title, latitude, longitude, status, category, reporter_id)
SELECT 'Public showers not working', 41.3784, 2.1925, 'REPORTED', 'OTHER', id
FROM users WHERE email = 'citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Public showers not working');

-- Issue 5: IN_PROGRESS (Active work) - Camp Nou
-- Category: TRASH (Container)
INSERT INTO urban_issues (title, latitude, longitude, status, category, reporter_id)
SELECT 'Burnt trash container', 41.3809, 2.1228, 'IN_PROGRESS', 'TRASH', id
FROM users WHERE email = 'citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Burnt trash container');