package com.dgut.config;

import com.dgut.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.LazyCsrfTokenRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.PrintWriter;

@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    final UserService userService;
    final ObjectMapper objectMapper;
    final DataSource dataSource;
    final FindByIndexNameSessionRepository<?> sessionRepository;

    /**
     * sessionRepository 注入报错，实际运行无问题，只能加上 @SuppressWarnings 消除警告
     */
    public SecurityConfig(UserService userService, ObjectMapper objectMapper, DataSource dataSource,
                          @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") FindByIndexNameSessionRepository<?> sessionRepository) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.dataSource = dataSource;
        this.sessionRepository = sessionRepository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Bean
    JdbcTokenRepositoryImpl jdbcTokenRepository() {
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        return repository;
    }

    @Bean
    SpringSessionBackedSessionRegistry<?> springSessionBackedSessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
    }

    //anyRequest只能放在最后配置
    //否则报错
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .successHandler(loginSuccessHandler())
                .failureHandler(loginFailureHandler())
                .permitAll()
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler())
                .permitAll()
                .and()
                .rememberMe()
                .tokenRepository(jdbcTokenRepository())
                .and()
                .csrf()
                .csrfTokenRepository(new LazyCsrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .and()
                .sessionManagement()
                //只允许一台设备在线
                .maximumSessions(1)
                .expiredSessionStrategy(event -> {
                    //被其他设备挤下线时的返回信息
                    final HttpServletResponse resp = event.getResponse();
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("utf-8");
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    PrintWriter out = resp.getWriter();
                    out.write(objectMapper.writeValueAsString("您已在另一台设备登录，当前设备已下线"));
                    out.flush();
                    out.close();
                })
                .and()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(noLoginExceptionHandler())
                .and();
    }

    //授权
    //admin 具有 user 的权限
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }

    /**
     * 未登录处理
     */
    private AuthenticationEntryPoint noLoginExceptionHandler() {
        return (req, resp, e) -> {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = resp.getWriter();
            out.write(objectMapper.writeValueAsString("尚未登录，请登录"));
            out.flush();
            out.close();
        };
    }

    /**
     * 注销成功处理
     */
    private LogoutSuccessHandler logoutSuccessHandler() {
        return (req, resp, authentication) -> {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            PrintWriter out = resp.getWriter();
            out.write(objectMapper.writeValueAsString("注销登录成功"));
            out.flush();
            out.close();
        };
    }

    /**
     * 登陆成功处理
     */
    private AuthenticationSuccessHandler loginSuccessHandler() {
        return (req, resp, authentication) -> {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            PrintWriter out = resp.getWriter();
            out.write(objectMapper.writeValueAsString(authentication));
            out.flush();
            out.close();
        };
    }

    /**
     * 登录失败处理
     */
    private AuthenticationFailureHandler loginFailureHandler() {
        return (req, resp, e) -> {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = resp.getWriter();
            //判断错误类型
            String message;
            if (e instanceof LockedException) {
                message = "帐户被锁定";
            } else if (e instanceof CredentialsExpiredException) {
                message = "密码过期";
            } else if (e instanceof AccountExpiredException) {
                message = "帐户过期";
            } else if (e instanceof DisabledException) {
                message = "帐户被禁用";
            } else if (e instanceof BadCredentialsException) {
                message = "用户名或密码输入错误";
            } else {
                message = e.getMessage();
            }
            out.write(objectMapper.writeValueAsString(message));
            out.flush();
            out.close();
        };
    }
}