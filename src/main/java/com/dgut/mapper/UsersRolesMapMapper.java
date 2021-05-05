package com.dgut.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dgut.model.UsersRolesMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * @Entity com.dgut.model.UsersRolesMap
 */
@Mapper
public interface UsersRolesMapMapper extends BaseMapper<UsersRolesMap> {

    int insertBatch(@Param("usersRolesMapCollection") Collection<UsersRolesMap> usersRolesMapCollection);

}




