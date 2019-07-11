package com.drivercar.dataaccessobject;

import com.drivercar.domainobject.ManufacturerDO;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Database Access Object for manufacturer table.
 * <p/>
 */
public interface ManufacturerRepository extends CrudRepository<ManufacturerDO, Long> {
    Optional<ManufacturerDO> findByName(String name);
}
