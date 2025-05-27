<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.model.Member" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sửa thông tin thành viên</title>
    <base href="${pageContext.request.contextPath}/">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
    <style>
        .password-toggle {
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
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
    <h2 class="text-center">Sửa thông tin thành viên</h2>
    <% Member member = (Member) request.getAttribute("member"); %>
    <form action="admin/members" method="post" class="w-50 mx-auto position-relative">
        <input type="hidden" name="id" value="<%= member.getId() %>">
        <div class="mb-3">
            <label for="username" class="form-label">Tên đăng nhập</label>
            <input type="text" class="form-control" id="username" name="username" value="<%= member.getUsername() %>" required>
        </div>
        <div class="mb-3 position-relative">
            <label for="password" class="form-label">Mật khẩu</label>
            <input type="password" class="form-control" id="password" name="password" value="<%= member.getPassword() %>" required>
            <span class="password-toggle" onclick="togglePassword()">
                <i class="bi bi-eye" id="toggleIcon"></i>
            </span>
        </div>
        <div class="mb-3">
            <label for="fullName" class="form-label">Họ và tên</label>
            <input type="text" class="form-control" id="fullName" name="fullName" value="<%= member.getFullName() %>" required>
        </div>
        <div class="mb-3">
            <label for="role" class="form-label">Vai trò</label>
            <select class="form-control" id="role" name="role" required>
                <option value="ADMIN" <%= member.getRole().equals("ADMIN") ? "selected" : "" %>>ADMIN</option>
                <option value="MEMBER" <%= member.getRole().equals("MEMBER") ? "selected" : "" %>>MEMBER</option>
            </select>
        </div>
        <button type="submit" class="btn btn-primary w-100">Cập nhật</button>
        <a href="admin/members" class="btn btn-secondary w-100 mt-2">Quay lại</a>
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css"></script>
<script>
    function togglePassword() {
        const passwordField = document.getElementById("password");
        const toggleIcon = document.getElementById("toggleIcon");
        if (passwordField.type === "password") {
            passwordField.type = "text";
            toggleIcon.classList.remove("bi-eye");
            toggleIcon.classList.add("bi-eye-slash");
        } else {
            passwordField.type = "password";
            toggleIcon.classList.remove("bi-eye-slash");
            toggleIcon.classList.add("bi-eye");
        }
    }
</script>
</body>
</html>