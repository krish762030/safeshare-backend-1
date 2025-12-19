package org.safe.share.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Unauthorized");
        }

        Object principal = auth.getPrincipal();

        // Could be "anonymousUser" or userId as String
        if (principal instanceof String s) {
            if ("anonymousUser".equals(s)) {
                throw new RuntimeException("Unauthorized");
            }
            return Long.parseLong(s);
        }

        if (principal instanceof Long l) {
            return l;
        }

        throw new RuntimeException("Invalid authentication principal: " + principal.getClass());
    }
}

