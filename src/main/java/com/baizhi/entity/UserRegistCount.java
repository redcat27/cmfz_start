package com.baizhi.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserRegistCount {
    private Integer month;
    private Long count;
}
