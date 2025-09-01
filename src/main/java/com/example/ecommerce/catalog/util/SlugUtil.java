package com.example.ecommerce.catalog.util;

public final class SlugUtil {

    private SlugUtil() {
    }

    public static String toSlug(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
