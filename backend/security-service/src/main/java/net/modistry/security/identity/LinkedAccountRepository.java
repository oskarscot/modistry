package net.modistry.security.identity;

import net.modistry.security.identity.entity.LinkedAccountEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkedAccountRepository extends ListCrudRepository<LinkedAccountEntity, UUID> {

    Optional<LinkedAccountEntity> findByProviderNameAndProviderSub(String name, String sub);

    List<LinkedAccountEntity> findAllByUserAccountIdOrderByProviderNameAsc(UUID accountId);

}
