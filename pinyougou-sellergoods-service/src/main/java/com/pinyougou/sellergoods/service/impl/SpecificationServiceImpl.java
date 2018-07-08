package com.pinyougou.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;

import entry.PageResult;
import entry.Specification;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	
	@Autowired
	private TbSpecificationOptionMapper tsop;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		TbSpecification tbSpecification = specification.getTbSpecification();
		specificationMapper.insert(tbSpecification);
		List<TbSpecificationOption> tbSpecificationOptions = specification.getTbSpecificationOption();
		for (TbSpecificationOption tfo : tbSpecificationOptions) {
			tfo.setSpecId(tbSpecification.getId());
			tsop.insert(tfo);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		TbSpecification tbSpecification = specification.getTbSpecification();
		
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		example.createCriteria().andSpecIdEqualTo(tbSpecification.getId());
		tsop.deleteByExample(example);
		List<TbSpecificationOption> tbSpecificationOptions = specification.getTbSpecificationOption();
		for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptions) {
			tbSpecificationOption.setSpecId(tbSpecification.getId());
			tsop.insert(tbSpecificationOption);
		}
		specificationMapper.updateByPrimaryKey(tbSpecification);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		Specification specification = new Specification();
		specification.setTbSpecification(specificationMapper.selectByPrimaryKey(id));
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		specification.setTbSpecificationOption(tsop.selectByExample(example));
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			List<TbSpecificationOption> selects = tsop.selectByExample(example);
			for(TbSpecificationOption t:selects){
				tsop.deleteByPrimaryKey(t.getId());
			}
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public List<Map<String, String>> findSpecList() {
			// TODO Auto-generated method stub
			List<TbSpecification> specifications = specificationMapper.selectByExample(null);
			List<Map<String, String>> lists = new ArrayList<>();
			for (TbSpecification tbSpecification : specifications) {
				Map<String,String> map = new HashMap<>();
				map.put("id", tbSpecification.getId()+"");
				map.put("text",tbSpecification.getSpecName());
				lists.add(map);
			}
			return lists;
		}
	
}
