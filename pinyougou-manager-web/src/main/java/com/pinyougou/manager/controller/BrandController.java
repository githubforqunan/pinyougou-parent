package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entry.PageResult;
import entry.Result;

@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;
	
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.selectAll();
	}
	
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int rows){
		return brandService.findPage(page, rows);
	}
	
	@RequestMapping("/add")
	public Result add(@RequestBody TbBrand brand){
		
		try {
			brandService.saveBrand(brand);
			return new Result(true,"添加成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return new Result(false,"添加失败");
		}

	}
	
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand brand){
		
		try {
			brandService.updateBrand(brand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return new Result(false,"修改失败");
		}

	}
	
	@RequestMapping("/findById")
	public TbBrand findById(Long id){
		return brandService.findById(id);
	}
	
	@RequestMapping("/delSelect")
	public Result delSelect(Long[] ids){
		try {
			brandService.deleteByIds(ids);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return new Result(false,"删除失败");
		}
	}
	
	@RequestMapping(value="/search",method=RequestMethod.POST)
	public PageResult search(@RequestBody TbBrand brand,int page,int rows){
		return brandService.search(brand,page, rows);
	}
	
	@RequestMapping("/findBrandList")
	public List<Map<String, String>> findBrandList(){
		return brandService.findBrandList();
	}
	
	
}
