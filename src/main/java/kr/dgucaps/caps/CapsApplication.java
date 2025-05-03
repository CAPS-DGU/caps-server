package kr.dgucaps.caps;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(
		servers = {
				@Server(url = "https://api.dgucaps.shop", description = "테스트 서버"),
		}
)
@EnableJpaAuditing
@SpringBootApplication
public class CapsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CapsApplication.class, args);
	}

}
