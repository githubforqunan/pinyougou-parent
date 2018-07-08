/**
 * 
 */
app.service('brandService',function($http){
		
		this.save = function(action,entry){
			return $http.post('../brand/' + action + '.do', entry);
		}
		
		this.findOne = function(id){
			return $http.get("../brand/findById.do?id=" + id);
		}
		
		this.delSelect = function(ids){
			return $http.get('../brand/delSelect.do?ids=' +ids);
		}
		
		this.search = function(page, rows,queryEntry){
			
			return $http.post('../brand/search.do?page=' + page + '&rows=' +rows, queryEntry);
		}
		
		this.findBrandList = function(){
			return $http.get('../brand/findBrandList.do');
		}
	});