package es.rust;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import es.rust.exceptions.RustException;
import es.rust.helper.UserRustHelper;
import es.rust.models.User;
import es.rust.security.RustAuthenticationEntryPoint;
import es.rust.security.RustLogoutSuccessHandler;
import es.rust.services.UserService;

@Configuration
public class OAuth2Configuration {

	protected static class CustomTokenEnhancer implements TokenEnhancer {

		@Autowired
		private HttpServletRequest request;
		@Autowired
		private UserService userService;

		@Override
		public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
			UserRustHelper helper = new UserRustHelper(request);
			User user = userService.findByUsernameAndPassword(helper.findDataFromRequest());
			if (user == null) {
				throw new UsernameNotFoundException("User doesn't exists in database");
			}
			Map<String, Object> additionalInfo = new HashMap<>();
			additionalInfo.put("userId", user.getId());
			additionalInfo.put("tokenExpiration", user.getExpirationTime());
			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
			return accessToken;
		}
	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Autowired
		private RustAuthenticationEntryPoint rustAuthenticationEntryPoint;

		@Autowired
		private RustLogoutSuccessHandler rustLogoutSuccessHandler;

		@Bean
		CorsConfigurationSource corsConfigurationSource() {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:4200", "http://localhost:4200"));
			configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE"));
			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", configuration);
			return source;
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {

			http.exceptionHandling().authenticationEntryPoint(rustAuthenticationEntryPoint).and().logout()
					.logoutUrl("/oauth/logout").logoutSuccessHandler(rustLogoutSuccessHandler).and().cors().and()
					.csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable()
					.headers().frameOptions().disable().and().authorizeRequests()
					.antMatchers(HttpMethod.OPTIONS, "/oauth/login").permitAll().antMatchers("/hello/").permitAll()
					.antMatchers("/secure/**").authenticated().and().sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		}

	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		private static final String PROP_CLIENTID = "rustUser";
		private static final String PROP_SECRET = "rustPassword";
		private static final Integer PROP_TOKEN_VALIDITY_SECONDS = 100;

		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;

		@Value("${jwt.secret}")
		private String secret;

		@Bean
		public JwtAccessTokenConverter accessTokenConverter() {
			if (secret == null) {
				throw new RustException("JWT secret can't be null, please define property jwt.secret on Application.yml");
			}
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			converter.setSigningKey(secret);
			return converter;
		}

		@Bean
		public TokenStore tokenStore() {
			return new InMemoryTokenStore();
		}

		@Bean
		public TokenEnhancer tokenEnhancer() {
			return new CustomTokenEnhancer();
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
			tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));

			endpoints.tokenStore(tokenStore()).tokenEnhancer(tokenEnhancerChain);
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory().withClient(PROP_CLIENTID).scopes("read", "write")
					.authorizedGrantTypes("password", "refresh_token", "client_credentials").secret(PROP_SECRET)
					.accessTokenValiditySeconds(PROP_TOKEN_VALIDITY_SECONDS);
		}
	}

}