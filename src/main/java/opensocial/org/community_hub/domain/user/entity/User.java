package opensocial.org.community_hub.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "app_user") // 테이블 이름 변경 : sql 예약어(user) 중복 방지
@EntityListeners(AuditingEntityListener.class) //날짜 자동 업데이트를 위한 애노테이션
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String loginId;
    private String password;
    private String name;
    private String email;
    private String profileImageUrl;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(String loginId, String password, String name, String email, String profileImageUrl) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}
