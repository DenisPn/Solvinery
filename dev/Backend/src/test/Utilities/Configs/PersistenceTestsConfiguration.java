package Utilities.Configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "Persistence.Repositories",
        "Persistence.Entities.Image.Repositories"
})
@EntityScan(basePackages = "Persistence.Entities")
@ComponentScan(basePackages = "Persistence")
public class PersistenceTestsConfiguration {
}
