//服务层
app.service('contentService',function($http){
	    	


    this.findByCategoryId = function (id) {
        return $http.get('../content/findByCategory.do?id='+id);
    }
});
