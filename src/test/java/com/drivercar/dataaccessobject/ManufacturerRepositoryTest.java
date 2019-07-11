package com.drivercar.dataaccessobject;

import com.drivercar.DriverCarServerApplicantTestApplication;
import com.drivercar.domainobject.ManufacturerDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DriverCarServerApplicantTestApplication.class)
@ActiveProfiles("test")
public class ManufacturerRepositoryTest {
    @Autowired
    ManufacturerRepository manufacturerRepository;

    @Test
    public void testCount() {
        Long count = manufacturerRepository.count();
        assertEquals(count, new Long(2));
    }

    @Test
    public void testFindAll() {
        Iterable<ManufacturerDO> manufacturerDOIterable = manufacturerRepository.findAll();
        assertEquals(((Collection<ManufacturerDO>) manufacturerDOIterable).size(), 2);
    }

    @Test
    public void testFindByName() {
        Optional<ManufacturerDO> optionalManufacturerDO = manufacturerRepository.findByName("ALFA ROMEO");
        assertEquals(optionalManufacturerDO.isPresent(), true);
        assertEquals(optionalManufacturerDO.get().getId(), new Long(1));

        optionalManufacturerDO = manufacturerRepository.findByName("TOYOTA");
        assertEquals(optionalManufacturerDO.isPresent(), true);
        assertEquals(optionalManufacturerDO.get().getId(), new Long(2));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testSave_UniqueManufacturerNameException() {
        ManufacturerDO manufacturerDO = new ManufacturerDO("TOYOTA");
        manufacturerRepository.save(manufacturerDO);
    }

    @Test
    public void testSave() {
        ManufacturerDO manufacturerDO = new ManufacturerDO("SEAT");
        ManufacturerDO manufacturerDOResult = manufacturerRepository.save(manufacturerDO);
        assertEquals(manufacturerDO, manufacturerDOResult);

        manufacturerRepository.delete(manufacturerDOResult);
    }
}
