package kara.spotifyassistant.security;

import kara.spotifyassistant.appuser.AppUserService;
import kara.spotifyassistant.security.auth.apikey.ApiKeyAuthenticationEntryPoint;
import kara.spotifyassistant.security.auth.apikey.PublicKeyRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ApiKeyAuthenticationEntryPoint keyAuthenticationEntryPoint;

    @Autowired
    private PublicKeyRequestFilter keyRequestFilter;

    @Autowired
    private AppUserService appUserService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .mvcMatchers("/", "/public/**", "/auth/**", "/css/*.css")
                .permitAll()
                .antMatchers("/", "/public/**", "/auth/**", "/css/*.css")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(keyAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(keyRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
