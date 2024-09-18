package com.example.clinic.service;

import com.example.clinic.dto.ClinicBaseDto;
import com.example.clinic.dto.ClinicDto;
import com.example.clinic.exception.ClinicAlreadyExistException;
import com.example.clinic.exception.DomainObjectNotFoundException;
import com.example.clinic.mapper.ClinicMapper;
import com.example.clinic.mapper.LogoMapper;
import com.example.clinic.model.Clinic;
import com.example.clinic.repository.ClinicRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;
    private final LogoMapper logoMapper;

    /**
     * Creates a new clinic using the provided clinicDto.
     *
     * @param clinicDto the DTO object containing clinic information
     * @param file     the logo file to set for the clinic
     * @return the created clinic object
     * @throws ClinicAlreadyExistException if a clinic already exists in the database
     */
    @Transactional
    public ClinicDto create(ClinicBaseDto clinicDto, MultipartFile file) throws IOException {
        if (clinicRepository.count() > 0) {
            throw new ClinicAlreadyExistException();
        }
        var logo = logoMapper.toEntity(file);
        var clinic = clinicMapper.toEntity(clinicDto).setLogo(logo);
        return clinicMapper.toDto(clinicRepository.save(clinic));
    }

    /**
     * Returns clinic information if the clinic exists.
     * @throws DomainObjectNotFoundException if the clinic is not found
     */
    @Transactional(readOnly = true)
    public ClinicDto getClinic() {
        return clinicMapper.toDto(getOne());
    }

    /**
     * Updates a clinic with the provided clinicDto and logo bytes.
     *
     * @param clinicDto the DTO object containing clinic information
     * @param file      the logo file to set for the clinic
     * @return the updated clinic object
     * @throws DomainObjectNotFoundException if the clinic is not found
     */
    @Transactional
    public ClinicDto update(ClinicBaseDto clinicDto, MultipartFile file) throws IOException {
        var clinic = getOne();

        clinic.setLogo(logoMapper.toEntity(file, clinic.getLogo()));
        clinic = clinicRepository.save(clinicMapper.toEntity(clinicDto, clinic));

        return clinicMapper.toDto(clinic);
    }

    private Clinic getOne() {
        Specification<Clinic> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        return clinicRepository.findOne(specification)
                .orElseThrow(() ->
                        new DomainObjectNotFoundException("Could not find existing clinic"));
    }
}