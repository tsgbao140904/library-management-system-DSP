<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.model.Book" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sua thong tin sach</title>
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
    <h2>Sua thong tin sach</h2>
    <% Book book = (Book) request.getAttribute("book"); %>
    <% if (book != null) { %>
    <form action="admin/books" method="post" class="w-50 mx-auto">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="id" value="<%= book.getId() %>">
        <div class="mb-3">
            <label for="title" class="form-label">Tieu de</label>
            <input type="text" class="form-control" id="title" name="title" value="<%= book.getTitle() %>" required>
        </div>
        <div class="mb-3">
            <label for="author" class="form-label">Tac gia</label>
            <input type="text" class="form-control" id="author" name="author" value="<%= book.getAuthor() %>" required>
        </div>
        <div class="mb-3">
            <label for="type" class="form-label">Loai sach</label>
            <select class="form-control" id="type" name="type" required>
                <option value="Printed" <%= book.getType().equals("Printed") ? "selected" : "" %>>Sach giay</option>
                <option value="EBook" <%= book.getType().equals("EBook") ? "selected" : "" %>>Sach dien tu</option>
            </select>
        </div>
        <button type="submit" class="btn btn-primary w-100">Cap nhat</button>
        <a href="admin/books" class="btn btn-secondary w-100 mt-2">Quay lai</a>
    </form>
    <% } else { %>
    <div class="alert alert-danger">
        Khong tim thay sach de sua.
    </div>
    <a href="admin/books" class="btn btn-secondary">Quay lai</a>
    <% } %>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>