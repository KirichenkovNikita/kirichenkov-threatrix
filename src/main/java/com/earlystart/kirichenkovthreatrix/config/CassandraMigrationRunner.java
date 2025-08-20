package com.earlystart.kirichenkovthreatrix.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.cognitor.cassandra.migration.Database;
import org.cognitor.cassandra.migration.MigrationRepository;
import org.cognitor.cassandra.migration.MigrationTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class CassandraMigrationRunner implements CommandLineRunner {
    @Value("${spring.cassandra.port}")
    private int port;

    @Value("${spring.cassandra.keyspace-name}")
    private String keyspaceName;
    @Value("${spring.cassandra.contact-points}")
    private String contactPoints;

    @Override
    public void run(String... args) {
        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoints, port))
                .withLocalDatacenter("datacenter1")
                .build()) {
            Database database = new Database(session, keyspaceName);
            MigrationTask migration = new MigrationTask(database, new MigrationRepository());
            migration.migrate();
        }
    }
}