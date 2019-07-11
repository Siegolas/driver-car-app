package com.drivercar.controller;

import com.drivercar.controller.mapper.CarMapper;
import com.drivercar.datatransferobject.CarDTO;
import com.drivercar.domainobject.CarDO;
import com.drivercar.exception.ConstraintsViolationException;
import com.drivercar.exception.EntityNotFoundException;
import com.drivercar.service.car.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * All operations with a car will be routed by this controller.
 * <p/>
 */
@RestController
@RequestMapping("v1/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(final CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/{carId}")
    public CarDTO getCar(@PathVariable long carId) throws EntityNotFoundException {
        return CarMapper.makeCarDTO(carService.find(carId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarDTO createCar(@Valid @RequestBody CarDTO carDTO) throws ConstraintsViolationException, EntityNotFoundException {
        CarDO carDO = CarMapper.makeCarDO(carDTO);
        return CarMapper.makeCarDTO(carService.create(carDO));
    }

    @PatchMapping("/{carId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCar(@PathVariable long carId, @RequestBody Map<String, Object> updates) throws EntityNotFoundException, ConstraintsViolationException {
        carService.update(updates, carId);
    }

    @DeleteMapping("/{carId}")
    public void deleteCar(@PathVariable long carId) throws EntityNotFoundException {
        carService.delete(carId);
    }

    @GetMapping
    public List<CarDTO> findCars() {
        return CarMapper.makeCarDTOList(carService.find());
    }
}
