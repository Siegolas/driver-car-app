package com.drivercar.service.manufacturer;

import com.drivercar.dataaccessobject.ManufacturerRepository;
import com.drivercar.domainobject.ManufacturerDO;
import com.drivercar.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service to encapsulate the link between DAO and controller and to have business logic for some manufacturer specific things.
 * <p/>
 */
@Service
public class DefaultManufacturerService implements ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;

    public DefaultManufacturerService(final ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    @Override
    public ManufacturerDO findByName(String name) throws EntityNotFoundException {
        return manufacturerRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Could not find entity with name: " + name));
    }
}
