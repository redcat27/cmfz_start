package com.baizhi.realm;

import com.baizhi.dao.AdminDao;
import com.baizhi.entity.Admin;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class MyRealm extends AuthorizingRealm {
    @Autowired
    private AdminDao adminDao;

    //对登录的用户进行收授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //1.获取主身份
        String username = (String) principalCollection.getPrimaryPrincipal();
        //根据主身份进行授权，如果是admin则是管理员，否则均为普通管理员
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if ("admin".equals(username)) {
            //说明是管理员,管理员标记的amdin角色赋值给授权信息
            info.addRole("admin");
        }
        //info.add(list);   list中存的是角色信息
        return info;
    }


    //身份认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        //从数据库查询
        Admin adminDB = adminDao.selectOne(new Admin().setUsername(username));
        if (adminDB == null) {
            //说明用户名不存在，返回null
            return null;
        } else {
            //将用户名，密码。盐和当前类对象，传参给到simpleAccount对象进行密码验证
            SimpleAccount simpleAccount =
                    new SimpleAccount(adminDB.getUsername(), adminDB.getPassword(), ByteSource.Util.bytes("abcd"), this.getName());
            return simpleAccount;
        }
    }
}
