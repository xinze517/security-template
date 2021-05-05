package com.dgut.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dgut.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Entity com.dgut.model.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    User findOneByUsername(@Param("username") String username);
}




