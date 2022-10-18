package com.vdubchak.telegrambricklinkbot.bricklink;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class BrickLinkClient {

    private final RestTemplate restTemplate;

    public BrickLinkClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getPrice(String set) {

        URI url = UriComponentsBuilder.fromHttpUrl("https://api.bricklink.com/api/store/v1/items/SET/" + set + "/price")
                .queryParam("region", "eu")
                .queryParam("new_or_used", "N")
                .build().toUri();

        return restTemplate.getForEntity(url, String.class).getBody();
    }

    public String getPrice(String set, String state) {

        URI url = UriComponentsBuilder.fromHttpUrl("https://api.bricklink.com/api/store/v1/items/SET/" + set + "/price")
                                      .queryParam("region", "eu")
                                      .queryParam("new_or_used", state)
                                      .build().toUri();

        return restTemplate.getForEntity(url, String.class).getBody();
    }

    public String getInfo(String set) {

        URI url = UriComponentsBuilder.fromHttpUrl("https://api.bricklink.com/api/store/v1/items/SET/" + set)
                                      .build().toUri();

        return restTemplate.getForEntity(url, String.class).getBody();
    }

}
