package com.example.clinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The {@code AuditableEntity} class represents an auditable entity in the system.
 * It is a base class that provides auditing functionality for entities that need to track creation, modification,
 * and deletion information.
 *
 * <p>The class is annotated with {@code @MappedSuperclass} to indicate that it is not an entity itself, but it is
 * mapped to the entity subclasses.
 * It is also annotated with {@code @EntityListeners(AuditingEntityListener.class)} to enable auditing and
 * automatically populate the auditing fields.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 *     @Entity
 *     public class Appointment extends AuditableEntity {
 *         // Entity fields and associations
 *     }
 * }</pre>
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditableEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", insertable = false, updatable = false)
    private Doctor createdBy;

    @CreatedBy
    @Column(name = "created_by_id")
    private Long createdById;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id", insertable = false, updatable = false)
    private Doctor updatedBy;

    @LastModifiedBy
    @Column(name = "updated_by_id")
    private Long updatedById;

    @Column
    @LastModifiedDate
    private LocalDateTime updatedAt;
}