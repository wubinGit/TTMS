package com.ttms.Advice;

import com.ttms.Exception.TTMSException;
import com.ttms.Vo.ExceptionResult;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



/**
 * 通用异常处理  拦截controller中throw的Exception
 */
@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(TTMSException.class)
    public ResponseEntity<ExceptionResult> handleException(TTMSException e) {
        val em = e.getExceptionEnum();  //status :403  message
        return ResponseEntity.status(em.getStatus()).body(new ExceptionResult(em));

    }
}
