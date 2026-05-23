package com.nuvemite.cms.planner.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class CmsAuthenticationToken extends JwtAuthenticationToken {

    private final CmsUserPrincipal principal;

    public CmsAuthenticationToken(Jwt jwt, CmsUserPrincipal principal, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities);
        this.principal = principal;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
