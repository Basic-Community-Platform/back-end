package opensocial.org.community_hub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing //날짜 자동 업데이트를 위한 빈 설정
public class JpaConfig {
}
