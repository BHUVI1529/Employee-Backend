package com.example.employeeAtt.security;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//import io.jsonwebtoken.lang.Arrays;
import java.util.Arrays;


@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
 public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                //.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // .cors()
                // .and()  // 👈 Enable CORS here
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login" ,"/api/auth/employees","/api/auth/employees/count","/api/auth/forgot-password","/api/auth/reset-password").permitAll()
                        //.requestMatchers(HttpMethod.GET, "/api/auth/employees/count").hasAuthority("ADMIN")
                        
                        //.requestMatchers(HttpMethod.GET, "/api/auth/employees/count").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,"/api/attendance/getAll","/api/attendance/by-date","/api/attendance/duration","/api/attendance/by-month","/api/admin/absentees/today","api/admin/absentees").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/admin/absentees","/attendance/absentees" ).permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/auth/update/{employeeId}").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/attendance/count","/api/attendance/present-today").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/api/auth/delete/{employeeId}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/attendance/mark").hasAnyRole("TEACHING", "NON_TEACHING")
                        .requestMatchers("/api/attendance/getAll").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/admin/weekly-report").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/institute/id").permitAll()
                        .requestMatchers(HttpMethod.POST,  "/api/leave/apply","/api/leave/All").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/leave/").hasAnyRole("TEACHING", "NON_TEACHING")
                        .requestMatchers(HttpMethod.GET,"/api/attendance/count/today","/api/attendance/absent-today").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    //     return new WebMvcConfigurer() {
    //         @Override
    //         public void addCorsMappings(CorsRegistry registry) {
    //             registry.addMapping("/**")
    //                 .allowedOrigins("https://employeeattendance.vercel.app")
    //                 //.allowedOrigins("http://localhost:3000")
    //                 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    //                 .allowedHeaders("*")
    //                 .allowCredentials(true);
    //         }
    //     };
    // }

        @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://employeeattendance.vercel.app"));
        //configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}

