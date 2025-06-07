package roomescape.model;

public enum Role {
    ADMIN, USER;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
