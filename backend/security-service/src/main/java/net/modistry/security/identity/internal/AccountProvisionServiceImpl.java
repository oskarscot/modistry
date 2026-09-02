package net.modistry.security.identity.internal;

import jakarta.validation.constraints.NotNull;
import net.modistry.security.identity.*;
import net.modistry.security.identity.entity.AccountEntity;
import net.modistry.security.identity.entity.LinkedAccountEntity;
import net.modistry.security.identity.entity.UserAttributeDetails;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
class AccountProvisionServiceImpl implements AccountProvisionService {

    private final AccountRepository accountRepository;
    private final LinkedAccountRepository linkedAccountRepository;

    AccountProvisionServiceImpl(AccountRepository accountRepository, LinkedAccountRepository linkedAccountRepository) {
        this.accountRepository = accountRepository;
        this.linkedAccountRepository = linkedAccountRepository;
    }

    @Transactional
    @Override
    public @NotNull AccountEntity findOrCreate(@NonNull UserAttributeDetails details) {
        var existingAccount = this.linkedAccountRepository
                .findByProviderNameAndProviderSub(details.provider(), details.subject());

        if(existingAccount.isPresent()) {
            var linkedAccountEntity = existingAccount.get();
            return accountRepository.findById(linkedAccountEntity.userAccountId())
                    .orElseThrow(() -> new IllegalStateException("Linked account references a non-existing account."));
        }

        var accountId = UUID.randomUUID();
        var account = accountRepository.save(AccountEntity.create(
                accountId,
                details.displayName(),
                details.email(),
                details.avatarUrl()
        ));
        linkedAccountRepository.save(LinkedAccountEntity.fromAttributeDetails(details, accountId));
        return account;
    }
}
