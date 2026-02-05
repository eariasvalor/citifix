-- ======================================================================================
-- 1. USERS (Idempotent: if they exist, it does nothing)
-- ======================================================================================

-- Admin (Password: citifix-admin-2026-secure)
INSERT INTO users (email, password)
SELECT 'admin@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@cityfix.com');

-- Citizen (Password: citifix-admin-2026-secure)
INSERT INTO users (email, password)
SELECT 'citizen@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'citizen@cityfix.com');

-- ======================================================================================
-- 1.1 ADDITION OF 11 NEW USERS (Password: citifix-admin-2026-secure)
-- ======================================================================================

INSERT INTO users (email, password)
SELECT 'low_active@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'low_active@cityfix.com');

INSERT INTO users (email, password)
SELECT 'mid_active@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'mid_active@cityfix.com');

INSERT INTO users (email, password)
SELECT 'high_active@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'high_active@cityfix.com');

INSERT INTO users (email, password)
SELECT 'expert_reporter@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'expert_reporter@cityfix.com');

INSERT INTO users (email, password)
SELECT 'new_user@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'new_user@cityfix.com');

INSERT INTO users (email, password)
SELECT 'user_sevilla@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user_sevilla@cityfix.com');

INSERT INTO users (email, password)
SELECT 'user_madrid@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user_madrid@cityfix.com');

INSERT INTO users (email, password)
SELECT 'user_bilbao@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user_bilbao@cityfix.com');

INSERT INTO users (email, password)
SELECT 'user_valencia@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user_valencia@cityfix.com');

INSERT INTO users (email, password)
SELECT 'user_malaga@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user_malaga@cityfix.com');

INSERT INTO users (email, password)
SELECT 'user_zaragoza@cityfix.com', '$2a$10$lKJ6wnM3s920LY/nonSoWO.OgdPhyKc2QmlA7C/wHFKWW5Sg9AmE6'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user_zaragoza@cityfix.com');

-- ======================================================================================
-- 2. ROLES
-- ======================================================================================

-- ROLE_ADMIN for admin@cityfix.com
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM users WHERE email = 'admin@cityfix.com'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    JOIN users u ON ur.user_id = u.id
    WHERE u.email = 'admin@cityfix.com' AND ur.role = 'ROLE_ADMIN'
);

-- ROLE_USER for admin@cityfix.com
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_USER' FROM users WHERE email = 'admin@cityfix.com'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    JOIN users u ON ur.user_id = u.id
    WHERE u.email = 'admin@cityfix.com' AND ur.role = 'ROLE_USER'
);

-- ROLE_USER for citizen@cityfix.com
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_USER' FROM users WHERE email = 'citizen@cityfix.com'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    JOIN users u ON ur.user_id = u.id
    WHERE u.email = 'citizen@cityfix.com' AND ur.role = 'ROLE_USER'
);

INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_USER' FROM users WHERE email IN (
    'low_active@cityfix.com', 'mid_active@cityfix.com',
    'high_active@cityfix.com', 'expert_reporter@cityfix.com', 'new_user@cityfix.com',
    'user_sevilla@cityfix.com', 'user_madrid@cityfix.com', 'user_bilbao@cityfix.com',
    'user_valencia@cityfix.com', 'user_malaga@cityfix.com', 'user_zaragoza@cityfix.com'
)
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur JOIN users u ON ur.user_id = u.id
    WHERE u.email = users.email AND ur.role = 'ROLE_USER'
);


-- ======================================================================================
-- 3. ISSUES (Variety of statuses and locations in Spain)
-- ======================================================================================

-- NOTE: We use subqueries to get the reporter's ID (citizen@cityfix.com)
-- Table: urban_issues (matches IssueEntity)
-- Fields: title, description, latitude, longitude, status, category, reporter_id

-- --- BARCELONA (5 Issues) ---
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Broken traffic light', 'Red light stuck near Sagrada Familia.', 41.4036, 2.1744, 'REPORTED', 'LIGHTING', id, CURRENT_TIMESTAMP
FROM users
WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Broken traffic light');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Dangerous pothole', 'Bike lane near Plaça Catalunya has a deep hole.', 41.3870, 2.1700, 'IN_PROGRESS', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Dangerous pothole');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Broken bench', 'Park Güell viewpoint bench missing a leg.', 41.4145, 2.1527, 'RESOLVED', 'OTHER', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Broken bench');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'No water in showers', 'Barceloneta beach showers not working.', 41.3784, 2.1925, 'REPORTED', 'OTHER', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'No water in showers');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Melted container', 'Trash container burnt near Camp Nou.', 41.3809, 2.1228, 'IN_PROGRESS', 'TRASH', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Melted container');


