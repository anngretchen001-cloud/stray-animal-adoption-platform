package org.example.backend.common;

public enum PostType {

    ANNOUNCEMENT,
    POPULAR_SCIENCE,
    HELP,
    SHARE;

    public static boolean isValid(String type) {
        for (PostType value : values()) {
            if (value.name().equals(type)) {
                return true;
            }
        }
        return false;
    }
}

