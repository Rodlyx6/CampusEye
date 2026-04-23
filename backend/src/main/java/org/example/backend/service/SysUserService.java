package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    SysUser findByUsername(String username);
}
