package com.drivercar.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password("{noop}password")
                .roles("USER")
                .and()
                .withUser("admin")
                .password("{noop}admin")
                .roles("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers(HttpMethod.POST, "/v1/drivers/searches").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/v1/drivers/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/v1/drivers/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/v1/drivers/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/v1/drivers/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/v1/drivers/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/v1/cars/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/v1/cars/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/v1/cars/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/v1/cars/**").hasAnyRole("ADMIN", "USER")
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }
}