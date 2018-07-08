package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entry.PageResult;

public interface BrandService {
	
	public List<TbBrand> selectAll();
	
	public PageResult findPage(int pageNum,int pageSize);

	public void saveBrand(TbBrand brand);

	public void updateBrand(TbBrand brand);

	public TbBrand findById(Long id);

	public void deleteByIds(Long[] ids);

	public PageResult search(TbBrand brand, int page, int rows);

	public List<Map<String, String>> findBrandList();
}
