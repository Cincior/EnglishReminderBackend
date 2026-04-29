package pl.cinkus.backend;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class AuthFilter extends OncePerRequestFilter {
    private static final String ROLE = "ROLE_%s";
    private static final String ID_HEADER = "X-UserId";
    private static final String ROLE_HEADER = "X-UserRole";

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getHeader(ID_HEADER);
        String userRole = request.getHeader(ROLE_HEADER);

        if (userId == null || userRole == null || userId.isEmpty() || userRole.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(ROLE.formatted(userRole));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userId, null, List.of(grantedAuthority));

        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);
    }
}
