CREATE TABLE license_type_planning_config (
  license_type                VARCHAR(64) PRIMARY KEY,
  expected_duration_minutes   INT NOT NULL,
  max_per_inspector_per_day   INT,
  updated_by                  VARCHAR(128),
  updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now()
);
