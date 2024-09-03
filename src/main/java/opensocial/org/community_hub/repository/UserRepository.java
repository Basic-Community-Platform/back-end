package opensocial.org.community_hub.repository;

import opensocial.org.community_hub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLoginId(String loginId);
}