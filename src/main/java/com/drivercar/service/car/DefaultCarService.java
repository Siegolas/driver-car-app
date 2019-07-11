package com.drivercar.service.car;

import com.drivercar.dataaccessobject.CarRepository;
import com.drivercar.domainobject.CarDO;
import com.drivercar.domainobject.ManufacturerDO;
import com.drivercar.exception.ConstraintsViolationException;
import com.drivercar.exception.DriverIsDeletedException;
import com.drivercar.exception.EmptyMapException;
import com.drivercar.exception.EntityNotFoundException;
import com.drivercar.exception.EntityNotFoundRunTimeException;
import com.drivercar.service.manufacturer.ManufacturerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Service to encapsulate the link between DAO and controller and to have business logic for some car specific things.
 * <p/>
 */
@Service
public class DefaultCarService implements CarService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCarService.class);

    private final CarRepository carRepository;
    private final ManufacturerService manufacturerService;

    public DefaultCarService(final CarRepository carRepository, final ManufacturerService manufacturerService) {
        this.carRepository = carRepository;
        this.manufacturerService = manufacturerService;
    }

    /**
     * Selects a car by id.
     *
     * @param carId
     * @return found car
     * @throws EntityNotFoundException if no car with the given id was found.
     */
    @Override
    public CarDO find(Long carId) throws EntityNotFoundException {
        return findCarChecked(carId);
    }

    /**
     * Creates a new car.
     *
     * @param carDO
     * @return
     * @throws ConstraintsViolationException if a car already exists with the given licensePlate, ... .
     */
    @Override
    public CarDO create(CarDO carDO) throws EntityNotFoundException, ConstraintsViolationException {
        CarDO car;
        try {
            if (carDO.getManufacturer() != null) {
                ManufacturerDO manufacturerDO = manufacturerService.findByName(carDO.getManufacturer().getName());
                carDO.setManufacturer(manufacturerDO);
            }

            car = carRepository.save(carDO);
        } catch (DataIntegrityViolationException e) {
            LOG.warn("ConstraintsViolationException while creating a car: {}", carDO, e);
            throw new ConstraintsViolationException(e.getMessage());
        }
        return car;
    }

    /**
     * Deletes an existing car by id.
     *
     * @param carId
     * @throws EntityNotFoundException if no car with the given id was found.
     */
    @Override
    @Transactional
    public void delete(Long carId) throws EntityNotFoundException {
        CarDO carDO = findCarChecked(carId);
        carDO.setDeleted(true);
    }

    /**
     * Updates a car applying the values received in {@code updates} Map to the CarDO with id as {@code id}.
     *
     * @param updates
     * @param id
     */
    @Override
    @Transactional
    public void update(Map<String, Object> updates, Long id) throws ConstraintsViolationException, EntityNotFoundException {
        try {
            if (updates == null || updates.isEmpty()) {
                throw new EmptyMapException("Empty update map provided");
            }

            CarDO carDO = findCarChecked(id);
            if (carDO.getDeleted()) {
                throw new DriverIsDeletedException("Car with id " + id + " is deleted, can't update a deleted car");
            }

            updates.forEach((key, value) -> {
                // Use reflection to get field k on CarDO and set it to value k
                Field field = ReflectionUtils.findField(CarDO.class, key);
                ReflectionUtils.makeAccessible(field);
                if (field.getType().isEnum()) {
                    ReflectionUtils.setField(field, carDO, Enum.valueOf((Class<Enum>) field.getType(), (String) value));
                } else if (field.getType() == ManufacturerDO.class) {
                    ManufacturerDO manufacturerDO;
                    try {
                        manufacturerDO = manufacturerService.findByName((String) value);
                    } catch (EntityNotFoundException e) {
                        throw new EntityNotFoundRunTimeException(e.getMessage());
                    }
                    ReflectionUtils.setField(field, carDO, manufacturerDO);
                } else {
                    ReflectionUtils.setField(field, carDO, value);
                }
            });
        } catch (EntityNotFoundRunTimeException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            LOG.warn("ConstraintsViolationException while updating car with id: {}", id, e);
            throw new ConstraintsViolationException(e.getMessage());
        }
    }

    /**
     * Find all cars.
     *
     * @return
     */
    @Override
    public List<CarDO> find() {
        return carRepository.findAll();
    }

    /**
     * Find car with id {@code carId}.
     *
     * @param carId
     * @return
     * @throws EntityNotFoundException
     */
    private CarDO findCarChecked(Long carId) throws EntityNotFoundException {
        return carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: " + carId));
    }
}
