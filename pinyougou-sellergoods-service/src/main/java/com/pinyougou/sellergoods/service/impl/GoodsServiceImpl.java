package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.grouppojo.Goods;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entry.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {


	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemCatMapper catMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbSellerMapper sellerMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");
		TbGoods tbGoods = goods.getGoods();
		goodsMapper.insert(goods.getGoods());
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());
		insertItem(goods, tbGoods);
		
	}



	private void insertItem(Goods goods, TbGoods tbGoods) {
		String title=goods.getGoods().getGoodsName();
		if("1".equals(goods.getGoods().getIsEnableSpec())){
			for(TbItem item:goods.getItemList()){

				Map<String,String> map = JSON.parseObject(item.getSpec(), Map.class);
				for(String key:map.keySet()){
					title += " "+map.get(key);
				}
				item.setTitle(title);
				setItemValues(goods,item);

			}
		}else{
			TbItem item = new TbItem();
			item.setTitle(title);
			item.setIsDefault("0");
			item.setNum(9999);
			item.setPrice(tbGoods.getPrice());
			item.setStatus("0");
			item.setSpec("{}");
			setItemValues(goods,item);
		}
	}

	private void setItemValues(Goods goods,TbItem item){
		TbGoods tbGoods = goods.getGoods();
		item.setCategoryid(tbGoods.getCategory3Id());
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		item.setGoodsId(tbGoods.getId());
		item.setSellerId(tbGoods.getSellerId());
		String category = catMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
		item.setCategory(category);
		String brand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId()).getName();
		item.setBrand(brand);
		String seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId()).getName();
		item.setSeller(seller);
		
		List<Map> list = JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
		if(list.size()>0){
			item.setImage(list.get(0).get("url")+"");
		}
		
		itemMapper.insert(item);
	}

	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods) {
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(goods.getGoods().getId());
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		itemMapper.deleteByExample(example);

		insertItem(goods, goods.getGoods());

	}

	@Override
	public void updateStatus(Long[] ids,String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id) {
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		System.out.println(tbItems.size());
		goods.setItemList(tbItems);
		return goods;
	}

	@Override
	public TbGoods findGoods(Long id) {
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {

			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();

		if (goods != null ) {
			if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
				criteria.andSellerIdLike(goods.getSellerId() );
			}
			if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
				criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
			}
			if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
				criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
			}
			if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
				criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
			}
			if (goods.getCaption() != null && goods.getCaption().length() > 0) {
				criteria.andCaptionLike("%" + goods.getCaption() + "%");
			}
			if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
				criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
			}
			if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
				criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
			}
			if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
				criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
			}

		}

		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(TbGoods goods) {
		goodsMapper.updateByPrimaryKey(goods);
	}

	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(goodsIds));
		criteria.andStatusEqualTo(status);

		return itemMapper.selectByExample(example);
	}

}
