
angular.module('market-front').controller('edit_productController',
	function ($scope, $http, $routeParams, $location)
{
/*	$routeParams - позволяет при маршрутизации передавать парметры в адресной строке (маршрутизация описывается в index10.js. >> function config)

	$location - позволяет переходить на др.страницу.
*/
	const contextProductPath = 'http://localhost:12440/market/api/v1/products';
	const contextAuthoPath = 'http://localhost:12440/market/api/v1/auth';

	var contextPrompt_Creation = "Создание нового товара.";
	var contextPrompt_Editing = "Изменение существующего товара.";
	var contextPrompt_AccessDenied = "У вас нет разрешения для редактирования товаров.";
	$scope.contextPrompt = "";

	$scope.prepareEditProductPage = function ()
	{
	/* имя параметра (pid) должно совпадать с именем элемента в index10.js. >> function config >> ….when('/edit_product/:pid'…)	*/
		if ($routeParams.pid == null)
		{
			$scope.contextPrompt = contextPrompt_Creation;
		}
		else
		{
			$scope.contextPrompt = contextPrompt_Editing;

			$http.get (contextProductPath + '/' + $routeParams.pid)
			.then (
			function successCallback (response)
			{
				$scope.new_product = response.data;
				console.log (response.data);
			},
			function failureCallback (response)
			{
				alert ('Не удалось получить информацию о продукте.\r'+ response.data.messages);	//< название параметра взято из ErrorMessage
			});
		}
	}
//----------------------------------------------------------------------- редактирование
	$scope.createOrUpdateProduct = function ()
	{
		if ($scope.new_product != null)
		{
			if ($scope.new_product.productId == null)
			{
				$scope.createNewProduct ($scope.new_product);
			}
			else
			{
				$scope.updateProduct ($scope.new_product);
			}
		}
	}

	$scope.createNewProduct = function (p)
	{
		$http.post (contextProductPath, p)
		.then (
		function successCallback (response)
		{
			$scope.contextPrompt = 'Товар успешно создан.';
			$scope.new_product = response.data;	//< показываем хар-ки товара, полученные от бэкэнда (включая id)
			// остаёмся на странице, чтобы дать возможность юзеру внести правки
		},
		function failureCallback (response)
		{
			$scope.contextPrompt = 'Не удалось создать товар!';
			alert (response.data.messages);	/* Имя параметра должно совпадать с именем поля в
			передаваемом объекте, коим в данном случае выступает
			ru.gb.antonov.j67.beans.errorhandlers.ErrorMessage.	*/
		});
	}

	$scope.updateProduct = function (p)
	{
		$http.put (contextProductPath, p)
		.then(
		function successCallback (response)
		{
			$scope.contextPrompt = 'Товар успешно изменён.';
			$scope.new_product = response.data;	//< показываем харак-ки товара, полученные от бэкэнда
			// остаёмся на странице, чтобы дать возможность юзеру внести правки
		},
		function failureCallback (response)
		{
			$scope.contextPrompt = 'Не удалось изменить товар!';
			alert (response.data.messages);	//< название параметра взято из ErrorMessage
		});
	}

	$scope.cancelProductEditing = function ()
	{
		$scope.new_product = null;
		$location.path('/store');
	}
//----------------------------------------------------------------------- разрешения
//----------------------------------------------------------------------- вызовы
	$scope.prepareEditProductPage();	//< вызов описанной выше функции
});
