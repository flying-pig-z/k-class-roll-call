package com.flyingpig.kclassrollcall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flyingpig.kclassrollcall.common.RedisConstant;
import com.flyingpig.kclassrollcall.common.Result;
import com.flyingpig.kclassrollcall.common.RollCallMode;
import com.flyingpig.kclassrollcall.dto.req.StudentExcelModel;
import com.flyingpig.kclassrollcall.dto.resp.StudentInfoInCache;
import com.flyingpig.kclassrollcall.entity.Student;
import com.flyingpig.kclassrollcall.filter.UserContext;
import com.flyingpig.kclassrollcall.mapper.StudentMapper;
import com.flyingpig.kclassrollcall.service.IStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.kclassrollcall.util.cache.ListCacheUtil;
import com.flyingpig.kclassrollcall.util.cache.StringCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author flyingpig
 * @since 2024-09-25
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {


    @Autowired
    ListCacheUtil listCacheUtil;

    @Autowired
    StringCacheUtil stringCacheUtil;

    @Override
    public Result rollCall(String mode) {
        if (!mode.equals(RollCallMode.EQUAL)) {
            return Result.success(rollBackStudentBaseScore());
        } else {
            return Result.success(selectStudentByTeacherId().get(new Random().nextInt(count()) - 1));
        }
    }


    private List<Student> selectStudentByTeacherId() {
        List<StudentInfoInCache> cachedList = listCacheUtil.safeGetWithLock(
                RedisConstant.STUDENT_LIST_KEY + UserContext.getUser(),
                StudentInfoInCache.class, // 使用具体的 StudentInfoInCache.class 作为类型
                () -> {
                    // 查询学生的逻辑,转换为 StudentInfoInCache 列表
                    return this.baseMapper.selectList(new LambdaQueryWrapper<Student>()
                                    .eq(Student::getTeacherId, UserContext.getUser())).stream()
                            .map(student -> new StudentInfoInCache(student.getId(), student.getName(), student.getNo(), student.getMajor()))
                            .collect(Collectors.toList());
                },
                30L,
                TimeUnit.MINUTES
        );
        List<Student> students = new ArrayList<>();
        for (StudentInfoInCache studentInfo : cachedList) {
            Student newStudent = new Student();
            BeanUtil.copyProperties(studentInfo, newStudent);
            newStudent.setScore(stringCacheUtil.safeGetWithLock(
                    RedisConstant.STUDENT_SCORE_KEY + studentInfo.getId(),
                    Double.class, // 传入正确的类型
                    () -> {
                        return this.getBaseMapper().selectById(studentInfo.getId()).getScore();
                    },
                    30L,
                    TimeUnit.MINUTES
            ));
            newStudent.setTeacherId(Long.parseLong(UserContext.getUser()));
            students.add(newStudent);
        }
        return students;
    }


    private Student rollBackStudentBaseScore() {
        // 获取符合条件的学生列表
        List<Student> students = selectStudentByTeacherId();
        // 计算权重
        double totalWeight = 0;
        Map<Student, Double> weightMap = new HashMap<>();
        for (Student student : students) {
            double weight;
            if (student.getScore() > 0) {
                weight = 1.0 / student.getScore();
            } else {
                weight = 1; // 给分数为0的学生一个较大的固定权重
            }
            weightMap.put(student, weight);
            totalWeight += weight;
        }

        // 生成随机数
        double randomValue = Math.random() * totalWeight;

        // 根据权重选择学生
        double cumulativeWeight = 0;
        for (Map.Entry<Student, Double> entry : weightMap.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (cumulativeWeight >= randomValue) {
                return entry.getKey();
            }
        }
        return null; // 若无符合条件的学生，返回null
    }


    public Result search(String no, String name, Integer pageNo, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();

        // 如果 no 和 name 不为空，则分别进行模糊查询
        queryWrapper
                .like(StringUtils.isNotBlank(no), Student::getNo, no)
                .or(StringUtils.isNotBlank(name)) // 仅在 name 不为空时才使用 or
                .like(StringUtils.isNotBlank(name), Student::getName, name)
                .eq(Student::getTeacherId, UserContext.getUser());
        System.out.println(queryWrapper.getTargetSql());

        // 分页查询
        Page<Student> page = new Page<>(pageNo, pageSize);
        Page<Student> result = page(page, queryWrapper);
        result.setTotal(result.getRecords().size());
        // 返回分页结果
        return Result.success(result);
    }


    @Override
    public Result importExcel(MultipartFile file) {
        // 读取数据并转换为 Student 对象
        List<Student> students = readStudentsFromExcel(file);

        // 保存到数据库
        if (!students.isEmpty()) {
            saveBatch(students);
        }
        stringCacheUtil.getInstance().delete(RedisConstant.STUDENT_LIST_KEY + UserContext.getUser());
        return Result.success("导入成功，已添加 " + students.size() + " 条记录");
    }

    private List<Student> readStudentsFromExcel(MultipartFile file) {
        List<Student> students = new ArrayList<>();

        // 创建 Excel 读取器并指定监听器
        try {
            EasyExcel.read(file.getInputStream(), StudentExcelModel.class, new ReadListener<StudentExcelModel>() {
                @Override
                public void invoke(StudentExcelModel data, AnalysisContext context) {
                    // 直接从第二行开始读取数据，跳过第一行
                    if (context.readRowHolder().getRowIndex() >= 1) {
                        Student student = new Student(null, null, null, null, (double) 0, Long.parseLong(UserContext.getUser()));
                        BeanUtil.copyProperties(data, student);
                        students.add(student);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                }
            }).sheet().doRead(); // 读取工作表
        } catch (IOException e) {
            System.out.println("读取错误");
        }
        return students;
    }

}
