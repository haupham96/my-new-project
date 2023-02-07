package com.example.aws_s3_upload.app.reposiroty;

import com.example.aws_s3_upload.app.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITeacherRepository extends JpaRepository<Teacher,Long> {
}
