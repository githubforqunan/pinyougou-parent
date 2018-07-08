app.controller("itemSearchController",function($scope,$location,itemSearchService,$controller){


    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,"pageSize":10,"sort":'',"sortField":''};
    $scope.search = function () {

        itemSearchService.searchItem($scope.searchMap).success(function (response) {

            $scope.resultMap = response;
            $scope.buildPageLable();

        });

    }

    $scope.addSearchItem = function (key,value) {
        if(key=='brand' || key=='category' || key=='price' ){
            $scope.searchMap[key] = value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }

    $scope.delSearchItem = function (key) {
        if(key=='brand' || key=='category' || key=='price' ){
            $scope.searchMap[key] = '';
        }else{
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }


    $scope.buildPageLable =function () {
        $scope.pageLable=[];
        var maxPageNum = $scope.resultMap.totalPage;
        var startPage = 1;
        var endPage = maxPageNum;

        if ($scope.resultMap.totalPage>5){
            startPage = $scope.searchMap.pageNo-2;
            endPage = $scope.searchMap.pageNo+2;
            if ($scope.searchMap.pageNo<=3){
                startPage=1;
                endPage=5;
            }
            if ($scope.searchMap.pageNo>=maxPageNum-2) {
                    startPage=maxPageNum-4;
                    endPage = maxPageNum;
            }

        }

        for (var i=startPage;i<=endPage;i++){
            $scope.pageLable.push(i);
        }

    }

    $scope.queryByPage = function (num) {
        if(num<1 || num>$scope.resultMap.totalPage){
            return null;
        }
        searchMap.pageNo=num;
        search();
    }

    $scope.orderByPrice = function (sort,sortField) {

        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }

    $scope.haveBrand=false;
    $scope.keywordsIsBrand=function(keywords){
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if ($scope.resultMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0) {
                $scope.haveBrand=true;
                break;
            }
        }
    }

    $scope.loadkeywords = function () {
        $scope.searchMap.keywords=  $location.search()['keywords'];
        $scope.search();

    }


});