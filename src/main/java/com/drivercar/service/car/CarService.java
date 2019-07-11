package com.drivercar.service.car;

import com.drivercar.domainobject.CarDO;
import com.drivercar.exception.ConstraintsViolationException;
import com.drivercar.exception.EntityNotFoundException;

import java.util.List;
import java.util.Map;

public interface CarService {

    CarDO find(Long carId) throws EntityNotFoundException;

    CarDO create(CarDO carDO) throws ConstraintsViolationException, EntityNotFoundException;

    void delete(Long carId) throws EntityNotFoundException;

    void update(Map<String, Object> updates, Long id) throws ConstraintsViolationException, EntityNotFoundException;

    List<CarDO> find();

}
