-- =====================================================================
-- TransitGo Supabase PostgreSQL Database Schema
-- Fully dynamic tables corresponding to DatabaseManager.kt
-- =====================================================================

-- 1. Users Table (Passengers, Drivers, Conductors, Admins, Staff)
CREATE TABLE IF NOT EXISTS users (
    user_id TEXT PRIMARY KEY,
    fullname TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'Passenger',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Buses Table (Fleet Management)
CREATE TABLE IF NOT EXISTS buses (
    bus_id TEXT PRIMARY KEY,
    bus_number TEXT NOT NULL,
    capacity INTEGER DEFAULT 50,
    status TEXT DEFAULT 'Active',
    plate_number TEXT,
    bus_type TEXT DEFAULT 'Air-conditioned',
    assigned_driver TEXT DEFAULT 'Unassigned',
    operator TEXT,
    route TEXT,
    bus_stop TEXT,
    departure_time TEXT DEFAULT '08:00',
    image_url TEXT
);

-- 3. Drivers Table
CREATE TABLE IF NOT EXISTS drivers (
    driver_id TEXT PRIMARY KEY,
    driver_name TEXT NOT NULL,
    contact_number TEXT,
    license_number TEXT,
    license_expiry TEXT,
    assigned_bus TEXT,
    employment_status TEXT DEFAULT 'Active',
    email TEXT,
    password TEXT
);

-- 4. Conductors Table
CREATE TABLE IF NOT EXISTS conductors (
    id TEXT PRIMARY KEY,
    full_name TEXT NOT NULL,
    employee_id TEXT,
    phone_number TEXT,
    email TEXT,
    username TEXT,
    role TEXT DEFAULT 'Conductor',
    assigned_bus_id TEXT,
    status TEXT DEFAULT 'On Duty',
    password TEXT
);

-- 5. Bus Stops / Terminals Table
CREATE TABLE IF NOT EXISTS bus_stops (
    stop_id TEXT PRIMARY KEY,
    stop_name TEXT NOT NULL,
    location TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    bays_count INTEGER DEFAULT 8,
    active_bays INTEGER DEFAULT 5,
    operatingHours TEXT DEFAULT '04:00 AM - 11:00 PM',
    status TEXT DEFAULT 'Active',
    image_url TEXT
);

-- 6. Bus Routes Table
CREATE TABLE IF NOT EXISTS bus_routes (
    route_id TEXT PRIMARY KEY,
    route_name TEXT NOT NULL,
    start_point TEXT,
    destination TEXT,
    status TEXT DEFAULT 'Active',
    start_lat DOUBLE PRECISION DEFAULT 0.0,
    start_lng DOUBLE PRECISION DEFAULT 0.0,
    dest_lat DOUBLE PRECISION DEFAULT 0.0,
    dest_lng DOUBLE PRECISION DEFAULT 0.0,
    fare DOUBLE PRECISION DEFAULT 0.0
);

-- 7. Bus Schedules Table
CREATE TABLE IF NOT EXISTS bus_schedules (
    schedule_id TEXT PRIMARY KEY,
    bus_number TEXT,
    route_name TEXT,
    driver_name TEXT,
    departure_time TEXT,
    arrival_time TEXT,
    status TEXT DEFAULT 'Scheduled',
    schedule_date TEXT,
    rejection_reason TEXT
);

-- 8. Bookings Table (Passenger Ticketing & Reservations)
CREATE TABLE IF NOT EXISTS bookings (
    booking_id TEXT PRIMARY KEY,
    bus_id TEXT,
    route_name TEXT,
    seats TEXT,
    total_price DOUBLE PRECISION,
    passenger_email TEXT,
    status TEXT DEFAULT 'Confirmed',
    payment_method TEXT DEFAULT 'Cash',
    receipt_url TEXT,
    payment_status TEXT DEFAULT 'Pending',
    discount_type TEXT DEFAULT 'Regular',
    discount_id_url TEXT
);

-- 9. Payment Settings Table (GCash & PayMaya QR Configuration)
CREATE TABLE IF NOT EXISTS payment_settings (
    id TEXT PRIMARY KEY DEFAULT 'default_settings',
    gcash_number TEXT,
    gcash_qr_url TEXT,
    paymaya_number TEXT,
    paymaya_qr_url TEXT
);

-- 10. Live Bus Locations Table (GPS Tracking)
CREATE TABLE IF NOT EXISTS bus_locations (
    bus_id TEXT PRIMARY KEY,
    bus_number TEXT,
    driver_name TEXT,
    route_name TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    speed_kmh REAL,
    heading REAL,
    status TEXT,
    timestamp BIGINT
);

-- 11. Driver Ratings Table
CREATE TABLE IF NOT EXISTS driver_ratings (
    id TEXT PRIMARY KEY,
    driver_id TEXT,
    passenger_email TEXT,
    rating INTEGER,
    feedback TEXT,
    timestamp BIGINT
);

-- =====================================================================
-- Temporary Admin Accounts & RLS Permissions Setup
-- =====================================================================

-- 1. Primary Admin Account (Upsert on Primary Key)
INSERT INTO users (user_id, fullname, email, password, role)
VALUES ('admin_01', 'System Administrator', 'admin@transitgo.com', 'admin123', 'Administrator')
ON CONFLICT (user_id) DO UPDATE
SET fullname = EXCLUDED.fullname,
    email = EXCLUDED.email,
    password = EXCLUDED.password,
    role = EXCLUDED.role;

-- 2. Temporary Admin Account (Upsert on Primary Key)
INSERT INTO users (user_id, fullname, email, password, role)
VALUES ('admin_temp', 'Temporary Admin', 'temp.admin@transitgo.com', 'admin123', 'Administrator')
ON CONFLICT (user_id) DO UPDATE
SET fullname = EXCLUDED.fullname,
    email = EXCLUDED.email,
    password = EXCLUDED.password,
    role = EXCLUDED.role;

-- 3. Disable Row Level Security (RLS)
ALTER TABLE users DISABLE ROW LEVEL SECURITY;
ALTER TABLE buses DISABLE ROW LEVEL SECURITY;
ALTER TABLE drivers DISABLE ROW LEVEL SECURITY;
ALTER TABLE conductors DISABLE ROW LEVEL SECURITY;
ALTER TABLE bus_stops DISABLE ROW LEVEL SECURITY;
ALTER TABLE bus_routes DISABLE ROW LEVEL SECURITY;
ALTER TABLE bus_schedules DISABLE ROW LEVEL SECURITY;
ALTER TABLE bookings DISABLE ROW LEVEL SECURITY;
ALTER TABLE payment_settings DISABLE ROW LEVEL SECURITY;
ALTER TABLE bus_locations DISABLE ROW LEVEL SECURITY;
ALTER TABLE driver_ratings DISABLE ROW LEVEL SECURITY;

-- 4. Grant Full API Access
GRANT ALL ON ALL TABLES IN SCHEMA public TO anon, authenticated, service_role;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO anon, authenticated, service_role;
