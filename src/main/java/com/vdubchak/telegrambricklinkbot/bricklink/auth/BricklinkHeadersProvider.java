package com.vdubchak.telegrambricklinkbot.bricklink.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class BricklinkHeadersProvider implements ClientHttpRequestInterceptor {
    public static final String TIMESTAMP = "oauth_timestamp";
    public static final String SIGN_METHOD = "oauth_signature_method";
    public static final String SIGNATURE = "oauth_signature";
    public static final String CONSUMER_SECRET = "oauth_consumer_secret";
    public static final String CONSUMER_KEY = "oauth_consumer_key";
    public static final String HEADER_NAME = "Authorization";
    public static final String VERSION = "oauth_version";
    public static final String NONCE = "oauth_nonce";
    public static final String TOKEN = "oauth_token";
    public static final String TOKEN_SECRET = "oauth_token_secret";

    private static final String HMAC_SHA1 = "HmacSHA1";
    private final String signMethod = "HMAC-SHA1";
    private final String version = "1.0";

    private String url;
    @Value("${bricklink.auth.consumer_key}")
    private String consumerKey;
    @Value("${bricklink.auth.access_token}")
    private String token;
    @Value("${bricklink.auth.consumer_secret}")
    private String consumerSecret;
    @Value("${bricklink.auth.token_secret}")
    private String tokenSecret;
    private Map<String, String> oauthParameters;
    private Map<String, String> params;
    private String verb;
    private Timer timer;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            this.timer = new Timer();
            url= request.getURI().toString();
            setVerb(request);
            params = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().toSingleValueMap();
            request.getHeaders().add(HEADER_NAME, getOauthHeader());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return execution.execute(request, body);
    }

    private String getOauthHeader() throws Exception {
        StringBuilder stringBuilder = new StringBuilder("Oauth ");
        getFinalOAuthParams().forEach((name, value) -> stringBuilder.append(name).append("=\"").append(value).append("\",") );
        if(stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        }

        return stringBuilder.toString();
    }
    private Map<String, String> getFinalOAuthParams() throws Exception {
        oauthParameters = new TreeMap<>();
        String signature = computeSignature();

        Map<String, String> params = new TreeMap<>();
        params.putAll(oauthParameters);
        params.put(SIGNATURE, signature);

        return params;
    }

    private void setVerb(HttpRequest request) {
        verb = request.getMethodValue();
    }

    private String computeSignature() {
        addOAuthParameter(VERSION, version);
        addOAuthParameter(TIMESTAMP, getTimestampInSeconds());
        addOAuthParameter(NONCE, getNonce());
        addOAuthParameter(TOKEN, token);
        addOAuthParameter(CONSUMER_KEY, consumerKey);
        addOAuthParameter(SIGN_METHOD, signMethod);
        Map<String, String> params = new HashMap<>();
        params.putAll(oauthParameters);
        params.putAll(this.params);
        Map<String, String> sortedParams = params.entrySet().stream().sorted(Map.Entry.comparingByKey())
                                                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        StringBuilder base = new StringBuilder();
        sortedParams.forEach((key, value) -> base.append(encode(key)).append("=").append(encode(value)).append("&"));
        base.deleteCharAt(base.length() - 1);
        String baseString = verb + "&" + encode(normalizeUrl(url)) + "&" + encode(base.toString());

        String keyString = encode(consumerSecret) + '&' + encode(tokenSecret);
        return doSign(baseString, keyString);
    }

    private String normalizeUrl(String url) {
        int iend = url.indexOf("?"); //this finds the first occurrence of "."
        String normalizedUrl;
        if (iend != -1) {
            normalizedUrl= url.substring(0 , iend); //this will give abc
        } else  {
            normalizedUrl = url;
        }
        return normalizedUrl;
    }
    private void addOAuthParameter(String key, String value) {
        oauthParameters.put(key, value);
    }

    private String getTimestampInSeconds() {
        Long ts = timer.getMilis();
        return String.valueOf(TimeUnit.MILLISECONDS.toSeconds(ts));
    }

    private String getNonce() {
        Long ts = timer.getMilis();
        return String.valueOf(ts + Math.abs(timer.getRandomInteger()));
    }

    private String doSign(String toSign, String keyString) {
        addOAuthParameter(CONSUMER_SECRET, consumerSecret);
        addOAuthParameter(TOKEN_SECRET, tokenSecret);
        byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
        SecretKey key = new SecretKeySpec(keyBytes, HMAC_SHA1);
        Mac mac;
        try {
            mac = Mac.getInstance(HMAC_SHA1);
            mac.init(key);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
        byte[] signatureBytes = mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8));
        return new String(java.util.Base64.getEncoder().encode(signatureBytes));
    }

    private String encode(String value) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                sb.append("%2A");
            } else if (focus == '+') {
                sb.append("%20");
            } else if (focus == '%' && i + 1 < encoded.length() && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                sb.append('~');
                i += 2;
            } else {
                sb.append(focus);
            }
        }
        return sb.toString();
    }

    static class Timer {
        private final Random rand = new Random();

        Long getMilis() {
            return System.currentTimeMillis();
        }

        Integer getRandomInteger() {
            return rand.nextInt();
        }
    }
}
