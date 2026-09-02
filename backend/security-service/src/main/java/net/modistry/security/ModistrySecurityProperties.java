package net.modistry.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("modistry.security.signing-key")
public record ModistrySecurityProperties(
        @NotNull Resource location,
        @NotBlank String password,
        @NotBlank String alias
) { }
