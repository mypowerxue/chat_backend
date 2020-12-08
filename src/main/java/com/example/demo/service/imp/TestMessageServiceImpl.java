package com.example.demo.service.imp;

import com.example.demo.mapper.bean.TestMessage;
import com.example.demo.mapper.TestMessageDAO;
import com.example.demo.service.TestMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TestMessageServiceImpl implements TestMessageService {

    @Autowired
    TestMessageDAO testMessageDAO;

    @Override
    public boolean insertMessage(TestMessage testMessage) {
        int insert = testMessageDAO.insert(testMessage);
        return insert != 0;
    }

    @Override
    public boolean updateMessage(TestMessage testMessage) {
        int insert = testMessageDAO.updateByPrimaryKey(testMessage);
        return insert != 0;
    }

    @Override
    public List<TestMessage> selectMessage(Integer userId, Integer status) {
        List<TestMessage> list = testMessageDAO.selectAll(userId, userId);
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getStatus().equals(status)) {
                list.remove(i);
                i--;
            }
        }
        return list;
    }
}
