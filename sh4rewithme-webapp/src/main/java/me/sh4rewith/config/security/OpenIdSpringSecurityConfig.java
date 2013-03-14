package me.sh4rewith.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("openid-security")
@ImportResource("classpath:spring-security-openid.xml")
public class OpenIdSpringSecurityConfig {
}