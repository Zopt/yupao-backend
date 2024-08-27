package zerodot.usercenter.common;

/**
 * 全局错误码
 * @author zerodot
 */
public enum ErrorCode {
    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    PARAMS_NULL_ERROR(40001,"请求数据为空",""),
    NO_LOGIN(40100,"未登录",""),
    NOT_AUTH(40101,"无权限",""),
    FORBIDDEN(40301,"禁止访问",""),
    SYSTEM_ERROR(50001,"系统内部异常","系统内部异常");

    /**
     * 错误码
     */
    private final int code;
    /**
     * 错误信心
     */
    private final String  message;
    /**
     * 错误描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
