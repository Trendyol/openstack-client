package com.trendyol.openstack.client.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@ComponentScan(basePackages = {"com.trendyol.openstack.client"})
@AutoConfiguration
@EnableAsync
@EnableScheduling
@EnableRetry
@RequiredArgsConstructor
public class OpenstackClientAutoConfiguration {
    private final DefaultOpenStackProperties openStackProperties;

    @Bean(name = "openstackRestTemplate")
    RestTemplate openstackRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(openStackProperties.getWebClient().getConnectTimeoutMillis());
        simpleClientHttpRequestFactory.setReadTimeout(openStackProperties.getWebClient().getReadTimeoutMillis());

        RestTemplate build = restTemplateBuilder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory))
                .build();
        build.getMessageConverters().add(0, converter);
        return build;
    }
}
