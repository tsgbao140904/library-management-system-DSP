<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.model.Member" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sua thong tin thanh vien</title> <!-- Thay "Sửa thông tin thành viên" bằng "Sua thong tin thanh vien" để tránh dấu gạch -->
    <base href="${pageContext.request.contextPath}/">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Thu vien</a> <!-- Thay "Thư viện" bằng "Thu vien" để tránh dấu gạch -->
        <div class="navbar-nav">
            <a class="nav-link" href="admin/books">Sach</a> <!-- Thay "Sách" bằng "Sach" -->
            <a class="nav-link" href="admin/members">Thanh vien</a> <!-- Thay "Thành viên" bằng "Thanh vien" -->
            <a class="nav-link" href="admin/loans">Muon sach</a> <!-- Thay "Mượn sách" bằng "Muon sach" -->
            <a class="nav-link" href="admin/reports">Bao cao</a> <!-- Thay "Báo cáo" bằng "Bao cao" -->
            <a class="nav-link" href="logout">Dang xuat</a> <!-- Thay "Đăng xuất" bằng "Dang xuat" -->
        </div>
    </div>
</nav>
<div class="container mt-5">
    <h2>Sua thong tin thanh vien</h2> <!-- Thay "Sửa thông tin thành viên" bằng "Sua thong tin thanh vien" -->
    <% Member member = (Member) request.getAttribute("member"); %>
    <form action="admin/members" method="post" class="w-50 mx-auto">
        <input type="hidden" name="id" value="<%= member.getId() %>">
        <div class="mb-3">
            <label for="username" class="form-label">Ten dang nhap</label> <!-- Thay "Tên đăng nhập" bằng "Ten dang nhap" -->
            <input type="text" class="form-control" id="username" name="username" value="<%= member.getUsername() %>" required>
        </div>
        <div class="mb-3">
            <label for="password" class="form-label">Mat khau</label> <!-- Thay "Mật khẩu" bằng "Mat khau" -->
            <input type="password" class="form-control" id="password" name="password" value="<%= member.getPassword() %>" required>
        </div>
        <div class="mb-3">
            <label for="fullName" class="form-label">Ho va ten</label> <!-- Thay "Họ và tên" bằng "Ho va ten" -->
            <input type="text" class="form-control" id="fullName" name="fullName" value="<%= member.getFullName() %>" required>
        </div>
        <div class="mb-3">
            <label for="role" class="form-label">Vai tro</label> <!-- Thay "Vai trò" bằng "Vai tro" -->
            <select class="form-control" id="role" name="role" required>
                <option value="ADMIN" <%= member.getRole().equals("ADMIN") ? "selected" : "" %>>ADMIN</option>
                <option value="MEMBER" <%= member.getRole().equals("MEMBER") ? "selected" : "" %>>MEMBER</option>
            </select>
        </div>
        <button type="submit" class="btn btn-primary w-100">Cap nhat</button> <!-- Thay "Cập nhật" bằng "Cap nhat" -->
        <a href="admin/members" class="btn btn-secondary w-100 mt-2">Quay lai</a> <!-- Thay "Quay lại" bằng "Quay lai" -->
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>