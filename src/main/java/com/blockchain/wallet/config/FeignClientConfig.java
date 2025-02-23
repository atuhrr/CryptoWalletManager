package com.blockchain.wallet.config;

import feign.Retryer;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(1000, 5000, 3);
    }
    
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(2000, 5000);
    }
}
