package com.highpass.runspot.common.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MultipartJackson2HttpMessageConverter multipartConverter;

    public WebConfig(MultipartJackson2HttpMessageConverter multipartConverter) {
        this.multipartConverter = multipartConverter;
    }

    @Override
    public void extendMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        converters.add(0, multipartConverter);
    }
}