package com.drivercar.dataaccessobject;

import com.drivercar.DriverCarServerApplicantTestApplication;
import com.drivercar.domainobject.CarDO;
import com.drivercar.domainobject.DriverDO;
import com.drivercar.domainvalue.OnlineStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DriverCarServerApplicantTestApplication.class)
@ActiveProfiles("test")
public class DriverRepositoryTest {
    @Autowired
    DriverRepository driverRepository;

    @Test
    public void testCount() {
        Long count = driverRepository.count();
        assertEquals(count, new Long(8));
    }

    @Test
    public void testFindAll() {
        Iterable<DriverDO> driverDOIterable = driverRepository.findAll();
        assertEquals(((Collection<DriverDO>) driverDOIterable).size(), 8);
    }

    @Test
    public void testFindByOnlineStatus() {
        List<DriverDO> driverDOList = driverRepository.findByOnlineStatus(OnlineStatus.ONLINE);
        assertEquals(driverDOList.size(), 4);
    }

    @Test
    public void testFindByOfflineStatus() {
        List<DriverDO> driverDOList = driverRepository.findByOnlineStatus(OnlineStatus.OFFLINE);
        assertEquals(driverDOList.size(), 4);
    }

    @Test
    public void testFindByCar() {
        CarDO carDO = new CarDO();
        carDO.setId(1L);

        Optional<DriverDO> optionalDriverDO = driverRepository.findByCar(carDO);
        assertEquals(optionalDriverDO.isPresent(), true);
        assertEquals(optionalDriverDO.get().getId(), new Long(4));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testSave_UniqueUsernameException() {
        DriverDO driverDO = new DriverDO("driver01", "driver01pw");
        driverRepository.save(driverDO);
    }

    @Test
    public void testSave() {
        DriverDO driverDO = new DriverDO("driver09", "driver09pw");
        DriverDO driverDOResult = driverRepository.save(driverDO);
        assertEquals(driverDO, driverDOResult);

        driverRepository.delete(driverDOResult);
    }
}
