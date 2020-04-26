package cn.stevekung.service;

import cn.stevekung.dao.UserDao;
import cn.stevekung.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    public UserDao userDao;

    public User getById(int id){
        return userDao.getById(id);
    }

    @Transactional
    public boolean tx(){
        User u1 = new User();
        u1.setId(2);
        u1.setName("jg");
        int i = userDao.insert(u1);

        User u2 = new User();
        u2.setId(1);
        u2.setName("gj");
        int j = userDao.insert(u2);

        return true;
    }
}
