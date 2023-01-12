
package com.example.bt.app.entity;

import com.example.bt.common.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author : HauPV
 * class entity cho table user
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = TableName.APP_USER)
public class AppUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(unique = true)
    private String username;
    private String password;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Role role;

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof AppUser)) return false;
        return getUserId() == ((AppUser) obj).getUserId();
    }
}
