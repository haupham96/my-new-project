package com.example.aws_s3_upload.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "student",
        uniqueConstraints = @UniqueConstraint(name = "EMAIL_UNQ", columnNames = "emailId"))
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long studentID;
    private String firstName;
    private String lastName;
    private String emailId;

    /* embedded all field in Guardian.class */
    @Embedded
    private Guardian guardian;

}
