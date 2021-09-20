
angular.module('market-front').controller('orderController',
	function ($rootScope, $scope, $http, $location)
{
	const contextOrderPath = 'http://localhost:8189/market/api/v1/order';
	var cartPageCurrent = 0;
	var cartPageTotal = 0;
	$scope.contextPrompt = "";
	$scope.orderNumber = 0;
	$scope.showForm = true;
	$scope.wellDone = false;

	$scope.loadOrderDetailes = function ()
	{
		$http.get (contextOrderPath + '/details')
		.then (
		function successCallback (response)
		{
		//TODO:	тут мы получаем данные о выбранных товарах. В списке отсутствуют «пустые» позиции.
		//		Возможно, следует юзеру сообщить об их отсутствии.
			$scope.contextPrompt = "Ваш заказ сформирован.";
			$scope.orderDetails = response.data;
			$scope.cart = $scope.orderDetails.cart;
/*			console.log ('Детали заказа загружены:');
			console.log (response.data);*/
		},
		function failureCallback (response)
		{
			$scope.contextPrompt = "Произошла ошибка!";
			alert (response.data);
		});
	}

	$scope.confirmOrder = function ()
	{
		$http
		({	url:	contextOrderPath + '/confirm',
			method:	'POST',
			data:	$scope.orderDetails
		})
		.then(
		function successCallback (response)
		{
			$scope.showForm = false;
			$scope.contextPrompt = 'Ваш заказ оформлен.';
			$scope.orderDetails = response.data;
			alert ('Ваш заказ оформлен.');	//< кажется, это тоже работает асинхронно
		},
		function failureCallback (response)
		{
			$scope.contextPrompt = "Произошла ошибка!";
//			console.log ('Error: '+ response.data.messages);
			alert (response.data.messages);
			/* если выводим сообщение от валидатора, то нужно укзаывать имя поля с сообщением,
			например:	response.data.messages,
			а для всех нормальных сообщений — указываем только response.data.	*/
		});
	}

	$scope.cancelOrdering = function () { $location.path('/cart'); }

	$scope.ok = function () { $location.path('/store'); }
//----------------------------------------------------------------------- действия
	$scope.infoProduct = function (oitem)	//+
	{
		alert('id:              '+ oitem.productId +
		   ',\rкатегория:       '+ oitem.category +
		   ',\rназвание:        '+ oitem.title +
		   ',\rцена:            '+ oitem.price +
		   ',\rколичество:      '+ oitem.quantity +
		   ',\rобщая стоимость: '+ oitem.cost);
	}
//----------------------------------------------------------------------- условия
	$scope.canShowConfirmationButton = function ()	//+
	{
		return $rootScope.isUserLoggedIn();
	}

	$scope.canShowOrderedItems = function ()	//+
	{
		return $rootScope.isUserLoggedIn();
	}
//----------------------------------------------------------------------- вызовы
	$scope.loadOrderDetailes();
});