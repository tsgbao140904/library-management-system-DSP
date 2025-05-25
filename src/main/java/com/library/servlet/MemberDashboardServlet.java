package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.dao.LoanDAO;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@WebServlet({"/member/dashboard", "/member/return", "/member/borrow"})
public class MemberDashboardServlet extends HttpServlet {
    private BookDAO bookDAO;
    private LoanDAO loanDAO;

    @Override
    public void init() {
        bookDAO = new BookDAO();
        loanDAO = new LoanDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String contextPath = req.getContextPath();
        if (session == null || session.getAttribute("user") == null || !((Member) session.getAttribute("user")).getRole().equals("MEMBER")) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        String path = req.getServletPath();
        try {
            if ("/member/dashboard".equals(path)) {
                List<Book> books = bookDAO.getAllBooks();
                req.setAttribute("books", books);
                req.getRequestDispatcher("/member/dashboard.jsp").forward(req, resp);
            } else if ("/member/borrow".equals(path)) {
                List<Book> books = bookDAO.getAllBooks();
                req.setAttribute("books", books);
                req.getRequestDispatcher("/member/borrow.jsp").forward(req, resp);
            } else if ("/member/return".equals(path)) {
                String action = req.getParameter("action");
                if ("return".equals(action)) {
                    handleReturn(req, resp);
                    return;
                }
                List<Loan> loans = loanDAO.getLoansByMember(((Member) session.getAttribute("user")).getId());
                // Thêm thông tin sách cho từng khoản mượn
                for (Loan loan : loans) {
                    try {
                        Book book = bookDAO.getBookById(loan.getBookId());
                        loan.setBook(book);
                    } catch (SQLException e) {
                        e.printStackTrace(); // Xử lý lỗi theo cách phù hợp
                    }
                }
                req.setAttribute("loans", loans);
                req.getRequestDispatcher("/member/return.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            if ("/member/dashboard".equals(path)) {
                req.getRequestDispatcher("/member/dashboard.jsp").forward(req, resp);
            } else if ("/member/borrow".equals(path)) {
                req.getRequestDispatcher("/member/borrow.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/member/return.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Member user = (Member) session.getAttribute("user");
        if (user == null || !"MEMBER".equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();
        try {
            if ("/member/borrow".equals(path)) {
                String bookId = req.getParameter("bookId");
                if (bookId == null || bookId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Vui lòng chọn một sách để mượn.");
                }
                int bookIdInt = Integer.parseInt(bookId);
                // Kiểm tra xem sách có tồn tại không
                Book book = bookDAO.getBookById(bookIdInt);
                if (book == null) {
                    throw new IllegalArgumentException("Sách không tồn tại.");
                }
                // Kiểm tra xem sách đã được mượn chưa
                List<Loan> loans = loanDAO.getAllLoans();
                boolean isBorrowed = loans.stream()
                        .anyMatch(loan -> loan.getBookId() == bookIdInt && loan.getReturnDate() == null);
                if (isBorrowed) {
                    throw new IllegalArgumentException("Sách này đã được mượn. Vui lòng chọn sách khác.");
                }
                Loan loan = new Loan();
                loan.setBookId(bookIdInt);
                loan.setMemberId(user.getId());
                loan.setBorrowDate(LocalDate.now());
                loan.setDueDate(LocalDate.now().plusDays(14));
                loanDAO.addLoan(loan);
                req.setAttribute("success", "Mượn sách thành công!");
                List<Book> books = bookDAO.getAllBooks();
                req.setAttribute("books", books);
                req.getRequestDispatcher("/member/borrow.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi khi mượn sách: " + e.getMessage());
            List<Book> books = null;
            try {
                books = bookDAO.getAllBooks();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            req.setAttribute("books", books);
            req.getRequestDispatcher("/member/borrow.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            try {
                List<Book> books = bookDAO.getAllBooks();
                req.setAttribute("books", books);
                req.getRequestDispatcher("/member/borrow.jsp").forward(req, resp);
            } catch (SQLException ex) {
                req.setAttribute("error", e.getMessage() + ". Không thể tải danh sách sách: " + ex.getMessage());
                req.setAttribute("books", null);
                req.getRequestDispatcher("/member/borrow.jsp").forward(req, resp);
            }
        }
    }

    private void handleReturn(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String loanId = req.getParameter("id");
        HttpSession session = req.getSession();
        Member user = (Member) session.getAttribute("user");
        List<Loan> loans = null;
        try {
            Loan loan = loanDAO.getLoanById(Integer.parseInt(loanId));
            if (loan == null || loan.getMemberId() != user.getId()) {
                req.setAttribute("error", "Khoản vay không tồn tại hoặc không thuộc về bạn.");
            } else {
                loan.setReturnDate(LocalDate.now());
                loanDAO.updateLoan(loan);
                req.setAttribute("success", "Trả sách thành công!");
            }
            loans = loanDAO.getLoansByMember(user.getId());
            // Thêm thông tin sách cho từng khoản mượn
            for (Loan l : loans) {
                try {
                    Book book = bookDAO.getBookById(l.getBookId());
                    l.setBook(book);
                } catch (SQLException e) {
                    e.printStackTrace(); // Xử lý lỗi theo cách phù hợp
                }
            }
            req.setAttribute("loans", loans);
            req.getRequestDispatcher("/member/return.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi khi trả sách: " + e.getMessage());
            try {
                loans = loanDAO.getLoansByMember(user.getId());
                for (Loan l : loans) {
                    try {
                        Book book = bookDAO.getBookById(l.getBookId());
                        l.setBook(book);
                    } catch (SQLException ex) {
                        e.printStackTrace();
                    }
                }
                req.setAttribute("loans", loans);
            } catch (SQLException ex) {
                req.setAttribute("error", req.getAttribute("error") + ". Không thể tải danh sách khoản vay: " + ex.getMessage());
                req.setAttribute("loans", null);
            }
            req.getRequestDispatcher("/member/return.jsp").forward(req, resp);
        }
    }
}