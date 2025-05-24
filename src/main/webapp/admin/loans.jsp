<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.model.Loan, java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quan ly muon sach</title>
    <base href="${pageContext.request.contextPath}/">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Thu vien</a>
        <div class="navbar-nav">
            <a class="nav-link" href="admin/books">Sach</a>
            <a class="nav-link" href="admin/members">Thanh vien</a>
            <a class="nav-link" href="admin/loans">Muon sach</a>
            <a class="nav-link" href="admin/reports">Bao cao</a>
            <a class="nav-link" href="logout">Dang xuat</a>
        </div>
    </div>
</nav>
<div class="container mt-5">
    <h2>Quan ly muon sach</h2>
    <form action="admin/loans" method="post" class="mb-4">
        <div class="row">
            <div class="col">
                <input type="number" class="form-control" name="bookId" placeholder="ID Sach" required>
            </div>
            <div class="col">
                <input type="number" class="form-control" name="memberId" placeholder="ID Thanh vien" required>
            </div>
            <div class="col">
                <button type="submit" class="btn btn-primary">Muon sach</button>
            </div>
        </div>
    </form>
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>ID Sach</th>
            <th>ID Thanh vien</th>
            <th>Ngay muon</th>
            <th>Han tra</th>
            <th>Ngay tra</th>
            <th>Phi qua han</th>
            <th>Hanh dong</th>
        </tr>
        </thead>
        <tbody>
        <% List<Loan> loans = (List<Loan>) request.getAttribute("loans"); %>
        <% for (Loan loan : loans) { %>
        <tr>
            <td><%= loan.getId() %></td>
            <td><%= loan.getBookId() %></td>
            <td><%= loan.getMemberId() %></td>
            <td><%= loan.getBorrowDate() %></td>
            <td><%= loan.getDueDate() %></td>
            <td><%= loan.getReturnDate() != null ? loan.getReturnDate() : "Chua tra" %></td>
            <td><%= loan.getOverdueFee() %></td>
            <td>
                <% if (loan.getReturnDate() == null) { %>
                <a href="admin/loans?action=return&id=<%= loan.getId() %>&feeType=daily" class="btn btn-sm btn-success">Tra (Theo ngay)</a>
                <a href="admin/loans?action=return&id=<%= loan.getId() %>&feeType=quantity" class="btn btn-sm btn-success">Tra (Theo so luong)</a>
                <% } %>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>