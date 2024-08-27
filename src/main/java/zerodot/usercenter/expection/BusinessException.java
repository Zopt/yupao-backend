package zerodot.usercenter.expection;

import zerodot.usercenter.common.BaseResponse;
import zerodot.usercenter.common.ErrorCode;

/**
 * 自定义异常
 */
public class BusinessException extends RuntimeException{

    private final int code;

    private final String description;

    public BusinessException(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(String message, Throwable cause, int code, String description) {
        super(message, cause);
        this.code = code;
        this.description = description;
    }

    public BusinessException(Throwable cause, int code, String description) {
        super(cause);
        this.code = code;
        this.description = description;
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, String description) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.description = description;
    }


    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode,String description){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
