package net.modistry.security.identity.internal;

import net.modistry.security.identity.entity.AccountEntity;
import net.modistry.security.identity.LocalAccountPrincipal;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class LocalOidcUser implements OidcUser, LocalAccountPrincipal {

    private final AccountEntity account;
    private final OidcUser oidcUser;
    private final Collection<? extends GrantedAuthority> authorities;

    public LocalOidcUser(AccountEntity account, OidcUser oidcUser, Collection<? extends GrantedAuthority> authorities) {
        this.account = account;
        this.oidcUser = oidcUser;
        this.authorities = authorities;
    }

    @Override
    public @NonNull Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public @Nullable OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public @NonNull OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public @NonNull Map<String, Object> getAttributes() {
        return oidcUser.getAttributes(); // TODO: adjust
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @NonNull String getName() {
        return account.uuid().toString();
    }

    @Override
    public @NonNull UUID getAccountId() {
        return account.uuid();
    }
}
