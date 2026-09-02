package net.modistry.security.identity;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public record AccountProfile(
        UUID id,
        @Nullable String username,
        @Nullable String email,
        @Nullable String avatarUrl,
        List<LinkedAccount> linkedAccounts
) {

    public AccountProfile {
        linkedAccounts = List.copyOf(linkedAccounts);
    }

    // TODO: add more identifiers about the account
    public record LinkedAccount(String provider) { }
}
