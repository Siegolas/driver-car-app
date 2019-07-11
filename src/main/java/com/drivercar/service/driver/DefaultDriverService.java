package com.drivercar.service.driver;

import com.drivercar.dataaccessobject.DriverRepository;
import com.drivercar.domainobject.CarDO;
import com.drivercar.domainobject.DriverDO;
import com.drivercar.domainobject.QDriverDO;
import com.drivercar.domainvalue.EngineType;
import com.drivercar.domainvalue.GeoCoordinate;
import com.drivercar.domainvalue.OnlineStatus;
import com.drivercar.exception.CarAlreadyInUseException;
import com.drivercar.exception.CarIsDeletedException;
import com.drivercar.exception.ConstraintsViolationException;
import com.drivercar.exception.DriverIsDeletedException;
import com.drivercar.exception.DriverIsOfflineException;
import com.drivercar.exception.EntityNotFoundException;
import com.drivercar.exception.OperationNotAllowedException;
import com.drivercar.exception.SearchFilterException;
import com.drivercar.service.car.CarService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service to encapsulate the link between DAO and controller and to have business logic for some driver specific things.
 * <p/>
 */
@Service
public class DefaultDriverService implements DriverService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDriverService.class);

    private final DriverRepository driverRepository;
    private final CarService carService;

    public DefaultDriverService(final DriverRepository driverRepository, final CarService carService) {
        this.driverRepository = driverRepository;
        this.carService = carService;
    }

    /**
     * Selects a driver by id.
     *
     * @param driverId
     * @return found driver
     * @throws EntityNotFoundException if no driver with the given id was found.
     */
    @Override
    public DriverDO find(Long driverId) throws EntityNotFoundException {
        return findDriverChecked(driverId);
    }

    /**
     * Creates a new driver.
     *
     * @param driverDO
     * @return
     * @throws ConstraintsViolationException if a driver already exists with the given username, ... .
     */
    @Override
    public DriverDO create(DriverDO driverDO) throws ConstraintsViolationException {
        DriverDO driver;
        try {
            driver = driverRepository.save(driverDO);
        } catch (DataIntegrityViolationException e) {
            LOG.warn("ConstraintsViolationException while creating a driver: {}", driverDO, e);
            throw new ConstraintsViolationException(e.getMessage());
        }
        return driver;
    }

    /**
     * Deletes an existing driver by id.
     *
     * @param driverId
     * @throws EntityNotFoundException if no driver with the given id was found.
     */
    @Override
    @Transactional
    public void delete(Long driverId) throws EntityNotFoundException {
        DriverDO driverDO = findDriverChecked(driverId);
        driverDO.setDeleted(true);
    }

    /**
     * Update the location for a driver.
     *
     * @param driverId
     * @param longitude
     * @param latitude
     * @throws EntityNotFoundException
     */
    @Override
    @Transactional
    public void updateLocation(long driverId, double longitude, double latitude) throws EntityNotFoundException {
        DriverDO driverDO = findDriverChecked(driverId);
        driverDO.setCoordinate(new GeoCoordinate(latitude, longitude));
    }

    /**
     * Find all drivers by online state.
     *
     * @param onlineStatus
     */
    @Override
    public List<DriverDO> find(OnlineStatus onlineStatus) {
        return driverRepository.findByOnlineStatus(onlineStatus);
    }

    /**
     * Updates a car applying the values received in {@code updates} Map to the DriverDO with id equal to {@code id}.
     *
     * @param updates
     * @param id
     */
    @Override
    @Transactional
    public void update(Map<String, Object> updates, Long id) throws ConstraintsViolationException, EntityNotFoundException {
        try {
            DriverDO driverDO = findDriverChecked(id);
            if (driverDO.getDeleted()) {
                throw new DriverIsDeletedException("Driver with id " + id + " is deleted, can't update a deleted driver");
            }

            updates.forEach((key, value) -> {
                // Use reflection to get field k on CarDO and set it to value k
                Field field = ReflectionUtils.findField(DriverDO.class, key);
                ReflectionUtils.makeAccessible(field);
                if (field.getType().isEnum()) {
                    ReflectionUtils.setField(field, driverDO, Enum.valueOf((Class<Enum>) field.getType(), (String) value));
                } else if (field.getType() == CarDO.class) {
                    throw new OperationNotAllowedException("Update on field " + key + " is not allowed");
                } else {
                    ReflectionUtils.setField(field, driverDO, value);
                }
            });
        } catch (DataIntegrityViolationException e) {
            LOG.warn("ConstraintsViolationException while updating driver with id: {}", id, e);
            throw new ConstraintsViolationException(e.getMessage());
        }
    }

    /**
     * Assigns the car {@code carId} to the the driver {@code driverId}.
     *
     * @param driverId
     * @param carId
     * @throws EntityNotFoundException
     */
    @Override
    @Transactional
    public void assignCarToDriver(Long driverId, Long carId) throws EntityNotFoundException {
        DriverDO driverDO = findDriverChecked(driverId);
        if (driverDO.getDeleted()) {
            throw new DriverIsDeletedException("Driver with id " + driverId + " is deleted, can't perform operation");
        }

        if (driverDO.getOnlineStatus() == OnlineStatus.OFFLINE) {
            throw new DriverIsOfflineException("Driver with id " + driverId + " is offline, can't assign car to a offline driver");
        }

        // Find the car
        CarDO carDO = carService.find(carId);

        // Check deleted car
        if (carDO.getDeleted()) {
            throw new CarIsDeletedException("Car with id " + carId + " is deleted, can't assign a deleted car to a driver");
        }

        // Check if car is assigned to a driver
        Optional<DriverDO> optionalDriverAssignedToCarDO = driverRepository.findByCar(carDO);

        //Car is assigned to driver
        if (optionalDriverAssignedToCarDO.isPresent()) {
            //Car driver is different to applied id
            if (optionalDriverAssignedToCarDO.get().getId() != driverId) {
                throw new CarAlreadyInUseException("Car with id " + carId + " is already in use by driver with id " + optionalDriverAssignedToCarDO.get()
                        .getId());
            }
        } else {
            driverDO.setCar(carDO);
        }
    }

    /**
     * Unassigns car from the driver {@code driverId}.
     *
     * @param driverId
     * @throws EntityNotFoundException
     */
    @Override
    @Transactional
    public void unassignCarFromDriver(Long driverId) throws EntityNotFoundException {
        DriverDO driverDO = findDriverChecked(driverId);
        if (driverDO.getDeleted()) {
            throw new DriverIsDeletedException("Driver with id " + driverId + " is deleted, can't perform operation");
        }
        driverDO.setCar(null);
    }

    /**
     * Find drivers satisfying filter specified by the map {@code searchMap}.
     *
     * @param searchMap
     * @return list of matching Drivers
     */
    @Override
    public List<DriverDO> findByFilter(Map<String, Object> searchMap) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // Username driver property
        if (searchMap.get("username") != null) {
            if (!(searchMap.get("username") instanceof String)) {
                throw new SearchFilterException("Bad username value, must be a String");
            }

            BooleanExpression beDriverUserName = QDriverDO.driverDO.username.containsIgnoreCase((String) searchMap.get("username"));
            booleanBuilder.and(beDriverUserName);
        }

        // Online status driver property
        if (searchMap.get("onlinestatus") != null) {
            if (!(searchMap.get("onlinestatus") instanceof String)) {
                throw new SearchFilterException("Bad onlinestatus value, must be a String");
            }

            try {
                OnlineStatus onlineStatus = OnlineStatus.valueOf((String) searchMap.get("onlinestatus"));
                BooleanExpression beDriverOnlineStatus = QDriverDO.driverDO.onlineStatus.eq(onlineStatus);
                booleanBuilder.and(beDriverOnlineStatus);
            } catch (IllegalArgumentException e) {
                throw new SearchFilterException("Bad onlinestatus value, doesn't match Enum values");
            }
        }

        // License plate car property
        if (searchMap.get("licenseplate") != null) {
            if (!(searchMap.get("licenseplate") instanceof String)) {
                throw new SearchFilterException("Bad licenseplate value, must be a String");
            }

            BooleanExpression beDriversCarLicensePlate = QDriverDO.driverDO.car.licensePlate.containsIgnoreCase((String) searchMap.get("licenseplate"));
            booleanBuilder.and(beDriversCarLicensePlate);
        }

        // Seat count car property
        if (searchMap.get("seatcount") != null) {
            if (!(searchMap.get("seatcount") instanceof Integer)) {
                throw new SearchFilterException("Bad seatcount value, must be a Integer");
            }

            BooleanExpression beSeatCount = QDriverDO.driverDO.car.seatCount.goe((Integer) searchMap.get("seatcount"));
            booleanBuilder.and(beSeatCount);
        }

        // Convertible car property
        if (searchMap.get("convertible") != null) {
            if (!(searchMap.get("convertible") instanceof Boolean)) {
                throw new SearchFilterException("Bad convertible value, must be a Boolean");
            }

            BooleanExpression beConvertible = QDriverDO.driverDO.car.convertible.eq((Boolean) searchMap.get("convertible"));
            booleanBuilder.and(beConvertible);
        }

        // Rating car property
        if (searchMap.get("rating") != null) {
            if (!(searchMap.get("rating") instanceof Integer)) {
                throw new SearchFilterException("Bad rating value, must be a Integer");
            }

            BooleanExpression beDriversCarRating = QDriverDO.driverDO.car.rating.goe((Integer) searchMap.get("rating"));
            booleanBuilder.and(beDriversCarRating);
        }

        // Engine type car property
        if (searchMap.get("enginetype") != null) {
            if (!(searchMap.get("enginetype") instanceof String)) {
                throw new SearchFilterException("Bad enginetype value, must be a String");
            }

            try {
                EngineType engineType = EngineType.valueOf((String) searchMap.get("enginetype"));
                BooleanExpression beDriverOnlineStatus = QDriverDO.driverDO.car.engineType.eq(engineType);
                booleanBuilder.and(beDriverOnlineStatus);
            } catch (IllegalArgumentException e) {
                throw new SearchFilterException("Bad enginetype value, doesn't match Enum values");
            }
        }

        // Manufacturer car property
        if (searchMap.get("manufacturer") != null) {
            if (!(searchMap.get("manufacturer") instanceof String)) {
                throw new SearchFilterException("Bad manufacturer value, must be a String");
            }

            BooleanExpression beManufacturer = QDriverDO.driverDO.car.manufacturer.name.containsIgnoreCase((String) searchMap.get("manufacturer"));
            booleanBuilder.and(beManufacturer);
        }

        Iterable<DriverDO> iterable = driverRepository.findAll(booleanBuilder);
        List<DriverDO> employees = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());

        return employees;
    }

    /**
     * Find driver with id {@code driverId}.
     *
     * @param driverId
     * @return
     * @throws EntityNotFoundException
     */
    private DriverDO findDriverChecked(Long driverId) throws EntityNotFoundException {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: " + driverId));
    }
}
