package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    List<AddressBook> findAll();

    AddressBook findDefault();

    void save(AddressBook addressBook);

    void setDefault(Long id);

    AddressBook findById(Long id);

    void deleteById(Long id);

    void editById(AddressBook addressBook);
}
