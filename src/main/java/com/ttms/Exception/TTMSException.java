package com.ttms.Exception;

import com.ttms.Enum.ExceptionEnum;
import com.ttms.Vo.ExceptionResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

/*通用异常处理*/
@AllArgsConstructor
@Getter
public class TTMSException extends RuntimeException{
    private ExceptionEnum exceptionEnum;
}
