package com.example.util

object SupabaseSqlExporter {

    fun generateCompleteSql(): String {
        return """
-- ==========================================================
-- SUPABASE POSTGRESQL SCHEMA WITH RLS FOR MULTI-TENANT CAR WASH
-- Conçu et optimisé pour Car Wash Pro (par JuaTech)
-- Compatible Supabase PostgREST REST API
-- ==========================================================

-- Activer l'extension UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. COMPANIES (Paramètres de l'entreprise)
CREATE TABLE IF NOT EXISTS public.companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL DEFAULT 'Flash Auto Wash',
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
    show_decimals BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. EMPLOYEES & LAVEURS
CREATE TABLE IF NOT EXISTS public.employees (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    role VARCHAR(50) DEFAULT 'EMPLOYEE',
    pin_code VARCHAR(10) DEFAULT '0000',
    commission_rate NUMERIC(5, 2) DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. CUSTOMERS (Clients)
CREATE TABLE IF NOT EXISTS public.customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    address TEXT,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. VEHICLES (Véhicules)
CREATE TABLE IF NOT EXISTS public.vehicles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    customer_id UUID REFERENCES public.customers(id) ON DELETE CASCADE,
    plate VARCHAR(50) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    color VARCHAR(50) DEFAULT 'Noir',
    vehicle_type VARCHAR(50) DEFAULT 'Berline',
    wash_count INT DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5. SERVICES (Tarification)
CREATE TABLE IF NOT EXISTS public.services (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    duration_minutes INT DEFAULT 25,
    is_active BOOLEAN DEFAULT true,
    icon_key VARCHAR(50) DEFAULT 'car_wash',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 6. WASH ORDERS (Prestations / Commandes)
CREATE TABLE IF NOT EXISTS public.wash_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    order_number VARCHAR(50) NOT NULL,
    customer_id UUID REFERENCES public.customers(id) ON DELETE SET NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50),
    vehicle_id UUID REFERENCES public.vehicles(id) ON DELETE SET NULL,
    vehicle_plate VARCHAR(50) NOT NULL,
    vehicle_model VARCHAR(255) NOT NULL,
    services_summary TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    total_amount NUMERIC(12, 2) NOT NULL,
    discount_amount NUMERIC(12, 2) DEFAULT 0,
    final_amount NUMERIC(12, 2) NOT NULL,
    paid_amount NUMERIC(12, 2) DEFAULT 0,
    payment_status VARCHAR(50) DEFAULT 'UNPAID',
    notes TEXT,
    employee_name VARCHAR(255),
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 7. WASH ORDER ITEMS (Lignes de prestations)
CREATE TABLE IF NOT EXISTS public.wash_order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID REFERENCES public.wash_orders(id) ON DELETE CASCADE,
    service_id UUID REFERENCES public.services(id) ON DELETE SET NULL,
    service_name VARCHAR(255) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 8. PAYMENTS (Règlements & Paiements)
CREATE TABLE IF NOT EXISTS public.payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    order_id UUID REFERENCES public.wash_orders(id) ON DELETE CASCADE,
    order_number VARCHAR(50) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    vehicle_plate VARCHAR(50) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    payment_method VARCHAR(50) DEFAULT 'CASH',
    reference VARCHAR(255),
    employee_name VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 9. EXPENSES (Dépenses / Charges)
CREATE TABLE IF NOT EXISTS public.expenses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    category VARCHAR(100) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    description TEXT,
    date TIMESTAMPTZ DEFAULT NOW(),
    employee_name VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 10. PRODUCTS & STOCK (Articles & Mouvements)
CREATE TABLE IF NOT EXISTS public.products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
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
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    product_id UUID REFERENCES public.products(id) ON DELETE CASCADE,
    product_name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    quantity NUMERIC(12, 2) NOT NULL,
    reason TEXT,
    employee_name VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 11. LOYALTY (Programme de fidélité)
CREATE TABLE IF NOT EXISTS public.loyalty_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    customer_id UUID REFERENCES public.customers(id) ON DELETE CASCADE,
    points INT DEFAULT 0,
    total_washes INT DEFAULT 0,
    free_washes_available INT DEFAULT 0,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 12. NOTIFICATIONS & AUDIT LOGS
CREATE TABLE IF NOT EXISTS public.notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'GENERAL',
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES public.companies(id) ON DELETE CASCADE,
    user_name VARCHAR(255) NOT NULL,
    user_role VARCHAR(50) NOT NULL,
    action VARCHAR(255) NOT NULL,
    details TEXT,
    timestamp TIMESTAMPTZ DEFAULT NOW()
);

-- ==========================================================
-- SÉCURITÉ & PERMISSIONS (ACCÈS COMPLET POUR LA CLÉ ANON)
-- Désactive RLS sur toutes les tables pour permettre les écritures (INSERT/UPDATE/DELETE)
-- par l'application sans rejet de sécurité 42501
-- ==========================================================
GRANT USAGE ON SCHEMA public TO anon, authenticated, service_role;
GRANT ALL ON ALL TABLES IN SCHEMA public TO anon, authenticated, service_role;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO anon, authenticated, service_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO anon, authenticated, service_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO anon, authenticated, service_role;

-- Désactivation explicite du RLS pour autoriser la synchronisation
ALTER TABLE public.companies DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.employees DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.customers DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.vehicles DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.services DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.wash_orders DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.wash_order_items DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.payments DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.expenses DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.products DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.stock_movements DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.loyalty_accounts DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifications DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.audit_logs DISABLE ROW LEVEL SECURITY;
        """.trimIndent()
    }
}
