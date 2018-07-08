 //控制层 
app.controller('contentController' ,function($scope,$controller ,contentService){
	

    $scope.contentList=[];
    $scope.findByCategoryId	= function (id) {
		contentService.findByCategoryId(id).success(function (response) {
            $scope.contentList[1] = response;
        });
    }
    $scope.keywords ='';
    $scope.search = function(){
        location.href="http://localhost:9095/search.html#?keywords="+$scope.keywords;
    }
    
});	
