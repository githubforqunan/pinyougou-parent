package com.pinyougou.utils;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtils {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper itemMapper;

    public void importItemData(){
        TbItemExample example = new TbItemExample();
        example.createCriteria().andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        for (TbItem tbItem : tbItems) {
            Map maps = JSON.parseObject(tbItem.getSpec(),Map.class);
            tbItem.setSpecMap(maps);
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtils solrUtils = (SolrUtils) ac.getBean("solrUtils");
        solrUtils.importItemData();
//        SolrUtils bean = ac.getBean(SolrUtils.class);

    }
}
