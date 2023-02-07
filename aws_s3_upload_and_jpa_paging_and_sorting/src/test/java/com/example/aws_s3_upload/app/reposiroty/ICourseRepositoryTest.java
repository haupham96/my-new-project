package com.example.aws_s3_upload.app.reposiroty;

import com.example.aws_s3_upload.app.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ICourseRepositoryTest {

    @Autowired
    ICourseRepository iCourseRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    public void pageing() {
        int pageNumber1 = 0;
        int recordNumber1 = 3;
        Pageable firstPageAndThreeRecords = PageRequest.of(pageNumber1, recordNumber1);
        Pageable secondPageWithTwoRecordsWithSort = PageRequest.of(1, 2, Sort.by("title").descending());
        Pageable thirdPageWithTwoRecordsWithSort = PageRequest.of(2, 2,
                Sort.by("title").descending()
                        .and(Sort.by("credit").ascending())
        );

        List<Course> list1 = iCourseRepository.findAll(firstPageAndThreeRecords).getContent();
        long totalElements = iCourseRepository.findAll(secondPageWithTwoRecordsWithSort).getTotalElements();
        long totalPages = iCourseRepository.findAll(secondPageWithTwoRecordsWithSort).getTotalPages();
    }
}