package vn.edu.bookstore.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class BookApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listBooksShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/books").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }
}
