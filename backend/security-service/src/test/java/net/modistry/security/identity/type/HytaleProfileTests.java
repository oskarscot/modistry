package net.modistry.security.identity.type;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HytaleProfileTests {

    @Test
    void createsProfileFromNestedClaimMap() {
        var uuid = UUID.randomUUID();

        var profile = HytaleProfile.fromClaim(Map.of(
                "uuid", uuid.toString(),
                "username", "oskar"
        ));

        assertThat(profile.uuid()).isEqualTo(uuid);
        assertThat(profile.username()).isEqualTo("oskar");
    }
}
