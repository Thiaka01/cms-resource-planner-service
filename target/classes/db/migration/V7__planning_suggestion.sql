CREATE TABLE planning_suggestion (
  id              UUID PRIMARY KEY,
  run_date_from   DATE NOT NULL,
  run_date_to     DATE NOT NULL,
  status          VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  suggestion_json JSONB NOT NULL DEFAULT '{}',
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  created_by      VARCHAR(128),
  CONSTRAINT chk_planning_suggestion_status CHECK (status IN ('PENDING', 'READY', 'APPLIED', 'EXPIRED'))
);
