package com.drivercar.service.driver;

import com.drivercar.domainobject.DriverDO;
import com.drivercar.domainvalue.OnlineStatus;
import com.drivercar.exception.ConstraintsViolationException;
import com.drivercar.exception.EntityNotFoundException;

import java.util.List;
import java.util.Map;

public interface DriverService {

    DriverDO find(Long driverId) throws EntityNotFoundException;

    DriverDO create(DriverDO driverDO) throws ConstraintsViolationException;

    void delete(Long driverId) throws EntityNotFoundException;

    void updateLocation(long driverId, double longitude, double latitude) throws EntityNotFoundException;

    List<DriverDO> find(OnlineStatus onlineStatus);

    void update(Map<String, Object> updates, Long id) throws ConstraintsViolationException, EntityNotFoundException;

    void assignCarToDriver(Long driverId, Long carId) throws EntityNotFoundException;

    void unassignCarFromDriver(Long driverId) throws EntityNotFoundException;

    List<DriverDO> findByFilter(Map<String, Object> searchMap);

}
