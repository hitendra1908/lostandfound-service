package com.lostandfound.controller;

import com.lostandfound.dto.ClaimedItemsResponseDto;
import com.lostandfound.dto.UserRequestDto;
import com.lostandfound.model.User;
import com.lostandfound.service.ClaimedItemService;
import com.lostandfound.service.LostItemService;
import com.lostandfound.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser("admin")
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LostItemService lostItemService;

    @MockBean
    private ClaimedItemService claimedItemService;

    @MockBean
    private UserService userService;

    @Test
    void shouldUploadLostItemsFile() throws Exception {
        byte[] fileContent = "sample data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                "application/pdf", fileContent);

        doNothing().when(lostItemService).uploadLostItems(any());

        mockMvc.perform(multipart("/api/admin/upload")
                        .file(file)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().string("File uploaded and processed successfully"));

        verify(lostItemService, times(1)).uploadLostItems(any());
    }

    @Test
    void shouldGetAllClaimedItems() throws Exception {
        ClaimedItemsResponseDto claimedItem = new ClaimedItemsResponseDto();
        List<ClaimedItemsResponseDto> claimedItems = Collections.singletonList(claimedItem);

        when(claimedItemService.getAllClaimedItems()).thenReturn(claimedItems);

        mockMvc.perform(get("/api/admin/claimed-items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{}]"));

        verify(claimedItemService, times(1)).getAllClaimedItems();
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setName("admin");
        user.setPassword("admin123");

        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setName("admin");
        user.setPassword("admin123");

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("admin");
        userRequestDto.setName("admin");
        userRequestDto.setPassword("admin123");
        userRequestDto.setRoles(List.of("ROLE_ADMIN"));

        when(userService.saveUser(Mockito.any(UserRequestDto.class))).thenReturn(user);

        mockMvc.perform(post("/api/admin/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"admin\", \"username\": \"admin\",\"password\": \"admin123\", \"roles\": [\"USER_ADMIN\"]}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
