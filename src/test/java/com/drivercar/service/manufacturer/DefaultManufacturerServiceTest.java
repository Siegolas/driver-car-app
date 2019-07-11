package com.drivercar.service.manufacturer;

import com.drivercar.dataaccessobject.ManufacturerRepository;
import com.drivercar.domainobject.ManufacturerDO;
import com.drivercar.exception.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultManufacturerServiceTest {
    @Spy
    @InjectMocks
    DefaultManufacturerService defaultManufacturerService;

    @Mock
    ManufacturerRepository manufacturerRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        defaultManufacturerService = new DefaultManufacturerService(manufacturerRepository);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFindNyName_NotFound() throws EntityNotFoundException {
        String manufacturerName = "AUDI";
        when(manufacturerRepository.findByName(manufacturerName)).thenReturn(Optional.empty());

        defaultManufacturerService.findByName(manufacturerName);
    }

    @Test
    public void testFindNyName() throws EntityNotFoundException {
        String manufacturerName = "BMW";
        ManufacturerDO manufacturerDO = new ManufacturerDO(manufacturerName);
        when(manufacturerRepository.findByName(manufacturerName)).thenReturn(Optional.of(manufacturerDO));

        defaultManufacturerService.findByName(manufacturerName);
    }
}
