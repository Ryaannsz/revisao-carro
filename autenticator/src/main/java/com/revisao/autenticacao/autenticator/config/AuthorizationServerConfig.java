package com.revisao.autenticacao.autenticator.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class AuthorizationServerConfig {

    @Value("${uri.api.auth-server}")
    private String uriAuthServer;

    // 1. SecurityFilterChain para o Authorization Server
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
	OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
	http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(Customizer.withDefaults()); // OpenID Connect

	// Cata exceção de sem token
	http.exceptionHandling(
		exceptions -> exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));
	return http.build();
    }

    // 2. SecurityFilterChain para a aplicação (autenticação de usuário)
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
	http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
		.formLogin(Customizer.withDefaults());
	return http.build();
    }

    // 3. Gerenciamento de Clientes Registrados (BFF neste caso)
    // Usamos JdbcRegisteredClientRepository para persistir clientes no DB
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
	JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

	// Verifique se o cliente já existe antes de salvá-lo para evitar duplicatas
	if (registeredClientRepository.findByClientId("bff-client") == null) {
	    RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
		    .clientId("bff-client") // ID do cliente para o BFF
		    .clientSecret("{noop}secret") // sem enconder para dev
		    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
		    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
		    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
		    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
		    .redirectUri("http://127.0.0.1:8080/authorized") // URI de redirecionamento do BFF
		    .scope(OidcScopes.OPENID).scope(OidcScopes.PROFILE).scope("car.read").scope("car.write")
		    .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
		    .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(5))
			    .refreshTokenTimeToLive(Duration.ofHours(1)).reuseRefreshTokens(false).build())
		    .build();

	    registeredClientRepository.save(registeredClient);
	}
	return registeredClientRepository;
    }

    // 4. JWKSource para assinar os JWTs
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
	KeyPair keyPair = generateRsaKey();
	RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
	RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
	RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString())
		.build();
	JWKSet jwkSet = new JWKSet(rsaKey);
	return new ImmutableJWKSet<>(jwkSet);
    }

    // Gera um par de chaves RSA
    private static KeyPair generateRsaKey() {
	KeyPair keyPair;
	try {
	    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	    keyPairGenerator.initialize(2048);
	    keyPair = keyPairGenerator.generateKeyPair();
	} catch (Exception ex) {
	    throw new IllegalStateException(ex);
	}
	return keyPair;
    }

    // 5. Configurações do Authorization Server
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
	return AuthorizationServerSettings.builder().issuer(uriAuthServer) // URL do seu Authorization Server
		.build();
    }

    // 6. Persistência de Autorizações (tokens)
    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate,
	    RegisteredClientRepository registeredClientRepository) {
	return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }
}
