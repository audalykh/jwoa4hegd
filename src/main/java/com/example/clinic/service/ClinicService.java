package com.example.clinic.service;

import com.example.clinic.dto.ClinicBaseDto;
import com.example.clinic.dto.ClinicDto;
import com.example.clinic.dto.LogoResourceDto;
import com.example.clinic.exception.ClinicAlreadyExistException;
import com.example.clinic.exception.DomainObjectNotFoundException;
import com.example.clinic.mapper.ClinicMapper;
import com.example.clinic.mapper.LogoMapper;
import com.example.clinic.model.Clinic;
import com.example.clinic.repository.ClinicRepository;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;
    private final LogoMapper logoMapper;

    /**
     * Creates a new clinic using the provided clinicDto and logo dto.
     * @throws ClinicAlreadyExistException if a clinic already exists in the database
     */
    @Transactional
    public ClinicDto create(ClinicBaseDto clinicDto, LogoResourceDto logoResourceDto) throws IOException {
        if (isClinicExist()) {
            throw new ClinicAlreadyExistException();
        }

        var logo = logoMapper.toEntity(logoResourceDto);
        var clinic = clinicMapper.toEntity(clinicDto).setLogo(logo);
        return clinicMapper.toDto(clinicRepository.save(clinic));
    }

    /**
     * Returns clinic information if the clinic exists.
     * @throws DomainObjectNotFoundException if the clinic is not found
     */
    @Transactional(readOnly = true)
    public ClinicDto getClinic() {
        return clinicMapper.toDto(getOneOrThrow());
    }

    /**
     * Updates a clinic with the provided clinicDto and logo dto.
     * @throws DomainObjectNotFoundException if the clinic is not found
     */
    @Transactional
    public ClinicDto update(ClinicBaseDto clinicDto, LogoResourceDto logoResourceDto) throws IOException {
        var clinic = getOneOrThrow();

        clinic.setLogo(logoMapper.toEntity(logoResourceDto, clinic.getLogo()));
        clinic = clinicRepository.save(clinicMapper.toEntity(clinicDto, clinic));

        return clinicMapper.toDto(clinic);
    }

    /**
     * Checks if at least one clinic exists in the database.
     */
    @Transactional(readOnly = true)
    public boolean isClinicExist() {
        return clinicRepository.exists(alwaysTrueSpec());
    }

    private Clinic getOneOrThrow() {
        return getOne().orElseThrow(() ->
                        new DomainObjectNotFoundException("Could not find existing clinic"));
    }

    private Optional<Clinic> getOne() {
        Specification<Clinic> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        return clinicRepository.findOne(specification);
    }

    private static Specification<Clinic> alwaysTrueSpec() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }
}