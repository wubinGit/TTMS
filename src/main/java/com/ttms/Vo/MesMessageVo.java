package com.ttms.Vo;

import com.ttms.Entity.MesMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesMessageVo extends MesMessage {
    private String username;
}
