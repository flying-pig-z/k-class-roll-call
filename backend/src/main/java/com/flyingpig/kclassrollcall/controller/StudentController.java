package com.flyingpig.kclassrollcall.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.flyingpig.kclassrollcall.common.Result;
import com.flyingpig.kclassrollcall.entity.Student;
import com.flyingpig.kclassrollcall.filter.UserContext;
import com.flyingpig.kclassrollcall.service.IStudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author flyingpig
 * @since 2024-09-25
 */
@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {

    @Autowired
    IStudentService studentService;

    @PutMapping("/score")
    public Result modifyScore(Long id, Double score) {
        return Result.success(studentService.updateById(new Student(id,
                null, null, null, studentService.getById(id).getScore() + score, null)));
    }

    @GetMapping("/roll-call")
    public Result rollCall(String mode) {
        return studentService.rollCall(mode);
    }

    @GetMapping("/search")
    public Result search(String no, String name, Integer pageNo, Integer pageSize) {
        return studentService.search(no, name, pageNo, pageSize);
    }

    @PostMapping("/import")
    public Result importExcel(@RequestParam("file") MultipartFile file) {
        // 如果 validateExcelHeader 返回 false，则文件类型或格式错误
        if (!validateExcelHeader(file)) {
            return Result.error("文件类型或格式错误");
        }
        return studentService.importExcel(file);
    }

    private boolean validateExcelHeader(MultipartFile file) {
        // 检查文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null || !(fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
            return false; // 文件扩展名不正确
        }
        // 使用 EasyExcel 检查文件内容
        // 自定义监听器用于验证标题
        ReadListener<String[]> listener = new ReadListener<String[]>() {
            @Override
            public void invoke(String[] data, AnalysisContext context) {
                // 检查第一行的标题
                if (context.readRowHolder().getRowIndex() == 0) { // 仅检查第一行
                    if (!"学生学号".equals(data[0]) || !"姓名".equals(data[1]) || !"专业".equals(data[2])) {
                        throw new RuntimeException("标题不匹配");
                    }
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
            }
        };
        return true; // 如果没有异常抛出，标题验证通过
    }


    @DeleteMapping("/{id}")
    public Result deleteStudent(@PathVariable String id) {
        if (studentService.removeById(id)) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }

    }

}
