package ru.bot.resolady.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yaml")
public class BotProperties {
    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }
}
