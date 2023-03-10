package com.example.chat2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@EnableWebMvc
@Configuration//설정파일이라는 것을 알려줌
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//cors를 적용할 URL패턴 정의
//                .allowedOrigins("http://localhost:8080")
//                .allowedOrigins("http://localhost:3000", "http://dabinweb.shop.s3-website.ap-northeast-2.amazonaws.com/")//자원 공유 허락할 Origin 허락
                .allowedOriginPatterns("*")//자원 공유 허락할 Origin 허락
                .allowedMethods("*")//허락할 HTTP method 지정
                .allowCredentials(true)
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("/webjars/");
    }

}

