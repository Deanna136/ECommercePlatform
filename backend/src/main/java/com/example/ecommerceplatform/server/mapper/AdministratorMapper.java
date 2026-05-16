package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.dto.UpdatePasswordDTO;
import com.example.ecommerceplatform.pojo.entity.Administrator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface AdministratorMapper {
    //无增删

    /**
     * 修改密码
     * @param password,id
     * @return
     */
    @Update("update administrator set password = #{password} where id = #{id}")
    int updatePassword(@Param("password") String password,@Param("id") Long id);

    /**
     * 查询全部
     *
     * @return
     */
    @Select("select * from administrator")
    List<Administrator> getAll();

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Select("select * from administrator where id = #{id}")
    Administrator getById(Long id);

    /**
     * 根据用户名查询
     * @param userName
     * @return
     */
    @Select("select * from administrator where user_name = #{userName}")
    Administrator getByUserName(String userName);
}
