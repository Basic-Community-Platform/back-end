package opensocial.org.community_hub.domain.user.repository;

import opensocial.org.community_hub.domain.user.dto.UserDetailsResponse;

public interface UserQueryRepository {
    UserDetailsResponse findUserDetailsByLoginId(String loginId);
}
