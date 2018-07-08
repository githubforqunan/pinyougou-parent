package com.pinyougou.shop.service.impl;

import java.util.ArrayList;
import java.util.List;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService{

	
	private SellerService sellerService;
	
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		System.out.println("经过了UserDetailsService");
		List<GrantedAuthority> grantedList = new ArrayList<>();
		grantedList.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		TbSeller seller = sellerService.findOne(username);
		
		if(seller!=null){
			if(seller.getStatus().equals("1")){
				return new User(username,seller.getPassword(),grantedList);
			}
		}
		return null;
	}

}
