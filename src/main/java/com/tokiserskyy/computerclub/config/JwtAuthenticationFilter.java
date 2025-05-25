package com.tokiserskyy.computerclub.config;

import com.tokiserskyy.computerclub.repository.PersonRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final PersonRepository personRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, PersonRepository personRepository) {
        this.jwtUtil = jwtUtil;
        this.personRepository = personRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: " + authHeader);

        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            logger.debug("Extracted token: " + token);
            try {
                username = jwtUtil.extractUsername(token);
                logger.debug("Extracted username: " + username);
            } catch (Exception e) {
                logger.error("Failed to extract username from token: " + e.getMessage());
            }
        } else {
            logger.debug("No Bearer token found in Authorization header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = personRepository.findByUsername(username)
                    .map(person -> new User(
                            person.getUsername(),
                            person.getPassword(),
                            Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                    person.getRole() == 1 ? "ROLE_ADMIN" : "ROLE_USER"))
                    ))
                    .orElse(null);

            logger.debug("Loaded UserDetails: username=" + (userDetails != null ? userDetails.getUsername() : "null") +
                    ", authorities=" + (userDetails != null ? userDetails.getAuthorities() : "null"));

            if (userDetails == null) {
                logger.error("UserDetails is null for username: " + username);
            } else if (userDetails.getUsername() == null || userDetails.getUsername().isEmpty()) {
                logger.error("Username in UserDetails is null or empty");
            } else if (userDetails.getAuthorities() == null || userDetails.getAuthorities().isEmpty()) {
                logger.error("Authorities in UserDetails is null or empty");
            } else if (jwtUtil.validateToken(token, username)) {
                logger.debug("Token validated for user: " + username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("Authentication set for user: " + username);
            } else {
                logger.debug("Token validation failed for user: " + username);
            }
        } else {
            logger.debug("Skipping authentication: username=" + username + ", existing authentication=" +
                    SecurityContextHolder.getContext().getAuthentication());
        }

        filterChain.doFilter(request, response);
    }
}