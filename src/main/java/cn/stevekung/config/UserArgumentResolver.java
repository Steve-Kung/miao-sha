package cn.stevekung.config;

import cn.stevekung.access.UserContext;
import cn.stevekung.domain.MiaoshaUser;
import cn.stevekung.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        // 采用HandlerMethodArgumentResolver方法参数解析器
//        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
//
//        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
//        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);
//        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(cookieToken)){
//            return null;
//        }
//        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
//        return miaoshaUserService.getByToken(response, token);

        // 采用拦截器
        return UserContext.getUser();
    }

//    private String getCookieValue(HttpServletRequest request, String cookiName) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null || cookies.length <= 0){
//            return null;
//        }
//        for (Cookie cookie : cookies) {
//            if(cookie.getName().equals(cookiName)){
//                return cookie.getValue();
//            }
//        }
//        return null;
//    }
}
