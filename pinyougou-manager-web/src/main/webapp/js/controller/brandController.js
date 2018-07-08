/**
 * 
 */
app.controller('brandController', function($scope, $controller,brandService) {
	$controller('baseController',{$scope:$scope});
		$scope.reloadList = function() {
			//切换页码  
			$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
		}
		
		/* //分页
		$scope.findPage = function(page, rows) {
			
			$http.get('../brand/findPage.do?page=' + page + '&rows=' + rows)
					.success(function(response) {
						
						$scope.list = response.rows;
						$scope.paginationConf.totalItems = response.total;//更新总记录数
					});
		} */

		$scope.save = function() {
			//alert($scope.entry.name);
			var action = "update";
			if ($scope.entry.id == null) {
				action = "add";
			}
			brandService.save(action,$scope.entry).success(
					function(response) {
						if (response.success) {
							$scope.reloadList();
						} else {
							alert(response.message);
						}
					});
		}

		$scope.findOne = function(id) {

			brandService.findOne(id).success(
					function(response) {

						$scope.entry = response;
					});
		}

		

		$scope.delSelect = function() {
			
			if ($scope.ids != null) {
				brandService.delSelect($scope.ids).success(function(response) {
							if (response.success) {
								$scope.reloadList();
							} else {
								alert(respose.message);
							}
						});
			}
		}
		$scope.queryEntry={};
		$scope.search = function(page, rows) {
			
			brandService.search(page, rows,$scope.queryEntry).success(function(response) {
				$scope.list = response.rows;
				$scope.paginationConf.totalItems = response.total;
			});
		} 

	});