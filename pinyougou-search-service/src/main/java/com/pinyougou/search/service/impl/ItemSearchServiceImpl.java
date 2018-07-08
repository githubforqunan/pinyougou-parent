package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {


    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));

        Map<String,Object> map = new HashMap<>();
        System.out.println("searchMap="+searchMap);
        List searchCategryList = searchCategryList(searchMap);
        map.put("categoryList", searchCategryList);
        map.putAll(searchList(searchMap));
        if(StringUtils.isEmpty(searchMap.get("category"))){
            if(searchCategryList.size()>0) {
                map.putAll(searchBrandAndSpecList((String) searchCategryList.get(0)));
            }
        }else{
            map.putAll(searchBrandAndSpecList((String) searchMap.get("category")));
        }


        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    private Map<String, Object> searchList(Map searchMap){
        Map<String,Object> map = new HashMap<>();
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");

        if (!StringUtils.isEmpty(searchMap.get("pageNo"))&&!StringUtils.isEmpty(searchMap.get("pageSize"))){
            Integer pageNo = (Integer) searchMap.get("pageNo");
            Integer pageSize = (Integer) searchMap.get("pageSize");
            query.setOffset((pageNo-1)*pageSize);
            query.setRows(pageSize);
        }

        if(!StringUtils.isEmpty(searchMap.get("category"))){

            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        if(!StringUtils.isEmpty(searchMap.get("brand"))){

            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        if(!StringUtils.isEmpty(searchMap.get("brand"))){

            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        if(searchMap.get("spec")!=null && !"".equals(searchMap.get("spec"))){
            System.out.println("spec="+searchMap.get("spec"));
            Map<String,String> map1 = (Map<String,String>)searchMap.get("spec");
            for (String s : map1.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_"+s).is(map1.get(s));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }

        if(!StringUtils.isEmpty(searchMap.get("price"))){
            String priceStr = (String) searchMap.get("price");
            String[] price = priceStr.split("-");

            if (!price[0].equals("0")){
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

            if(!price[1].equals("*")){
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);

                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }

        String sort = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");

        if (sortField!=null && !sortField.equals("")){
            if (sort.equals("ASC")){
                Sort sort1 = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort1);
            }

            if (sort.equals("DESC")){
                Sort sort1 = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort1);
            }
        }

        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));



        query.addCriteria(criteria);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        List<HighlightEntry<TbItem>> highlightEntries = page.getHighlighted();

//        List<HighlightEntry.Highlight> highlights1 = page.getHighlights();
        for (HighlightEntry<TbItem> highlightEntry : highlightEntries) {
            TbItem tbItem = highlightEntry.getEntity();
            tbItem.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));
//            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
////            for (HighlightEntry.Highlight highlight : highlights) {
////                List<String> snipplets = highlight.getSnipplets();
////            }
        }
        map.put("rows",page.getContent());
        map.put("totalPage",page.getTotalPages());
        map.put("total",page.getTotalElements());
        return map;
    }

    private List searchCategryList(Map searchMap){
        List list = new ArrayList();
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            list.add(tbItemGroupEntry.getGroupValue());
        }
        return list;
    }

    private Map<String,List> searchBrandAndSpecList(String catagory){
        Map<String,List> map = new HashMap<>();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(catagory);
        if (typeId!=null){
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);

            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }

        return map;
    }

}
