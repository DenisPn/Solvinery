package groupId;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan(basePackages = {"Persistence.Entities"})
@EnableJpaRepositories(basePackages = "Persistence.Repositories")
@OpenAPIDefinition(
		info = @Info(
				title = "Solvinery API",
				version = "1.0",
				description = "Solvinery API Documentation"
		)
)

public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
