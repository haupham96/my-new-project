package com.example.aws_s3_upload.app.reposiroty;

import com.example.aws_s3_upload.app.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICourseRepository extends JpaRepository<Course,Long> {
}
