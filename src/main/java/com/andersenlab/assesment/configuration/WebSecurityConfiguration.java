package com.andersenlab.assesment.configuration;

import com.andersenlab.assesment.dto.owner.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs*", "/v3/api-docs/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/owners").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/owners").hasRole(Role.ADMIN.name())
                        .requestMatchers("/v1/owners/search").hasRole(Role.ADMIN.name())
                        .requestMatchers("/v1/owners/roles").hasRole(Role.ADMIN.name())
                        .requestMatchers("/v1/breeds/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/v1/dogs/search").hasRole(Role.ADMIN.name())
                        .anyRequest().hasAnyRole(Role.USER.name(), Role.ADMIN.name()))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                ).build();
    }

    @Bean
    @SuppressWarnings("unchecked")
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = grantedAuthoritiesConverter.convert(jwt);
            List<String> roles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
            return Stream.concat(
                    authorities.stream(),
                    roles.stream().filter(role -> role.equals(Role.USER.name()) || role.equals(Role.ADMIN.name()))
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .map(GrantedAuthority.class::cast)
            ).toList();
        });
        return authenticationConverter;
    }
}
