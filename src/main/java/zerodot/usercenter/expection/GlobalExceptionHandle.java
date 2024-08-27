package zerodot.usercenter.expection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zerodot.usercenter.common.BaseResponse;
import zerodot.usercenter.common.ErrorCode;
import zerodot.usercenter.common.ResultUtils;

/**
 * 全局异常处理类
 *
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {


    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException" + e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException",e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }
}
