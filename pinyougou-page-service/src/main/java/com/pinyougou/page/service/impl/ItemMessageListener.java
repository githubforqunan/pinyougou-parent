package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class ItemMessageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage tm = (ObjectMessage) message;
            Long ids[] = (Long[]) tm.getObject();
            for (Long id : ids) {
                itemPageService.genItemHtml(id);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
