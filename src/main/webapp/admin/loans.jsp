<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.model.Loan, java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý mượn sách</title>
    <base href="${pageContext.request.contextPath}/">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
    <style>
        .pagination {
            margin-top: 1rem;
            justify-content: center;
        }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Thư viện</a>
        <div class="navbar-nav">
            <a class="nav-link" href="admin/books">Sách</a>
            <a class="nav-link" href="admin/members">Thành viên</a>
            <a class="nav-link" href="admin/loans">Mượn sách</a>
            <a class="nav-link" href="admin/reports">Báo cáo</a>
            <a class="nav-link" href="logout">Đăng xuất</a>
        </div>
    </div>
</nav>
<div class="container mt-5">
    <h2>Quản lý mượn sách</h2>
    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <%= request.getAttribute("error") %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% } %>
    <% if (session.getAttribute("success") != null) { %>
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <%= session.getAttribute("success") %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% session.removeAttribute("success"); %>
    <% } %>
    <form action="admin/loans" method="post" class="mb-4">
        <div class="row">
            <div class="col">
                <input type="number" class="form-control" name="bookId" placeholder="ID Sách" required>
            </div>
            <div class="col">
                <input type="number" class="form-control" name="memberId" placeholder="ID Thành viên" required>
            </div>
            <div class="col">
                <button type="submit" class="btn btn-primary">Mượn sách</button>
            </div>
        </div>
    </form>
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>ID Sách</th>
            <th>ID Thành viên</th>
            <th>Ngày mượn</th>
            <th>Hạn trả</th>
            <th>Ngày trả</th>
            <th>Phí quá hạn</th>
            <th>Hành động</th>
        </tr>
        </thead>
        <tbody>
        <% List<Loan> loans = (List<Loan>) request.getAttribute("loans"); %>
        <% if (loans != null && !loans.isEmpty()) { %>
        <% for (Loan loan : loans) { %>
        <tr>
            <td><%= loan.getId() %></td>
            <td><%= loan.getBookId() %></td>
            <td><%= loan.getMemberId() %></td>
            <td><%= loan.getBorrowDate() %></td>
            <td><%= loan.getDueDate() %></td>
            <td><%= loan.getReturnDate() != null ? loan.getReturnDate() : "Chưa trả" %></td>
            <td><%= loan.getOverdueFee() %></td>
            <td>
                <% if (loan.getReturnDate() == null) { %>
                <a href="admin/loans?action=return&id=<%= loan.getId() %>&feeType=daily" class="btn btn-sm btn-success">Trả (Theo ngày)</a>
                <a href="admin/loans?action=return&id=<%= loan.getId() %>&feeType=quantity" class="btn btn-sm btn-success">Trả (Theo số lượng)</a>
                <% } %>
            </td>
        </tr>
        <% } %>
        <% } else { %>
        <tr>
            <td colspan="8" class="text-center">Không có khoản mượn nào.</td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <% Integer currentPage = (Integer) request.getAttribute("currentPage"); %>
    <% Integer totalPages = (Integer) request.getAttribute("totalPages"); %>
    <% if (totalPages != null && totalPages > 1) { %>
    <nav aria-label="Page navigation">
        <ul class="pagination">
            <% if (currentPage > 1) { %>
            <li class="page-item">
                <a class="page-link" href="admin/loans?page=<%= currentPage - 1 %>">Trước</a>
            </li>
            <% } %>
            <% for (int i = 1; i <= totalPages; i++) { %>
            <li class="page-item <%= (i == currentPage) ? "active" : "" %>">
                <a class="page-link" href="admin/loans?page=<%= i %>"><%= i %></a>
            </li>
            <% } %>
            <% if (currentPage < totalPages) { %>
            <li class="page-item">
                <a class="page-link" href="admin/loans?page=<%= currentPage + 1 %>">Sau</a>
            </li>
            <% } %>
        </ul>
    </nav>
    <% } %>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>