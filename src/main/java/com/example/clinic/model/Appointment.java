package com.example.clinic.model;

import com.example.clinic.validation.RevisitTimeValid;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@SQLDelete(sql = "UPDATE appointment SET deleted = true WHERE id = ?")
public class Appointment implements Serializable {

    @Serial
    private static final long serialVersionUID = 2148186452067092804L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_created_id")
    private Doctor doctorCreated;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_updated_id")
    private Doctor doctorUpdated;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column
    private LocalDateTime updatedAt;

    @Column
    @RevisitTimeValid
    private LocalDateTime revisitDateTime;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Test> tests;

    @Column
    private boolean deleted;
}