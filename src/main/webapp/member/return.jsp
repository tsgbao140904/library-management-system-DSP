<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.model.Loan, java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trả sách</title>
    <base href="${pageContext.request.contextPath}/">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Thư viện</a>
        <div class="navbar-nav">
            <a class="nav-link" href="member/dashboard">Bảng điều khiển</a>
            <a class="nav-link" href="member/borrow">Mượn sách</a>
            <a class="nav-link" href="member/return">Trả sách</a>
            <a class="nav-link" href="logout">Đăng xuất</a>
        </div>
    </div>
</nav>
<div class="container mt-5">
    <h2>Trả sách</h2>
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>ID Sách</th>
            <th>Ngày mượn</th>
            <th>Hạn trả</th>
            <th>Phí quá hạn</th>
            <th>Hành động</th>
        </tr>
        </thead>
        <tbody>
        <% List<Loan> loans = (List<Loan>) request.getAttribute("loans"); %>
        <% if (loans != null && !loans.isEmpty()) { %>
        <% for (Loan loan : loans) { %>
        <% if (loan.getReturnDate() == null) { %>
        <tr>
            <td><%= loan.getId() %></td>
            <td><%= loan.getBookId() %></td>
            <td><%= loan.getBorrowDate() %></td>
            <td><%= loan.getDueDate() %></td>
            <td><%= loan.getOverdueFee() %></td>
            <td>
                <a href="member/return?action=return&id=<%= loan.getId() %>&feeType=daily" class="btn btn-sm btn-success">Trả (Theo ngày)</a>
                <a href="member/return?action=return&id=<%= loan.getId() %>&feeType=quantity" class="btn btn-sm btn-success">Trả (Theo số lượng)</a>
            </td>
        </tr>
        <% } %>
        <% } %>
        <% } else { %>
        <tr>
            <td colspan="6" class="text-center">Chưa có sách nào để trả.</td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>