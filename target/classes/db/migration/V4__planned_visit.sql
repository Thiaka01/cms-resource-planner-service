CREATE TABLE planned_visit (
  id                  UUID PRIMARY KEY,
  visit_date          DATE NOT NULL,
  inspector_id        UUID NOT NULL REFERENCES inspector (id),
  status              VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_planned_visit_status CHECK (status IN ('DRAFT', 'SUGGESTED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

CREATE TABLE visit_stop (
  id                    UUID PRIMARY KEY,
  planned_visit_id      UUID NOT NULL REFERENCES planned_visit (id),
  inspection_request_id UUID NOT NULL REFERENCES inspection_request (id),
  sequence_order        INT NOT NULL,
  UNIQUE (planned_visit_id, inspection_request_id)
);

CREATE TABLE transport_assignment (
  id                UUID PRIMARY KEY,
  planned_visit_id  UUID NOT NULL UNIQUE REFERENCES planned_visit (id),
  driver_id         UUID NOT NULL REFERENCES driver (id),
  vehicle_id        UUID NOT NULL REFERENCES vehicle (id),
  assigned_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_planned_visit_inspector_date ON planned_visit (inspector_id, visit_date);
CREATE INDEX idx_visit_stop_request ON visit_stop (inspection_request_id);
