package com.freelycar.saas.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author tangwei [toby911115@gmail.com]
 * @date 2018/9/27
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    // 设置 HTTP 验证规则
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭csrf验证
        http.csrf().disable()
                // 对请求进行认证
                .authorizeRequests()
                // 所有 / 的所有请求 都放行
                .antMatchers("/").permitAll()
                // 所有 /login 的POST请求 都放行
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                //放行Swagger2页面
                .antMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v2/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/swagger-ui.html/**").permitAll()
                //放行Druid监控页面
                .antMatchers("/druid/**").permitAll()
                //放行与iotcloudcn通信的接口
//                .antMatchers("/iot/**").permitAll()
                //放行公司宣传网站需要的接口
                .antMatchers("/webapi/**").permitAll()
                //放行微信公众号SDK接口
                .antMatchers("/wechat/config/**").permitAll()
                //放行微信支付接口
                .antMatchers("/wechat/pay/**").permitAll()
                //放行微信登录相关接口
                .antMatchers("/wechat/login/**").permitAll()
                .antMatchers("/wechat/employee/**").permitAll()
                .antMatchers("/wechat/staff/login").permitAll()
                .antMatchers("/wechat/staff/logout").permitAll()
                // 上传接口
                .antMatchers("/upload/**").permitAll()
                //测试请求mobile
                .antMatchers(HttpMethod.GET, "/mobile/**").hasRole("ADMIN")
                // 添加权限检测
                .antMatchers("/hello").hasAuthority("AUTH_WRITE")
                // 角色检测
                .antMatchers("/world").hasRole("ADMIN")
                // 所有请求需要身份认证
                .anyRequest().authenticated()
                .and()
                // 添加一个过滤器 所有访问 /login 的请求交给 JWTLoginFilter 来处理 这个类处理所有的JWT相关内容
                .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                // 添加一个过滤器验证其他请求的Token是否合法
                .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用自定义身份验证组件
        auth.authenticationProvider(this.customAuthenticationProvider());

    }
}
