package net.modistry.security.identity;

import java.util.UUID;

public interface AccountQueryService {

    /// Returns a [AccountProfile] view for the provided ``accountId``
    AccountProfile getAccount(UUID accountId);

}
