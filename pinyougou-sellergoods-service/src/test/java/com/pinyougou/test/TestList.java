package com.pinyougou.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.pinyougou.pojo.TbBrand;

public class TestList {

	@Test
	public void test1(){
		TbBrand t1 = new TbBrand();
		t1.setId(1L);
		t1.setFirstChar("A");
		t1.setName("aaa");
		TbBrand t2 = new TbBrand();
		t1.setId(1L);
		t1.setFirstChar("B");
		t1.setName("bbb");
		TbBrand t3 = new TbBrand();
		t1.setId(1L);
		t1.setFirstChar("C");
		t1.setName("ccc");
		
		List<TbBrand> list = new ArrayList<>();
		List<TbBrand> list2 = new ArrayList<>();
		list.add(t1);
		list.add(t2);
		list.add(t2);
		for (TbBrand tbBrand : list) {
			tbBrand.setId(10L);
			list2.add(tbBrand);
		}
		
		for (TbBrand tbBrand : list2) {
			System.out.println(tbBrand.getId());
		}
	}
}
