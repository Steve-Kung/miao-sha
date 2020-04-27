package cn.stevekung.service;

import cn.stevekung.dao.MiaoshaUserDao;
import cn.stevekung.domain.MiaoshaUser;
import cn.stevekung.result.CodeMsg;
import cn.stevekung.util.MD5Util;
import cn.stevekung.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaUserService {
    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    public MiaoshaUser getById(Long id){
        return miaoshaUserDao.getById(id);
    }

    public CodeMsg login(LoginVO loginVO) {
        if (loginVO == null){
            return CodeMsg.SERVER_ERROR;
        }
        String mobile = loginVO.getMobile();
        String formPass = loginVO.getPassword();
        // 判断手机号是否存在
        MiaoshaUser user = miaoshaUserDao.getById(Long.parseLong(mobile));
        if (user == null){
            return CodeMsg.MOBILE_NOT_EXIST;
        }
        // 验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)){
            return CodeMsg.PASSWORD_ERROR;
        }
        return CodeMsg.SUCCESS;
    }
}
