package Utilities;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "Persistence.Repositories",
})
@EntityScan(basePackages = "groupId.Services")
@ComponentScan(basePackages = "groupId")
public class UnitTestsConfiguration {
}
