package com.pretender.myApp.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class ForwardedHeaderConfig {

  @Bean
  public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
    ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
    FilterRegistrationBean<ForwardedHeaderFilter> bean = new FilterRegistrationBean<>(filter);
    // SecurityFilterChain 보다 먼저 실행되도록 최우선 순서
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
  }
}