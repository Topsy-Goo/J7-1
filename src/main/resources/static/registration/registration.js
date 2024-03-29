
angular.module('market-front').controller('registrationController',
						function ($rootScope, $scope, $http, $location, $localStorage)
{
/*	$routeParams -	позволяет при маршрутизации передавать парметры в адресной строке (маршрутизация
					описывается в index10.js. >> function config)
	$location - позволяет переходить на др.страницу.
	$http - позволяет посылать из приложения http-запросы
	$scope - некий обменник между этим js-файлом и html-файлом.
	$rootScope - глобальный контекст (позволяет обращаться к ф-циям (и переменным?) откуда угодно)
	$localStorage - локальное хранилище браузера (требуется подкл. скрипт ngStorage.min.js.)
*/
	const contextAuthoPath = 'http://localhost:12440/market/api/v1/auth';
	const contextCartPath  = 'http://localhost:12440/market/api/v1/cart';

	var contextPrompt_Registered = "Вы успешно зарегистрированы.";
	var contextPrompt_Unathorized = "Введите логин, паоль и адрес электронной почты.";
	var contextPrompt_LogedIn = "Вы авторизованы.";
	var contextPrompt_Error = "Ошибка регистрации.";
	$scope.contextPrompt = "";


	$scope.prepareToRegistration = function()
	{
		$scope.clearNewUserFields();

		if ($rootScope.isUserLoggedIn())
		{
			$scope.contextPrompt = contextPrompt_LogedIn;
		}
		else
		{
			$scope.contextPrompt = contextPrompt_Unathorized;
		}
	}

	$scope.tryToRegister = function ()
	{
		if ($scope.new_user != null)
		{
			$http.post (contextAuthoPath + '/register', $scope.new_user)
			.then(
			function successCallback (response)
			{
				if (response.data.token)
				{
					$http.defaults.headers.common.Authorization = 'Bearer ' + response.data.token;
					$localStorage.webMarketUser = {login: $scope.new_user.login, token: response.data.token};
					$scope.clearNewUserFields();
					$scope.contextPrompt = contextPrompt_Registered;
				}
				$scope.tryMergeCarts();
			},
			function failureCallback (response)	//кажется, errorCallback тоже можно использовать
			{
				alert ('ОШИБКА: '+ response.data.messages);
				console.log ('$scope.tryToRegister failure callback. : ', response.data.messages);
				$scope.contextPrompt = contextPrompt_Error;
			});
		}
	}

	$scope.cancelRegistration = function()
	{
		$scope.clearNewUserFields();
		$location.path('/main');
	}

	$scope.clearNewUserFields = function()	{	$scope.new_user = null;	}

	$scope.tryMergeCarts = function ()
	{
		if ($localStorage.gbj7MarketGuestCartId)
		{
			$http.get (contextCartPath + '/merge/' + $localStorage.gbj7MarketGuestCartId)
			.then (
			function successCallback (response)
			{
				console.log ('registration - $scope.tryMergeCarts - OK');
//////////////////////////				$scope.loadCart();
			},
			function failureCallback (response)
			{
				console.log ('Ой! @ registration - $scope.tryMergeCarts');
				alert (response.data);
			});
		}
	}
//-------------------------------------------------------------------------------- условия
	$scope.canShow = function()	{	return !$rootScope.isUserLoggedIn();	}
//-------------------------------------------------------------------------------- вызовы
	$scope.prepareToRegistration();	//< вызов описанной выше функции
});
