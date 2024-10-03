package com.flyingpig.kclassrollcall.controller;


import com.flyingpig.kclassrollcall.common.Result;
import com.flyingpig.kclassrollcall.dto.req.LoginReq;
import com.flyingpig.kclassrollcall.entity.Teacher;
import com.flyingpig.kclassrollcall.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author flyingpig
 * @since 2024-09-25
 */
@RestController
@RequestMapping("/user")
public class TeacherController {

    @Autowired
    ITeacherService teacherService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginReq loginReq){
        return teacherService.login(loginReq);
    }

    @PostMapping("/register")
    public Result register(@RequestBody Teacher teacher){
        return teacherService.register(teacher);
    }

}
