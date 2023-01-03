package cn.hashq.netpoststation.vo;

import lombok.Data;

/**
 * 通用响应实体
 *
 * @param <T> 返回的数据体po类型
 * @author HashQ
 * @since 1.0
 */
@Data
public class ServerResponse<T> {


    /**
     * 响应码
     * 正常：200
     * 异常：500(服务器内部错误)
     */
    private int code;

    /**
     * 响应数据体
     */
    private T data;

    /**
     * 响应信息
     */
    private String msg;

    public ServerResponse(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static ServerResponse createSuccessResult() {
        return createSuccessResult(null);
    }

    public static <T> ServerResponse createSuccessResult(T data) {
        return createSuccessResult(data, "");
    }

    public static <T> ServerResponse createSuccessResult(T data, String msg) {
        return new ServerResponse(200, data, msg);
    }

    public static ServerResponse createFailedResult() {
        return createFailedResult("");
    }

    public static ServerResponse createFailedResult( String msg) {
        return createFailedResult(null, msg);
    }

    public static ServerResponse createFailedResult(int code, String msg) {
        ServerResponse failedResult = createFailedResult(null, msg);
        failedResult.setCode(code);
        return failedResult;
    }

    public static <T> ServerResponse createFailedResult(T data, String msg) {
        return new ServerResponse(500, data, msg);
    }

}
