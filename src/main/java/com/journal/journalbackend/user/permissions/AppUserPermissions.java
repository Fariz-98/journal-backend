package com.journal.journalbackend.user.permissions;

public enum AppUserPermissions {

    JOURNAL_WRITE("journal:write"),
    COMMENT_WRITE("comment:write"),
    USER_WRITE("user:write");

    private final String permissions;

    AppUserPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getPermissions() {
        return permissions;
    }

}
