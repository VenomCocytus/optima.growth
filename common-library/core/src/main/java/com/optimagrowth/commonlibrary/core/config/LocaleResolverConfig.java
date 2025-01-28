package com.optimagrowth.commonlibrary.core.config;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;
import java.util.TimeZone;

@NonNullApi
@Configuration
public class LocaleResolverConfig implements WebMvcConfigurer {

    /**
     * ReloadableResourceBundleMessageSource is the most common MessageSource implementation that
     * resolves messages from resource bundles for different locales.
     *
     * @return The MessageSource instance
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource resourceBundleMessageSource =  new ResourceBundleMessageSource();
        resourceBundleMessageSource.setUseCodeAsDefaultMessage(true);
        resourceBundleMessageSource.setAlwaysUseMessageFormat(true);
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setBasename("messages");
        resourceBundleMessageSource.setCacheSeconds(5);

        return resourceBundleMessageSource;
    }

    /**
     * To use custom name messages in a properties file like we need to define a
     * LocalValidatorFactoryBean and register the messageSource.
     * Example:
     * &#64;NotEmpty(message = "{email.nonempty}")
     * &#64;Email
     * private String email;
     *
     * @return The LocalValidatorFactoryBean instance
     */
    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean =  new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource());

        return localValidatorFactoryBean;
    }

    /**
     * In order for our application to be able to determine which locale is currently being used.
     *
     * @return -
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        sessionLocaleResolver.setDefaultTimeZone(TimeZone.getTimeZone("UTC"));

        return sessionLocaleResolver;
    }

    /**
     * Add an interceptor bean that will switch to a new locale based on the value of the "lang" query
     * string parameter appended to a request.
     * Eg: /entities/27?lang=pl
     * Note: To take effect, this bean needs to be added to the application's interceptor
     * registry
     *
     * @return -
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");

        return localeChangeInterceptor;
    }

    /**
     * Register the LocaleChangeInterceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
