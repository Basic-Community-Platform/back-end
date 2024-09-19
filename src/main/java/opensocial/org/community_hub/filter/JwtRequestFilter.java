package opensocial.org.community_hub.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import opensocial.org.community_hub.util.JwtTokenUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService; // UserService 대신 UserDetailsService 사용

    public JwtRequestFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // JWT 토큰이 Authorization 헤더에 있는지 확인
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);  // "Bearer " 접두사 제거
            username = jwtTokenUtil.extractUsername(jwt);  // JWT에서 사용자 이름 추출
        }

        // username이 있고, SecurityContext에서 아직 인증되지 않은 경우 처리
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // UserDetailsService를 사용하여 UserDetails 객체 로드
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // UserDetails를 사용하여 토큰 검증
            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                // 인증 토큰을 생성하고, SecurityContext에 설정
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // 다음 필터 체인으로 요청 전달
        chain.doFilter(request, response);
    }
}
