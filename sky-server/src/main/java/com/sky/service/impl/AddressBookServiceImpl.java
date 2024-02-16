package com.sky.service.impl;

import com.sky.context.ThreadLocalUtil;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public List<AddressBook> findAll() {
        return addressBookMapper.findAll();
    }

    @Override
    public AddressBook findDefault() {
        return addressBookMapper.findDefault();
    }

    @Override
    public void save(AddressBook addressBook) {
        addressBookMapper.save(addressBook);
    }

    @Override
    public void setDefault(Long id) {
        // 其他地址置非默认
        addressBookMapper.noDefault(ThreadLocalUtil.getCurrentId());
        addressBookMapper.setDefault(ThreadLocalUtil.getCurrentId(),id);
    }

    @Override
    public AddressBook findById(Long id) {
        return addressBookMapper.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }

    @Override
    public void editById(AddressBook addressBook) {
        addressBookMapper.editById(addressBook);
    }
}
