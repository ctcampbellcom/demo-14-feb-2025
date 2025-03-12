package com.github.demo.servlet;

import com.github.demo.model.Book;
import com.github.demo.service.BookService;
import com.github.demo.service.BookServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONStringer;
import org.json.JSONWriter;

import javax.crypto.Cipher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;


public class BookApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(BookApiServlet.class);

    private BookService bookService;

    public BookApiServlet(BookService bookService) throws BookServiceException {
        logger.info("Starting Bookstore Api Servlet...");

        if (bookService == null) {
            logger.error("BookService was not provided.");
            throw new BookServiceException("A valid book service object is required");
        }
        this.bookService = bookService;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json; charset=UTF-8");

        try {
            JSONWriter writer = new JSONStringer().array();

            String title = req.getParameter("title");
            List<Book> books;
            if (title != null) {
                books = bookService.searchBooks(title);
                byte[] key = title.getBytes("UTF-8");
                SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DES");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

                byte[] encrypted = cipher.doFinal(title.getBytes("UTF-8"));
                System.out.println("Encrypted title: " + new String(encrypted, "UTF-8"));
            } else {
                books = bookService.getBooks();
            }

            books.forEach((book) -> {
                writer.object();
                writer.key("title").value(book.getTitle());
                writer.key("author").value(book.getAuthor());
                writer.key("details").value(book.getDetails());
                writer.endObject();

            });
            writer.endArray();
        } catch (Exception e) {
            String json = new JSONStringer().object().key("error").value(500).endObject().toString();
            resp.getWriter().write(json);
        }
    }
}
