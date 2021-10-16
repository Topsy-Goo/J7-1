
angular.module('market-front').controller('user_profileController', function ($rootScope, $scope, $http, $location)
{
	const contextUserProfilePath = 'http://localhost:12440/market/api/v1/user_profile';
	const contextAuthoPath	= 'http://localhost:12440/market/api/v1/auth';
	$scope.canUserEditProducts = false;


	$scope.loadUserInfo = function ()
	{
		$http.get (contextUserProfilePath + '/userinfo')
		.then(
		function successCallback (response)
		{
			$scope.userInfo = response.data;
//			$scope.canEdit();
		},
		function failureCallback (response)
		{
			alert (response.data);
			console.log ('Error: '+ response.data);
		});
	}

	$scope.loadOrders = function ()
	{
		$http.get (contextUserProfilePath + '/orders')
		.then(
		function successCallback (response)
		{
			$scope.orders = response.data;
			$scope.ordersLength = response.data.length;
			console.log (response.data);
			console.log ($scope.paginationArray);
		},
		function failureCallback (response)
		{
			alert (response.data);
			console.log ('Error: '+ response.data.messages);
		});
	}
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
	$scope.gotoStore = function () { $location.path('/store'); }
//----------------------------------------------------------------------- условия
	$scope.canShow = function ()	{	return $rootScope.isUserLoggedIn();	}

	$scope.enableEditProducts = function ()
	{
		$rootScope.canEditProducts = !$rootScope.canEditProducts;
	}

	$scope.canEdit = function ()
	{
/*	Почему-то этот метод вызывается бесконечно, если его вызывать из html.
	Кажется, такие методы вызываются асинхронно.
	В какой-то момент он вдруг начал выводить в консоль комментарии, находящиеся внутри него, и кусочки своего кода. Разумеется, синтаксис при этом был в полном порядке. Сейчас метод работает нормально, но его тело значительно изменено.
*/
		$http.get (contextAuthoPath + '/can_edit_product')
		.then (
		function successCallback (response)
		{
			$scope.canUserEditProducts = response.data;
		},
		function failureCallback (response)
		{
			$scope.canUserEditProducts = false;
			console.log ('ОШИБКА.');
		});
	}

	$scope.statusColor = function (s)
	{
		if (s == 'Оплачен')	return 'blue';
		else
		if (s == 'Ожидает подтверждения')	return 'green';
		else
		if (s == 'Выполняется')	return 'red';
		else
		if (s == 'Отменён')	return 'grey';
/*		else
		if (s == '(Нет статуса)')	return '';*/
		else
		return 'black';
	}
//----------------------------------------------------------------------- вызовы
	$scope.loadUserInfo();
	$scope.loadOrders();
	$scope.canEdit();
});