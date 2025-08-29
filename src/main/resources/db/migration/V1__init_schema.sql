-- V1__init_schema.sql

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    parent_id UUID,
    slug VARCHAR(200) UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories (id) ON DELETE SET NULL
);

CREATE TABLE products (
    id UUID PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description VARCHAR(20000),
    brand_name VARCHAR(255) NOT NULL,
    rating DECIMAL(3,2) DEFAULT 1.00 CHECK (rating >= 0.00),
    stock_quantity BIGINT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    weight DECIMAL(10,2) NOT NULL CHECK (weight > 0),
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    category_id UUID NOT NULL,
    status VARCHAR(25) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);

CREATE TABLE product_images (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    alt_text VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

CREATE TABLE product_variants (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    price_override DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (price_override >= 0.00),
    stock_quantity BIGINT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_variant_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

-- Indexes for faster lookup
CREATE INDEX idx_category_name ON categories(name);
CREATE INDEX idx_category_slug ON categories(slug);
CREATE INDEX idx_product_name ON products(product_name);
CREATE INDEX idx_product_brand ON products(brand_name);
CREATE INDEX idx_product_status ON products(status);
CREATE INDEX idx_image_product ON product_images(product_id);
CREATE INDEX idx_variant_product ON product_variants(product_id);
