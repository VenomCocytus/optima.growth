//package com.optimagrowth.commonlibrary.core.config;
//
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.DispatcherServlet;
//
//@Configuration
//public class WebConfig {
//
//    @Bean
//    public ServletRegistrationBean<DispatcherServlet> dispatcherServletServletRegistrationBean() {
//
//        ServletRegistrationBean<DispatcherServlet> dispatcherServletServletRegistrationBean =
//                new ServletRegistrationBean<>(new DispatcherServlet());
//
//        dispatcherServletServletRegistrationBean.addUrlMappings("/");
//        dispatcherServletServletRegistrationBean.setName("api");
//        dispatcherServletServletRegistrationBean.addInitParameter("throwExceptionIfNoHandlerFound", "true");
//
//        return dispatcherServletServletRegistrationBean;
//    }
//}
