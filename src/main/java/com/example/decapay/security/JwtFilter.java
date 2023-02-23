package com.example.decapay.security;

import com.example.decapay.utils.LocalMemStorage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    @Value(value = "${auth.user}")
    private final String AUTH_USER = "AuthUser:";
    private final JwtUtil jwtUtil;
    private final UserDetailsService detailsService;
    private final LocalMemStorage localStorage;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");
            String username = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.replace("Bearer", "").replace("\\s", "");
                logger.info("The token is " + token);
                username = jwtUtil.extractUsername(token);
                logger.info(username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = detailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    String tokenKey = AUTH_USER + username;
                    localStorage.saveToken(tokenKey, token.trim());
                }
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);

        } catch (Exception ex) {
            ex.printStackTrace();
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
}
