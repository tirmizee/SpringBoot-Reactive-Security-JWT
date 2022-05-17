package com.tirmizee.repository.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PermissionEntity implements Serializable {

    private Integer id;
    private String code;
    private String description;

}
