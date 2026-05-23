CREATE TABLE premise_location_cache (
  premise_id    UUID PRIMARY KEY,
  latitude      DOUBLE PRECISION NOT NULL,
  longitude     DOUBLE PRECISION NOT NULL,
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE inspection_neighbor_rank (
  inspection_request_id   UUID NOT NULL REFERENCES inspection_request (id),
  neighbor_request_id     UUID NOT NULL REFERENCES inspection_request (id),
  rank                    INT NOT NULL,
  travel_duration_seconds INT,
  travel_distance_meters  INT,
  computed_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (inspection_request_id, neighbor_request_id),
  CONSTRAINT chk_neighbor_rank CHECK (rank BETWEEN 1 AND 10)
);

CREATE INDEX idx_neighbor_rank_request ON inspection_neighbor_rank (inspection_request_id, rank);
