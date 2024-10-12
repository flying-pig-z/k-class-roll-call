package com.flyingpig.kclassrollcall;

import com.flyingpig.kclassrollcall.common.Result;
import com.flyingpig.kclassrollcall.dto.req.LoginReq;
import com.flyingpig.kclassrollcall.dto.resp.LoginResp;
import com.flyingpig.kclassrollcall.entity.Teacher;
import com.flyingpig.kclassrollcall.mapper.TeacherMapper;
import com.flyingpig.kclassrollcall.service.ITeacherService;
import com.flyingpig.kclassrollcall.service.impl.TeacherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TeacherServiceImplTest {

    @InjectMocks
    private TeacherServiceImpl teacherService; // 替换为你的实际服务类

    @Mock
    private TeacherMapper teacherMapper; // 替换为你的 TeacherMapper 类

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() {
        // Arrange
        LoginReq loginReq = new LoginReq("flyingpig", "flyingpig");
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("John Doe");
        teacher.setUsername("flyingpig");
        teacher.setPassword("flyingpig");

        when(teacherMapper.selectOne(any())).thenReturn(teacher); // 模拟数据库返回教师对象

        // Act
        Result result = teacherService.login(loginReq);

        // Assert
        assertEquals(200, result.getCode()); // 检查返回代码
        assertEquals("John Doe", ((LoginResp) result.getData()).getName()); // 检查返回的教师名称
    }

    @Test
    public void testLoginFailure() {
        // Arrange
        LoginReq loginReq = new LoginReq("username", "wrongpassword");

        when(teacherMapper.selectOne(any())).thenReturn(null);

        // Act
        Result result = teacherService.login(loginReq);

        // Assert
        assertEquals(500, result.getCode()); // 检查返回代码
        assertEquals("账号或密码错误", result.getMsg()); // 检查返回的错误信息
    }

}