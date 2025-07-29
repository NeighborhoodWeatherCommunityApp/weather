package org.pknu.weather.config;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.filter.InitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {
    private final InitFilter initFilter;

    @Bean
    public FilterRegistrationBean<InitFilter> initFilterRegister() {
        FilterRegistrationBean<InitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(initFilter);
        registrationBean.addUrlPatterns("/health-check");
        registrationBean.setOrder(0);
        return registrationBean;
    }
}

