package com.drivercar.datatransferobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManufacturerDTO {
    @JsonIgnore
    private Long id;

    private String name;

    private ManufacturerDTO() {
    }

    private ManufacturerDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ManufacturerDTOBuilder newBuilder() {
        return new ManufacturerDTOBuilder();
    }

    @JsonProperty
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static class ManufacturerDTOBuilder {
        private Long id;
        private String name;

        public ManufacturerDTOBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public ManufacturerDTOBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ManufacturerDTO createManufacturerDTO() {
            return new ManufacturerDTO(id, name);
        }

    }
}
