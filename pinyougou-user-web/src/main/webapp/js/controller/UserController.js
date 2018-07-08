app.controller("userController",function ($scope,userService) {

    $scope.password="";
    $scope.smsCode="";
    $scope.reg = function () {

      if($scope.password==$scope.entity.password){
          userService.register($scope.entity,$scope.smsCode).success(
              function (response) {
                  alert(response.message);
              }
          );
      } else{
          $scope.password="";
          $scope.entity.password="";
          alert("两次输入不一致!");
      }
    }

    $scope.sendCode = function () {
        if($scope.entity.phone==null){
            alert("手机号码不能为空！")
            return;
        }
        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        );
    }
});