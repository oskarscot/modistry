package net.modistry.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import net.modistry.security.identity.AccountProvisionService;
import net.modistry.security.identity.AuthorityRepository;
import net.modistry.security.identity.UserAuthority;
import net.modistry.security.identity.entity.UserAttributeDetails;
import net.modistry.security.identity.internal.LocalOidcUser;
import net.modistry.security.identity.type.HytaleProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

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
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .oauth2AuthorizationServer(authorizationServer -> {
                    http.securityMatcher(authorizationServer.getEndpointsMatcher());
                    authorizationServer.oidc(withDefaults());
                })
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/hytale"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                .build();
    }

    @Bean
    @Order(2)
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

            var authorities = new ArrayList<GrantedAuthority>(authorityRepository.findAllByUserId(account.uuid()).stream()
                    .map(UserAuthority::authority)
                    .map(SimpleGrantedAuthority::new)
                    .toList());

            authorities.add(FactorGrantedAuthority.fromAuthority(FactorGrantedAuthority.AUTHORIZATION_CODE_AUTHORITY));

            return new LocalOidcUser(account, externalUser, authorities);
        };
    }

    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9000")
                .build();
    }

    @Bean
    JWKSource<SecurityContext> jwkSource(ModistrySecurityProperties properties) {
        try {
            var keyStore = KeyStore.getInstance("PKCS12");

            try(var input = properties.location().getInputStream()) {
                keyStore.load(input, properties.password().toCharArray());
            }

            var privateKey = (RSAPrivateKey) keyStore.getKey(
              properties.alias(),
              properties.password().toCharArray()
            );

            var certificate = keyStore.getCertificate(properties.alias());
            var publicKey = (RSAPublicKey) certificate.getPublicKey();

            var rsaKey = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(properties.alias())
                    .build();

            return new ImmutableJWKSet<>(new JWKSet(rsaKey));
        } catch (GeneralSecurityException | IOException exception) {
            throw new IllegalStateException("Could not load signing key", exception);
        }
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    RegisteredClientRepository registeredClientRepository(@Value("${MODISTRY_GATEWAY_CLIENT_SECRET_HASH}") String clientSecretHash) {
        var gateway = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("modistry-gateway")
                .clientSecret(clientSecretHash)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:8081/login/oauth2/code/modistry")
                .postLogoutRedirectUri("http://localhost:8081/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .build()
                )
                .build();

        return new InMemoryRegisteredClientRepository(gateway);
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
                user.getPicture()
        );
    }
}
