package com.earlystart.kirichenkovthreatrix.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.cognitor.cassandra.migration.Database;
import org.cognitor.cassandra.migration.MigrationRepository;
import org.cognitor.cassandra.migration.MigrationTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CassandraMigrationRunner implements CommandLineRunner {
    @Value("${spring.cassandra.keyspace-name}")
    private String keyspaceName;

    @Override
    public void run(String... args) {
        var session = CqlSession.builder().build();
        Database database = new Database(session, keyspaceName);
        MigrationTask migration = new MigrationTask(database, new MigrationRepository());
        migration.migrate();
        session.close();
    }
}