package com.top.security.util;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsCertificationUtil {

    @Value("${coolsms.apikey}")
    private String apiKey;

    @Value("${coolsms.apisecret}")
    private String apiSecret;

    @Value("${coolsms.fromnumber}")
    private String fromNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        // 주입된 값 확인을 위한 로그 출력
        System.out.println("API Key: " + apiKey);
        System.out.println("API Secret: " + apiSecret);
        System.out.println("From Number: " + fromNumber);

        // 값이 정상적으로 주입된 경우 메시지 서비스 초기화
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendSMS(String to, String certificationCode) {
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(to);
        message.setText("인증번호: " + certificationCode);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
