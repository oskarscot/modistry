package net.modistry.security.identity;

import net.modistry.security.identity.entity.AccountEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends ListCrudRepository<AccountEntity, UUID> { }
