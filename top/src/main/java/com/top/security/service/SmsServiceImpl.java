package com.top.security.service;

import com.top.security.dto.SmsRequestDto;
import com.top.security.util.SmsCertificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SmsServiceImpl implements SmsService {

    private final SmsCertificationUtil smsCertificationUtil;

    @Autowired
    public SmsServiceImpl(SmsCertificationUtil smsCertificationUtil) {
        this.smsCertificationUtil = smsCertificationUtil;
    }

    @Override
    public String sendSms(SmsRequestDto smsRequestDto) {
        String certificationCode = String.format("%06d", new Random().nextInt(1000000)); // 6자리 인증 코드
        smsCertificationUtil.sendSMS(smsRequestDto.getPhoneNum(), certificationCode);
        return certificationCode;
    }
}

