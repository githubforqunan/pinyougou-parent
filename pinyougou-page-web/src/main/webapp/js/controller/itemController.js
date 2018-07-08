app.controller('itemController',function($scope){
	$scope.specificationItems={};
	$scope.sku={};
	$scope.addNum = function(i){
		
		$scope.num=$scope.num+i;
		if($scope.num<1){
			$scope.num=1;
		}
		
	}
	
	$scope.isSelected = function(name,value){
		
		if($scope.specificationItems[name]==value){
			
			return true;
		}else{
			return false
		}
	}
	
	$scope.selectSpecification = function(name,value){

		$scope.specificationItems[name]=value;
		searchSku();
	}
	
	$scope.loadSku=function(){
		$scope.sku=skuList[0];		
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}
	
	matchObject=function(map1,map2){		
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}			
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}			
		}
		return true;		
	}

	searchSku=function(){
		for(var i=0;i<skuList.length;i++ ){
			if( matchObject(skuList[i].spec ,$scope.specificationItems ) ){
				$scope.sku=skuList[i];
				return ;
			}			
		}	
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的		
	}

	$scope.addToCart=function(){
		alert('skuid:'+$scope.sku.id);				
	}


});