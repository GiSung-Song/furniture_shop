package furniture.shop.configure;

import furniture.shop.configure.jwt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class SpringSecurity {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final PrincipalDetailsService principalDetailsService;
    private final RedisTemplate<String, String> redisTemplate;
    private final CustomLogoutHandler logoutHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomUsernameAuthenticationFilter customUsernameAuthenticationFilter() {
        CustomUsernameAuthenticationFilter customUsernameAuthenticationFilter = new CustomUsernameAuthenticationFilter(authenticationManager(), tokenProvider);

        customUsernameAuthenticationFilter.setAuthenticationManager(authenticationManager());

        return customUsernameAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(principalDetailsService);

        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring()
                    .requestMatchers(HttpMethod.POST, "/join", "/login")
                    .requestMatchers(HttpMethod.GET, "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/login");
        };
    }

    @Bean
    public SecurityFilterChain filterChainAPI(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests((request) ->
                        request.requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**",
                                        "/api-docs", "/api-docs/**", "/v3/api-docs/**", "/").permitAll()
                                .requestMatchers("/join").permitAll()
                                .requestMatchers("/actuator/prometheus").permitAll()
                                .requestMatchers(HttpMethod.GET, "/product").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/product/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/product").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .csrf(CsrfConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .exceptionHandling((exception) ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler))
                .headers((headers) ->
                        headers.frameOptions(option -> option.sameOrigin()))
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout((logout) ->
                        logout.logoutUrl("/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext())))
                .addFilterBefore(new JwtFilter(redisTemplate, tokenProvider), CustomUsernameAuthenticationFilter.class)
                .addFilterAfter(customUsernameAuthenticationFilter(), LogoutFilter.class);

        return httpSecurity.build();
    }

}
