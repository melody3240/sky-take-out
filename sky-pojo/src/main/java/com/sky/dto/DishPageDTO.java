package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DishPageDTO {
    private Integer page = 1;
    private Integer pageSize = 5;
    private String name;
    private Long categoryId;
    private Integer status;
}
