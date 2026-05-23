CREATE TABLE inspection_request (
  id                      UUID PRIMARY KEY,
  inspection_type         VARCHAR(32) NOT NULL,
  date_status             VARCHAR(32) NOT NULL,
  resource_status         VARCHAR(32) NOT NULL DEFAULT 'UNALLOCATED',
  license_application_id  UUID,
  license_grant_id        UUID,
  complaint_id            UUID,
  company_id              UUID NOT NULL,
  premise_id              UUID NOT NULL,
  license_type            VARCHAR(64) NOT NULL,
  company_preferred_dates DATE[],
  planner_proposed_date   DATE,
  confirmed_date          DATE,
  scheduled_date          DATE,
  company_visible         BOOLEAN NOT NULL DEFAULT true,
  created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
  version                 BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT chk_inspection_type CHECK (inspection_type IN ('APPLICATION', 'ANNUAL', 'SPECIAL')),
  CONSTRAINT chk_date_status CHECK (date_status IN (
    'PREFERRED_SUBMITTED', 'IN_DISCUSSION', 'PROPOSED_BY_PLANNER',
    'CONFIRMED_BY_COMPANY', 'CONFIRMED_BY_PLANNER'
  )),
  CONSTRAINT chk_resource_status CHECK (resource_status IN (
    'UNALLOCATED', 'SUGGESTED', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'
  ))
);

CREATE INDEX idx_inspection_request_company ON inspection_request (company_id);
CREATE INDEX idx_inspection_request_premise ON inspection_request (premise_id);
CREATE INDEX idx_inspection_request_date_status ON inspection_request (date_status);
CREATE INDEX idx_inspection_request_resource_status ON inspection_request (resource_status);
CREATE INDEX idx_inspection_request_license_app ON inspection_request (license_application_id);
