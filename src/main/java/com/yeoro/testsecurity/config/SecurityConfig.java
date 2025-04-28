package com.yeoro.testsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login", "/loginProc", "/join", "/joinProc").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/my/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                );

        // http basic 사용을 위한 form login 주석처리
//        http
//                .formLogin((auth) -> auth.loginPage("/login")
//                        .loginProcessingUrl("/loginProc")
//                        .permitAll()
//                );

        // http basic → 브라우저 헤더에 username/password를 전달하여 로그인하는 방식
        // /admin 접근시 브라우저 팝업창에 로그인 정보 입력
        http
                .httpBasic(Customizer.withDefaults());

        // stateless, jwt 방식의 경우 세션이 없기 때문에 csrf 공격에 대한 부담이 없어 disable 처리
//        http
//                .csrf((auth) -> auth.disable());

        // csrf 설정시 logout은 POST 방식으로 요청해야하나, 아래 방식을 통해 GET 방식으로 요청 가능
        http
                .logout((auth) -> auth.logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                );

        http
                .sessionManagement((auth) -> auth
                        .maximumSessions(1) // 하나의 아이디에 대한 다중 로그인 허용 개수
                        .maxSessionsPreventsLogin(true) // 다중 로그인 개수를 초과했을 때 → true(새로운 로그인 차단) / false(기존 세션 삭제)
                );

        // 세션 고정 보호
        http
                .sessionManagement((auth) -> auth
                        .sessionFixation().changeSessionId()
                );


        return http.build();
    }

    // DB를 사용하지 않는 InMemory 방식, 간단한 토이프로젝트에 사용하면 좋을듯
//    @Bean
//    public UserDetailsService userDetailsService() {
//
//        UserDetails user1 = User.builder()
//                .username("user1")
//                .password(bCryptPasswordEncoder().encode("1234"))
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user2 = User.builder()s
//                .username("user2")
//                .password(bCryptPasswordEncoder().encode("1234"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1, user2);
//    }
}
