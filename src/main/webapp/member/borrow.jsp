<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.model.Book, java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mượn sách</title>
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
    <h2>Mượn sách</h2>
    <form action="member/borrow" method="post">
        <div class="mb-3">
            <label for="bookId" class="form-label">ID Sách</label>
            <input type="number" class="form-control" id="bookId" name="bookId" required>
        </div>
        <button type="submit" class="btn btn-primary">Mượn</button>
    </form>
    <h3 class="mt-4">Danh sách sách</h3>
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>Tiêu đề</th>
            <th>Tác giả</th>
            <th>Loại</th>
        </tr>
        </thead>
        <tbody>
        <% List<Book> books = (List<Book>) request.getAttribute("books"); %>
        <% if (books != null && !books.isEmpty()) { %>
        <% for (Book book : books) { %>
        <tr>
            <td><%= book.getId() %></td>
            <td><%= book.getTitle() %></td>
            <td><%= book.getAuthor() %></td>
            <td><%= book.getType() %></td>
        </tr>
        <% } %>
        <% } else { %>
        <tr>
            <td colspan="4" class="text-center">Chưa có sách nào.</td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>