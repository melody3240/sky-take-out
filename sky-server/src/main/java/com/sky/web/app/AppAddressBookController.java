package com.sky.web.app;

import com.sky.context.ThreadLocalUtil;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
public class AppAddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("list")
    public Result findAll(){
        List<AddressBook> addressBookList = addressBookService.findAll();
        return Result.success(addressBookList);
    }

    @GetMapping("default")
    public Result findDefault(){
        AddressBook addressBook = addressBookService.findDefault();
        return Result.success(addressBook);
    }

    @PostMapping
    public Result save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(ThreadLocalUtil.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookService.save(addressBook);
        return Result.success();
    }

    @PutMapping("default")
    public Result setDefault(@RequestBody AddressBook addressBook){
        addressBookService.setDefault(addressBook.getId());
        return Result.success();
    }

    @GetMapping("{id}")
    public Result findById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.findById(id);
        return Result.success(addressBook);
    }

    @DeleteMapping
    public Result deleteById(Long id){
        addressBookService.deleteById(id);
        return Result.success();
    }

    @PutMapping
    public Result editById(@RequestBody AddressBook addressBook){
        addressBook.setUserId(ThreadLocalUtil.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookService.editById(addressBook);
        return Result.success();
    }

}
