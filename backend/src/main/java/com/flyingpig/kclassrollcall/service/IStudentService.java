package com.flyingpig.kclassrollcall.service;

import com.flyingpig.kclassrollcall.common.Result;
import com.flyingpig.kclassrollcall.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author flyingpig
 * @since 2024-09-25
 */
public interface IStudentService extends IService<Student> {

    Result rollCall(String mode);

    Result search(String no, String name, Integer pageNo, Integer pageSize);

    Result importExcel(MultipartFile file);
}
