package com.ttms.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownLoad {
    private byte[] filebyte;
    private String fileName;

}
