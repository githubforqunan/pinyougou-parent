package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.utils.PhoneFormatCheckUtils;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.DigestUtils;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;



@Service
public class UserServiceImpl implements UserService {

    @Value("${signName}")
    private String signName;
    @Value("${templateCode}")
    private String templateCode;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private Destination queueSmsDestination;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void register(TbUser user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        userMapper.insert(user);
    }

    @Override
    public boolean checkCode(String smsCode,String phone) {
        String code = (String) redisTemplate.boundHashOps("code").get(phone);
        System.out.println("code--------->"+code);
        if(code!=null && code.equals(smsCode)){

            return true;
        }
        return false;
    }

    @Override
    public boolean sendCode(String phone) {
        boolean isLegal = PhoneFormatCheckUtils.isChinaPhoneLegal(phone);
        if(!isLegal){
            return false;
        }
        createCode(phone);
        return true;
    }

    private void createCode(final String phone) {
        Random random = new Random();
        final String code = String.format("%06d",random.nextInt(1000000));
        System.out.println(code);
        redisTemplate.boundHashOps("code").put(phone,code);
        jmsTemplate.send(queueSmsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //String phoneNumber,String signName,String templateCode,String templateParam
                Map<String,String> map = new HashMap<>();
                map.put("phoneNumber",phone);
                map.put("signName",signName);


                map.put("templateCode",templateCode);
                map.put("templateParam","{\"code\":\""+code+"\"}");
                return session.createTextMessage(JSON.toJSONString(map));
            }
        });
    }


}
