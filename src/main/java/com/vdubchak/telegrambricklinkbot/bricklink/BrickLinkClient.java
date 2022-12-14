package com.vdubchak.telegrambricklinkbot.bricklink;

import com.vdubchak.telegrambricklinkbot.bricklink.entity.AbstractBricklinkEntity;
import com.vdubchak.telegrambricklinkbot.bricklink.entity.BricklinkHistoricPriceEntity;
import com.vdubchak.telegrambricklinkbot.bricklink.entity.BricklinkInfoEntity;
import com.vdubchak.telegrambricklinkbot.bricklink.entity.BricklinkPriceEntity;
import com.vdubchak.telegrambricklinkbot.bricklink.enums.Condition;
import com.vdubchak.telegrambricklinkbot.bricklink.enums.GuideType;
import com.vdubchak.telegrambricklinkbot.bricklink.enums.ItemType;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.security.auth.message.AuthException;
import java.net.URI;

@Service
public class BrickLinkClient {

    @Value("${bricklink.api_base_url}")
    private String apiBaseUrl;
    private final RestTemplate restTemplate;

    public BrickLinkClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @SneakyThrows
    @Retryable(value = {AuthException.class})
    public BricklinkPriceEntity getPrice(ItemType type, String number, Condition state){

        URI url = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/items/" + type + "/" + number + "/price")
                                      .queryParam("region", "europe")
                                      .queryParam("new_or_used", state)
                                      .queryParam("guide_type", GuideType.STOCK)
                                      .build().toUri();

        BricklinkPriceEntity result =  restTemplate.getForEntity(url, BricklinkPriceEntity.class).getBody();
        checkResponse(result);
        return result;
    }

    @SneakyThrows
    @Retryable(value = {AuthException.class})
    public BricklinkHistoricPriceEntity getPriceHistory(ItemType type, String number, Condition state){

        URI url = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/items/" + type + "/" + number + "/price")
                                      .queryParam("region", "europe")
                                      .queryParam("new_or_used", state)
                                      .queryParam("guide_type", GuideType.SOLD)
                                      .build().toUri();

        BricklinkHistoricPriceEntity result =  restTemplate.getForEntity(url, BricklinkHistoricPriceEntity.class).getBody();
        checkResponse(result);
        return result;
    }

    @SneakyThrows
    @Retryable(value = {AuthException.class})
    public BricklinkInfoEntity getInfo(ItemType type, String number) {

        URI url = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/items/" + type + "/" + number)
                                      .build().toUri();
        BricklinkInfoEntity result = restTemplate.getForEntity(url, BricklinkInfoEntity.class).getBody();
        checkResponse(result);
        return result;
    }

    private void checkResponse(AbstractBricklinkEntity result) throws AuthException {
        if(result == null || result.getMeta() == null || (result.getMeta().getCode() >= 400 && result.getMeta().getCode() < 404)) {
            throw new AuthException();
        } else if (result.getMeta().getCode() == 404 ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cant find item");
        }
    }
}
