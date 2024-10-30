package com.earlystart.kirichenkovthreatrix.service.asserts;

import com.datastax.oss.driver.api.core.CqlSession;
import com.earlystart.kirichenkovthreatrix.dao.AssertDao;
import com.earlystart.kirichenkovthreatrix.model.Asset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class AssetServiceTest {
    private static final int TEST_DATA_SIZE = 10000;

    @Container
    private static final CassandraContainer<?> cassandraContainer =
            new CassandraContainer<>("cassandra:3.11")
                    .withExposedPorts(9042);

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssertDao assertDao;

    @BeforeAll
    public static void setupTestData() {
        cassandraContainer.start();

        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(cassandraContainer.getContainerIpAddress(), cassandraContainer.getFirstMappedPort()))
                .withLocalDatacenter("datacenter1")
                .build()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS my_keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};");
        }

        System.setProperty("spring.cassandra.contact-points", cassandraContainer.getContainerIpAddress());
        System.setProperty("spring.cassandra.port", cassandraContainer.getFirstMappedPort().toString());
    }

    @BeforeEach
    public void setup() {
        initData();
    }

    @AfterEach
    public void cleanUp() {
        assertDao.deleteAll();
    }


    @Test
    void testGetAssetsByProjectId() {
        UUID sampleProjectId = assertDao.findAll().get(0).getProjectId();
        long startTime = System.nanoTime();

        List<Asset> results = assetService.getAssetsByProjectId(sampleProjectId);

        long duration = System.nanoTime() - startTime;
        System.out.println("testGetAssetsByProjectId execution time: " + duration / 1_000_000 + " ms");
        assertFalse(results.isEmpty());
    }

    @Test
    void testGetAssetsByName() {
        String sampleName = "TestAsset0";
        long startTime = System.nanoTime();

        List<Asset> results = assetService.getAssetsByName(sampleName);

        long duration = System.nanoTime() - startTime;
        System.out.println("testGetAssetsByName execution time: " + duration / 1_000_000 + " ms");
        assertFalse(results.isEmpty());
    }

    @Test
    void testGetAssetsByLicenses() {
        List<String> licenses = List.of("License1", "License7");
        long startTime = System.nanoTime();

        List<Asset> results = assetService.getAssetsByMultipleLicenses(licenses);

        long duration = System.nanoTime() - startTime;
        System.out.println("testGetAssetsByLicenses execution time: " + duration / 1_000_000 + " ms");
        assertFalse(results.isEmpty());
    }

    @Test
    void testGetAssetsByCategories() {
        List<String> categories = List.of("Category1", "Category2");
        long startTime = System.nanoTime();

        List<Asset> results = assetService.getAssetsByMultipleCategories(categories);

        long duration = System.nanoTime() - startTime;
        System.out.println("testGetAssetsByCategories execution time: " + duration / 1_000_000 + " ms");
        assertFalse(results.isEmpty());
    }

    @Test
    void testGetAssetsByLicensesAndCategories() {
        List<String> licenses = List.of("License1", "License7");
        List<String> categories = List.of("Category1", "Category2");
        long startTime = System.nanoTime();

        List<Asset> results = assetService.getAssetsByLicensesAndCategories(licenses, categories);

        long duration = System.nanoTime() - startTime;
        System.out.println("testGetAssetsByLicensesAndCategories execution time: " + duration / 1_000_000 + " ms");
        assertFalse(results.isEmpty());
    }

    private void initData() {
        var random = new Random();
        List<Asset> testAssets = new ArrayList<>();

        // Generate possible values for licenses and categories
        List<String> possibleLicenses = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> "License" + i)
                .toList();
        List<String> possibleCategories = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> "Category" + i)
                .toList();

        for (int i = 0; i < TEST_DATA_SIZE; i++) {
            // Generate a random projectId within the range of 0 to 100
            UUID projectId = UUID.fromString("00000000-0000-0000-0000-" + String.format("%012d", random.nextInt(101)));

            // Generate a random set of licenses (at least 2)
            Set<String> licenses = new HashSet<>();
            while (licenses.size() < 2) {
                licenses.add(possibleLicenses.get(random.nextInt(possibleLicenses.size())));
            }

            // Generate a random set of categories (at least 2)
            Set<String> categories = new HashSet<>();
            while (categories.size() < 2) {
                categories.add(possibleCategories.get(random.nextInt(possibleCategories.size())));
            }

            Asset asset = Asset.builder()
                    .assetId(UUID.randomUUID())
                    .projectId(projectId)
                    .name("TestAsset" + i)
                    .licenses(licenses)
                    .licenseCategories(categories)
                    .openSourceMatchPercent(random.nextDouble() * 100)
                    .createdAt(System.currentTimeMillis() - i * 1000L)
                    .build();

            testAssets.add(asset);
        }

        assertDao.saveAll(testAssets);
    }
}
