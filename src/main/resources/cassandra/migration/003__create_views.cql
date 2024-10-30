CREATE MATERIALIZED VIEW IF NOT EXISTS assets_by_project AS
SELECT * FROM assets
WHERE project_id IS NOT NULL AND asset_id IS NOT NULL
PRIMARY KEY (project_id, asset_id);

CREATE MATERIALIZED VIEW IF NOT EXISTS assets_by_name AS
SELECT * FROM assets
WHERE name IS NOT NULL AND asset_id IS NOT NULL
PRIMARY KEY (name, asset_id);