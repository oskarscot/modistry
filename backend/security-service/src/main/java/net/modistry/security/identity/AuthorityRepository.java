package net.modistry.security.identity;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthorityRepository extends ListCrudRepository<UserAuthority, UUID> {

    List<UserAuthority> findAllByUserId(UUID id);

}
