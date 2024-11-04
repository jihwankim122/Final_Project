package com.top.security.service;

import com.top.security.dto.SmsRequestDto;

public interface SmsService {
    String sendSms(SmsRequestDto smsRequestDto);
}
