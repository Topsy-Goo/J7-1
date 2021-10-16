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
			.when('/main',		//< задаём постфикс для перехода на главную страницу
			{
				templateUrl: 'main/main.html',		//< адрес главной страницы и…
				controller:	 'mainController'		//	…имя её контроллера
			})
			.when('/store',	//< задаём адрес страницы с товарами
			{
				templateUrl: 'store/store.html',	//<	адрес страницы с товарами и…
				controller:	 'storeController'		//	…имя её контроллера
			})
			.when('/product_page/:pid',
			{
				templateUrl: 'product_page/product_page.html',
				controller:	 'product_pageController'
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
			.when('/order',
			{
				templateUrl: 'order/order.html',
				controller:	 'orderController'
			})
			.when('/registration',
			{
				templateUrl: 'registration/registration.html',
				controller:	 'registrationController'
			})
			.when('/user_profile',
			{
				templateUrl: 'user_profile/user_profile.html',
				controller:	 'user_profileController'
			})
			.otherwise(
			{
				redirectTo:	'/main'
			});
	}

	function run ($rootScope, $http, $localStorage)
	{
	/*	При запуске приложения во фронте неразлогиненный юзер будет считан (из локального хранилища
	браузера) и в соотв-ии с ним будет добавлен и настроен умолчальный заголовок Authorization, как
	при авторизации и регистрации.
	(В нашем учебном проекте это не заработает, т.к. при старте
	приложения, бэк считывает БД из sql-файла, а при регистрации нового юзера он не записывается в
	упомянутый файл).
	*/
		const contextAuthoPath	= 'http://localhost:12440/market/api/v1/auth';
		const contextCartPath	= 'http://localhost:12440/market/api/v1/cart';

        if ($localStorage.webMarketUser)
        {
            $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.webMarketUser.token;
        }

		if (!$localStorage.gbj7MarketGuestCartId)
		{
			$http.get(contextCartPath + '/uuid')
			.then(
			function successCallback(response)
			{
				$localStorage.gbj7MarketGuestCartId = response.data.value;
				console.log ('Temporary cartID is generated:'+ response.data.value);
//				console.log ();
			});
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
	const contextAuthoPath = 'http://localhost:12440/market/api/v1/auth';
	const contextCartPath  = 'http://localhost:12440/market/api/v1/cart';

	$scope.appTitle = 'Marketplace';
	$scope.mainPageTitle = 'Главная страница';
	$scope.storePageTitle = 'Каталог продуктов';
	$scope.edit_productPageTitle = 'Создать продукт';
	$scope.cartPageTitle = 'Ваша корзина';
	$rootScope.canEditProducts = false;

	$scope.tryToRegister = function ()
	{
		console.log ('$scope.tryToRegister call.');
		$scope.clearUserFields();		//<	это очистит поля формы авторизации
		$location.path('/registration');
	}

	$scope.clearUserFields = function () { $scope.user = null; }

	$scope.tryToLogin = function ()
	{
		if ($scope.user != null)
		{
			$http.post (contextAuthoPath + '/login',
						$scope.user)
			.then(
			function successCallback (response)
			{
				if (response.data.token)	//< проверка, что в ответе именно токен
				{
					$http.defaults.headers.common.Authorization = 'Bearer ' + response.data.token;
					$localStorage.webMarketUser = {login: $scope.user.login, token: response.data.token};
					$scope.clearUserFields();
				}
				$scope.tryMergeCarts();

				$rootScope.canEditProducts = $scope.canUserEditProducts();
			},
			function failureCallback (response)
			{
				alert ('ОШИБКА: '+ response.data.messages);
			});
		}
	}

	$scope.tryToLogout = function ()
	{
		$rootScope.canEditProducts = false;
		$scope.removeUserFromLocalStorage();
		$scope.clearUserFields();
		$location.path('/store');
	}

	$scope.removeUserFromLocalStorage = function ()
	{
		delete $localStorage.webMarketUser;
		$http.defaults.headers.common.Authorization = '';
	}

	$scope.tryMergeCarts = function ()
	{
		if ($localStorage.gbj7MarketGuestCartId)
		{
			$http.get (contextCartPath + '/merge/' + $localStorage.gbj7MarketGuestCartId)
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
	}

//----------------------------------------------------------------------- разрешения
	$rootScope.isUserLoggedIn = function ()
	{
		if ($localStorage.webMarketUser)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
});
