package com.journal.journalbackend.user.permissions;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum AppUserRole {

    USER(Set.of()),
    ADMIN(Set.of(
            AppUserPermissions.USER_WRITE,
            AppUserPermissions.COMMENT_WRITE,
            AppUserPermissions.JOURNAL_WRITE
    )),
    MODERATOR(Set.of(
            AppUserPermissions.JOURNAL_WRITE,
            AppUserPermissions.COMMENT_WRITE));

    private final Set<AppUserPermissions> permissions;
    private static Map<Enum<AppUserRole>, String> roleMap;

    AppUserRole(Set<AppUserPermissions> permissions) {
        this.permissions = permissions;
    }

    public static Map<Enum<AppUserRole>, String> getRoleMap() {
        for (Enum<AppUserRole> e : AppUserRole.values()) {
            roleMap.put(e, e.toString());
        }

        return roleMap;
    }

    public Set<AppUserPermissions> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> simpleGrantedAuthoritySet = new HashSet<>();

        for (AppUserPermissions appUserPermissions: getPermissions()) {
            simpleGrantedAuthoritySet.add(new SimpleGrantedAuthority(appUserPermissions.getPermissions()));
        }

        simpleGrantedAuthoritySet.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return simpleGrantedAuthoritySet;
    }

}
