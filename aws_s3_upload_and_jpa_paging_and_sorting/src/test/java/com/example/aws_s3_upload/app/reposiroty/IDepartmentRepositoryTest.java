package com.example.aws_s3_upload.app.reposiroty;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class IDepartmentRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    IDepartmentRepository iDepartmentRepository;

    @BeforeEach
    void setUp() {
    }
}