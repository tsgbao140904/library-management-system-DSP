package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.model.book.AcademicBookFactory;
import com.library.model.book.EntertainmentBookFactory;
import com.library.model.book.BookFactory;
import com.library.model.decorator.FavoriteBookDecorator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/books")
public class BookServlet extends HttpServlet {
    private BookDAO bookDAO;

    @Override
    public void init() {
        bookDAO = new BookDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String contextPath = req.getContextPath();
        if (session == null || session.getAttribute("user") == null || !((com.library.model.Member) session.getAttribute("user")).getRole().equals("ADMIN")) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Book book = bookDAO.getBookById(id);
                if (book == null) {
                    req.setAttribute("error", "Khong tim thay sach voi ID: " + id);
                    List<Book> books = bookDAO.getAllBooks();
                    req.setAttribute("books", books);
                    req.getRequestDispatcher("/admin/books.jsp").forward(req, resp);
                    return;
                }
                req.setAttribute("book", book);
                req.getRequestDispatcher("/admin/editbook.jsp").forward(req, resp);
                return;
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                bookDAO.deleteBook(id);
            } else if ("favorite".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                bookDAO.markAsFavorite(id);
            }

            List<Book> books = bookDAO.getAllBooks();
            req.setAttribute("books", books);
            req.getRequestDispatcher("/admin/books.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Loi: " + e.getMessage());
            try {
                List<Book> books = bookDAO.getAllBooks();
                req.setAttribute("books", books);
                req.getRequestDispatcher("/admin/books.jsp").forward(req, resp);
            } catch (SQLException ex) {
                throw new ServletException("Database error", ex);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String contextPath = req.getContextPath();
        if (session == null || session.getAttribute("user") == null || !((com.library.model.Member) session.getAttribute("user")).getRole().equals("ADMIN")) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("update".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String title = req.getParameter("title");
                String author = req.getParameter("author");
                String type = req.getParameter("type");

                // Sử dụng factory để tạo đối tượng Book phù hợp
                BookFactory factory = type.equals("Printed") ? new AcademicBookFactory() : new EntertainmentBookFactory();
                Book book = factory.createBook(id, title, author); // ID được truyền vào để cập nhật
                bookDAO.updateBook(book);
                resp.sendRedirect(contextPath + "/admin/books");
                return;
            }

            String title = req.getParameter("title");
            String author = req.getParameter("author");
            String type = req.getParameter("type");

            BookFactory factory = type.equals("Printed") ? new AcademicBookFactory() : new EntertainmentBookFactory();
            Book book = factory.createBook(0, title, author); // ID = 0 cho sách mới
            bookDAO.addBook(book);
            resp.sendRedirect(contextPath + "/admin/books");
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}