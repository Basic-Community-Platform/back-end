package opensocial.org.community_hub.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDTO {
    private String loginId;
    private String name;
    private String profileImageUrl;
    private String email;
}