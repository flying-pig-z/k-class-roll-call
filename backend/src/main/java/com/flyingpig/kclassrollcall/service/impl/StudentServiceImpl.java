package com.flyingpig.kclassrollcall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flyingpig.kclassrollcall.common.Result;
import com.flyingpig.kclassrollcall.common.RollCallMode;
import com.flyingpig.kclassrollcall.dto.req.StudentExcelModel;
import com.flyingpig.kclassrollcall.entity.Student;
import com.flyingpig.kclassrollcall.filter.UserContext;
import com.flyingpig.kclassrollcall.mapper.StudentMapper;
import com.flyingpig.kclassrollcall.service.IStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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

    @Override
    public Result rollCall(String mode) {
        if (!mode.equals(RollCallMode.EQUAL)) {
            return Result.success(rollBackStudentBaseScore());
        } else {
            return Result.success(this.baseMapper.rollCall(UserContext.getUser(), new Random().nextInt(count())));
        }
    }

    private Student rollBackStudentBaseScore() {
        // 获取符合条件的学生列表
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<Student>()
                .eq(Student::getTeacherId, UserContext.getUser());
        List<Student> students = this.baseMapper.selectList(queryWrapper);

        // 计算权重
        double totalWeight = 0;
        Map<Student, Double> weightMap = new HashMap<>();
        for (Student student : students) {
            double weight;
            if (student.getScore() > 0) {
                weight = 1.0 / student.getScore(); // 正常权重
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
        queryWrapper.like(StringUtils.isNotBlank(no), Student::getNo, no)
                .or()
                .like(StringUtils.isNotBlank(name), Student::getName, name)
                .eq(Student::getTeacherId, UserContext.getUser());

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
