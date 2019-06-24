package com.ttms.Vo;

import com.ttms.Entity.ProProject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectVo extends ProProject {
    private String dpartname;
}
