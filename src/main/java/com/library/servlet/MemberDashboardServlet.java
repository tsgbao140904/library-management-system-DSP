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
                Member member = (Member) session.getAttribute("user");
                List<Loan> loans = loanDAO.getLoansByMember(member.getId());
                for (Loan loan : loans) {
                    try {
                        Book book = bookDAO.getBookById(loan.getBookId());
                        loan.setBook(book);
                        // Không gọi calculateOverdueFee() để giữ giá trị từ DB
                        // Giá trị overdue_fee đã được lấy từ loanDAO.getLoansByMember()
                    } catch (SQLException e) {
                        e.printStackTrace();
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
                Book book = bookDAO.getBookById(bookIdInt);
                if (book == null) {
                    throw new IllegalArgumentException("Sách không tồn tại.");
                }
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
        String feeType = req.getParameter("feeType"); // Lấy chiến lược phí từ giao diện
        try {
            Loan loan = loanDAO.getLoanById(Integer.parseInt(loanId));
            if (loan == null) {
                req.setAttribute("error", "Khoản vay không tồn tại.");
            } else {
                loan.setReturnDate(LocalDate.now()); // Đặt ngày trả là ngày hiện tại (27/05/2025)
                loan.setFeeStrategy(feeType); // Áp dụng chiến lược phí từ giao diện
                loan.calculateOverdueFee(); // Tính lại phí dựa trên chiến lược mới
                loanDAO.updateLoan(loan); // Lưu cả return_date và overdue_fee
                String strategyDisplay = "daily".equalsIgnoreCase(loan.getFeeStrategy()) ? "Theo ngày" : "Theo số lượng";
                req.setAttribute("success", "Trả sách thành công! Phí trễ hạn: " + loan.getOverdueFee() + " USD (" + strategyDisplay + ")");
            }

            Member member = (Member) req.getSession().getAttribute("user");
            List<Loan> loans = loanDAO.getLoansByMember(member.getId());
            for (Loan l : loans) {
                try {
                    Book book = bookDAO.getBookById(l.getBookId());
                    l.setBook(book);
                    // Không tính lại overdue_fee, giữ giá trị từ DB
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            req.setAttribute("loans", loans);
            req.getRequestDispatcher("/member/return.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi khi trả sách: " + e.getMessage());
            req.getRequestDispatcher("/member/return.jsp").forward(req, resp);
        }
    }
}