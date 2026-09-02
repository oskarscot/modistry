package net.modistry.security.identity.type;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record HytaleProfile(UUID uuid, String username) {

    public static HytaleProfile fromClaim(Map<String, Object> claim) {
        Objects.requireNonNull(claim, "Hytale profile claim is missing.");

        var uuid = requiredString(claim, "uuid");
        var username = requiredString(claim, "username");
        return new HytaleProfile(UUID.fromString(uuid), username);
    }

    private static String requiredString(Map<String, Object> claim, String name) {
        var value = claim.get(name);
        if (value instanceof String string && !string.isBlank()) {
            return string;
        }
        throw new IllegalArgumentException("Hytale profile claim is missing '" + name + "'.");
    }
}
