package com.nuvemite.cms.planner.security;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.jwt.Jwt;

public final class CmsPrincipalParser {

    private CmsPrincipalParser() {}

    public static CmsUserPrincipal fromJwt(Jwt jwt) {
        return CmsUserPrincipal.fromClaims(
                jwt.getSubject(),
                jwt.getClaimAsString("platform_role"),
                jwt.getClaimAsStringList("company_ids"),
                jwt.getClaimAsStringList("premise_ids"),
                jwt.getClaim("company_memberships"));
    }

    static Set<UUID> parseUuids(List<String> values) {
        if (values == null || values.isEmpty()) {
            return new HashSet<>();
        }
        return values.stream().map(UUID::fromString).collect(Collectors.toCollection(HashSet::new));
    }
}
