package com.lostandfound.controller;

import com.lostandfound.model.LostItem;
import com.lostandfound.service.ClaimedItemService;
import com.lostandfound.service.LostItemService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser("testUser")
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LostItemService lostItemService;

    @MockBean
    private ClaimedItemService claimedItemService;

    @Test
    void shouldGetAllLostItems_whenLostItemsEndpointCalled() throws Exception {

        LostItem item1 = new LostItem(1L, "Wallet", 2, "Train");
        LostItem item2 = new LostItem(2L, "Phone", 1, "College");
        List<LostItem> lostItems = Arrays.asList(item1, item2);

        Mockito.when(lostItemService.getAllLostItems()).thenReturn(lostItems);

        mockMvc.perform(get("/api/users/lost-items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{\"id\":1,\"itemName\":\"Wallet\",\"quantity\":2,\"place\":\"Train\"},{\"id\":2,\"itemName\":\"Phone\",\"quantity\":1,\"place\":\"College\"}]"));
    }

    @Test
    void shouldClaimItem_WhenClaimEndpointCalled() throws Exception {
        Long lostItemId = 1L;
        int quantity = 1;
        Long userId = 1001L;

        Mockito.doNothing().when(claimedItemService).claimItem(eq(lostItemId), eq(quantity), eq(userId));

        mockMvc.perform(post("/api/users/claim")
                        .with(csrf())
                        .param("lostItemId", String.valueOf(lostItemId))
                        .param("quantity", String.valueOf(quantity))
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Item claimed successfully"));
    }
}
