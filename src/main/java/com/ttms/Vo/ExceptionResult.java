package com.ttms.Vo;

import com.ttms.Enum.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 异常时返回消息的格式
 */
@Data
@AllArgsConstructor
public class ExceptionResult {
    private int status;
    private String message;
    private Long timestamp;

    public ExceptionResult(ExceptionEnum em) {
        this.status = em.getStatus();
        this.message = em.getMessage();
        this.timestamp = System.currentTimeMillis();
    }
}
