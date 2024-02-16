package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    @Select("select * from address_book")
    List<AddressBook> findAll();

    @Select("select * from address_book where is_default = 1")
    AddressBook findDefault();

    void save(AddressBook addressBook);

    @Update("update address_book set is_default = 1 where id = #{id} and user_id = #{userId}")
    void setDefault(Long userId, Long id);

    @Select("select * from address_book where id = #{id}")
    AddressBook findById(Long id);

    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);

    void editById(AddressBook addressBook);

    @Update("update address_book set is_default = 0 where user_id = #{currentId}")
    void noDefault(Long currentId);
}
