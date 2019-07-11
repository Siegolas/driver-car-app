package com.drivercar.controller;

import com.drivercar.controller.mapper.DriverMapper;
import com.drivercar.datatransferobject.DriverDTO;
import com.drivercar.domainobject.DriverDO;
import com.drivercar.domainvalue.OnlineStatus;
import com.drivercar.exception.ConstraintsViolationException;
import com.drivercar.exception.EntityNotFoundException;
import com.drivercar.service.driver.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * All operations with a driver will be routed by this controller.
 * <p/>
 */
@RestController
@RequestMapping("v1/drivers")
public class DriverController {

    private final DriverService driverService;

    @Autowired
    public DriverController(final DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/{driverId}")
    public DriverDTO getDriver(@PathVariable long driverId) throws EntityNotFoundException {
        return DriverMapper.makeDriverDTO(driverService.find(driverId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverDTO createDriver(@Valid @RequestBody DriverDTO driverDTO) throws ConstraintsViolationException {
        DriverDO driverDO = DriverMapper.makeDriverDO(driverDTO);
        return DriverMapper.makeDriverDTO(driverService.create(driverDO));
    }

    @DeleteMapping("/{driverId}")
    public void deleteDriver(@PathVariable long driverId) throws EntityNotFoundException {
        driverService.delete(driverId);
    }

    @PutMapping("/{driverId}")
    public void updateLocation(
            @PathVariable long driverId, @RequestParam double longitude, @RequestParam double latitude)
            throws EntityNotFoundException {
        driverService.updateLocation(driverId, longitude, latitude);
    }

    @GetMapping
    public List<DriverDTO> findDrivers(@RequestParam OnlineStatus onlineStatus) {
        return DriverMapper.makeDriverDTOList(driverService.find(onlineStatus));
    }

    @PatchMapping("/{driverId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long driverId, @RequestBody Map<String, Object> updates) throws EntityNotFoundException, ConstraintsViolationException {
        driverService.update(updates, driverId);
    }

    @PostMapping("/{driverId}/car/{carId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignCarToDriver(@PathVariable long driverId, @PathVariable long carId) throws EntityNotFoundException, ConstraintsViolationException {
        driverService.assignCarToDriver(driverId, carId);
    }

    @DeleteMapping("/{driverId}/car")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignCarFromDriver(@PathVariable long driverId) throws EntityNotFoundException, ConstraintsViolationException {
        driverService.unassignCarFromDriver(driverId);
    }

    @PostMapping("/searches")
    @ResponseStatus(HttpStatus.OK)
    public List<DriverDTO> findDriversByFilter(@RequestBody Map<String, Object> searchMap) throws ConstraintsViolationException {
        return DriverMapper.makeDriverDTOList(driverService.findByFilter(searchMap));
    }
}
