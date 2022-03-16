package de.thkoeln.syp.team17.backend.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import javax.servlet.Filter;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new CookieCsrfTokenRepository();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProviderImpl();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandlerImpl();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandlerImpl();
    }

    @Bean
    public Filter authenticationFilter() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return filter;
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandlerImpl();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(false);
        rememberMeServices.setRememberMeParameterName("rememberMe");
        return rememberMeServices;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Configure CSRF:
        // * CSRF tokens are read from the HTTP header X-XSRF-Token
        // * CSRF tokens are persistent in a cookie named XSRF Token, which is accessible via JavaScript
        // * CSRF protection is disabled for the /devices/authenticate and /devices/acl-check endpoints used by mosquitto go auth.
        http.csrf()
            .csrfTokenRepository(csrfTokenRepository())
            .ignoringAntMatchers("/devices/authenticate")
            .ignoringAntMatchers("/devices/acl-check");

        // Add a filter, which will extract an AuthenticationToken from the request body in login requests.
        http.addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Configure logout behaviour.
        http.logout()
            .logoutUrl("/auth/logout")
            .logoutSuccessHandler(logoutSuccessHandler());

        // Enable remember me functionality
        http.rememberMe().rememberMeServices(rememberMeServices());

        // Configure access policies.
        http.authorizeRequests()
            .antMatchers(HttpMethod.GET, "/auth/csrf").permitAll()
            .antMatchers(HttpMethod.POST, "/auth/login").permitAll()
            .antMatchers(HttpMethod.POST, "/auth/register").permitAll()
            .antMatchers(HttpMethod.GET,"/swagger-ui.html").permitAll()
            .antMatchers(HttpMethod.GET,"/swagger-ui/**").permitAll()
            .antMatchers(HttpMethod.GET,"/v3/api-docs/**").permitAll()
            .antMatchers(HttpMethod.POST, "/auth/verify").authenticated()
            .antMatchers("/auth/me").authenticated()
            .antMatchers(HttpMethod.POST, "/devices/authenticate").permitAll()
            .antMatchers(HttpMethod.POST, "/devices/acl-check").permitAll()
            .antMatchers("/users/**").hasRole("SYSADMIN")
            .anyRequest().hasRole("VERIFIED");

        // Handle AccessDeniedException (the authenticated user is missing a required authority).
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());

        // Handle AuthenticationException (there is no user authenticated).
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
    }

}
