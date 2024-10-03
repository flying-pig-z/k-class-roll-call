package com.flyingpig.kclassrollcall.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentExcelModel {
    private String no;      // 学号
    private String name;    // 姓名
    private String major;   // 专业
}
