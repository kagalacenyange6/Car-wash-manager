package com.example.util

object SupabaseSqlExporter {

    fun generateCompleteSql(): String {
        return """
-- ==========================================================
-- SUPABASE POSTGRESQL SCHEMA WITH RLS FOR MULTI-TENANT CAR WASH
-- Generated for Car Wash Pro SaaS
-- ==========================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. COMPANIES (Car Wash tenants)
CREATE TABLE IF NOT EXISTS public.companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    address TEXT,
    currency_symbol VARCHAR(10) DEFAULT '$',
    loyalty_enabled BOOLEAN DEFAULT true,
    loyalty_threshold INT DEFAULT 10,
    loyalty_reward_name VARCHAR(255) DEFAULT '1 Lavage extérieur offert',
    receipt_paper_width VARCHAR(10) DEFAULT '80mm',
    receipt_header_note TEXT,
    receipt_footer_note TEXT,
    mobile_money_number VARCHAR(50),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. PROFILES & ROLES
CREATE TYPE user_role AS ENUM ('ADMIN', 'MANAGER', 'EMPLOYEE');

CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    role user_role DEFAULT 'EMPLOYEE',
    pin_code VARCHAR(10) DEFAULT '1234',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. CUSTOMERS
CREATE TABLE IF NOT EXISTS public.customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    address TEXT,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. VEHICLES
CREATE TABLE IF NOT EXISTS public.vehicles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    customer_id UUID NOT NULL REFERENCES public.customers(id) ON DELETE CASCADE,
    plate VARCHAR(50) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    color VARCHAR(50),
    vehicle_type VARCHAR(50) DEFAULT 'Berline',
    wash_count INT DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5. SERVICES
CREATE TABLE IF NOT EXISTS public.services (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    duration_minutes INT DEFAULT 25,
    is_active BOOLEAN DEFAULT true,
    icon_key VARCHAR(50) DEFAULT 'car_wash',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 6. WASH ORDERS (Prestations)
CREATE TYPE order_status AS ENUM ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE payment_status AS ENUM ('UNPAID', 'PARTIAL', 'DEBT', 'PAID');

CREATE TABLE IF NOT EXISTS public.wash_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID REFERENCES public.customers(id) ON DELETE SET NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50),
    vehicle_id UUID REFERENCES public.vehicles(id) ON DELETE SET NULL,
    vehicle_plate VARCHAR(50) NOT NULL,
    vehicle_model VARCHAR(255) NOT NULL,
    services_summary TEXT,
    status order_status DEFAULT 'PENDING',
    total_amount NUMERIC(12, 2) NOT NULL,
    discount_amount NUMERIC(12, 2) DEFAULT 0,
    final_amount NUMERIC(12, 2) NOT NULL,
    paid_amount NUMERIC(12, 2) DEFAULT 0,
    payment_status payment_status DEFAULT 'UNPAID',
    notes TEXT,
    employee_name VARCHAR(255),
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 7. WASH ORDER ITEMS
CREATE TABLE IF NOT EXISTS public.wash_order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES public.wash_orders(id) ON DELETE CASCADE,
    service_id UUID REFERENCES public.services(id) ON DELETE SET NULL,
    service_name VARCHAR(255) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 8. PAYMENTS
CREATE TYPE payment_method AS ENUM ('CASH', 'MOBILE_MONEY', 'CARD', 'CREDIT');

CREATE TABLE IF NOT EXISTS public.payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    order_id UUID NOT NULL REFERENCES public.wash_orders(id) ON DELETE CASCADE,
    order_number VARCHAR(50) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    vehicle_plate VARCHAR(50) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    payment_method payment_method DEFAULT 'CASH',
    reference VARCHAR(255),
    employee_name VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 9. EXPENSES
CREATE TABLE IF NOT EXISTS public.expenses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    category VARCHAR(100) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    description TEXT,
    date TIMESTAMPTZ DEFAULT NOW(),
    employee_name VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 10. PRODUCTS & STOCK
CREATE TABLE IF NOT EXISTS public.products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    current_stock NUMERIC(12, 2) DEFAULT 0,
    unit VARCHAR(50) DEFAULT 'L',
    min_threshold NUMERIC(12, 2) DEFAULT 5,
    unit_price NUMERIC(12, 2) DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.stock_movements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    product_name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    quantity NUMERIC(12, 2) NOT NULL,
    reason TEXT,
    employee_name VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 11. LOYALTY
CREATE TABLE IF NOT EXISTS public.loyalty_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    customer_id UUID NOT NULL REFERENCES public.customers(id) ON DELETE CASCADE,
    points INT DEFAULT 0,
    total_washes INT DEFAULT 0,
    free_washes_available INT DEFAULT 0,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 12. NOTIFICATIONS & AUDIT LOGS
CREATE TABLE IF NOT EXISTS public.notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'GENERAL',
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    user_name VARCHAR(255) NOT NULL,
    user_role VARCHAR(50) NOT NULL,
    action VARCHAR(255) NOT NULL,
    details TEXT,
    timestamp TIMESTAMPTZ DEFAULT NOW()
);

-- ==========================================================
-- ROW LEVEL SECURITY (RLS) MULTI-TENANT ISOLATION
-- ==========================================================
ALTER TABLE public.companies ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.customers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vehicles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.services ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.wash_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.expenses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.products ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.audit_logs ENABLE ROW LEVEL SECURITY;

-- Helper function to get current user's company_id
CREATE OR REPLACE FUNCTION public.current_user_company_id()
RETURNS UUID AS $$
  SELECT company_id FROM public.profiles WHERE id = auth.uid() LIMIT 1;
$$ LANGUAGE sql STABLE SECURITY DEFINER;

-- RLS Tenant Isolation Policy Examples
CREATE POLICY "Tenant isolation for customers" ON public.customers
    FOR ALL USING (company_id = public.current_user_company_id());

CREATE POLICY "Tenant isolation for vehicles" ON public.vehicles
    FOR ALL USING (company_id = public.current_user_company_id());

CREATE POLICY "Tenant isolation for wash_orders" ON public.wash_orders
    FOR ALL USING (company_id = public.current_user_company_id());

CREATE POLICY "Tenant isolation for payments" ON public.payments
    FOR ALL USING (company_id = public.current_user_company_id());

CREATE POLICY "Tenant isolation for products" ON public.products
    FOR ALL USING (company_id = public.current_user_company_id());
        """.trimIndent()
    }
}
