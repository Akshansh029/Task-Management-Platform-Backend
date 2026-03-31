package com.akshansh.taskmanagementplatform.entity;

import java.util.Arrays;

public enum UserRole {
    ADMIN, MEMBER, VIEWER;

    public static boolean isUserRole(String str) {
        return Arrays.stream(UserRole.values())
                .anyMatch(e -> e.name().equals(str));
    }
}
