package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {
        try{
            Configuration configuration = freemarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            Map map = new HashMap();

            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);

            map.put("category1",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName());
            map.put("category2",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName());
            map.put("category3",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName());
            map.put("goodsDesc",goodsDescMapper.selectByPrimaryKey(goodsId));
            TbItemExample example=new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//状态为有效
            criteria.andGoodsIdEqualTo(goodsId);//指定SPU ID
            example.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默认
            List<TbItem> itemList = itemMapper.selectByExample(example);
            map.put("itemList", itemList);

            map.put("goods",tbGoods);
            FileWriter out = new FileWriter(pagedir+tbGoods.getId()+".html");
            template.process(map,out);


            out.close();
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public void deleteItemHtml( Long[] ids){
        for (Long id : ids) {
            new File(pagedir+id+".html").delete();
        }
    }
}
