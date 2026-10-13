package team2.carservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team2.carservice.dto.AssembleCarRequestDTO;
import team2.carservice.dto.CarDTO;
import team2.carservice.dto.CreateCarDTO;
import team2.carservice.entity.CarStatus;
import team2.carservice.exception.CarAssemblyException;
import team2.carservice.exception.CarNotFoundException;
import team2.carservice.exception.GlobalExceptionHandler;
import team2.carservice.facade.CarFacade;
import team2.carservice.util.ObjectConverter;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarFacade carFacade;

    // -------------------------------------------------------------------------
    // GET /cars
    // -------------------------------------------------------------------------

    @Test
    void getAllCars_carsExist_returnsOkWithList() throws Exception {
        // Arrange
        List<CarDTO> cars = List.of(
                newCarDto(1L, "Ferrari F1-24"),
                newCarDto(2L, "Red Bull RB20")
        );
        when(carFacade.findAll()).thenReturn(cars);

        // Act & Assert
        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ferrari F1-24"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Red Bull RB20"));

        verify(carFacade).findAll();
    }

    @Test
    void getAllCars_noCars_returnsOkWithEmptyList() throws Exception {
        // Arrange
        when(carFacade.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(carFacade).findAll();
    }

    // -------------------------------------------------------------------------
    // GET /cars/{id}
    // -------------------------------------------------------------------------

    @Test
    void getCarById_carExists_returnsOk() throws Exception {
        // Arrange
        CarDTO dto = newCarDto(1L, "Ferrari F1-24");
        when(carFacade.findById(1L)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/cars/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ferrari F1-24"))
                .andExpect(jsonPath("$.status").value("NEW"));

        verify(carFacade).findById(1L);
    }

    @Test
    void getCarById_carNotFound_returnsNotFound() throws Exception {
        // Arrange
        when(carFacade.findById(99L)).thenThrow(new CarNotFoundException(99L));

        // Act & Assert
        mockMvc.perform(get("/cars/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Car not found"))
                .andExpect(jsonPath("$.detail").value("Car with id 99 was not found."));

        verify(carFacade).findById(99L);
    }

    // -------------------------------------------------------------------------
    // POST /cars
    // -------------------------------------------------------------------------

    @Test
    void createCar_validRequest_returnsCreated() throws Exception {
        // Arrange
        CreateCarDTO request = new CreateCarDTO("McLaren MCL38");
        CarDTO response = newCarDto(3L, "McLaren MCL38");
        when(carFacade.create(request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("McLaren MCL38"));

        verify(carFacade).create(request);
    }

    @Test
    void createCar_blankName_returnsBadRequest() throws Exception {
        // Arrange
        CreateCarDTO request = new CreateCarDTO("");

        // Act & Assert
        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation error"));
    }

    @Test
    void createCar_missingBody_returnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // POST /cars/assemble
    // -------------------------------------------------------------------------

    @Test
    void assembleCar_validRequest_returnsOkWithAssembledCar() throws Exception {
        // Arrange
        AssembleCarRequestDTO request = validAssembleRequest(1L);
        CarDTO response = assembledCarDto(1L, "Ferrari F1-24");
        when(carFacade.assemble(request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/cars/assemble")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ASSEMBLED"))
                .andExpect(jsonPath("$.engineId").value("engine-1"))
                .andExpect(jsonPath("$.suspensionId").value("suspension-1"))
                .andExpect(jsonPath("$.aerodynamicsId").value("aero-1"))
                .andExpect(jsonPath("$.gearboxId").value("gearbox-1"))
                .andExpect(jsonPath("$.electronicsId").value("electronics-1"));

        verify(carFacade).assemble(request);
    }

    @Test
    void assembleCar_carAlreadyAssembled_returnsBadRequest() throws Exception {
        // Arrange
        AssembleCarRequestDTO request = validAssembleRequest(1L);
        when(carFacade.assemble(any())).thenThrow(
                new CarAssemblyException("Car with id 1 is already assembled.")
        );

        // Act & Assert
        mockMvc.perform(post("/cars/assemble")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid car assembly"))
                .andExpect(jsonPath("$.detail").value("Car with id 1 is already assembled."));
    }

    @Test
    void assembleCar_duplicateComponents_returnsBadRequest() throws Exception {
        // Arrange
        AssembleCarRequestDTO request = validAssembleRequest(1L);
        when(carFacade.assemble(any())).thenThrow(
                new CarAssemblyException("Each car component must be unique.")
        );

        // Act & Assert
        mockMvc.perform(post("/cars/assemble")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid car assembly"))
                .andExpect(jsonPath("$.detail").value("Each car component must be unique."));
    }

    @Test
    void assembleCar_carNotFound_returnsNotFound() throws Exception {
        // Arrange
        AssembleCarRequestDTO request = validAssembleRequest(99L);
        when(carFacade.assemble(any())).thenThrow(new CarNotFoundException(99L));

        // Act & Assert
        mockMvc.perform(post("/cars/assemble")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Car not found"));
    }

    @Test
    void assembleCar_missingCarId_returnsBadRequest() throws Exception {
        // Arrange — carId is null, which fails @NotNull
        String body = """
                {
                  "engineId": "engine-1",
                  "suspensionId": "suspension-1",
                  "aerodynamicsId": "aero-1",
                  "gearboxId": "gearbox-1",
                  "electronicsId": "electronics-1"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/cars/assemble")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation error"));
    }

    @Test
    void assembleCar_blankEngineId_returnsBadRequest() throws Exception {
        // Arrange — engineId is blank, which fails @NotBlank
        String body = """
                {
                  "carId": 1,
                  "engineId": "",
                  "suspensionId": "suspension-1",
                  "aerodynamicsId": "aero-1",
                  "gearboxId": "gearbox-1",
                  "electronicsId": "electronics-1"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/cars/assemble")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation error"));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private CarDTO newCarDto(Long id, String name) {
        return new CarDTO(id, name, null, null, null, null, null, CarStatus.NEW, null);
    }

    private CarDTO assembledCarDto(Long id, String name) {
        return new CarDTO(id, name, "engine-1", "suspension-1", "aero-1", "gearbox-1", "electronics-1",
                CarStatus.ASSEMBLED, LocalDateTime.now());
    }

    private AssembleCarRequestDTO validAssembleRequest(Long carId) {
        return new AssembleCarRequestDTO(carId, "engine-1", "suspension-1", "aero-1", "gearbox-1", "electronics-1");
    }
}
