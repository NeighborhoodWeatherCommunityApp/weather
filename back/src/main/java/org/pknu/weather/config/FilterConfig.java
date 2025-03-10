package org.pknu.weather.config;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.filter.InitFilter;
import org.pknu.weather.filter.RefreshTokenFilter;
import org.pknu.weather.filter.GenerateTokenFilter;
import org.pknu.weather.filter.TokenCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final GenerateTokenFilter generateTokenFilter;
    private final TokenCheckFilter tokencheckFilter;
    private final RefreshTokenFilter refreshTokenFilter;
    private final InitFilter initFilter;

    @Bean
    public FilterRegistrationBean<InitFilter> initFilterRegister() {
        FilterRegistrationBean<InitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(initFilter);
        registrationBean.addUrlPatterns("/health-check");
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RefreshTokenFilter> refreshTokenFilterRegister() {
        FilterRegistrationBean<RefreshTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(refreshTokenFilter);
        registrationBean.addUrlPatterns("/refreshToken");
        registrationBean.setOrder(1);
        return registrationBean;
    }


    @Bean
    public FilterRegistrationBean<GenerateTokenFilter> socialLoginFilterRegister() {
        FilterRegistrationBean<GenerateTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(generateTokenFilter);
        registrationBean.addUrlPatterns("/token");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TokenCheckFilter> tokenCheckFilterRegister() {
        FilterRegistrationBean<TokenCheckFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(tokencheckFilter);
        registrationBean.addUrlPatterns("/api/*", "/actuator/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }

}

