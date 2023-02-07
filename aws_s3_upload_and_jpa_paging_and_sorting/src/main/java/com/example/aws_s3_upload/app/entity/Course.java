package com.example.aws_s3_upload.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course {

    @Id
    @SequenceGenerator(
            name = "course_sequence",
            sequenceName = "course_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "course_sequence"
    )
    private Long courseId;
    private String title;
    private Integer credit;

    @OneToOne(
            mappedBy = "course"
    )
    private CourseMaterial courseMaterial;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "teacher_id",
            referencedColumnName = "teacherId"
    )
    private Teacher teacher;

    @ManyToMany(
            cascade = CascadeType.ALL
    )
    /* create new table with name = studen_course_map */
    @JoinTable(
            /* generate table name */
            name = "studen_course_map",
            joinColumns = @JoinColumn(
                    /* generate table with column name = corse_id */
                    name = "course_id",
                    /* reference to this.courseId */
                    referencedColumnName = "courseId"
            ),
            inverseJoinColumns = @JoinColumn(
                    /* generate table with column name = student_id */
                    name = "student_id",
                    /* reference to Student.studentId */
                    referencedColumnName = "studentId"
            )
    )
    private List<Student> students;

    public void addStudents(Student student) {
        if (students == null) students = new ArrayList<>();
        students.add(student);
    }
}
