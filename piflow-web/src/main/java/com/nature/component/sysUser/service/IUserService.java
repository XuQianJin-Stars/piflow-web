package com.nature.component.sysUser.service;

import com.nature.component.sysUser.model.SysUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {

    public SysUser findByUsername(String useename);

    public List<SysUser> findByName(String name);

    public List<SysUser> getUserList();

    public SysUser addUser(SysUser user);

    public int saveOrUpdate(SysUser user);

    public int deleteUser(int id);
}
