package com.wzh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.entity.AddressBook;
import com.wzh.reggie.mapper.AddressBookMapper;
import com.wzh.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author wzh
 * @date 2023年04月05日 13:08
 * Description:
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
