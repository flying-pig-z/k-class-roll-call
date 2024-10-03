package com.flyingpig.kclassrollcall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyingpig.kclassrollcall.common.Result;
import com.flyingpig.kclassrollcall.dto.req.LoginReq;
import com.flyingpig.kclassrollcall.dto.resp.LoginResp;
import com.flyingpig.kclassrollcall.entity.Teacher;
import com.flyingpig.kclassrollcall.mapper.TeacherMapper;
import com.flyingpig.kclassrollcall.service.ITeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.kclassrollcall.util.JwtUtil;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author flyingpig
 * @since 2024-09-25
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements ITeacherService {


    @Override
    public Result login(LoginReq loginReq) {
        Teacher teacher = this.getOne(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getUsername, loginReq.getUsername())
                .eq(Teacher::getPassword, loginReq.getPassword()));
        if(teacher == null){
            return Result.error("账号或密码错误");
        }else {
            return Result.success(new LoginResp(teacher.getName(), JwtUtil.createJWT(teacher.getId().toString())));
        }
    }

    @Override
    public Result register(Teacher teacher) {
        try {
            save(teacher);
            return Result.success("注册成功");
        }catch (Exception e){
            log.error(e.getMessage());
            return Result.error("注册失败,可能存在用户名已被注册等问题");
        }
    }
}
