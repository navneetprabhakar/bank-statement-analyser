package com.yourco.bsa.controller;

import com.yourco.bsa.model.Transaction;
import com.yourco.bsa.repository.TransactionRepository;
import com.yourco.bsa.service.categorization.CategorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private CategorizationService categorizationService;

    @Test
    @WithMockUser
    void shouldTriggerCategorization() throws Exception {
        mockMvc.perform(post("/api/v1/analytics/categorize/DOC-123")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(categorizationService).categorizeTransactions("DOC-123");
    }

    @Test
    @WithMockUser
    void shouldGetSpendingByCategory() throws Exception {
        Transaction t1 = new Transaction();
        t1.setCategory("Food");
        t1.setDebitAmount(new BigDecimal("10.00"));

        Transaction t2 = new Transaction();
        t2.setCategory("Food");
        t2.setDebitAmount(new BigDecimal("15.50"));

        Transaction t3 = new Transaction();
        t3.setCategory("Transport");
        t3.setDebitAmount(new BigDecimal("20.00"));

        when(transactionRepository.findByDocNumber("DOC-123")).thenReturn(List.of(t1, t2, t3));

        mockMvc.perform(get("/api/v1/analytics/spending-by-category")
                .param("docNumber", "DOC-123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Food").value(25.5))
                .andExpect(jsonPath("$.Transport").value(20.0));
    }
}
