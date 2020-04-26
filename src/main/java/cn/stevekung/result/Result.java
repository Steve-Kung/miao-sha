package cn.stevekung.result;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg cm) {
        if (cm == null){
            return;
        }
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }

    // 请求成功时，只给数据即可
    public static <T>Result<T> success(T data){
        return new Result<T>(data);
    }

    // 失败时, 传个codeMsg即可 知道错误类型
    public static <T>Result<T> error(CodeMsg cm){
        return new Result<T>(cm);
    }
}
