package com.ttms.Properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix ="ttms.upload" )
@Data
public class UploadProperties {
    private String baseUrl;
    private int maxsize;
    private String unit;
}
