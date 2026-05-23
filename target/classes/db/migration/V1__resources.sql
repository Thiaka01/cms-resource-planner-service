CREATE TABLE inspector (
  id                UUID PRIMARY KEY,
  user_id           UUID NOT NULL UNIQUE,
  employee_code     VARCHAR(64) NOT NULL UNIQUE,
  full_name         VARCHAR(255) NOT NULL,
  email             VARCHAR(255),
  phone             VARCHAR(64),
  home_premise_id   UUID NOT NULL,
  active            BOOLEAN NOT NULL DEFAULT true,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE driver (
  id                UUID PRIMARY KEY,
  home_premise_id   UUID NOT NULL,
  full_name         VARCHAR(255) NOT NULL,
  license_number    VARCHAR(64),
  phone             VARCHAR(64),
  active            BOOLEAN NOT NULL DEFAULT true,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE vehicle (
  id                  UUID PRIMARY KEY,
  home_premise_id     UUID NOT NULL,
  registration_number VARCHAR(64) NOT NULL UNIQUE,
  vehicle_type        VARCHAR(64),
  capacity_notes      VARCHAR(255),
  active              BOOLEAN NOT NULL DEFAULT true,
  created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_inspector_home_premise ON inspector (home_premise_id);
CREATE INDEX idx_driver_home_premise ON driver (home_premise_id);
CREATE INDEX idx_vehicle_home_premise ON vehicle (home_premise_id);
