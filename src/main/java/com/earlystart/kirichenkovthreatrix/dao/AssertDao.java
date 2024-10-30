package com.earlystart.kirichenkovthreatrix.dao;

import com.earlystart.kirichenkovthreatrix.model.Asset;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssertDao extends CassandraRepository<Asset, UUID> {
    @Query("SELECT * FROM assets_by_project WHERE project_id = ?0")
    List<Asset> findByProjectId(UUID projectId);

    @Query("SELECT * FROM assets_by_name WHERE name = ?0")
    List<Asset> findByName(String name);

    List<Asset> findByLicensesContains(String license);

    List<Asset> findByLicenseCategoriesContains(String category);
}