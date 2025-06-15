package org.smarthire.AUTH_SERVICE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan("org.smarthire.AUTH_SERVICE.MODELS")
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
public class AuthServiceApplication implements ApplicationListener<WebServerInitializedEvent> {

	private static final Logger log = LoggerFactory.getLogger(AuthServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {

		int port = event.getWebServer().getPort();
		log.warn("\uD83D\uDE80 Application started on port:  {}", port);
	}
}
