package net.modistry.security.identity.entity;

import org.jspecify.annotations.Nullable;

public record UserAttributeDetails(
        String provider,
        String subject,
        @Nullable String email,
        @Nullable Boolean emailVerified,
        @Nullable String displayName,
        @Nullable String avatarUrl
) { }
