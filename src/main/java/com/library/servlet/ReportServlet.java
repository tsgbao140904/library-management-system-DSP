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
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/admin/reports")
public class ReportServlet extends HttpServlet {
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
        if (session.getAttribute("user") == null || !"ADMIN".equals(((Member) session.getAttribute("user")).getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            List<Loan> loans = loanDAO.getAllLoans();
            List<Book> books = bookDAO.getAllBooks();
            List<Member> members = memberDAO.getAllMembers();

            // Log để kiểm tra dữ liệu
            System.out.println("Số lượng loans (ReportServlet): " + loans.size());
            long currentlyBorrowed = loans.stream().filter(loan -> loan.getReturnDate() == null).count();
            System.out.println("Số lượng sách đang mượn (ReportServlet): " + currentlyBorrowed);

            List<Loan> overdueLoans = loans.stream()
                    .filter(loan -> loan.getReturnDate() == null && loan.getDueDate().isBefore(java.time.LocalDate.now()))
                    .collect(Collectors.toList());

            req.setAttribute("loans", loans);
            req.setAttribute("overdueLoans", overdueLoans);
            req.setAttribute("books", books);
            req.setAttribute("members", members);
            req.getRequestDispatcher("/admin/reports.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi khi lấy dữ liệu báo cáo: " + e.getMessage());
            req.getRequestDispatcher("/admin/reports.jsp").forward(req, resp);
        }
    }
}