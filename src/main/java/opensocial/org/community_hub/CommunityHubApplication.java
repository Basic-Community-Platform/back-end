package opensocial.org.community_hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //날짜 자동 업데이트 위해
public class CommunityHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityHubApplication.class, args);
	}
}
