package com.drivercar.service.driver;

import com.drivercar.dataaccessobject.DriverRepository;
import com.drivercar.domainobject.CarDO;
import com.drivercar.domainobject.DriverDO;
import com.drivercar.domainvalue.OnlineStatus;
import com.drivercar.exception.CarAlreadyInUseException;
import com.drivercar.exception.CarIsDeletedException;
import com.drivercar.exception.ConstraintsViolationException;
import com.drivercar.exception.DriverIsDeletedException;
import com.drivercar.exception.DriverIsOfflineException;
import com.drivercar.exception.EntityNotFoundException;
import com.drivercar.exception.SearchFilterException;
import com.drivercar.service.car.DefaultCarService;
import com.querydsl.core.BooleanBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDriverServiceTest {
    @Spy
    @InjectMocks
    DefaultDriverService defaultDriverService;

    @Mock
    DriverRepository driverRepository;

    @Mock
    DefaultCarService defaultCarService;

    DriverDO driverDO;
    Optional<DriverDO> optionalDriverDO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        defaultDriverService = new DefaultDriverService(driverRepository, defaultCarService);
        driverDO = new DriverDO("driver", "password");
        optionalDriverDO = Optional.of(driverDO);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFind_DriverNotFound() throws EntityNotFoundException {
        Long id = new Long(1);
        when(driverRepository.findById(id)).thenReturn(Optional.empty());

        defaultDriverService.find(id);
    }

    @Test
    public void testFind_DriverFound() throws EntityNotFoundException {
        Long id = new Long(1);
        when(driverRepository.findById(id)).thenReturn(optionalDriverDO);

        DriverDO driverDO = defaultDriverService.find(id);
        Assert.assertEquals(this.driverDO, driverDO);
    }

    @Test
    public void testFindByOnlineStatus() {
        List<DriverDO> driverDOList = new ArrayList<>();
        driverDOList.add(new DriverDO("driver1", "password1"));
        driverDOList.add(new DriverDO("driver2", "password2"));
        driverDOList.add(new DriverDO("driver3", "password3"));

        when(driverRepository.findByOnlineStatus(OnlineStatus.OFFLINE)).thenReturn(driverDOList);

        List<DriverDO> driverDOResultList = defaultDriverService.find(OnlineStatus.OFFLINE);
        Assert.assertEquals(driverDOResultList.size(), 3);
        Assert.assertEquals(driverDOResultList, driverDOList);
    }

    @Test(expected = ConstraintsViolationException.class)
    public void testCreate_ConstraintsViolationException() throws ConstraintsViolationException {
        when(driverRepository.save(driverDO)).thenThrow(new DataIntegrityViolationException(""));

        defaultDriverService.create(driverDO);
    }

    @Test
    public void testCreate() throws ConstraintsViolationException {
        when(driverRepository.save(driverDO)).thenReturn(driverDO);

        DriverDO driverDOResult = defaultDriverService.create(driverDO);

        Assert.assertEquals(driverDOResult, driverDO);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDelete_NotFound() throws EntityNotFoundException {
        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.empty());

        defaultDriverService.delete(driverDO.getId());
    }

    @Test
    public void testDelete() throws EntityNotFoundException {
        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.of(driverDO));

        defaultDriverService.delete(driverDO.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUpdateLocation_NotFound() throws EntityNotFoundException {
        double doubleValue = 0;
        driverDO.setId(1L);
        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.empty());

        defaultDriverService.updateLocation(driverDO.getId(), doubleValue, doubleValue);
    }

    @Test
    public void testUpdateLocation() throws EntityNotFoundException {
        double doubleValue = 0;
        driverDO.setId(1L);
        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.of(driverDO));

        defaultDriverService.updateLocation(driverDO.getId(), doubleValue, doubleValue);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testAssignCarToDriver_DriverNotFound() throws EntityNotFoundException {
        Long driverId = new Long(1);
        Long carId = new Long(1);
        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        defaultDriverService.assignCarToDriver(driverId, carId);
    }

    @Test(expected = DriverIsDeletedException.class)
    public void testAssignCarToDriver_DriverIsDeleted() throws EntityNotFoundException {
        Long carId = new Long(1);
        driverDO.setDeleted(true);
        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.of(driverDO));

        defaultDriverService.assignCarToDriver(driverDO.getId(), carId);
    }

    @Test(expected = DriverIsOfflineException.class)
    public void testAssignCarToDriver_DriverIsOffline() throws EntityNotFoundException {
        Long carId = new Long(1);
        driverDO.setOnlineStatus(OnlineStatus.OFFLINE);
        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.of(driverDO));

        defaultDriverService.assignCarToDriver(driverDO.getId(), carId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testAssignCarToDriver_CarNotFound() throws EntityNotFoundException {
        Long carId = new Long(1);
        driverDO.setOnlineStatus(OnlineStatus.ONLINE);
        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.of(driverDO));
        when(defaultCarService.find(carId)).thenThrow(new EntityNotFoundException(""));

        defaultDriverService.assignCarToDriver(driverDO.getId(), carId);
    }

    @Test(expected = CarIsDeletedException.class)
    public void testAssignCarToDriver_CarIsDeletedFound() throws EntityNotFoundException {
        Long carId = new Long(1);
        driverDO.setOnlineStatus(OnlineStatus.ONLINE);
        CarDO carDO = new CarDO();
        carDO.setDeleted(true);

        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.of(driverDO));
        when(defaultCarService.find(carId)).thenReturn(carDO);

        defaultDriverService.assignCarToDriver(driverDO.getId(), carId);
    }

    @Test(expected = CarAlreadyInUseException.class)
    public void testAssignCarToDriver_CarAlreadyAssigned() throws EntityNotFoundException {
        Long carId = new Long(1);
        driverDO.setOnlineStatus(OnlineStatus.ONLINE);
        driverDO.setId(1L);
        CarDO carDO = new CarDO();
        DriverDO anotherDriverDO = new DriverDO("driver", "password");
        anotherDriverDO.setId(2L);

        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.of(driverDO));
        when(defaultCarService.find(carId)).thenReturn(carDO);
        when(driverRepository.findByCar(carDO)).thenReturn(Optional.of(anotherDriverDO));

        defaultDriverService.assignCarToDriver(driverDO.getId(), carId);
    }

    @Test
    public void testAssignCarToDriver() throws EntityNotFoundException {
        Long carId = new Long(1);
        driverDO.setOnlineStatus(OnlineStatus.ONLINE);
        driverDO.setId(1L);
        CarDO carDO = new CarDO();

        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.of(driverDO));
        when(defaultCarService.find(carId)).thenReturn(carDO);
        when(driverRepository.findByCar(carDO)).thenReturn(Optional.empty());

        defaultDriverService.assignCarToDriver(driverDO.getId(), carId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUnassignCarFromDriver_DriverNotFound() throws EntityNotFoundException {
        Long driverId = new Long(1);
        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        defaultDriverService.unassignCarFromDriver(driverId);
    }

    @Test(expected = DriverIsDeletedException.class)
    public void testUnassignCarToDriver_DriverIsDeleted() throws EntityNotFoundException {
        driverDO.setDeleted(true);
        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.of(driverDO));

        defaultDriverService.unassignCarFromDriver(driverDO.getId());
    }

    @Test
    public void testUnassignCarToDriver() throws EntityNotFoundException {
        when(driverRepository.findById(driverDO.getId())).thenReturn(Optional.of(driverDO));

        defaultDriverService.unassignCarFromDriver(driverDO.getId());
    }

    @Test(expected = SearchFilterException.class)
    public void testFindByFilter_UsernameSearchFilterException() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", true);

        defaultDriverService.findByFilter(map);
    }

    @Test(expected = SearchFilterException.class)
    public void testFindByFilter_OnlineStatusSearchFilterException() {
        Map<String, Object> map = new HashMap<>();
        map.put("onlinestatus", true);

        defaultDriverService.findByFilter(map);
    }

    @Test(expected = SearchFilterException.class)
    public void testFindByFilter_OnlineStatusSearchFilterExceptionNotEnumValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("onlinestatus", "online");

        defaultDriverService.findByFilter(map);
    }

    @Test(expected = SearchFilterException.class)
    public void testFindByFilter_LicensePlateSearchFilterException() {
        Map<String, Object> map = new HashMap<>();
        map.put("licenseplate", true);

        defaultDriverService.findByFilter(map);
    }

    @Test(expected = SearchFilterException.class)
    public void testFindByFilter_SeatCountSearchFilterException() {
        Map<String, Object> map = new HashMap<>();
        map.put("seatcount", true);

        defaultDriverService.findByFilter(map);
    }

    @Test(expected = SearchFilterException.class)
    public void testFindByFilter_ConvertibleSearchFilterException() {
        Map<String, Object> map = new HashMap<>();
        map.put("convertible", "yes");

        defaultDriverService.findByFilter(map);
    }

    @Test(expected = SearchFilterException.class)
    public void testFindByFilter_RatingSearchFilterException() {
        Map<String, Object> map = new HashMap<>();
        map.put("rating", "excellent");

        defaultDriverService.findByFilter(map);
    }

    @Test(expected = SearchFilterException.class)
    public void testFindByFilter_EngineTypeSearchFilterException() {
        Map<String, Object> map = new HashMap<>();
        map.put("enginetype", true);

        defaultDriverService.findByFilter(map);
    }

    @Test(expected = SearchFilterException.class)
    public void testFindByFilter_EngineTypeSearchFilterExceptionNotEnumValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("enginetype", "OIL");

        defaultDriverService.findByFilter(map);
    }

    @Test(expected = SearchFilterException.class)
    public void testFindByFilter_ManufacturerSearchFilterException() {
        Map<String, Object> map = new HashMap<>();
        map.put("manufacturer", true);

        defaultDriverService.findByFilter(map);
    }

    @Test
    public void testFindByFilter() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", "username");
        map.put("onlinestatus", "ONLINE");
        map.put("licenseplate", "A1234UX");
        map.put("seatcount", 2);
        map.put("convertible", false);
        map.put("rating", 80);
        map.put("enginetype", "GASOLINE");
        map.put("manufacturer", "HONDA");

        List<DriverDO> driverDOList = new ArrayList<>();
        driverDOList.add(new DriverDO("driver1", "password1"));
        driverDOList.add(new DriverDO("driver2", "password2"));
        driverDOList.add(new DriverDO("driver3", "password3"));

        when(driverRepository.findAll(any(BooleanBuilder.class))).thenReturn(driverDOList);

        List<DriverDO> driverDOListResult = defaultDriverService.findByFilter(map);
        Assert.assertEquals(driverDOList.size(), driverDOListResult.size());
        Assert.assertEquals(driverDOList, driverDOListResult);
    }
}
