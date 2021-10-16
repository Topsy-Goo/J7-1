
angular.module('market-front').controller('cartController',
	function ($rootScope, $scope, $http, $location, $localStorage)
{
	const contextCartPath = 'http://localhost:12440/market/api/v1/cart';
	var cartPageCurrent = 0;
	var cartPageTotal = 0;
	$scope.cartLoad = 0;
	$scope.cartCost = 0;
	$scope.titlesCount = 0;	//< используется для проверки, пустая ли корзина.


	$scope.loadCart = function ()
	{
		$http.get (contextCartPath + '/' + $localStorage.gbj7MarketGuestCartId)
		.then (
		function successCallback (response)
		{
			$scope.cart = response.data;	//< собственно, список наименований (включая «пустые» позиции)
			$scope.cartLoad = response.data.load;	//< количество единиц товара
			$scope.cartCost = response.data.cost;	//< общая стоимость товаров в корзине
			$scope.titlesCount = response.data.titlesCount;	//< количество наименований (включая «пустые» позиции)
		},
		function failureCallback (response)	//< вызывается асинхронно.
		{
			$scope.cartLoad = 0;
			$scope.cartCost = 0;
			$scope.titlesCount = 0;
			alert (response.data);
		});
	}

	$scope.gotoOrder = function ()
	{
		if ($scope.isAuthenticatedUser())
		{
			$location.path('/order');
		}
		else
		{	alert ('Чтобы оформить заказ, Вы должны авторизоваться.\r\r'+
				   'При авторизации выбранные Вами товары будут перемещены в корзину вашей учётной записи.');
		}
	}
//----------------------------------------------------------------------- плюс/минус
	$scope.cartMinus = function (pid, quantity)
	{
		if (quantity > 0)
		{
			$http.get (contextCartPath + '/minus/'+ pid + '/' + $localStorage.gbj7MarketGuestCartId)
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
		$http.get (contextCartPath + '/plus/'+ pid + '/' + $localStorage.gbj7MarketGuestCartId)
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
		   ',\rостаток:         '+ oitem.rest +
		   ',\rобщая стоимость: '+ oitem.cost);
	}

	$scope.removeFromCart = function (pid)
	{
		$http.get (contextCartPath +'/remove/'+ pid +'/'+ $localStorage.gbj7MarketGuestCartId)
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
		$http.get (contextCartPath +'/clear/'+ $localStorage.gbj7MarketGuestCartId)
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
	$scope.isAuthenticatedUser = function ()
	{
		if ($localStorage.webMarketUser) { return true; } else { return false; }
	}

	$scope.isCartEmpty = function ()
	{
		if ($scope.titlesCount <= 0) { return true; } else { return false; }
	}
//----------------------------------------------------------------------- вызовы
	$scope.loadCart();
});

