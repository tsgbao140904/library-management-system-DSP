<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.model.Member, java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quan ly thanh vien</title>
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
    <h2>Quan ly thanh vien</h2>
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>Ten dang nhap</th>
            <th>Ho va ten</th>
            <th>Vai tro</th>
            <th>Hanh dong</th>
        </tr>
        </thead>
        <tbody>
        <% List<Member> members = (List<Member>) request.getAttribute("members"); %>
        <% for (Member member : members) { %>
        <tr>
            <td><%= member.getId() %></td>
            <td><%= member.getUsername() %></td>
            <td><%= member.getFullName() %></td>
            <td><%= member.getRole() %></td>
            <td>
                <a href="admin/members?action=edit&id=<%= member.getId() %>" class="btn btn-sm btn-warning">Sua</a>
                <a href="admin/members?action=delete&id=<%= member.getId() %>" class="btn btn-sm btn-danger">Xoa</a>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>