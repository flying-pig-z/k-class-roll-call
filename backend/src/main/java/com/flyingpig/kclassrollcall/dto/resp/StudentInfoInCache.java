package com.flyingpig.kclassrollcall.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfoInCache {
    private Long id;

    private String name;

    private Integer no;

    private String major;

}
