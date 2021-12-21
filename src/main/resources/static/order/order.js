
angular.module('market-front').controller('orderController',
	function ($rootScope, $scope, $http, $location, $localStorage)
{
	const contextOrderPath = 'http://localhost:12440/market/api/v1/order';
	var cartPageCurrent = 0;
	var cartPageTotal = 0;
	$scope.contextPrompt = "";
	$scope.orderNumber = 0;
	$scope.showForm = true;
	$scope.wellDone = false;
	$scope.canPay = false;

	$scope.loadOrderDetailes = function ()
	{
		$http.get (contextOrderPath + '/details')
		.then (
		function successCallback (response)
		{
			$scope.orderDetails = response.data;
			console.log ($scope.orderDetails);
			if ($scope.orderDetails.cartDto.load <= 0)
			{
				message = 'Заказ пуст.';
				alert (message);
				console.log (message);
				$location.path('/cart')
			}
			else
			{	$scope.contextPrompt = "Ваш заказ сформирован.";
				$scope.cart = $scope.orderDetails.cartDto;
				console.log ('Детали заказа загружены:');
				console.log (response.data);
			}
		},
		function failureCallback (response)
		{
			$scope.contextPrompt = "Произошла ошибка!";
			alert (response.data);
		});
	}

	$scope.confirmOrder = function ()
	{
		if ($scope.orderDetails.cartDto.load > 0)
		$http
		({	url:	contextOrderPath + '/confirm',
			method:	'POST',
			data:	$scope.orderDetails
		})
		.then(
		function successCallback (response)
		{
			$scope.showForm = false;
			$scope.wellDone = true;
			$scope.canPay   = true;
			$scope.contextPrompt = 'Ваш заказ оформлен!';
			$scope.orderDetails = response.data;
			alert ($scope.contextPrompt);	//< кажется, это тоже работает асинхронно
		},
		function failureCallback (response)
		{
			$scope.contextPrompt = "Произошла ошибка! ";
			console.log (response.data);
			console.log (response.data.messages);
			alert ($scope.contextPrompt + response.data.messages);
			/* если выводим сообщение от валидатора, то нужно укзаывать имя поля с сообщением,
			например:	response.data.messages,
			а для всех нормальных сообщений — указываем только response.data.	*/
		});
	}

	$scope.cancelOrdering = function () { $location.path('/cart'); }

	$scope.ok = function () { $location.path('/store'); }

	$scope.pay = function ()
	{
		$http
		({	url:	contextOrderPath + '/pay',
			method:	'POST',
			data:	$scope.orderDetails
		})
		.then(
		function successCallback (response)
		{
			$scope.showForm = false;
			$scope.wellDone = true;
			$scope.canPay   = false;
			$scope.orderDetails.orderState = 'Оплачен';
			$scope.contextPrompt = 'Ваш заказ оплачен.';
//			$scope.orderDetails = response.data;
			alert ($scope.contextPrompt);	//< кажется, это тоже работает асинхронно
		},
		function failureCallback (response)
		{
			$scope.showForm = false;
			$scope.wellDone = true;
			$scope.canPay   = true;
			$scope.contextPrompt = 'ОШИБКА!  Не удалось оплатить ваш заказ.\r\rВы можете ещё раз попробовать оплатить его, или отбсудите вопрос оплаты с нашим менеджером.';
			alert ($scope.contextPrompt);	//< кажется, это тоже работает асинхронно
		});
	}
//----------------------------------------------------------------------- действия
	$scope.infoProduct = function (oitem)
	{
		alert('id:                '+ oitem.productId +
		   ',\rкатегория:         '+ oitem.category +
		   ',\rназвание:          '+ oitem.title +
		   ',\rцена:              '+ oitem.price +
		   ',\rколичество:        '+ oitem.quantity +
		   ',\rеденица измерения: '+ oitem.measure +
		   ',\rостаток:           '+ oitem.rest +
		   ',\rобщая стоимость:   '+ oitem.cost);
	}
//----------------------------------------------------------------------- условия
	$scope.canShowConfirmationButton = function ()	{ return $rootScope.isUserLoggedIn(); }

	$scope.canShowOrderedItems = function ()	{ return $rootScope.isUserLoggedIn(); }
//----------------------------------------------------------------------- вызовы
	$scope.loadOrderDetailes();
});