
angular.module('market-front').controller('user_profileController', function ($rootScope, $scope, $http, $location)
{
	const contextUserProfilePath = 'http://localhost:8189/market/api/v1/user_profile';

	$scope.loadUserInfo = function ()
	{
		$http.get (contextUserProfilePath + '/userinfo')
		.then(
		function successCallback (response)
		{
			$scope.userInfo = response.data;
			console.log (response.data);
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
	$scope.canShow = function () { return $rootScope.isUserLoggedIn(); }
//----------------------------------------------------------------------- вызовы
	$scope.loadUserInfo();
	$scope.loadOrders();
});