package com.ttms.Vo;

import com.ttms.Entity.ResoAttachment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResoAttachmentVo extends ResoAttachment {
    private String username;
}
