CREATE TABLE inspection_date_message (
  id                      UUID PRIMARY KEY,
  inspection_request_id   UUID NOT NULL REFERENCES inspection_request (id),
  author_role             VARCHAR(16) NOT NULL,
  author_user_id          VARCHAR(128) NOT NULL,
  body                    TEXT NOT NULL,
  attached_preferred_dates DATE[],
  created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_author_role CHECK (author_role IN ('COMPANY', 'PLANNER'))
);

CREATE INDEX idx_date_message_request ON inspection_date_message (inspection_request_id, created_at);
