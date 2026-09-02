package net.modistry.security.identity.entity;

import org.jspecify.annotations.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("linked_accounts")
public record LinkedAccountEntity(
        @Id UUID id,
        String providerName,
        String providerSub,
        UUID userAccountId,
        @Version Long version
) {

    public static LinkedAccountEntity fromAttributeDetails(UserAttributeDetails userAttributeDetails, @NonNull UUID userAccountId) {
        return new LinkedAccountEntity(UUID.randomUUID(), userAttributeDetails.provider(), userAttributeDetails.subject(), userAccountId, null);
    }
}
