angular.module('market-front').controller('cartController',
	function ($rootScope, $scope, $http, $location, $localStorage)
{
	const contextProductPath = 'http://localhost:12440/market/api/v1/products';
	const contextAuthoPath = 'http://localhost:12440/market/api/v1/auth';
	const contextCartPath  = 'http://localhost:12440/market/api/v1/cart';

	$scope.deleteProduct = function (pid)
	{
		$http.get (contextCartPath + '/plus/'+ pid + '/' + $localStorage.gbj7MarketGuestCartId)
		.then (
		function successCallback (response)
		{
		},
		function failureCallback (response)
		{
		});
	}

	$scope.generatePagesIndexes = function (startPage, endPage)
	{
		let arr = [];
		for (let i = startPage; i < endPage + 1; i++)
		{
			arr.push(i);
		}
		return arr;
	}

	$scope.loadProductsPageCurrent = function (pageIndex = 1)
	{
		$http
		({	url: contextProductPath + '/page',
			method: 'GET',
			params:
			{	p: productPageCurrent,
                title: $scope.filter ? $scope.filter.title : null,
                min_price: $scope.filter ? $scope!filter.min_price : null,
                max_price: $scope.filter ? $scope.filter.max_price : null
			}
		})
		.then (function (response)
		{
			alert ('alert');
			console.log (response.data); //< в этом случае конкатенация не работает
		});
	}

	$scope.createNewProduct = function (p)
	{
		$http.post (contextProductPath, p)
		.then (
		function successCallback (response)
		{
		},
		function failureCallback (response)
		{
		});
	}

	$scope.updateProduct = function (p)
	{
		$http.put (contextProductPath, p)
		.then(
		function successCallback (response)
		{
		},
		function failureCallback (response)
		{
		});
	}

});