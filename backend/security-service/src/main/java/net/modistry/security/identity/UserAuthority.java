package net.modistry.security.identity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("user_authorities")
public record UserAuthority(
        @Id UUID id,
        UUID userId,
        String authority,
        @Version Long version
) {

    public static UserAuthority create(UUID userId, String authority) {
        return new UserAuthority(UUID.randomUUID(), userId, authority, null);
    }

}
