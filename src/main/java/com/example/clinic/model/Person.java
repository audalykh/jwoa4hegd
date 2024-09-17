package com.example.clinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SQLRestriction("deleted = false")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Person implements Serializable {

    protected static final String SQL_DELETE = "UPDATE person SET deleted = true WHERE id = ?";

    @Serial
    private static final long serialVersionUID = -9005156236410197640L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @NotBlank
    @Column
    @Size(max = 128)
    private String password;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime lastLoginDate;

    @Column
    private boolean deleted;
 }
