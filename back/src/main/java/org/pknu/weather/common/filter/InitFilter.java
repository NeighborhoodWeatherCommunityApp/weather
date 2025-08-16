package org.pknu.weather.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InitFilter implements Filter {

    private static String URL = "/health-check";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // 특정 URL 패턴 무시
        if (requestURI.startsWith(URL)) {
            chain.doFilter(request, response);
            return;
        }

        // 필터 로직 수행
        System.out.println("Filtering request: " + requestURI);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}