package ru.gb.antonov.j71.beans.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.gb.antonov.j71.beans.services.OurUserService;

@EnableWebSecurity  //< «включатель» правил безопасности, описанных в нижеописанном классе
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    private final OurUserService   ourUserService;
    private final JwtRequestFilter jwtRequestFilter;


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean () throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder()  {   return new BCryptPasswordEncoder();   }

    @Override
    protected void configure (HttpSecurity httpSec) throws Exception
    {
        httpSec.csrf().disable()
               .authorizeRequests()
               .antMatchers ("/h2_console/**").permitAll()
               .and()
               .sessionManagement().sessionCreationPolicy (SessionCreationPolicy.STATELESS)
               .and()
               .headers().frameOptions().disable()
               .and()
               .exceptionHandling()
               .authenticationEntryPoint (new HttpStatusEntryPoint (HttpStatus.UNAUTHORIZED))
               ;
        httpSec.addFilterBefore (jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
