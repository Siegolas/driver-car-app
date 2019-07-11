package com.drivercar.dataaccessobject;

import com.drivercar.domainobject.CarDO;
import com.drivercar.domainobject.DriverDO;
import com.drivercar.domainvalue.OnlineStatus;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Database Access Object for driver table.
 * <p/>
 */
public interface DriverRepository extends CrudRepository<DriverDO, Long>, QuerydslPredicateExecutor<DriverDO> {

    List<DriverDO> findByOnlineStatus(OnlineStatus onlineStatus);

    Optional<DriverDO> findByCar(CarDO carDO);
}