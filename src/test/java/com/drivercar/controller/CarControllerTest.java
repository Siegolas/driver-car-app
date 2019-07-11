package com.drivercar.controller;

import com.drivercar.controller.mapper.CarMapper;
import com.drivercar.datatransferobject.CarDTO;
import com.drivercar.datatransferobject.ManufacturerDTO;
import com.drivercar.domainobject.CarDO;
import com.drivercar.domainobject.ManufacturerDO;
import com.drivercar.domainvalue.EngineType;
import com.drivercar.service.car.DefaultCarService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CarController.class})
@WebMvcTest(CarController.class)
@AutoConfigureMockMvc(secure = false)
public class CarControllerTest {
    @MockBean
    DefaultCarService defaultCarServiceMock;
    @Autowired
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private CarDTO carDTO;
    private CarDO carDO;
    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    public void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters).filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull(
                "the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    private String json(Object o) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String dtoAsString = mapper.writeValueAsString(o);
        return dtoAsString;
    }

    @Before
    public void setUp() {
        ManufacturerDTO.ManufacturerDTOBuilder manufacturerDTOBuilder = ManufacturerDTO.newBuilder();
        manufacturerDTOBuilder.setName("HONDA");
        ManufacturerDTO manufacturerDTO = manufacturerDTOBuilder.createManufacturerDTO();

        CarDTO.CarDTOBuilder carDTOBuilder = CarDTO.newBuilder();
        carDTOBuilder.setLicensePlate("B1234US")
                .setSeatCount(2)
                .setConvertible(false)
                .setRating(75)
                .setEngineType(EngineType.GASOLINE)
                .setDeleted(false)
                .setManufacturerDTO(manufacturerDTO);
        carDTO = carDTOBuilder.createCarDTO();
        carDO = CarMapper.makeCarDO(carDTO);
    }

    @Test
    public void testGetCar() throws Exception {
        String carId = "1";
        CarDO carDO;
        ManufacturerDO manufacturerDO;

        manufacturerDO = new ManufacturerDO("FIAT");
        carDO = new CarDO("B1234US", 5, false, 80, EngineType.DIESEL, manufacturerDO);

        when(defaultCarServiceMock.find(1L)).thenReturn(carDO);

        this.mockMvc.perform(get("/v1/cars/" + carId)
                .contentType(contentType)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate", is("B1234US")))
                .andExpect(jsonPath("$.seatCount", is(5)))
                .andExpect(jsonPath("$.convertible", is(false)))
                .andExpect(jsonPath("$.rating", is(80)))
                .andExpect(jsonPath("$.engineType", is("DIESEL")))
                .andExpect(jsonPath("$.deleted", is(false)))
                .andExpect(jsonPath("$.manufacturer.name", is("FIAT")))
                .andDo(print());

    }

    @Test
    public void testGetCars() throws Exception {
        List<CarDO> carDOList = new ArrayList<>();
        ManufacturerDO manufacturerDO = new ManufacturerDO("MERCEDES");
        carDOList.add(new CarDO("B2222US", 3, false, 85, EngineType.GASOLINE, manufacturerDO));
        carDOList.add(new CarDO("B1111US", 5, false, 55, EngineType.DIESEL, manufacturerDO));
        carDOList.add(new CarDO("B3333US", 4, false, 80, EngineType.HYBRID, manufacturerDO));
        when(defaultCarServiceMock.find()).thenReturn(carDOList);

        this.mockMvc.perform(get("/v1/cars")
                .contentType(contentType)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(carDOList.size())))
                .andExpect(jsonPath("$[0].licensePlate", is("B1111US")))
                .andExpect(jsonPath("$[1].licensePlate", is("B2222US")))
                .andExpect(jsonPath("$[2].licensePlate", is("B3333US")))
                .andDo(print());

        verify(defaultCarServiceMock, times(1)).find();
    }

    @Test
    public void testCreateCar() throws Exception {
        when(defaultCarServiceMock.create(any(CarDO.class))).thenReturn(carDO);

        this.mockMvc.perform(post("/v1/cars")
                .content(json(carDTO))
                .contentType(contentType)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licensePlate", is(carDTO.getLicensePlate())))
                .andExpect(jsonPath("$.seatCount", is(carDTO.getSeatCount())))
                .andExpect(jsonPath("$.convertible", is(carDTO.getConvertible())))
                .andExpect(jsonPath("$.rating", is(carDTO.getRating())))
                .andExpect(jsonPath("$.engineType", is(carDTO.getEngineType().toString())))
                .andExpect(jsonPath("$.deleted", is(carDTO.getDeleted())))
                .andExpect(jsonPath("$.manufacturer.name", is(carDTO.getManufacturer().getName())))
                .andDo(print());

        verify(defaultCarServiceMock, times(1)).create(any(CarDO.class));
    }

    @Test
    public void testUpdateCar() throws Exception {
        String carId = "1";

        Map<String, Object> map = new HashMap<>();
        map.put("engineType", carDO.getEngineType().toString());
        map.put("licensePlate", carDO.getLicensePlate());
        map.put("seatCount", carDO.getSeatCount());
        map.put("convertible", carDO.getConvertible());
        map.put("manufacturer", carDO.getManufacturer().getName());

        doNothing().when(defaultCarServiceMock).update(map, new Long(carId));

        this.mockMvc.perform(patch("/v1/cars/" + carId)
                .content(json(map))
                .contentType(contentType)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(defaultCarServiceMock, times(1)).update(map, new Long(carId));
    }

    @Test
    public void testDeleteCar() throws Exception {
        String carId = "1";

        doNothing().when(defaultCarServiceMock).delete(new Long(carId));

        this.mockMvc.perform(delete("/v1/cars/" + carId)
                .contentType(contentType)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        verify(defaultCarServiceMock, times(1)).delete(new Long(carId));
    }
}
