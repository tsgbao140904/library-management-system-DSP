<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.model.Book, com.library.model.Member, com.library.model.Loan, java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bao cao</title>
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
    <h2 class="mb-4">Bao cao thong ke</h2>
    <div class="row">
        <div class="col-md-3 mb-3">
            <div class="card text-white bg-primary">
                <div class="card-body">
                    <h5 class="card-title">Tong so sach</h5>
                    <p class="card-text display-6"><%= request.getAttribute("totalBooks") != null ? request.getAttribute("totalBooks") : 0 %></p>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-3">
            <div class="card text-white bg-success">
                <div class="card-body">
                    <h5 class="card-title">Tong so thanh vien</h5>
                    <p class="card-text display-6"><%= request.getAttribute("totalMembers") != null ? request.getAttribute("totalMembers") : 0 %></p>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-3">
            <div class="card text-white bg-info">
                <div class="card-body">
                    <h5 class="card-title">Tong so sach dang muon</h5>
                    <p class="card-text display-6"><%= request.getAttribute("totalLoans") != null ? request.getAttribute("totalLoans") : 0 %></p>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-3">
            <div class="card text-white bg-warning">
                <div class="card-body">
                    <h5 class="card-title">Tong phi qua han</h5>
                    <p class="card-text display-6"><%= request.getAttribute("totalOverdueFees") != null ? request.getAttribute("totalOverdueFees") : 0.0 %></p>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>