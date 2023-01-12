// Xoá tin nhắn thông báo sau 3 phút
$(document).ready(() => {
    setTimeout(() => {
        $("#errorMessage").removeClass("alert", "alert-danger");
        $("#errorMessage").text("");
    }, 3000);
});