package com.vdubchak.telegrambricklinkbot.configuration;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

/**
 * This was implemented to stop Heroku from overwriting app port, and using port provided by heroku
 * for Javalin server instead.
 */
@Component
public class ServerCustomizer
        implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(8086);
    }
}
