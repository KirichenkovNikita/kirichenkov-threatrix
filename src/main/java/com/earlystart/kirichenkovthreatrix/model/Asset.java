package com.earlystart.kirichenkovthreatrix.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("assets")
public class Asset {
    @PrimaryKey
    @Column("asset_id")
    private UUID assetId;

    @Column("project_id")
    private UUID projectId;

    @Column("name")
    private String name;

    @Column("licenses")
    private Set<String> licenses;

    @Column("license_categories")
    private Set<String> licenseCategories;

    @Column("open_source_match_percent")
    private double openSourceMatchPercent;

    @Column("created_at")
    private long createdAt;
}
