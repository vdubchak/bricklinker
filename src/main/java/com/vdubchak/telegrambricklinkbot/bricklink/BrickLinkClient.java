package com.vdubchak.telegrambricklinkbot.bricklink;

import com.vdubchak.telegrambricklinkbot.bricklink.entity.AbstractBricklinkEntity;
import com.vdubchak.telegrambricklinkbot.bricklink.entity.BricklinkInfoEntity;
import com.vdubchak.telegrambricklinkbot.bricklink.entity.BricklinkPriceEntity;
import lombok.SneakyThrows;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.security.auth.message.AuthException;
import java.net.URI;

@Service
public class BrickLinkClient {

    private final RestTemplate restTemplate;

    public BrickLinkClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SneakyThrows
    @Retryable(value = {AuthException.class})
    public BricklinkPriceEntity getPrice(String set) {

        URI url = UriComponentsBuilder.fromHttpUrl("https://api.bricklink.com/api/store/v1/items/SET/" + set + "/price")
                .queryParam("region", "eu")
                .queryParam("new_or_used", "N")
                .build().toUri();

        BricklinkPriceEntity result =  restTemplate.getForEntity(url, BricklinkPriceEntity.class).getBody();
        checkResponse(result);
        return result;
    }

    @SneakyThrows
    @Retryable(value = {AuthException.class})
    public BricklinkPriceEntity getPrice(String set, String state){

        URI url = UriComponentsBuilder.fromHttpUrl("https://api.bricklink.com/api/store/v1/items/SET/" + set + "/price")
                                      .queryParam("region", "eu")
                                      .queryParam("new_or_used", state)
                                      .build().toUri();

        BricklinkPriceEntity result =  restTemplate.getForEntity(url, BricklinkPriceEntity.class).getBody();
        checkResponse(result);
        return result;
    }

    @SneakyThrows
    @Retryable(value = {AuthException.class})
    public BricklinkInfoEntity getInfo(String set) {

        URI url = UriComponentsBuilder.fromHttpUrl("https://api.bricklink.com/api/store/v1/items/SET/" + set)
                                      .build().toUri();
        BricklinkInfoEntity result = restTemplate.getForEntity(url, BricklinkInfoEntity.class).getBody();
        checkResponse(result);
        return result;
    }

    private void checkResponse(AbstractBricklinkEntity result) throws AuthException {
        if(result == null || result.getMeta() == null || result.getMeta().getCode() != 200) {
            throw new AuthException();
        }
    }
}
