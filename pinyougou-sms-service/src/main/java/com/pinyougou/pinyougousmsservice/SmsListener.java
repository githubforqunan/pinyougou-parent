package com.pinyougou.pinyougousmsservice;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "pinyougou_sms")
    private void sendSms(String text){
        try {
            Map<String,String> map = JSON.parseObject(text,Map.class);
            System.out.println(map.get("templateParam"));
            System.out.println("--------->"+map.get("signName"));
            SendSmsResponse response = smsUtil.sendSms(map.get("phoneNumber"), map.get("signName"), map.get("templateCode"), map.get("templateParam"));
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());

        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
