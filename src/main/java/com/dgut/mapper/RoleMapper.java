package com.dgut.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dgut.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.dgut.model.Role
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<Role> findAllByUserid(@Param("userid") Long userid);

    List<Role> findAll();

}




