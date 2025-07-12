package com.boot1.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum PermissionName {
    USER_READ("user:read"),
    USER_CREATE("user:create"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),
    PERMISSION_READ("permission:read"),
    PERMISSION_CREATE("permission:create"),
    PERMISSION_UPDATE("permission:update"),
    PERMISSION_DELETE("permission:delete");
    String value;

    PermissionName(String value) {
        this.value = value;
    }
}
