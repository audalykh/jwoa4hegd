package com.example.clinic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Entity
public class Clinic implements Serializable {

    @Serial
    private static final long serialVersionUID = 7110993393388366374L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @NotBlank
    @Size(max = 100)
    private String name;

    @Column
    @NotBlank
    @Size(max = 255)
    private String address;

    @Column
    @NotBlank
    @Size(max = 32)
    private String phone;

    @NotNull
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "logo_id", referencedColumnName = "id")
    private Logo logo;

    @Column
    @NotNull
    @Min(0)
    @Max(24)
    private Integer fromHour;

    @Column
    @NotNull
    @Min(0)
    @Max(24)
    private Integer toHour;
}
