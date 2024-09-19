package com.example.clinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE test SET deleted = true WHERE id = ?")
public class Test implements Serializable {

    @Serial
    private static final long serialVersionUID = 4090099392270642623L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", insertable = false, updatable = false)
    private Appointment appointment;

    @NotNull
    @Column(name = "appointment_id")
    private Long appointmentId;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TestType type;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime testDateTime;

    @Column
    @Enumerated(EnumType.STRING)
    private TestResult result;

    @Column
    private LocalDateTime resultDateTime;

    @Column
    private boolean deleted;
}