package com.drivercar.controller.mapper;

import com.drivercar.datatransferobject.CarDTO;
import com.drivercar.domainobject.CarDO;
import com.drivercar.domainobject.ManufacturerDO;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CarMapper {
    public static CarDO makeCarDO(CarDTO carDTO) {
        ManufacturerDO manufacturerDO = null;
        if (carDTO.getManufacturer() != null) {
            manufacturerDO = ManufacturerMapper.makeManufacturerDO(carDTO.getManufacturer());
        }

        return new CarDO(carDTO.getLicensePlate(), carDTO.getSeatCount(), carDTO.getConvertible(), carDTO.getRating(), carDTO.getEngineType(), manufacturerDO);
    }

    public static CarDTO makeCarDTO(CarDO carDO) {
        CarDTO.CarDTOBuilder carDTOBuilder = CarDTO.newBuilder()
                .setId(carDO.getId())
                .setLicensePlate(carDO.getLicensePlate())
                .setSeatCount(carDO.getSeatCount())
                .setConvertible(carDO.getConvertible())
                .setRating(carDO.getRating())
                .setEngineType(carDO.getEngineType())
                .setDeleted(carDO.getDeleted());

        if (carDO.getManufacturer() != null) {
            carDTOBuilder.setManufacturerDTO(ManufacturerMapper.makeManufacturerDTO(carDO.getManufacturer()));
        }

        return carDTOBuilder.createCarDTO();
    }

    public static List<CarDTO> makeCarDTOList(Collection<CarDO> cars) {
        return cars.stream().
                map(CarMapper::makeCarDTO)
                .sorted(Comparator.comparing(car -> car.getLicensePlate()))
                .collect(Collectors.toList());
    }
}
