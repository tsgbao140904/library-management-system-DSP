package com.library.servlet;

import com.library.dao.MemberDAO;
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

@WebServlet("/admin/members")
public class MemberServlet extends HttpServlet {
    private MemberDAO memberDAO;

    @Override
    public void init() {
        memberDAO = new MemberDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String contextPath = req.getContextPath();
        if (session == null || session.getAttribute("user") == null || !((Member) session.getAttribute("user")).getRole().equals("ADMIN")) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Member member = memberDAO.getMemberById(id);
                if (member == null) {
                    req.setAttribute("error", "Không tìm thấy thành viên với ID: " + id);
                } else {
                    req.setAttribute("member", member);
                }
                req.getRequestDispatcher("/admin/editmember.jsp").forward(req, resp);
                return;
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                try {
                    memberDAO.deleteMember(id);
                    req.setAttribute("success", "Xóa thành viên thành công!");
                } catch (SQLException e) {
                    req.setAttribute("error", "Lỗi khi xóa thành viên: " + e.getMessage());
                }
            }

            List<Member> members = memberDAO.getAllMembers();
            req.setAttribute("members", members);
            req.getRequestDispatcher("/admin/members.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String contextPath = req.getContextPath();
        if (session == null || session.getAttribute("user") == null || !((Member) session.getAttribute("user")).getRole().equals("ADMIN")) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String fullName = req.getParameter("fullName");
            String role = req.getParameter("role");

            Member member = new Member(id, username, password, fullName, role);
            memberDAO.updateMember(member);
            resp.sendRedirect(contextPath + "/admin/members");
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID không hợp lệ: " + e.getMessage());
            try {
                List<Member> members = memberDAO.getAllMembers();
                req.setAttribute("members", members);
                req.getRequestDispatcher("/admin/members.jsp").forward(req, resp);
            } catch (SQLException ex) {
                throw new ServletException("Database error", ex);
            }
        }
    }
}