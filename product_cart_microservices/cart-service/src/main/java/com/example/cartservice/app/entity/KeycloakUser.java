package com.example.cartservice.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class KeycloakUser {
    /* claim preferred_username */
    @Id
    private String username;
    /* claim name */
    private String fullName;
    /* claim email */
    private String email;

    @OneToOne(mappedBy = "keycloakUser")
    private Cart cart;
}
