package vn.edu.bookstore.catalog.api;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import vn.edu.bookstore.catalog.domain.BookEntity;
import vn.edu.bookstore.catalog.service.BookService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void listBooksShouldReturnOk() throws Exception {

        PageImpl<BookEntity> emptyPage = new PageImpl<>(
            Collections.emptyList(),
            PageRequest.of(0, 10),
            0
        );

        given(bookService.list(isNull(), any(PageRequest.class)))
            .willReturn(emptyPage);

        mockMvc.perform(get("/api/v1/books"))
               .andExpect(status().isOk());
    }
}
