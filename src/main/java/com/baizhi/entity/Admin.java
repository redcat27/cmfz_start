package com.baizhi.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Accessors(chain = true)
@Table(name="t_admin")  //表和实体类名的映射
public class Admin {
    @Id
    private String id;
    private String username;
    private String password;
    private String nickname;

}
