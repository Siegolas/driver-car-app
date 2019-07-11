package com.drivercar.controller.mapper;

import com.drivercar.datatransferobject.ManufacturerDTO;
import com.drivercar.domainobject.ManufacturerDO;

public class ManufacturerMapper {
    public static ManufacturerDO makeManufacturerDO(ManufacturerDTO manufacturerDTO) {
        return new ManufacturerDO(manufacturerDTO.getName());
    }

    public static ManufacturerDTO makeManufacturerDTO(ManufacturerDO manufacturerDO) {
        ManufacturerDTO.ManufacturerDTOBuilder manufacturerDTOBuilder = ManufacturerDTO.newBuilder()
                .setId(manufacturerDO.getId())
                .setName(manufacturerDO.getName());

        return manufacturerDTOBuilder.createManufacturerDTO();
    }
}
