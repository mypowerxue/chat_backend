package com.example.demo.service.imp;

import com.example.demo.mapper.bean.TestUser;
import com.example.demo.mapper.TestUserDAO;
import com.example.demo.service.TestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TestUserServiceImpl implements TestUserService {

    @Autowired
    TestUserDAO testUserDAO;

    @Override
    public boolean insert(TestUser testUser) {
        int insert = testUserDAO.insert(testUser);
        return insert != 0;
    }

    @Override
    public boolean update(TestUser testUser) {
        int insert = testUserDAO.updateByPrimaryKey(testUser);
        return insert != 0;
    }

    @Override
    public TestUser selectById(Integer userId) {
        return testUserDAO.selectByPrimaryKey(userId);
    }

    @Override
    public TestUser selectByAccount(String account) {
        return testUserDAO.selectByAccount(account);
    }
}
