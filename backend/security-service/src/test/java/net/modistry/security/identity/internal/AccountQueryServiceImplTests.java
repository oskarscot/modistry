package net.modistry.security.identity.internal;

import net.modistry.security.identity.*;
import net.modistry.security.identity.entity.AccountEntity;
import net.modistry.security.identity.entity.LinkedAccountEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountQueryServiceImplTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LinkedAccountRepository linkedAccountRepository;

    @InjectMocks
    private AccountQueryServiceImpl accountQueryService;

    @Test
    void returnsAccountWithLinkedProviders() {
        var accountId = UUID.randomUUID();
        var now = Instant.now();
        var account = new AccountEntity(accountId, "oskar", "oskar@example.com", null, now, now, 0L);
        var hytale = new LinkedAccountEntity(UUID.randomUUID(), "hytale", "hytale-sub", accountId, 0L);
        var google = new LinkedAccountEntity(UUID.randomUUID(), "google", "google-sub", accountId, 0L);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(linkedAccountRepository.findAllByUserAccountIdOrderByProviderNameAsc(accountId))
                .thenReturn(List.of(google, hytale));

        var profile = accountQueryService.getAccount(accountId);

        assertThat(profile.id()).isEqualTo(accountId);
        assertThat(profile.username()).isEqualTo("oskar");
        assertThat(profile.linkedAccounts())
                .extracting(AccountProfile.LinkedAccount::provider)
                .containsExactly("google", "hytale");
    }
}