-- --- MADRID (3 Issues) ---
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Overflowing bins at Sol', 'Trash all over the floor near Metro entrance.', 40.4169, -3.7035, 'REPORTED', 'TRASH', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Overflowing bins at Sol');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Dark path in El Retiro', 'Lights out near the main pond.', 40.4180, -3.6830, 'RESOLVED', 'LIGHTING', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Dark path in El Retiro');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Loose cobblestones', 'Plaza Mayor pavement is uneven, tripping hazard.', 40.4154, -3.7074, 'IN_PROGRESS', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Loose cobblestones');


-- --- BILBAO (3 Issues) ---
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Loose tiles near Museum', 'Near Puppy statue, tiles are wobbly.', 43.2680, -2.9340, 'IN_PROGRESS', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Loose tiles near Museum');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Full recycling bins', 'Casco Viejo glass bins haven''t been emptied in weeks.', 43.2570, -2.9230, 'REPORTED', 'TRASH', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Full recycling bins');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Flickering street lamp', 'Near San Mamés stadium entrance.', 43.2640, -2.9490, 'RESOLVED', 'LIGHTING', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Flickering street lamp');


-- --- VALENCIA (3 Issues) ---
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Broken drinking fountain', 'Leaking water near Science Museum.', 39.4540, -0.3550, 'REPORTED', 'OTHER', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Broken drinking fountain');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Plastic waste on beach', 'Malvarrosa beach needs cleaning crew.', 39.4750, -0.3220, 'IN_PROGRESS', 'TRASH', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Plastic waste on beach');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Huge pothole', 'Near Mercado Central loading bay.', 39.4720, -0.3780, 'RESOLVED', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Huge pothole');


-- --- SEVILLA (3 Issues) ---
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Streetlight flickering', 'Lamp post near Plaza España bridge.', 37.3770, -5.9870, 'RESOLVED', 'LIGHTING', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Streetlight flickering');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Graffiti on monument', 'Torre del Oro base has been painted.', 37.3820, -5.9960, 'REPORTED', 'OTHER', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Graffiti on monument');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Broken sidewalk', 'Triana bridge access is dangerous for pedestrians.', 37.3870, -6.0030, 'IN_PROGRESS', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Broken sidewalk');


-- --- ZARAGOZA (3 Issues) ---
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Pothole in pedestrian zone', 'Plaza del Pilar dangerous hole.', 41.6560, -0.8780, 'REPORTED', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Pothole in pedestrian zone');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Park too dark', 'Aljafería surrounding park needs more lights.', 41.6565, -0.8970, 'REPORTED', 'LIGHTING', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Park too dark');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Debris on Stone Bridge', 'Construction material left on the bridge.', 41.6580, -0.8750, 'RESOLVED', 'TRASH', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Debris on Stone Bridge');


-- --- MÁLAGA (3 Issues) ---
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Debris on promenade', 'La Malagueta sidewalk blocked.', 36.7200, -4.4100, 'IN_PROGRESS', 'TRASH', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Debris on promenade');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Slippery stairs', 'Alcazaba entrance stairs are slippery and broken.', 36.7215, -4.4160, 'REPORTED', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Slippery stairs');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Calle Larios light out', 'Christmas lights cable hanging low.', 36.7220, -4.4200, 'RESOLVED', 'LIGHTING', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Calle Larios light out');


-- --- SANTIAGO (3 Issues) ---
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Damaged sign', 'Parador sign bent in Obradoiro.', 42.8800, -8.5450, 'REPORTED', 'OTHER', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Damaged sign');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Litter in Alameda', 'Park bench area full of cans.', 42.8760, -8.5480, 'IN_PROGRESS', 'TRASH', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Litter in Alameda');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Uneven stones', 'Pilgrim entrance road is very uneven.', 42.8810, -8.5430, 'RESOLVED', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Uneven stones');


-- --- MURCIA (3 Issues) ---
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Missing cobblestones', 'Cathedral square missing stones.', 37.9830, -1.1280, 'RESOLVED', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Missing cobblestones');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'River path dark', 'Segura river side path lights out.', 37.9810, -1.1300, 'REPORTED', 'LIGHTING', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'River path dark');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Broken swing', 'Floridablanca garden playground issue.', 37.9790, -1.1320, 'IN_PROGRESS', 'OTHER', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Broken swing');


