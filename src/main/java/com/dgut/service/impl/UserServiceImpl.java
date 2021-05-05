package com.dgut.service.impl;

import com.dgut.mapper.RoleMapper;
import com.dgut.mapper.UserMapper;
import com.dgut.mapper.UsersRolesMapMapper;
import com.dgut.model.User;
import com.dgut.model.UsersRolesMap;
import com.dgut.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UsersRolesMapMapper usersRolesMapMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, RoleMapper roleMapper, UsersRolesMapMapper usersRolesMapMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.usersRolesMapMapper = usersRolesMapMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userMapper.findOneByUsername(username);
        //找不到用户则抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        //查出所有身份
        user.setRoles(roleMapper.findAllByUserid(user.getId()));
        return user;
    }

    @Override
    public void saveUser(User user) {
        //加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //插入用户
        userMapper.insert(user);
        //批量插入用户角色关系
        final List<UsersRolesMap> usersRolesMaps = user.getRoles()
                .parallelStream()
                .map(role -> new UsersRolesMap(user.getId(), role.getId())).collect(Collectors.toList());
        usersRolesMapMapper.insertBatch(usersRolesMaps);
    }

}
