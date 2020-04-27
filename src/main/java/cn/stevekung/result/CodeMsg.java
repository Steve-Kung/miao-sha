package cn.stevekung.result;
import lombok.Data;

@Data
public class CodeMsg {
    private int code;
    private String msg;

    // 通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常: %s");


    // 登录模块5002XX
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已失效");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "登录密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "登录手机号码不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "登录手机号码格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "登录手机号码不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "登录密码错误");

    // 商品模块5003XX
    // 订单模块5004XX
    // 秒杀模块5005XX
    public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500, "商品秒杀已经结束");
    public static CodeMsg REPEATE_MIAOSHA = new CodeMsg(500501, "不能重复秒杀");




    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }
}
