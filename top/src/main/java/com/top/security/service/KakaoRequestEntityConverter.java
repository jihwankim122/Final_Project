package com.top.security.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;

import java.net.URI;
import java.util.Map;

public class KakaoRequestEntityConverter extends OAuth2AuthorizationCodeGrantRequestEntityConverter {

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest request) {
        RequestEntity<?> defaultRequestEntity = super.convert(request);
        HttpHeaders headers = new HttpHeaders();

        // Kakao의 BASIC 인증 헤더 설정
        headers.setBasicAuth(
                request.getClientRegistration().getClientId(),
                request.getClientRegistration().getClientSecret()
        );
        headers.setAll(defaultRequestEntity.getHeaders().toSingleValueMap());

        URI uri = defaultRequestEntity.getUrl();

        // 요청 파라미터 설정
        Map<String, String> params = Map.of(
                "code", request.getAuthorizationExchange().getAuthorizationResponse().getCode(),
                "grant_type", "authorization_code",
                "redirect_uri", request.getClientRegistration().getRedirectUri(),
                "client_id", request.getClientRegistration().getClientId(),
                "client_secret", request.getClientRegistration().getClientSecret()
        );

        return new RequestEntity<>(params, headers, HttpMethod.POST, uri);
    }
}
