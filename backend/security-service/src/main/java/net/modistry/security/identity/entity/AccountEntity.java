package net.modistry.security.identity.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("user_accounts")
public record AccountEntity(
        @Id UUID uuid,
        String username,
        String email,
        String avatarUrl,
        @CreatedDate Instant createdAt,
        @LastModifiedDate Instant updatedAt,
        @Version Long version
) {

    public static AccountEntity create(UUID uuid, String username, String email, String avatarUrl) {
        return new AccountEntity(uuid, username, email, avatarUrl, null, null, null);
    }
}
