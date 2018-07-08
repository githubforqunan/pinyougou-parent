app.service("itemSearchService",function ($http) {
    this.searchItem = function (searchMap) {
        return $http.post("itemSearch/findByPage.do",searchMap);
    }
});