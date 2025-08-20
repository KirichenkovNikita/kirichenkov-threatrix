CREATE INDEX IF NOT EXISTS ON assets (licenses);

CREATE INDEX IF NOT EXISTS ON assets (license_categories);

CREATE INDEX IF NOT EXISTS ON assets (VALUES(licenses));

CREATE INDEX IF NOT EXISTS ON assets (VALUES(license_categories));