(function ()	//< Описание основной ф-ции
{
	angular
		.module('market-front', ['ngRoute','ngStorage'])
		.config(config)
		.run(run);
/*
	anguler.module - создание (основного или дополнительного) модуля приложения.

	('market-front', […]) - название приложения и список модулей-зависимостей (разделённых запятыми;

	наличие []-скобок означает создание основного модуля, а в скобках можно указать список подключаемых модулей (возможно подключение сторонних модулей);

	отсутствие []-скобок означает создание доп.модуля. При его создании будет выполнен поиск пиложения с указанным именем (поиск осн.модуля указанного приложения).

	ngRoute - имя модуля, подключенного в html-файле при пом.тэга <script src="…/angular-route.min.js">. (используется тут же, ниже, в function config())

	ngStorage - имя модуля, подключенного в html-файле при пом.тэга <script src="…/ngStorage.min.js">. (используется ниже, в function run() и в контроллере)

	config(func_name) - указывает на ф-цию, которая будет конфигурировать приложение.

	run(func_name) - указывает на ф-цию, которая будет запускаться при старте приложения.
*/

	function config ($routeProvider)
	{
	/*	$routeProvider - модуль, который позволяет переходить между страницами
	*/
		$routeProvider
			.when('/store',	//< задаём адрес страницы с товарами
			{
				templateUrl: 'store/store.html',	//<	адрес страницы с товарами и…
				controller:	 'storeController'		//	…имя её контроллера
			})
			.when('/main',		//< задаём постфикс для перехода на главную страницу
			{
				templateUrl: 'main/main.html',		//< адрес главной страницы и…
				controller:	 'mainController'		//	…имя её контроллера
			})
			.when('/edit_product/:pid',	//< для возможности передавать параметр требуется указать $routeParams в объявлении edit_productController'а.
			{
				templateUrl: 'edit_product/edit_product.html',
				controller:	 'edit_productController'
			})
			.when('/edit_product',	//< пусть переход на страницу через главное меню означает намерение создать новый товар, а не редактировать существующий
			{
				templateUrl: 'edit_product/edit_product.html',
				controller:	 'edit_productController'
			})
			.when('/cart',
			{
				templateUrl: 'cart/cart.html',
				controller:	 'cartController'
			})
			.when('/registration',
			{
				templateUrl: 'registration/registration.html',
				controller:	 'registrationController'
			})
			.otherwise(
			{
				redirectTo:	'/main'
			});
	}

	function run ($rootScope, $http, $localStorage)
	{
	/*	При запуске приложения во фронте неразлогиненный юзер будет считан (из локального хранилища
	браузера) и в соотв-ии с ним будет добавлен и настроен умолчальный заголовок Authorization (как
	при авторизации и регистрации).	(В нашем учебном проекте это не заработает, т.к. при старте
	приложения, бэк считывает БД из sql-файла, а при регистрации нового юзера он не записывается в
	упомянутый файл).
	*/
        if ($localStorage.webMarketUser)
        {
            $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.webMarketUser.token;
        }
	}
})();

angular.module('market-front').controller('indexController',
								function ($rootScope, $scope, $http, $localStorage, $location)
{
/*	function ($scope, $http) - инжектим модули, которые входят в стандартную поставку ангуляра:

	$http - позволяет посылать из приложения http-запросы
	$scope - некий обменник между этим js-файлом и html-файлом.
	$rootScope - глобальный контекст (позволяет обращаться к ф-циям (и переменным?) откуда угодно)
	$localStorage - локальное хранилище браузера (требуется подкл. скрипт ngStorage.min.js.)
*/
	const contextProductPath = 'http://localhost:8189/market/api/v1/auth';
	$scope.appTitle = 'Marketplace';
	$scope.mainPageTitle = 'Главная страница';
	$scope.storePageTitle = 'Каталог продуктов';
	$scope.edit_productPageTitle = 'Создать продукт';
	$scope.cartPageTitle = 'Ваша корзина';

	$rootScope.isUserLoggedIn = function ()
	{
		if ($localStorage.webMarketUser)	{	return true;	}
		else	{	return false;	}
	}

	$scope.tryToRegister = function ()
	{
		console.log ('$scope.tryToRegister call.');
		$scope.clearUserFields();		//<	это очистит поля формы авторизации
		$location.path('/registration');
	}

	$scope.clearUserFields = function ()	{	$scope.user = null;	}

	$scope.tryToLogin = function ()
	{
		if ($scope.user != null)
		{
			$http.post(contextProductPath + '/login', $scope.user)
			.then
			(function successCallback (response)
			{
				if (response.data.token)	//< проверка, что в ответе именно токен
				{
					$http.defaults.headers.common.Authorization = 'Bearer ' + response.data.token;
					$localStorage.webMarketUser = {login: $scope.user.login, token: response.data.token};
					$scope.clearUserFields();
				}
			},
			function failureCallback (response)
			{
				console.log ('$scope.tryToLogin failure callback.');
				alert ('ОШИБКА: '+ response.data.messages);
			});
		}
	};

	$scope.tryToLogout = function ()
	{
		$scope.removeUserFromLocalStorage();
		$scope.clearUserFields();
		$location.path('/main');
	};

	$scope.removeUserFromLocalStorage = function ()
	{
		delete $localStorage.webMarketUser;
		$http.defaults.headers.common.Authorization = '';
	};
});