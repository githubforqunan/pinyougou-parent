 //控制层 

app.controller('goodsController' ,function($scope,$controller,$location,typeTemplateService,itemCatService,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	$scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){

        var id= $location.search()['id'];

		if(id==null){
			return;
		}
        $scope.entity.goods.id=id;
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				editor.html(response.goodsDesc.introduction);

				$scope.entity.goodsDesc.itemImages = JSON.parse(response.goodsDesc.itemImages);

                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.goodsDesc.customAttributeItems);

                $scope.entity.goodsDesc.specificationItems = JSON.parse(response.goodsDesc.specificationItems);

                for(var i=0;i<response.itemList.length;i++){
                    $scope.entity.itemList[i].spec = JSON.parse(response.itemList[i].spec);
				}

			}
		);
	}

	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID

			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加
		}
        $scope.entity.goodsDesc.introduction=editor.html();
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	$scope.add=function(){				
		
		serviceObject=goodsService.add( $scope.entity);//增加 
		$scope.entity.goodsDesc.introduction=editor.html();
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					alert(response.message);
		        	$scope.reloadList();//重新加载
		        	editor.html('');
		        	
		        	
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	

	
	$scope.searchEntity={auditStatus:0};//定义搜索对象
	
	//搜索
	$scope.search=function(page,rows){	
		
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	$scope.add_image_entity = function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	
	$scope.del_image_entity = function($index){
		
		$scope.entity.goodsDesc.itemImages.splice($index,1);
	}
	
	$scope.	itemCat1List=[];
	$scope.selectItemCat1List = function(){
		itemCatService.findParentId(0).success(
				function(response){
					$scope.itemCat1List = response;
				});
	}
	
	$scope.itemCat2List=[];
	$scope.$watch("entity.goods.category1Id",function(newValue,oldValue){
		
		itemCatService.findParentId(newValue).success(
				function(response){
					$scope.itemCat2List=response;
				}
		);
	});
	
	$scope.itemCat3List=[];
	$scope.$watch("entity.goods.category2Id",function(newValue,oldValue){
		itemCatService.findParentId(newValue).success(
				function(response){
					$scope.itemCat3List=response;
				}
		);
	});
	
	$scope.brandIds=[];
	$scope.specIds=[];
	$scope.specifiction=[{"options":[]}];
	$scope.$watch("entity.goods.category3Id",function(newValue,oldValue){
		itemCatService.findOne(newValue).success(
				function(response1){
					$scope.entity.goods.typeTemplateId = response1.typeId;
					typeTemplateService.findOne(response1.typeId).success(function(response2){
						
						$scope.brandIds = JSON.parse(response2.brandIds);
						if($location.search()['id']==null){
                            $scope.entity.goodsDesc.customAttributeItems= JSON.parse(response2.customAttributeItems);
						}

//						$scope.specIds = JSON.paeser(response.specIds);
						
						
						
					});
					
					typeTemplateService.findSpecList(response1.typeId).success(function(response3){
						
						$scope.specifiction = response3;
					});
				}
		);
		
		
	});
	
	$scope.updateSpecAttribute = function($event,text,value){
		var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",text);
		
		if(obj!=null){
			if($event.target.checked){
				obj.attributeValue.push(value);
			}else{
				obj.attributeValue.splice(value,1);
				if(obj.attributeValue.length==0){
					
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj),1);
				}
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":text,"attributeValue":[value]});
		}
		
		
	}
	
	$scope.createItemList = function(){
		$scope.entity.itemList=[{spec:{},price:0,num:10,status:'0',isDefault:'0'} ];
		var items = $scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<items.length;i++){
			$scope.entity.itemList = addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
	}
	
	addColumn = function(list,name,values){
		var newList=[];
		for(var i=0;i<list.length;i++){
			var oldRow=list[i];
			for(var j=0;j<values.length;j++){
				var newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[name]=values[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
	
	$scope.categorys=[];
	$scope.findAllCategory = function(){
//		alert("----");
		itemCatService.findAll().success(function(response){
			for(var i=0;i<response.length;i++){
				$scope.categorys[response.id] = response.name;
			}
			
		});
	}



    $scope.checkAttibuteValue = function(text,optionName){

        var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",text);
        if(obj==null){
        	return false;
		}else{
        	if(obj.attributeValue.indexOf(optionName)>=0){
        		return true;
			}else{
        		return false;
			}
		}
	}
    $scope.toList = function () {
		location.href="goods.html";
    }

    $scope.toAdd = function(){
        location.href="goods_edit.html";
	}
	
	$scope.updateStatus = function (status) {
		alert(status);
        goodsService.updateStatus(status,$scope.selectIds).success(function (response) {
            if(response.success){
                $scope.selectIds = {};
                $scope.reloadList();

            }else{
                alert(response.message);
            }
        });
    }

});	
