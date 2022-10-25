package com.vdubchak.telegrambricklinkbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
@EnableRetry
@Slf4j
public class TelegramBricklinkBotApplication {

	private final RestTemplate restTemplate;

	@Value("${bricklink_bot.token}")
	private String token;
	@Value("${bricklink_bot.url}")
	private Optional<String> url;

	public TelegramBricklinkBotApplication(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}


	public static void main(String[] args) {
		SpringApplication.run(TelegramBricklinkBotApplication.class, args);
	}

	/**
	 * Temp solution to register 'fake' webhook on telegram, because starter automatically deletes existing on shutdown.
	 * So when heroku puts app to sleep user requests can still 'wake' it up.
	 */
	@PreDestroy
	public void registerPseudoHook() {
		if(url.isPresent()) {
			String registerHookUrl = "https://api.telegram.org/bot" + token + "/setWebhook?url=" + url.get() + "/" + UUID.randomUUID();
			restTemplate.getForObject(registerHookUrl, Object.class);
			log.info("Setting fake webhook for program wake-up calls.");
		}
	}

}
