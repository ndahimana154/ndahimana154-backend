package com.bonheur.portfolio.config;

import java.util.List;

public final class SecurityConstants {
    private SecurityConstants() {
    }

    public static final List<String> AUTH_REQUIRED_PATH_PREFIXES = List.of(
            "/backoffice/projects",
            "/backoffice/users",
            "/backoffice/others");

    public static final java.util.List<String> PUBLIC_PATHS = java.util.List.of(
            "/backoffice/auth",
            "/remote");
}
