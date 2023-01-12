
package com.example.bt.app.entity;

import javax.persistence.*;

import com.example.bt.common.TableName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author : HauPV
 * class entity cho table role
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableName.ROLE)
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int roleId;
    private String roleName;

    @OneToMany(mappedBy = "role")
    private List<AppUser> user;
}
