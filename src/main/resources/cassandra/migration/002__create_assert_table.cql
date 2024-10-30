
CREATE TABLE IF NOT EXISTS assets (
                                      asset_id UUID PRIMARY KEY,
                                      project_id UUID,
                                      name TEXT,
                                      licenses SET<TEXT>,
                                      license_categories SET<TEXT>,
                                      open_source_match_percent DOUBLE,
                                      created_at TIMESTAMP
);

