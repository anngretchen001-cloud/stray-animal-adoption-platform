package org.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
//使用Mybatis_Plus直接继承BaseMapper，基础的CRUD都不用自己在mapper.xml去写SQL语句了，已经封装好。
