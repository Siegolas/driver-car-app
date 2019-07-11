package com.drivercar.datatransferobject;

import com.drivercar.domainvalue.EngineType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarDTO {
    @JsonIgnore
    private Long id;

    @NotNull(message = "LicensePlate can not be null!")
    private String licensePlate;

    @NotNull(message = "SeatCount can not be null!")
    private Integer seatCount;

    private Boolean convertible = false;

    private Integer rating;

    private EngineType engineType;

    private Boolean deleted = false;

    private ManufacturerDTO manufacturer;

    private CarDTO() {
    }

    private CarDTO(Long id, String licensePlate, Integer seatCount, Boolean convertible, Integer rating, EngineType engineType, Boolean deleted, ManufacturerDTO manufacturer) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.seatCount = seatCount;
        this.convertible = convertible;
        this.rating = rating;
        this.engineType = engineType;
        this.deleted = deleted;
        this.manufacturer = manufacturer;
    }

    public static CarDTOBuilder newBuilder() {
        return new CarDTOBuilder();
    }

    @JsonProperty
    public Long getId() {
        return id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public Boolean getConvertible() {
        return convertible;
    }

    public Integer getRating() {
        return rating;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public ManufacturerDTO getManufacturer() {
        return manufacturer;
    }

    public static class CarDTOBuilder {
        private Long id;
        private String licensePlate;
        private Integer seatCount;
        private Boolean convertible;
        private Integer rating;
        private EngineType engineType;
        private Boolean deleted;
        private ManufacturerDTO manufacturerDTO;

        public CarDTOBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public CarDTOBuilder setLicensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
            return this;
        }

        public CarDTOBuilder setSeatCount(Integer seatCount) {
            this.seatCount = seatCount;
            return this;
        }

        public CarDTOBuilder setConvertible(Boolean convertible) {
            this.convertible = convertible;
            return this;
        }

        public CarDTOBuilder setRating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public CarDTOBuilder setEngineType(EngineType engineType) {
            this.engineType = engineType;
            return this;
        }

        public CarDTOBuilder setDeleted(Boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public void setManufacturerDTO(ManufacturerDTO manufacturerDTO) {
            this.manufacturerDTO = manufacturerDTO;
        }

        public CarDTO createCarDTO() {
            return new CarDTO(id, licensePlate, seatCount, convertible, rating, engineType, deleted, manufacturerDTO);
        }

    }
}
