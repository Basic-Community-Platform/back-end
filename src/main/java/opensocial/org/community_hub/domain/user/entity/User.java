package opensocial.org.community_hub.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

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

    //엔티티 간의 동등성 비교와 컬렉션에서의 검색에 필요
    //엔티티가 Set이나 Map과 같은 컬렉션에 저장될 때, 동등성 비교를 위해 오버라이딩 필요
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);  // 유니크한 필드로 비교
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
