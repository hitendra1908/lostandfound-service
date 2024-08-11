package com.lostandfound.controller;

import com.lostandfound.dto.ClaimedItemsResponseDto;
import com.lostandfound.service.ClaimedItemService;
import com.lostandfound.service.LostItemService;
import com.lostandfound.service.UserService;
import org.junit.jupiter.api.Test;
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
}
