package com.blockchain.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.blockchain.wallet.client")
@EnableScheduling
public class CryptoWalletManagerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CryptoWalletManagerApplication.class, args);
	}

}
