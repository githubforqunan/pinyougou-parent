app.service("userService",function ($http) {

    this.register = function (user,smsCode) {
        return $http.post("user/register.do?smsCode="+smsCode,user);
    }

    this.sendCode = function (phone) {
        return $http.get("user/sendCode.do?phone="+phone);
    }
});