package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.dao.MemberDAO;
import com.library.dao.LoanDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/reports")
public class ReportServlet extends HttpServlet {
    private BookDAO bookDAO;
    private MemberDAO memberDAO;
    private LoanDAO loanDAO;

    @Override
    public void init() {
        bookDAO = new BookDAO();
        memberDAO = new MemberDAO();
        loanDAO = new LoanDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String contextPath = req.getContextPath();
        if (session == null || session.getAttribute("user") == null || !((com.library.model.Member) session.getAttribute("user")).getRole().equals("ADMIN")) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        try {
            int totalBooks = bookDAO.getAllBooks().size();
            int totalMembers = memberDAO.getAllMembers().size();
            int totalLoans = loanDAO.getAllLoans().size();
            double totalOverdueFees = loanDAO.getAllLoans().stream()
                    .mapToDouble(com.library.model.Loan::getOverdueFee)
                    .sum();

            req.setAttribute("totalBooks", totalBooks);
            req.setAttribute("totalMembers", totalMembers);
            req.setAttribute("totalLoans", totalLoans);
            req.setAttribute("totalOverdueFees", totalOverdueFees);
            req.getRequestDispatcher("/admin/reports.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}