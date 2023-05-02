package com.example.research.repository;

import com.example.research.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ICategoryRepository {

    @Select("select * from category")
    List<Category> getList();

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into category(name) values(#{cate.name})")
    int insert(@Param("cate") Category category);
}
