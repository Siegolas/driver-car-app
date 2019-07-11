package com.drivercar.service.manufacturer;

import com.drivercar.domainobject.ManufacturerDO;
import com.drivercar.exception.EntityNotFoundException;

public interface ManufacturerService {

    ManufacturerDO findByName(String name) throws EntityNotFoundException;

}
