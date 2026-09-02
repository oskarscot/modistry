package net.modistry.security;

import net.modistry.security.identity.AccountProvisionService;
import net.modistry.security.identity.AuthorityRepository;
import net.modistry.security.identity.UserAuthority;
import net.modistry.security.identity.entity.UserAttributeDetails;
import net.modistry.security.identity.internal.LocalOidcUser;
import net.modistry.security.identity.type.HytaleProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private final AccountProvisionService provisionService;
    private final AuthorityRepository authorityRepository;

    public WebSecurityConfig(AccountProvisionService provisionService, AuthorityRepository authorityRepository) {
        this.provisionService = provisionService;
        this.authorityRepository = authorityRepository;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth.
                        requestMatchers("/login/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oAuth2 ->
                        oAuth2.userInfoEndpoint(userInfo ->
                                userInfo.oidcUserService(oidcUserService(provisionService)))
                )
                .build();
    }

    @Bean
    OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(AccountProvisionService provisionService) {
        var delegate = new OidcUserService();
        return request -> {
            var externalUser = delegate.loadUser(request);
            var accountDetails = mapUserDetails(request, externalUser);
            var account = provisionService.findOrCreate(accountDetails);
            var authorities = authorityRepository.findAllByUserId(account.uuid()).stream()
                    .map(UserAuthority::authority)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            return new LocalOidcUser(account, externalUser, authorities);
        };
    }

    UserAttributeDetails mapUserDetails(OidcUserRequest request, OidcUser user) {
        var provider = request.getClientRegistration().getRegistrationId();
        var displayName = switch (provider) {
            case "hytale" -> {
                var profile = HytaleProfile.fromClaim(user.getClaimAsMap("profile"));
                yield profile.username();
            }
            default -> "unknown";
        };

        return new UserAttributeDetails(
                provider,
                user.getSubject(),
                user.getEmail(),
                user.getEmailVerified(),
                displayName,
                null // we will set the avatar at a later stage if it exists
        );
    }
}
