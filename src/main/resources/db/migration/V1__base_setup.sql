-- V1__create_devices_and_queries_tables.sql
-- Migration script to create the `devices` and `queries` tables

-- Create the devices table
CREATE TABLE devices (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- UUID generation depending on your database
                         subscription_id UUID,
                         created_at TIMESTAMPTZ NOT NULL
);

-- Create the queries table
CREATE TABLE queries (
                         id SERIAL PRIMARY KEY,  -- Auto-incrementing integer primary key
                         query TEXT NOT NULL,  -- Text column for the query
                         response TEXT NOT NULL,  -- Text column for the response
                         created_at TIMESTAMPTZ NOT NULL,  -- Timestamp with time zone
                         device_id UUID REFERENCES devices(id)  -- Foreign key referencing devices table
);