-- --- PALMA (3 Issues) ---
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Promenade lights out', 'Marina section completely dark.', 39.5660, 2.6400, 'IN_PROGRESS', 'LIGHTING', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Promenade lights out');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Pothole access road', 'Bellver Castle access road has a deep hole.', 39.5630, 2.6180, 'REPORTED', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Pothole access road');

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Construction waste', 'Old town alley blocked by debris.', 39.5690, 2.6510, 'RESOLVED', 'TRASH', id, CURRENT_TIMESTAMP FROM users WHERE email='citizen@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Construction waste');

-- ======================================================================================
-- 4. Issues for different stat ranges
-- ======================================================================================

-- USER 1: LOW ACTIVITY (1 Reported Issue)
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Fallen tree branch', 'Blocking sidewalk in Granada.', 37.1773, -3.5986, 'REPORTED', 'OTHER', id, CURRENT_TIMESTAMP
FROM users WHERE email='low_active@cityfix.com'
AND NOT EXISTS (SELECT 1 FROM urban_issues WHERE title = 'Fallen tree branch');

-- USER 2: MEDIUM ACTIVITY (3 Issues, 1 Resolved)
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Broken swing set', 'Dangerous for kids in local park.', 37.1881, -3.6066, 'RESOLVED', 'OTHER', id, CURRENT_TIMESTAMP
FROM users WHERE email='mid_active@cityfix.com';

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Graffiti on school wall', 'Needs cleaning before Monday.', 37.1700, -3.6100, 'REPORTED', 'OTHER', id, CURRENT_TIMESTAMP
FROM users WHERE email='mid_active@cityfix.com';

INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Dead street light', 'Very dark alleyway.', 37.1750, -3.6000, 'IN_PROGRESS', 'LIGHTING', id, CURRENT_TIMESTAMP
FROM users WHERE email='mid_active@cityfix.com';

-- USER 3: HIGH ACTIVITY (5 Issues, 3 Resolved)
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Oil spill on road', 'Slippery for motorbikes.', 36.5271, -6.2886, 'RESOLVED', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='high_active@cityfix.com';
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Broken manhole cover', 'In the middle of the street.', 36.5300, -6.2900, 'RESOLVED', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='high_active@cityfix.com';
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Abandoned furniture', 'Old sofa left on sidewalk.', 36.5200, -6.2800, 'RESOLVED', 'TRASH', id, CURRENT_TIMESTAMP FROM users WHERE email='high_active@cityfix.com';
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Leaking pipe', 'Water wasting on the street.', 36.5250, -6.2850, 'IN_PROGRESS', 'OTHER', id, CURRENT_TIMESTAMP FROM users WHERE email='high_active@cityfix.com';
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Stuck elevator', 'Public elevator to the beach not working.', 36.5220, -6.2820, 'REPORTED', 'OTHER', id, CURRENT_TIMESTAMP FROM users WHERE email='high_active@cityfix.com';

-- USER 4: EXPERT (8 issues, Variety of categories)
   -- (We simulate a fast bulk upload for this user)
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Expert-1', 'Desc', 40.0, -3.0, 'RESOLVED', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='expert_reporter@cityfix.com';
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Expert-2', 'Desc', 40.1, -3.1, 'RESOLVED', 'LIGHTING', id, CURRENT_TIMESTAMP FROM users WHERE email='expert_reporter@cityfix.com';
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Expert-3', 'Desc', 40.2, -3.2, 'RESOLVED', 'TRASH', id, CURRENT_TIMESTAMP FROM users WHERE email='expert_reporter@cityfix.com';
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Expert-4', 'Desc', 40.3, -3.3, 'IN_PROGRESS', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='expert_reporter@cityfix.com';
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Expert-5', 'Desc', 40.4, -3.4, 'REPORTED', 'LIGHTING', id, CURRENT_TIMESTAMP FROM users WHERE email='expert_reporter@cityfix.com';
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Expert-6', 'Desc', 40.5, -3.5, 'REPORTED', 'TRASH', id, CURRENT_TIMESTAMP FROM users WHERE email='expert_reporter@cityfix.com';
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Expert-7', 'Desc', 40.6, -3.6, 'REPORTED', 'OTHER', id, CURRENT_TIMESTAMP FROM users WHERE email='expert_reporter@cityfix.com';
INSERT INTO urban_issues (title, description, latitude, longitude, status, category, reporter_id, created_at)
SELECT 'Expert-8', 'Desc', 40.7, -3.7, 'RESOLVED', 'ROAD', id, CURRENT_TIMESTAMP FROM users WHERE email='expert_reporter@cityfix.com';

