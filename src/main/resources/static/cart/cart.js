
angular.module('market-front').controller('cartController', function ($rootScope, $scope, $http, $location)
{
	const contextCartPath = 'http://localhost:8189/market/api/v1/cart';
	var cartPageCurrent = 0;
	var cartPageTotal = 0;
	$scope.cartLoad = 0;
	$scope.cartCost = 0;
	$scope.titlesCount = 0;	//< используется для проверки, пустая ли корзина.


	$scope.loadCart = function ()
	{
		$http.get (contextCartPath)
		.then (
		function successCallback (response)
		{
			$scope.cart = response.data;	//< собственно, список наименований (включая «пустые» позиции)
			$scope.cartLoad = response.data.load;	//< количество единиц товара
			$scope.cartCost = response.data.cost;	//< общая стоимость товаров в корзине
			$scope.titlesCount = response.data.titlesCount;	//< количество наименований (включая «пустые» позиции)
//console.log (response.data);
		},
		function failureCallback (response)	//< вызывается асинхронно.
		{
			$scope.cartLoad = 0;
			$scope.cartCost = 0;
			$scope.titlesCount = 0;
/*console.log ('Error: '+ response.data);
console.log ('Error: '+ response.data.messages);*/
			alert (response.data);
		});
	}

	$scope.gotoOrder = function () { $location.path('/order'); }
//----------------------------------------------------------------------- плюс/минус
	$scope.cartMinus = function (pid, quantity)
	{
		if (quantity > 0)
		{
			$http.get (contextCartPath + '/minus/'+ pid)
			.then (
			function successCallback (response)
			{
				$scope.loadCart();
			},
			function failureCallback (response)
			{
				alert (response.data.messages);
				console.log ('Error: '+ response.data);
			});
		}
	}

	$scope.cartPlus = function (pid)
	{
		$http.get (contextCartPath + '/plus/'+ pid)
		.then (
		function successCallback (response)
		{
			$scope.loadCart();
		},
		function failureCallback (response)
		{
			alert (response.data);
			console.log ('Error: '+ response.data);
		});
	}

	$scope.infoProduct = function (oitem)
	{
		alert('id:              '+ oitem.productId +
		   ',\rкатегория:       '+ oitem.category +
		   ',\rназвание:        '+ oitem.title +
		   ',\rцена:            '+ oitem.price +
		   ',\rколичество:      '+ oitem.quantity +
		   ',\rобщая стоимость: '+ oitem.cost);
	}

	$scope.removeFromCart = function (pid)
	{
		$http.get (contextCartPath + '/remove/' + pid)
		.then (
		function successCallback (response)
		{
			$scope.loadCart();
		},
		function failureCallback (response)
		{
			alert (response.data);
		});
	}

	$scope.clearCart = function ()
	{
		$http.get (contextCartPath + '/clear')
		.then (
		function successCallback (response)
		{
			$scope.loadCart();
		},
		function failureCallback (response)
		{
			alert (response.data);
		});
	}
//----------------------------------------------------------------------- условия
	$scope.isCartEmpty = function ()
	{
		if ($scope.titlesCount <= 0) { return true; } else { return false; }
	}
//----------------------------------------------------------------------- вызовы
	$scope.loadCart();
});

