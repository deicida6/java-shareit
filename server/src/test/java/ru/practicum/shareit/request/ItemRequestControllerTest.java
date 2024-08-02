package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController controller;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private ItemRequestDto itemRequestDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("TestItemRequestDescription")
                .requester(null)
                .created(LocalDateTime.now())
                .items(List.of())
                .build();
        pageable = PageRequest.of(0, 10, Sort.by("created").descending());
    }

    @Test
    void addItemRequest() throws Exception {
        when(itemRequestService.addItemRequest(eq(1L), any())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
        verify(itemRequestService, times(1)).addItemRequest(eq(1L), any());
    }

    @Test
    void getAllItemRequestByOwner() throws Exception {
        when(itemRequestService.getAllItemRequestByOwner(anyLong())).thenReturn(Collections.emptyList());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemRequestService, times(1)).getAllItemRequestByOwner(anyLong());
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
        verify(itemRequestService, times(1)).getItemRequestById(anyLong());
    }

    @Test
    void getAllItemRequestByOtherUsers() throws Exception {
        when(itemRequestService.getAllItemRequestToOtherUser(pageable)).thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemRequestService, times(1)).getAllItemRequestToOtherUser(pageable);
    }
}