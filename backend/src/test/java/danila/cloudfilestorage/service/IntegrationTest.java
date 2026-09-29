package danila.cloudfilestorage.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;


@TestConfiguration
public class IntegrationTest {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer postgres() {
        return new PostgreSQLContainer("postgres:18");
    }

    @Bean
    public GenericContainer<?> minio() {
        return new GenericContainer<>("minio/minio:latest")
                .withExposedPorts(9000)
                .withEnv("MINIO_ROOT_USER", "minioadmin")
                .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
                .withCommand("server", "/data");
    }

    @Bean
    public DynamicPropertyRegistrar minioProperties(@Qualifier("minio") GenericContainer<?> minio) {
        return registry -> {
            registry.add("minio.url", () -> "http://" + minio.getHost() + ":"
                    + minio.getMappedPort(9000));
            registry.add("minio.access-key", () -> "minioadmin");
            registry.add("minio.secret-key", () -> "minioadmin");
        };
    }
}
