package com.example.clinic.service;

import com.example.clinic.dto.ClinicBaseDto;
import com.example.clinic.exception.ClinicAlreadyExistException;
import com.example.clinic.exception.DomainObjectNotFoundException;
import com.example.clinic.mapper.ClinicMapper;
import com.example.clinic.model.Clinic;
import com.example.clinic.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;

    /**
     * Creates a new clinic using the provided clinicDto.
     *
     * @param clinicDto the DTO object containing clinic information
     * @return the created clinic object
     * @throws ClinicAlreadyExistException if a clinic already exists in the database
     */
    @Transactional
    public Clinic create(ClinicBaseDto clinicDto, byte[] logo) {
        if (clinicRepository.count() > 0) {
            throw new ClinicAlreadyExistException();
        }
        var clinic = clinicMapper.toEntity(clinicDto).setLogo(logo);
        return clinicRepository.save(clinic);
    }

    /**
     * Finds a clinic by its ID.
     *
     * @param clinicId the ID of the clinic
     * @return the clinic object if found, else throws a DomainObjectNotFoundException
     * @throws DomainObjectNotFoundException if the clinic is not found
     */
    @Transactional(readOnly = true)
    public Clinic findById(Long clinicId) {
        return clinicRepository.findById(clinicId).orElseThrow(() ->
                new DomainObjectNotFoundException("Could not find clinic with ID: " + clinicId));
    }

    /**
     * Updates a clinic with the provided clinicDto and logo bytes.
     *
     * @param clinicId  the ID of the clinic to update
     * @param clinicDto the DTO object containing clinic information
     * @param bytes     the logo bytes to set for the clinic
     * @return the updated clinic object
     * @throws DomainObjectNotFoundException if the clinic is not found
     */
    @Transactional
    public Clinic update(Long clinicId, ClinicBaseDto clinicDto, byte[] bytes) {
        var clinic = findById(clinicId);
        return clinicRepository.save(clinicMapper.toEntity(clinicDto, clinic)
                .setLogo(bytes));
    }
}