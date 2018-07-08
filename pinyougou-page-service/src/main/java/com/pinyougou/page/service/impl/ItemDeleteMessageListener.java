package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.File;

public class ItemDeleteMessageListener implements MessageListener {


    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage tm = (ObjectMessage) message;
            Long ids[] = (Long[]) tm.getObject();
            itemPageService.deleteItemHtml(ids);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
