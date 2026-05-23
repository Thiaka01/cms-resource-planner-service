package com.nuvemite.cms.planner.security;

import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static CmsUserPrincipal currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CmsUserPrincipal principal)) {
            throw new IllegalStateException("No authenticated CMS user");
        }
        return principal;
    }

    public static String currentSubject() {
        return currentUser().subject();
    }
}
