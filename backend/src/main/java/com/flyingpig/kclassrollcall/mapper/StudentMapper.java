package com.flyingpig.kclassrollcall.mapper;

import com.flyingpig.kclassrollcall.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author flyingpig
 * @since 2024-09-25
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    Student rollCall(String teacherId, int randomIndex);
}
