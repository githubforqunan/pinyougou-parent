package com.pinyougou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class TestRedis {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testAdd(){

        redisTemplate.boundHashOps("itemCat").put("三星",35L);
    }

    @Test
    public void test1(){
        Long o = (Long)redisTemplate.boundHashOps("itemCat").get("电子书刊");
        System.out.println(o);
    }
}
