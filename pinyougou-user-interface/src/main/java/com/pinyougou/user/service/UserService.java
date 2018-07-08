package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;

public interface UserService {

    public void register(TbUser user);

    public boolean checkCode(String smsCode,String phone);

    boolean sendCode(String phone);
}
