package cn.stevekung.service;

import cn.stevekung.dao.MiaoshaUserDao;
import cn.stevekung.domain.MiaoshaUser;
import cn.stevekung.exception.GlobleException;
import cn.stevekung.redis.MiaoshaUserKey;
import cn.stevekung.redis.RedisService;
import cn.stevekung.result.CodeMsg;
import cn.stevekung.util.MD5Util;
import cn.stevekung.util.UUIDUtil;
import cn.stevekung.vo.LoginVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class MiaoshaUserService {
    public static final String COOKI_NAME_TOKEN="token";
    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(Long id){
//        return miaoshaUserDao.getById(id);
        //取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, ""+id, MiaoshaUser.class);
        if(user != null) {
            return user;
        }
        //取数据库
        user = miaoshaUserDao.getById(id);
        if(user != null) {
            redisService.set(MiaoshaUserKey.getById, ""+id, user);
        }
        return user;
    }

    // http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        MiaoshaUser user = getById(id);
        if(user == null) {
            throw new GlobleException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoshaUserKey.getById, ""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    public String login(HttpServletResponse response, LoginVO loginVO) {
        if (loginVO == null){
//            return CodeMsg.SERVER_ERROR;
            throw new GlobleException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVO.getMobile();
        String formPass = loginVO.getPassword();
        // 判断手机号是否存在
        MiaoshaUser user = miaoshaUserDao.getById(Long.parseLong(mobile));
        if (user == null){
//            return CodeMsg.MOBILE_NOT_EXIST;
            throw new GlobleException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // 验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)){
//            return CodeMsg.PASSWORD_ERROR;
            throw new GlobleException(CodeMsg.PASSWORD_ERROR);
        }
//        return CodeMsg.SUCCESS;

        // 生成cookie
        // 代码复用
        String token = UUIDUtil.uuid();
        addCookie(user, response, token);
//        String token = UUIDUtil.uuid();
//        redisService.set(MiaoshaUserKey.token,token,user);
//        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
//        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
//        cookie.setPath("/");
//        // cookie送至客户端
//        response.addCookie(cookie);


        return token;
    }

    private void addCookie(MiaoshaUser user, HttpServletResponse response, String token){
        // 生成cookie

        redisService.set(MiaoshaUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        // cookie送至客户端
        response.addCookie(cookie);
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        if (user != null){
            // 延长有效期
            // 生成cookie
            // 代码复用
            addCookie(user, response, token);
        }
        return user;
    }
}
