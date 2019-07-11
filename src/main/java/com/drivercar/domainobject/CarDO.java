package com.drivercar.domainobject;

import com.drivercar.domainvalue.EngineType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Table(
        name = "car",
        uniqueConstraints = @UniqueConstraint(name = "uc_licenseplate", columnNames = {"licenseplate"})
)
public class CarDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateCreated = ZonedDateTime.now();

    @Column(nullable = false)
    @NotNull(message = "LicensePlate can not be null!")
    private String licensePlate;

    @Column(nullable = false)
    @NotNull(message = "SeatCount can not be null!")
    private Integer seatCount;

    @Column(nullable = false)
    private Boolean convertible = false;

    private Integer rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EngineType engineType;

    @Column(nullable = false)
    private Boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id", referencedColumnName = "id")
    private ManufacturerDO manufacturer;

    public CarDO() {
    }

    public CarDO(String licensePlate, Integer seatCount, Boolean convertible, Integer rating, EngineType engineType, ManufacturerDO manufacturer) {
        this.licensePlate = licensePlate;
        this.seatCount = seatCount;
        this.convertible = convertible;
        this.rating = rating;
        this.engineType = engineType;
        this.manufacturer = manufacturer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public Boolean getConvertible() {
        return convertible;
    }

    public void setConvertible(Boolean convertible) {
        this.convertible = convertible;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public ManufacturerDO getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(ManufacturerDO manufacturer) {
        this.manufacturer = manufacturer;
    }
}
