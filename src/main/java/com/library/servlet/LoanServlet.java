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
import java.util.Comparator;
import java.util.List;

@WebServlet("/admin/loans")
public class LoanServlet extends HttpServlet {
    private LoanDAO loanDAO;
    private BookDAO bookDAO;
    private MemberDAO memberDAO;
    private static final int ITEMS_PER_PAGE = 10; // Số lượng khoản mượn trên mỗi trang

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
            // Lấy toàn bộ danh sách khoản mượn
            List<Loan> allLoans = loanDAO.getAllLoans();

            // Sắp xếp: Ưu tiên chưa trả (returnDate == null) lên đầu, sắp xếp theo borrowDate giảm dần
            // Các khoản đã trả (returnDate != null) xếp sau, sắp xếp theo returnDate giảm dần
            allLoans.sort(Comparator.comparing(
                            Loan::getReturnDate, Comparator.nullsFirst(Comparator.naturalOrder()) // Ưu tiên returnDate == null lên đầu
                    ).thenComparing(Loan::getBorrowDate, Comparator.reverseOrder()) // Sắp xếp borrowDate giảm dần cho chưa trả
                    .thenComparing(Loan::getReturnDate, Comparator.nullsLast(Comparator.reverseOrder()))); // Sắp xếp returnDate giảm dần cho đã trả

            // Lấy tham số trang từ request, mặc định là 1
            int page = 1;
            String pageParam = req.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            // Tính toán phân trang
            int totalItems = allLoans.size();
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            if (page > totalPages && totalPages > 0) page = totalPages;

            // Lấy danh sách khoản mượn cho trang hiện tại
            int start = (page - 1) * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, totalItems);
            List<Loan> loansForPage = (totalItems > 0) ? allLoans.subList(start, end) : List.of();

            // Truyền thông tin phân trang và danh sách khoản mượn cho JSP
            req.setAttribute("loans", loansForPage);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.getRequestDispatcher("/admin/loans.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi khi lấy danh sách khoản vay: " + e.getMessage());
            req.setAttribute("loans", List.of());
            req.setAttribute("currentPage", 1);
            req.setAttribute("totalPages", 1);
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

            // Sau khi thêm, chuyển hướng để áp dụng phân trang và sắp xếp
            resp.sendRedirect(req.getContextPath() + "/admin/loans");
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

            // Lấy lại danh sách và sắp xếp
            List<Loan> allLoans = loanDAO.getAllLoans();
            allLoans.sort(Comparator.comparing(
                            Loan::getReturnDate, Comparator.nullsFirst(Comparator.naturalOrder())
                    ).thenComparing(Loan::getBorrowDate, Comparator.reverseOrder())
                    .thenComparing(Loan::getReturnDate, Comparator.nullsLast(Comparator.reverseOrder())));

            int totalItems = allLoans.size();
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int page = 1; // Mặc định quay về trang 1 sau khi trả
            int start = (page - 1) * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, totalItems);
            List<Loan> loansForPage = (totalItems > 0) ? allLoans.subList(start, end) : List.of();
            req.setAttribute("loans", loansForPage);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.getRequestDispatcher("/admin/loans.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi khi trả sách: " + e.getMessage());
            req.getRequestDispatcher("/admin/loans.jsp").forward(req, resp);
        }
    }
}