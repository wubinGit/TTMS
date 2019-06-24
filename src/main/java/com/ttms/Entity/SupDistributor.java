package com.ttms.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "sup_distributor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupDistributor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String distributorname;

    private String distributorphone;

    private String distributoraddress;

    private String loginname;

    private String loginpass;
}