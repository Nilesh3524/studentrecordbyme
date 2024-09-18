package com.record.student.config;

import com.record.student.sevice.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    //Admin Security


    @Bean
    public UserDetailsService adminUserDetailsService(){
        UserDetails admin = User
                .builder()
                .username("himanshu")
                .password("{noop}ayush@123")
                .roles("ADMIN")
                .build();

        System.out.println("Admin username: " + admin.getUsername());
        System.out.println("Admin password: " + admin.getPassword());
        System.out.println("Admin authorities: " + admin.getAuthorities());

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .authorizeHttpRequests(auth->{
                    auth
                            .requestMatchers("/admin/login")
                            .permitAll()
                            .anyRequest()
                            .hasRole("ADMIN");
                })

                .formLogin(form->{

                    form
                            .loginPage("/admin/login")
                            .loginProcessingUrl("/admin/process-login")
                            .defaultSuccessUrl("/admin/dashboard",true)
                            .permitAll();
                })


                .logout(logout->{
                    logout
                            .logoutUrl("/admin/logout")
                            .logoutSuccessUrl("/admin/login?logout")
                            .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout","GET"))
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                            .permitAll();
                })

                .csrf(csrf->csrf.disable())

                .authenticationProvider(adminAuthenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminUserDetailsService());
        return provider;
    }


    //Student Security

    @Autowired
    private CustomUserDetailsService studentDetailsService;

    @Bean
    public PasswordEncoder studentPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain studentSecurityFilterChain(HttpSecurity http) throws Exception {
        http

                .securityMatcher("/student/**")
                .authorizeHttpRequests(auth->{
                    auth
                            .requestMatchers("/student/login")
                            .permitAll()
                            .anyRequest()
                            .hasRole("STUDENT");
                })


                .formLogin(form -> form
                        .loginPage("/student/login")
                        .loginProcessingUrl("/student/process-login")
                        .defaultSuccessUrl("/student/dashboard")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/student/logout")
                        .logoutSuccessUrl("/student/login?logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/student/logout","GET"))
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .authenticationProvider(studentAuthenticationProvider());

        return http.build();
    }


    @Bean
    public AuthenticationProvider studentAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(studentDetailsService);
        provider.setPasswordEncoder(studentPasswordEncoder());
        return provider;
    }

}
