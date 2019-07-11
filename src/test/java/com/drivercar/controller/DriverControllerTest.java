package com.drivercar.controller;

import com.drivercar.domainobject.DriverDO;
import com.drivercar.service.driver.DefaultDriverService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DriverController.class})
@WebMvcTest(DriverController.class)
@AutoConfigureMockMvc(secure = false)
public class DriverControllerTest {
    @MockBean
    DefaultDriverService defaultDriverServiceMock;
    @Autowired
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
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

    }

    @Test
    public void testAssignCarToDriver() throws Exception {
        String driverId = "1";
        String carId = "5";

        doNothing().when(defaultDriverServiceMock).assignCarToDriver(new Long(driverId), new Long(carId));

        this.mockMvc.perform(post("/v1/drivers/" + driverId + "/car/" + carId)
                .contentType(contentType)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(defaultDriverServiceMock, times(1)).assignCarToDriver(new Long(driverId), new Long(carId));
    }

    @Test
    public void testUnassignCarToDriver() throws Exception {
        String driverId = "1";

        doNothing().when(defaultDriverServiceMock).unassignCarFromDriver(new Long(driverId));

        this.mockMvc.perform(delete("/v1/drivers/" + driverId + "/car/")
                .contentType(contentType)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(defaultDriverServiceMock, times(1)).unassignCarFromDriver(new Long(driverId));
    }

    @Test
    public void testFindDriversByFilter() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("username", "username");
        map.put("onlinestatus", "ONLINE");
        map.put("licenseplate", "A1234UX");
        map.put("seatcount", 2);
        map.put("convertible", false);
        map.put("rating", 80);
        map.put("enginetype", "GASOLINE");
        map.put("manufacturer", "HONDA");

        List<DriverDO> driverDOList = new ArrayList<>();
        driverDOList.add(new DriverDO("driver1", "password1"));
        driverDOList.add(new DriverDO("driver2", "password2"));
        driverDOList.add(new DriverDO("driver3", "password3"));

        when(defaultDriverServiceMock.findByFilter(map)).thenReturn(driverDOList);

        this.mockMvc.perform(post("/v1/drivers/searches")
                .content(json(map))
                .contentType(contentType)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(driverDOList.size())))
                .andExpect(jsonPath("$[0].username", is("driver1")))
                .andExpect(jsonPath("$[1].username", is("driver2")))
                .andExpect(jsonPath("$[2].username", is("driver3")))
                .andDo(print());

        verify(defaultDriverServiceMock, times(1)).findByFilter(map);
    }

}
