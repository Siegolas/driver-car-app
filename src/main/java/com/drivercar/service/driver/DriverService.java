package com.drivercar.service.driver;

import com.drivercar.domainobject.DriverDO;
import com.drivercar.domainvalue.OnlineStatus;
import com.drivercar.exception.ConstraintsViolationException;
import com.drivercar.exception.EntityNotFoundException;

import java.util.List;

public interface DriverService {

    DriverDO find(Long driverId) throws EntityNotFoundException;

    DriverDO create(DriverDO driverDO) throws ConstraintsViolationException;

    void delete(Long driverId) throws EntityNotFoundException;

    void updateLocation(long driverId, double longitude, double latitude) throws EntityNotFoundException;

    List<DriverDO> find(OnlineStatus onlineStatus);

}
