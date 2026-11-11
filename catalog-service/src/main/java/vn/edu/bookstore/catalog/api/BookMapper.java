package vn.edu.bookstore.catalog.api;

import org.springframework.data.domain.Page;
import vn.edu.bookstore.catalog.api.model.Book;
import vn.edu.bookstore.catalog.api.model.BookPage;
import vn.edu.bookstore.catalog.api.model.CurrencyCode;
import vn.edu.bookstore.catalog.api.model.Money;
import vn.edu.bookstore.catalog.api.model.PageMetadata;
import vn.edu.bookstore.catalog.domain.BookEntity;

public class BookMapper {

    public static Book toApi(BookEntity e) {
        Book b = new Book();
        b.setIsbn(e.getIsbn());
        b.setTitle(e.getTitle());
        b.setAuthor(e.getAuthor());
        b.setAuthorId(e.getAuthorId());
        b.setPublishedYear(e.getPublishedYear());

        Money m = new Money();
        m.setAmount(e.getPriceAmount());
        if (e.getPriceCurrency() != null) {
            m.setCurrency(CurrencyCode.fromValue(e.getPriceCurrency()));
        }
        b.setPrice(m);
        return b;
    }

    public static BookPage toPage(Page<BookEntity> page) {
        BookPage bp = new BookPage();
        bp.setContent(page.getContent().stream().map(BookMapper::toApi).toList());

        PageMetadata meta = new PageMetadata();
        meta.setSize(page.getSize());
        meta.setNumber(page.getNumber());
        meta.setTotalElements(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        bp.setPage(meta);
        return bp;
    }

    public static String etag(BookEntity e) {
        return "\"" + e.getVersion() + "\"";
    }
}
