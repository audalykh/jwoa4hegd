package com.example.clinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SQLDelete(sql = "UPDATE user SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -9005156236410197640L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    @NotBlank
    @Size(max = 50)
    private String firstName;

    @Column
    @NotBlank
    @Size(max = 50)
    private String lastName;

    @Column
    @NotBlank
    @Email
    @Size(max = 128)
    private String email;

    @Column
    @NotBlank
    private LocalDateTime createdDate;

    @Column
    @NotBlank
    private LocalDateTime lastLogin;

    @Column
    private boolean deleted;
 }
