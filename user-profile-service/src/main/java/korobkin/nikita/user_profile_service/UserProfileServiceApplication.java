package korobkin.nikita.user_profile_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserProfileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserProfileServiceApplication.class, args);
	}

}
