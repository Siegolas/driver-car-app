package com.drivercar.service.car;

import com.drivercar.dataaccessobject.CarRepository;
import com.drivercar.domainobject.CarDO;
import com.drivercar.domainobject.ManufacturerDO;
import com.drivercar.domainvalue.EngineType;
import com.drivercar.exception.ConstraintsViolationException;
import com.drivercar.exception.DriverIsDeletedException;
import com.drivercar.exception.EmptyMapException;
import com.drivercar.exception.EntityNotFoundException;
import com.drivercar.service.manufacturer.DefaultManufacturerService;
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

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCarServiceTest {
    @Spy
    @InjectMocks
    DefaultCarService defaultCarService;

    @Mock
    CarRepository carRepository;

    @Mock
    DefaultManufacturerService defaultManufacturerService;

    ManufacturerDO manufacturerDO;
    CarDO carDO;
    Optional<CarDO> optionalCarDO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        defaultCarService = new DefaultCarService(carRepository, defaultManufacturerService);
        manufacturerDO = new ManufacturerDO("FIAT");
        carDO = new CarDO("B1234US", 5, false, 80, EngineType.DIESEL, manufacturerDO);
        optionalCarDO = Optional.of(carDO);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFind_CarNotFound() throws EntityNotFoundException {
        Long id = new Long(1);
        when(carRepository.findById(id)).thenReturn(Optional.empty());

        defaultCarService.find(id);
    }

    @Test
    public void testFind_CarFound() throws EntityNotFoundException {
        Long id = new Long(1);
        when(carRepository.findById(id)).thenReturn(optionalCarDO);

        CarDO carDO = defaultCarService.find(id);
        Assert.assertEquals(this.carDO, carDO);
    }

    @Test
    public void testFindAll() {
        List<CarDO> carDOList = new ArrayList<>();
        carDOList.add(new CarDO());
        carDOList.add(new CarDO());
        carDOList.add(new CarDO());

        when(carRepository.findAll()).thenReturn(carDOList);

        List<CarDO> carDOListResult = defaultCarService.find();
        Assert.assertEquals(carDOListResult.size(), 3);
        Assert.assertEquals(carDOListResult, carDOList);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCreate_ManufacturerNotFound() throws EntityNotFoundException, ConstraintsViolationException {
        when(defaultManufacturerService.findByName(carDO.getManufacturer()
                .getName())).thenThrow(new EntityNotFoundException("Could not find entity with name: " + carDO.getManufacturer()));

        defaultCarService.create(carDO);
    }

    @Test(expected = ConstraintsViolationException.class)
    public void testCreate_DataIntegrityViolation() throws EntityNotFoundException, ConstraintsViolationException {
        when(carRepository.save(carDO)).thenThrow(new DataIntegrityViolationException(""));

        defaultCarService.create(carDO);
    }

    @Test
    public void testCreate() throws EntityNotFoundException, ConstraintsViolationException {
        when(carRepository.save(carDO)).thenReturn(carDO);

        CarDO carDOResult = defaultCarService.create(carDO);

        Assert.assertEquals(carDOResult, carDO);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDelete_NotFound() throws EntityNotFoundException {
        when(carRepository.findById(carDO.getId())).thenReturn(Optional.empty());

        defaultCarService.delete(carDO.getId());
    }

    @Test
    public void testDelete() throws EntityNotFoundException {
        when(carRepository.findById(carDO.getId())).thenReturn(Optional.of(carDO));

        defaultCarService.delete(carDO.getId());
    }

    @Test(expected = EmptyMapException.class)
    public void testUpdate_EmptyMap() throws ConstraintsViolationException, EntityNotFoundException {
        defaultCarService.update(null, carDO.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUpdate_NotFound() throws ConstraintsViolationException, EntityNotFoundException {
        Map<String, Object> map = new HashMap<>();
        map.put("engineType", "GASOLINE");

        when(carRepository.findById(carDO.getId())).thenReturn(Optional.empty());

        defaultCarService.update(map, carDO.getId());
    }

    @Test(expected = DriverIsDeletedException.class)
    public void testUpdate_CarIsDeleted() throws ConstraintsViolationException, EntityNotFoundException {
        Map<String, Object> map = new HashMap<>();
        map.put("engineType", "GASOLINE");

        carDO.setDeleted(true);
        when(carRepository.findById(carDO.getId())).thenReturn(Optional.of(carDO));

        defaultCarService.update(map, carDO.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_BadEngineType() throws ConstraintsViolationException, EntityNotFoundException {
        Map<String, Object> map = new HashMap<>();
        map.put("engineType", "OIL");

        when(carRepository.findById(carDO.getId())).thenReturn(Optional.of(carDO));

        defaultCarService.update(map, carDO.getId());
    }

    @Test(expected = ClassCastException.class)
    public void testUpdate_BadManufacturerFieldType() throws ConstraintsViolationException, EntityNotFoundException {
        Map<String, Object> map = new HashMap<>();
        map.put("manufacturer", true);

        when(carRepository.findById(carDO.getId())).thenReturn(Optional.of(carDO));

        defaultCarService.update(map, carDO.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUpdate_ManufacturerNotFound() throws ConstraintsViolationException, EntityNotFoundException {
        Map<String, Object> map = new HashMap<>();
        String manufacturerName = "BENTLEY";
        map.put("manufacturer", manufacturerName);

        when(carRepository.findById(carDO.getId())).thenReturn(Optional.of(carDO));
        when(defaultManufacturerService.findByName(manufacturerName)).thenThrow(new EntityNotFoundException("Could not find entity with name: " + carDO.getManufacturer()));

        defaultCarService.update(map, carDO.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_BadLicensePlateFieldType() throws ConstraintsViolationException, EntityNotFoundException {
        Map<String, Object> map = new HashMap<>();
        map.put("licensePlate", true);

        when(carRepository.findById(carDO.getId())).thenReturn(Optional.of(carDO));

        defaultCarService.update(map, carDO.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_BadSeatCountFieldType() throws ConstraintsViolationException, EntityNotFoundException {
        Map<String, Object> map = new HashMap<>();
        map.put("seatCount", true);

        when(carRepository.findById(carDO.getId())).thenReturn(Optional.of(carDO));

        defaultCarService.update(map, carDO.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_BadConvertibleFieldType() throws ConstraintsViolationException, EntityNotFoundException {
        Map<String, Object> map = new HashMap<>();
        map.put("convertible", 5);

        when(carRepository.findById(carDO.getId())).thenReturn(Optional.of(carDO));

        defaultCarService.update(map, carDO.getId());
    }

    @Test
    public void testUpdate() throws ConstraintsViolationException, EntityNotFoundException {
        Map<String, Object> map = new HashMap<>();
        map.put("engineType", carDO.getEngineType().toString());
        map.put("licensePlate", carDO.getLicensePlate());
        map.put("seatCount", carDO.getSeatCount());
        map.put("convertible", carDO.getConvertible());
        map.put("manufacturer", carDO.getManufacturer().getName());

        when(carRepository.findById(carDO.getId())).thenReturn(Optional.of(carDO));
        when(defaultManufacturerService.findByName(carDO.getManufacturer().getName())).thenReturn(manufacturerDO);

        defaultCarService.update(map, carDO.getId());
    }
}
