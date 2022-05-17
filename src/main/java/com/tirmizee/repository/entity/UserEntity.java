package com.tirmizee.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Table("users")
public class UserEntity implements Serializable {

    @Id
    private Integer id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private Timestamp createdDate;
    private Timestamp lastLogin;

}
