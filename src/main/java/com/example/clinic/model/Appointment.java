package com.example.clinic.model;

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
import org.hibernate.annotations.SQLOrder;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE appointment SET deleted = true WHERE id = ?")
public class Appointment extends AuditableEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2148186452067092804L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

    @Column(name = "patient_id")
    private Long patientId;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column
    private LocalDateTime revisitDateTime;

    @SQLOrder("test_date_time")
    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Test> tests;

    @Column
    private boolean deleted;
}