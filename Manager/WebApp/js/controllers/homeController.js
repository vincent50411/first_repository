cetsim.controller("homeCtrl", ["$scope", "userService", "$location", function (scope, user, $location) {

    if (!user.isSignin()) {
        $location.path("/signin");
    } else {
        user.goHomePage();
    }

}]);


cetsim.controller("routeCtrl", ["$scope", "userService", "commonService", "$rootScope", function ($scope, $userService, commonService, $rootScope, $timeout) {
    var loginuser = $userService.getUserModelFromLocalInfo();
    if (loginuser) {
        $scope.name = loginuser.name;
    }

    $scope.signout = function () {
        $userService.logout();
    };
    $scope.curUser = $userService.getUserModelFromLocalInfo();


    $scope.adminInfo = {};
    $scope.showAdminContactPanel = function () {
        $userService.getAdminInfo(function (err, res) {

           if (!err && res.code === commonService.config.ajaxCode.success) {

               $scope.adminInfo = res.data;
               commonService.layerOpen({
                   area: "440px",
                   title: "帮助",
                   content : $("#showAdminContactPanel")
               })
           }
        });
    };


    //需要监听自定义事件，更新界面显示信息 by lws 20170329
    $scope.$on("updateUserInfoEvent", function () {
        var _localUser = $userService.getUserModelFromLocalInfo();
        _localUser.photo = commonService.addRandomTimestampForUrl($userService.getUserPhotoAbsolutePathByFileName(_localUser.photo));
        _localUser.reserved1 = _localUser.photo;
        $scope.curUser = _localUser;
    });

    var _unbind = $rootScope.$on("event.userPhotoChanged", function () {
        var user = $userService.getUserModelFromLocalInfo();
        user.photo = user.photo + "&t=" + Math.random();
        user.reserved1 = user.photo;
        $scope.curUser = user;
    });
    $scope.$on("$destroy", _unbind);


}]);