package com.ttms.Config;

import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@Slf4j
public class ConvertDate implements Converter<String,Date> {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public Date convert(String s) {
        if ("".equals(s) || s == null) {
              return null;
          }
          try {
                 return simpleDateFormat.parse(s);
         } catch (ParseException e) {
                log.error("[时间装换出错]");
               throw new TTMSException(ExceptionEnum.TIME_CONVERTER_ERROR);
        }
    }
}
