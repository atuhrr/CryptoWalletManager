package com.blockchain.wallet.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Crypto Wallet API")
                        .version("1.0")
                        .description("API for cryptocurrency wallet management"))
                .addTagsItem(new Tag().name("Wallets").description("Manage user wallets"))
                .addTagsItem(new Tag().name("Assets").description("Operations related to wallet assets"))
                .addTagsItem(new Tag().name("Evaluation").description("Evaluate portfolio performance"));
    }
}