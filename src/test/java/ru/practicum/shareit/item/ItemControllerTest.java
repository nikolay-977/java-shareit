package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.comment.CommentDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private ItemController itemController;
    private MockMvc mvc;
    private ItemDto itemDto;
    private ItemInfoDto itemInfoDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).build();
        itemDto = ItemDto.builder().id(1L).name("Name of item").description("Description of item").available(true).build();
        itemInfoDto = ItemInfoDto.builder().id(1L).name("Name of item").description("Description of item").available(true).build();
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.create(anyLong(), any())).thenReturn(itemDto);
        mvc.perform(post("/items").content(mapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class)).andExpect(jsonPath("$.name", is(itemDto.getName()), String.class)).andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class));
    }

    @Test
    void addCommentTest() throws Exception {
        commentDto = CommentDto.builder().id(1L).text("Text of comment").build();
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);
        mvc.perform(post("/items/1/comment").content(mapper.writeValueAsString(commentDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class)).andExpect(jsonPath("$.text", is(commentDto.getText()), String.class));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.update(anyLong(), any(), anyLong())).thenReturn(itemDto);
        mvc.perform(patch("/items/1").content(mapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class)).andExpect(jsonPath("$.name", is(itemDto.getName()), String.class)).andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class));
    }

    @Test
    void searchItemTest() throws Exception {
        List<ItemDto> itemDtoList = List.of(itemDto);
        when(itemService.search(anyLong(), anyString())).thenReturn(itemDtoList);
        mvc.perform(get("/items/search?text=search").characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemInfoDto);
        mvc.perform(get("/items/1").content(mapper.writeValueAsString(itemInfoDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(itemInfoDto.getId()), Long.class)).andExpect(jsonPath("$.name", is(itemInfoDto.getName()), String.class)).andExpect(jsonPath("$.description", is(itemInfoDto.getDescription()), String.class));
    }

    @Test
    void getAllItemsTest() throws Exception {
        List<ItemInfoDto> itemInfoDtoList = List.of(itemInfoDto);
        when(itemService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(itemInfoDtoList);
        mvc.perform(get("/items").characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id", is(itemInfoDto.getId()), Long.class));
    }
}