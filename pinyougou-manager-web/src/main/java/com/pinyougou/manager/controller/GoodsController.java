package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.grouppojo.Goods;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import entry.PageResult;
import entry.Result;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private ItemPageService itemPageService;
	@Reference
	private GoodsService goodsService;



	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination queueTextDestination;

	@Autowired
	private Destination queueDeleteTextDestination;

	@Autowired
	private Destination topicTextDestination;
	@Autowired
	private Destination topicTextDeleteDestination;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}


	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			if(ids.length>0) {
				jmsTemplate.send(queueDeleteTextDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});

				jmsTemplate.send(topicTextDeleteDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
			}
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页

	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}



	@RequestMapping("/marketable")
	public Result marketable(Long []ids  ){

		try {
			for (Long id : ids) {
				TbGoods goods = goodsService.findGoods(id);
				goods.setIsMarketable("1");
				goodsService.updateStatus(goods);
			}
			goodsService.delete(ids);
			return new Result(true, "上架成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上架失败");
		}
	}

	@RequestMapping("/findLoginName")
	public Map<String,String> findLoginName(){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String,String> map = new HashMap<>();
		map.put("loginName",name);
		return map;
	}




	@RequestMapping("/updateStatus")
	public Result updateStatus(final Long ids[], String status){
		try {
			goodsService.updateStatus(ids,status);

			List<TbItem> tbItems = goodsService.findItemListByGoodsIdandStatus(ids, status);
			if(tbItems.size()>0) {
				final String jsonString = JSON.toJSONString(tbItems);
				if (status.equals("1")) {
					jmsTemplate.send(queueTextDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});

					jmsTemplate.send(topicTextDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createObjectMessage(ids);
						}
					});
				}
			}else{
				System.out.println("没有数据");
			}
			return new Result(true, "审核成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "审核失败");
		}
	}

	@RequestMapping("/genHtml")
		public void genHtml(Long goodsId){
		itemPageService.genItemHtml(goodsId);
	}


}
