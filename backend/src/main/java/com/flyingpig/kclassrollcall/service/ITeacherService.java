package com.flyingpig.kclassrollcall.service;

import com.flyingpig.kclassrollcall.common.Result;
import com.flyingpig.kclassrollcall.dto.req.LoginReq;
import com.flyingpig.kclassrollcall.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author flyingpig
 * @since 2024-09-25
 */
public interface ITeacherService extends IService<Teacher> {

    Result login(LoginReq loginReq);

    Result register(Teacher teacher);
}
