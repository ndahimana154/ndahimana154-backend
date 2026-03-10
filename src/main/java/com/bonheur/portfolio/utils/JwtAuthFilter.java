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

        System.out.println("JwtAuthFilter: request= " + req.getMethod() + " " + req.getRequestURI());
        System.out.println("JwtAuthFilter: Authorization header= " + authHeader);

        boolean jwtAttempted = false;
        boolean jwtValid = false;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtAttempted = true;
            String token = authHeader.substring(7);

            System.out.println("JwtAuthFilter: Extracted token= " + token);
            String email = null;
            try {
                email = jwtService.extractEmail(token);
            } catch (Exception e) {
                System.out.println("JwtAuthFilter: extractEmail failed: " + e.getMessage());
            }

            System.out.println("JwtAuthFilter: Extracted email from token= " + email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    System.out.println("JwtAuthFilter: Loading user details for email= " + email);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    System.out.println("JwtAuthFilter: Loaded user details username= " + userDetails.getUsername());
                    boolean valid = jwtService.validateToken(token);
                    System.out.println("JwtAuthFilter: Token validation result= " + valid);

                    if (valid) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        jwtValid = true;
                        System.out.println("JwtAuthFilter: Authentication set for user= " + email);
                        System.out.println("JwtAuthFilter: SecurityContext authentication= "
                                + SecurityContextHolder.getContext().getAuthentication());
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
        System.out.println("JwtAuthFilter: After chain security context authentication= "
                + SecurityContextHolder.getContext().getAuthentication());
    }
}
