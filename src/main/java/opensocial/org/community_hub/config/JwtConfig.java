package opensocial.org.community_hub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt") // properties 파일 접두어로 필드 자동 바인딩 (하이픈 기준 Uppercase)
public class JwtConfig {

    private String secretKey;
    private long accessTokenExpireTime;
    private long refreshTokenExpireTime;

    public String getSecretKey() {
        return secretKey;
    }

    // properties 파일과 연동하려면 getter, setter 모두 포함되어야 함
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getAccessTokenExpireTime() {
        return accessTokenExpireTime;
    }

    public void setAccessTokenExpireTime(long accessTokenExpireTime) {
        this.accessTokenExpireTime = accessTokenExpireTime;
    }

    public long getRefreshTokenExpireTime() {
        return refreshTokenExpireTime;
    }

    public void setRefreshTokenExpireTime(long refreshTokenExpireTime) {
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }
}
