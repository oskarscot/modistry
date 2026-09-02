package net.modistry.security.identity;

import jakarta.validation.constraints.NotNull;
import net.modistry.security.identity.entity.AccountEntity;
import net.modistry.security.identity.entity.UserAttributeDetails;
import org.jspecify.annotations.NonNull;

public interface AccountProvisionService {

    /// Either finds an [AccountEntity] based of the provided provider name and subject from the [UserAttributeDetails]
    /// or constructs a new entity with the values extracted
    @NotNull
    AccountEntity findOrCreate(@NonNull UserAttributeDetails details);

}
