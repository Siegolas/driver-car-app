package com.drivercar.dataaccessobject;

import com.drivercar.DriverCarServerApplicantTestApplication;
import com.drivercar.domainobject.CarDO;
import com.drivercar.domainobject.ManufacturerDO;
import com.drivercar.domainvalue.EngineType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DriverCarServerApplicantTestApplication.class)
@ActiveProfiles("test")
public class CarRepositoryTest {
    @Autowired
    CarRepository carRepository;

    @Autowired
    ManufacturerRepository manufacturerRepository;

    @Test
    public void testCount() {
        Long count = carRepository.count();
        assertEquals(count, new Long(2));
    }

    @Test
    public void testFindAll() {
        List<CarDO> carDOList = carRepository.findAll();
        assertEquals(carDOList.size(), 2);
    }

    @Test
    public void testFindById() {
        Optional<CarDO> optionalCarDO = carRepository.findById(1L);
        assertEquals(optionalCarDO.isPresent(), true);
        assertEquals(optionalCarDO.get().getLicensePlate(), "B9863US");
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testSave_UniqueLicensePlateException() {
        Optional<ManufacturerDO> optionalManufacturerDO = manufacturerRepository.findByName("TOYOTA");
        CarDO carDO = new CarDO("B9863US", 5, false, 80, EngineType.DIESEL, optionalManufacturerDO.get());
        carRepository.save(carDO);
    }

    @Test
    public void testSave() {
        Optional<ManufacturerDO> optionalManufacturerDO = manufacturerRepository.findByName("TOYOTA");
        CarDO carDO = new CarDO("A9631XZ", 5, false, 80, EngineType.DIESEL, optionalManufacturerDO.get());
        CarDO carDOResult = carRepository.save(carDO);
        assertEquals(carDO, carDOResult);

        carRepository.delete(carDOResult);
    }
}
