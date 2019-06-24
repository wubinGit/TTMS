package com.ttms.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "mes_message")
@Data
public class MesMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String messagetitle;

    private String messagecontent;

    private Integer senderid;

    private Integer sendtype;

    private Integer toid;

    private Date sendtime;

    private Byte valid;

    private Date updatetime;

    @Transient
    private String senderName;

    @Transient
    private String userDepartment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}