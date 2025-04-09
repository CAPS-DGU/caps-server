package kr.dgucaps.caps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CapsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CapsApplication.class, args);
	}

}
