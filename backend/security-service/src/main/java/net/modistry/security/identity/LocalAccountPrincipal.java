package net.modistry.security.identity;

import org.springframework.security.core.AuthenticatedPrincipal;

import java.util.UUID;

public interface LocalAccountPrincipal extends AuthenticatedPrincipal {

    UUID getAccountId();

}
