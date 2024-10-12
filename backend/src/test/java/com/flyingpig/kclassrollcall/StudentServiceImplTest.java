package com.flyingpig.kclassrollcall;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flyingpig.kclassrollcall.common.RedisConstant;
import com.flyingpig.kclassrollcall.common.Result;
import com.flyingpig.kclassrollcall.common.RollCallMode;
import com.flyingpig.kclassrollcall.dto.resp.StudentInfoInCache;
import com.flyingpig.kclassrollcall.entity.Student;
import com.flyingpig.kclassrollcall.filter.UserContext;
import com.flyingpig.kclassrollcall.mapper.StudentMapper;
import com.flyingpig.kclassrollcall.service.impl.StudentServiceImpl;
import com.flyingpig.kclassrollcall.util.cache.ListCacheUtil;
import com.flyingpig.kclassrollcall.util.cache.StringCacheUtil;
import com.google.common.cache.CacheLoader;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private ListCacheUtil listCacheUtil;

    @Mock
    private StringCacheUtil stringCacheUtil;



    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    public <T>

    void testRollCall() {
        // 模拟依赖方法
        // 调用依赖方法方法
        Mockito.doReturn(Result.success()).when(studentService).rollCall(RollCallMode.EQUAL);
        List<Student> mockStudents = Arrays.asList(
                new Student(1L, "Alice", 1, "Math", 50.0, 100L),
                new Student(2L, "Bob", 2, "Science", 0.0, 100L)
        );
        when(studentMapper.selectList(new LambdaQueryWrapper<Student>().eq(Student::getTeacherId, any()))).thenReturn(mockStudents);
        // 模拟 rollCall 调用
        Result result = studentService.rollCall(RollCallMode.EQUAL);

        // 验证返回结果
        assertNotNull(result);
        assertTrue(mockStudents.contains(result.getData())); // 随机选择一个学生
    }
}
