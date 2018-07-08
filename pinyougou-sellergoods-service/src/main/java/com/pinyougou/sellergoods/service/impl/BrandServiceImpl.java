package com.pinyougou.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entry.PageResult;

@Service
public class BrandServiceImpl implements BrandService{

	@Autowired
	private TbBrandMapper tbBrandMapper;
	
	@Override
	public List<TbBrand> selectAll() {
		// TODO Auto-generated method stub
		
		return tbBrandMapper.selectAll();
	}

	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void saveBrand(TbBrand brand) {
		// TODO Auto-generated method stub
		tbBrandMapper.insert(brand);
	}

	@Override
	public void updateBrand(TbBrand brand) {
		// TODO Auto-generated method stub
		tbBrandMapper.updateByPrimaryKeySelective(brand);
	}

	@Override
	public TbBrand findById(Long id) {
		// TODO Auto-generated method stub
		return tbBrandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void deleteByIds(Long[] ids) {
		// TODO Auto-generated method stub
		for(Long id:ids){
			tbBrandMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult search(TbBrand brand, int page, int rows) {
		// TODO Auto-generated method stub
		PageHelper.startPage(page, rows);
		TbBrandExample tb = new TbBrandExample();
		Criteria criteria = tb.createCriteria();
		if(!StringUtils.isEmpty(brand.getFirstChar())){
			criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
		}
		
		if(!StringUtils.isEmpty(brand.getName())){
			criteria.andNameLike("%"+brand.getName()+"%");
		}
		
		Page<TbBrand> searchPage = (Page<TbBrand>) tbBrandMapper.selectByExample(tb);
		return new PageResult(searchPage.getTotal(), searchPage.getResult());
	}

	@Override
	public List<Map<String, String>> findBrandList() {
		// TODO Auto-generated method stub
		List<TbBrand> brands = tbBrandMapper.selectByExample(null);
		List<Map<String, String>> lists = new ArrayList<>();
		for(TbBrand brand:brands){
			Map<String, String> map = new HashMap<>();
			map.put("id", brand.getId()+"");
			map.put("text", brand.getName());
			lists.add(map);
		}
		return lists;
	}
	

}
