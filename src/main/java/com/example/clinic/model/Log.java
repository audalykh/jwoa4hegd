package com.example.clinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime timestamp;

    @NotNull
    @Column(nullable = false)
    @Enumerated
    private ActionType actionType;

    @Column
    @Enumerated
    private EntityType entityType;

    @NotNull
    @Column
    private Long entityId;

    @CreatedBy
    @Column(nullable = false)
    private Long actorId;
}