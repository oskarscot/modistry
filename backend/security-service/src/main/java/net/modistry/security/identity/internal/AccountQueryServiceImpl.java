package net.modistry.security.identity.internal;

import net.modistry.security.identity.AccountProfile;
import net.modistry.security.identity.AccountQueryService;
import net.modistry.security.identity.AccountRepository;
import net.modistry.security.identity.LinkedAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
class AccountQueryServiceImpl implements AccountQueryService {

    private final AccountRepository accountRepository;
    private final LinkedAccountRepository linkedAccountRepository;

    AccountQueryServiceImpl(AccountRepository accountRepository, LinkedAccountRepository linkedAccountRepository) {
        this.accountRepository = accountRepository;
        this.linkedAccountRepository = linkedAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountProfile getAccount(UUID accountId) {
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("Authenticated account no longer exists."));
        var linkedAccounts = linkedAccountRepository.findAllByUserAccountIdOrderByProviderNameAsc(accountId).stream()
                .map(linked -> new AccountProfile.LinkedAccount(linked.providerName()))
                .toList();

        return new AccountProfile(
                account.uuid(),
                account.username(),
                account.email(),
                account.avatarUrl(),
                linkedAccounts
        );
    }
}
