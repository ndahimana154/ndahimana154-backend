package com.bonheur.portfolio.utils;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bonheur.portfolio.config.SecurityConstants;
import com.bonheur.portfolio.services.api.JwtServices;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtServices jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");

        boolean jwtAttempted = false;
        boolean jwtValid = false;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtAttempted = true;
            String token = authHeader.substring(7);

            String email = null;
            try {
                email = jwtService.extractEmail(token);
            } catch (Exception e) {
                System.out.println("JwtAuthFilter: extractEmail failed: " + e.getMessage());
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    boolean valid = jwtService.validateToken(token);

                    if (valid) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        jwtValid = true;
                    } else {
                        System.out.println("JwtAuthFilter: Token invalid, auth not set");
                    }
                } catch (Exception ex) {
                    System.out.println("JwtAuthFilter: userDetailsService failed: " + ex.getMessage());
                }
            } else if (email == null) {
                System.out.println("JwtAuthFilter: Email is null; no authentication to set");
            }
        }

        boolean requiresAuth = SecurityConstants.AUTH_REQUIRED_PATH_PREFIXES.stream()
                .anyMatch(prefix -> req.getServletPath().startsWith(prefix));

        if (requiresAuth && (!jwtAttempted || !jwtValid)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            String path = req.getRequestURI();
            String message = "Unauthorized: token is missing or invalid";
            if (!jwtAttempted) {
                message = "Unauthorized: Authorization header missing";
            }
            res.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"" + message
                    + "\",\"path\":\"" + path + "\"}");
            return;
        }

        chain.doFilter(req, res);
    }
}
