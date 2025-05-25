package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.dao.LoanDAO;
import com.library.dao.MemberDAO;
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

@WebServlet("/admin/loans")
public class LoanServlet extends HttpServlet {
    private LoanDAO loanDAO;
    private BookDAO bookDAO;
    private MemberDAO memberDAO;

    @Override
    public void init() {
        loanDAO = new LoanDAO();
        bookDAO = new BookDAO();
        memberDAO = new MemberDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Member user = (Member) session.getAttribute("user");
        if (user == null || !user.getRole().equals("ADMIN")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if ("return".equals(action)) {
            handleReturn(req, resp);
            return;
        }

        try {
            List<Loan> loans = loanDAO.getAllLoans();
            req.setAttribute("loans", loans);
            req.getRequestDispatcher("/admin/loans.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi khi lấy danh sách khoản vay: " + e.getMessage());
            req.getRequestDispatcher("/admin/loans.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Member user = (Member) session.getAttribute("user");
        if (user == null || !user.getRole().equals("ADMIN")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String bookId = req.getParameter("bookId");
        String memberId = req.getParameter("memberId");

        try {
            Book book = bookDAO.getBookById(Integer.parseInt(bookId));
            Member member = memberDAO.getMemberById(Integer.parseInt(memberId));
            if (book == null || member == null) {
                req.setAttribute("error", "Sách hoặc thành viên không tồn tại.");
                req.getRequestDispatcher("/admin/loans.jsp").forward(req, resp);
                return;
            }

            Loan loan = new Loan(); // Sử dụng constructor mặc định
            loan.setBookId(book.getId());
            loan.setMemberId(member.getId());
            loan.setBorrowDate(LocalDate.now());
            loan.setDueDate(LocalDate.now().plusDays(14));
            loanDAO.addLoan(loan);

            List<Loan> loans = loanDAO.getAllLoans();
            req.setAttribute("loans", loans);
            req.getRequestDispatcher("/admin/loans.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi khi thêm khoản vay: " + e.getMessage());
            req.getRequestDispatcher("/admin/loans.jsp").forward(req, resp);
        }
    }

    private void handleReturn(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String loanId = req.getParameter("id");
        try {
            Loan loan = loanDAO.getLoanById(Integer.parseInt(loanId));
            if (loan == null) {
                req.setAttribute("error", "Khoản vay không tồn tại.");
            } else {
                loan.setReturnDate(LocalDate.now());
                loanDAO.updateLoan(loan);
                req.setAttribute("success", "Trả sách thành công!");
            }
            List<Loan> loans = loanDAO.getAllLoans();
            req.setAttribute("loans", loans);
            req.getRequestDispatcher("/admin/loans.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi khi trả sách: " + e.getMessage());
            req.getRequestDispatcher("/admin/loans.jsp").forward(req, resp);
        }
    }
}