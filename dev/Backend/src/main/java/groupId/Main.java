package groupId;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import Model.Model;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import parser.*;


@SpringBootApplication
@EntityScan(basePackages = {"Model","User","Image"})
@EnableJpaRepositories(basePackages = "Persistence.Repositories")
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
