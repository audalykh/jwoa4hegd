package com.example.clinic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SQLDelete(sql ="UPDATE person SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Person implements Serializable {

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

    @NotNull
    @Column(insertable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime lastLogin;

    @Column
    private boolean deleted;
 }
