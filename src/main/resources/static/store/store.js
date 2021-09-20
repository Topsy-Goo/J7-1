
angular.module('market-front').controller('storeController', function ($rootScope, $scope, $http, $location)
{
/*	angular.module('market-front')	означает: используем приложение 'market-front'

	controller('storeController', … означает, что здесь мы создаём контроллер и даём ему указанное имя;

	$location - позволяет переходить на др.страницу
	$scope-переменные не видны за пределами того контроллера, в котором они объявлены.
	$rootScope - глобальный контекст (позволяет обращаться к ф-циям (и переменным?) откуда угодно)

	Каждый раз, когда мы будем переходить на эту страницу, будет производиться её инициализация.
*/
	const contextProductPath = 'http://localhost:8189/market/api/v1/products';
	const contextCartPath = 'http://localhost:8189/market/api/v1/cart';

	var productPageCurrent = 0;
	var productPageTotal = 0;	//< такая переменная не видна в HTML-файле
	$scope.cartLoad = 0;	//< такая переменная    видна в HTML-файле

/*	Типовое (для этого проекта) описание функции в js (для функций $scope. и var используются также, как для переменных):	*/
	$scope.loadProductsPage = function ()
	{
		$scope.getCartLoad();
		$http
		({
			url: contextProductPath + '/page',
			method: 'GET',
			params:	{p: productPageCurrent}
		})
		.then (
		function successCallback (response)
		{
			$scope.productsPage = response.data;	//< переменную можно объявлять где угодно в коде
			productPageCurrent = $scope.productsPage.pageable.pageNumber;
			productPageTotal = $scope.productsPage.totalPages;

			$scope.paginationArray = $scope.generatePagesIndexes(1, productPageTotal);
console.log ('response.data: '+ response.data);
console.log (response.data);
		},
		function failureCallback (response)//< вызывается асинхронно.
		{
console.log ('Error: response.data:\r'+ response.data);
console.log ('Error: response.data.messages:\r'+ response.data.messages);
			alert ('Error: response.data:\r'+ response.data);
			alert ('Error: response.data.messages:\r'+ response.data.messages);
		});
	}
//----------------------------------------------------------------------- страницы
	$scope.generatePagesIndexes = function (startPage, endPage)
	{
		let arr = [];
		for (let i = startPage; i < endPage + 1; i++)
		{
			arr.push(i);
		}
		return arr;
	}

	$scope.loadProducts = function (pageIndex = 1)	//< загрузка страницы по индексу
	{
		productPageCurrent = pageIndex -1;
		$scope.loadProductsPage();
	}

	$scope.prevProductsPage = function ()	//< загрузка левой соседней страницы
	{
		productPageCurrent --;
		$scope.loadProductsPage();
	}

	$scope.nextProductsPage = function ()	//< загрузка правой соседней страницы
	{
		productPageCurrent ++;
		$scope.loadProductsPage();
	}
//----------------------------------------------------------------------- действия
	$scope.deleteProduct = function (pid)
	{
		$http.get (contextProductPath + '/delete/' + pid)
		.then (function (response)
		{
			$scope.loadProductsPage();
		});
	}

	$scope.infoProduct = function (p)
	{
		alert('id: '      + p.productId +
		   ',\rCategory: '+ p.category +
		   ',\rTitle: '   + p.title +
		   ',\rPrice: '   + p.price);
	}

	$scope.startEditProduct = function (pid)	{	$location.path ('/edit_product/'+ pid);	}

	$scope.getCartLoad = function()
	{
		if ($rootScope.isUserLoggedIn())
		{
			$http.get (contextCartPath + '/load')
			.then (
			function successCallback (response)
			{
				$scope.cartLoad = response.data;
				console.log ('$scope.cartLoad = '+ response.data);
			},
			function failureCallback (response)
			{
				$scope.cartLoad = 0;
				alert (response.data.messages);	//< название параметра взято из ErrorMessage
				console.log ('Error: '+ response.data.messages);
			});
		}
	}
//----------------------------------------------------------------------- плюс/минус
	$scope.cartPlus = function (pid)
	{
		$http.get (contextCartPath + '/plus/'+ pid)
		.then (
		function successCallback (response)
		{
			$scope.getCartLoad();
		},
		function failureCallback (response)
		{
			alert (response.data.messages);	//< название параметра взято из ErrorMessage
			console.log ('Error: '+ response.data.messages);
		});
	}

	$scope.cartMinus = function (pid)
	{
		if ($scope.cartLoad > 0)
		{
			$http.get (contextCartPath + '/minus/' + pid)
			.then (
			function successCallback (response)
			{
				$scope.getCartLoad();
			},
			function failureCallback (response)
			{
				alert (response.data.messages);	//< название параметра взято из ErrorMessage
				console.log ('Error: '+ response.data.messages);
			});
		}
	}

	$scope.canShow = function()	{	return $rootScope.isUserLoggedIn();	}
//----------------------------------------------------------------------- вызовы
	$scope.loadProductsPage();
});
