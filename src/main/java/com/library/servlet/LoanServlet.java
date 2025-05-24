package com.library.servlet;

import com.library.dao.LoanDAO;
import com.library.dao.BookDAO;
import com.library.model.Loan;
import com.library.model.Book;
import com.library.model.strategy.DailyFeeCalculator;
import com.library.model.strategy.FeeCalculator;
import com.library.model.strategy.QuantityFeeCalculator;
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

@WebServlet({"/admin/loans", "/member/borrow", "/member/return"})
public class LoanServlet extends HttpServlet {
    private LoanDAO loanDAO;
    private BookDAO bookDAO;

    @Override
    public void init() {
        loanDAO = new LoanDAO();
        bookDAO = new BookDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String contextPath = req.getContextPath();
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        String path = req.getServletPath();
        try {
            if ("/member/return".equals(path)) {
                String action = req.getParameter("action");
                int memberId = ((com.library.model.Member) session.getAttribute("user")).getId();
                if ("return".equals(action)) {
                    int id = Integer.parseInt(req.getParameter("id"));
                    String feeType = req.getParameter("feeType");
                    FeeCalculator feeCalculator = "daily".equals(feeType) ? new DailyFeeCalculator() : new QuantityFeeCalculator();
                    loanDAO.returnBook(id, feeCalculator);
                }
                List<Loan> loans = loanDAO.getAllLoans().stream()
                        .filter(loan -> loan.getMemberId() == memberId && loan.getReturnDate() == null)
                        .toList();
                req.setAttribute("loans", loans);
                req.getRequestDispatcher("/member/return.jsp").forward(req, resp);
            } else if ("/member/borrow".equals(path)) {
                List<Book> books = bookDAO.getAllBooks();
                req.setAttribute("books", books);
                req.getRequestDispatcher("/member/borrow.jsp").forward(req, resp);
            } else if ("/admin/loans".equals(path)) {
                String action = req.getParameter("action");
                if ("return".equals(action)) {
                    int id = Integer.parseInt(req.getParameter("id"));
                    String feeType = req.getParameter("feeType");
                    FeeCalculator feeCalculator = "daily".equals(feeType) ? new DailyFeeCalculator() : new QuantityFeeCalculator();
                    loanDAO.returnBook(id, feeCalculator);
                    resp.sendRedirect(contextPath + "/admin/loans"); // Thêm chuyển hướng để refresh trang
                    return;
                }
                List<Loan> loans = loanDAO.getAllLoans();
                req.setAttribute("loans", loans);
                req.getRequestDispatcher("/admin/loans.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String contextPath = req.getContextPath();
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        String path = req.getServletPath();
        try {
            if ("/admin/loans".equals(path) || "/member/borrow".equals(path)) {
                int bookId = Integer.parseInt(req.getParameter("bookId"));
                int memberId = "/member/borrow".equals(path) ?
                        ((com.library.model.Member) session.getAttribute("user")).getId() :
                        Integer.parseInt(req.getParameter("memberId"));
                Loan loan = new Loan(0, bookId, memberId, LocalDate.now(), LocalDate.now().plusDays(7));
                loanDAO.addLoan(loan);
                resp.sendRedirect(contextPath + path);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}