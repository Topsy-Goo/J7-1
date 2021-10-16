
angular.module('market-front').controller('product_pageController',
function ($rootScope, $scope, $http, $location, $routeParams)
{
	const contextProductPath = 'http://localhost:12440/market/api/v1/products';
	const contextAuthoPath	 = 'http://localhost:12440/market/api/v1/auth';
	const contextCartPath	 = 'http://localhost:12440/market/api/v1/cart';
	const contextOrderPath   = 'http://localhost:12440/market/api/v1/order';
	$scope.showReviewForm = false;
//	$scope.contextPrompt = 'Свойства товара';
	$scope.reviewHalfHeader = 'Пока нет отзывов';
	var failedToLoadProductDescription = 'Не удалось загрузить свойства товара.';
	var failedToLoadReviews = 'Не удалось загрузить отзывы о товаре.';

	$scope.loadProductProperties = function ()
	{
		$http.get (contextProductPath + '/' + $routeParams.pid)
		.then (
		function successCallback (response)
		{
			$scope.productDto = response.data;
			$scope.contextPrompt = response.data.title;
		},
		function failureCallback (response)
		{
			$scope.contextPrompt = failedToLoadProductDescription;
		});
	}

	$scope.loadReviews = function ()
	{
		$http.get (contextProductPath + '/load_reviews/' + $routeParams.pid)
		.then (
		function successCallback (response)
		{
			$scope.prodReviews = response.data;
			if ($scope.prodReviews.length > 0)
				$scope.reviewHalfHeader = 'Отзывы наших покупателей';
		},
		function failureCallback (response)
		{
			$scope.contextPrompt = failToLoadReviews;
			alert (failToLoadReviews +'\r'+ response.data/*.messages*/);
		});
	}
//----------------------------------------------------------------------- действия
	$scope.appendReview = function ()
	{
		$scope.review.productId = $routeParams.pid;
		$scope.review.authorName = null;
		$scope.review.date = null;
		console.log ($scope.review);

		$http.post (contextProductPath + '/new_review', $scope.review)
		.then (
		function successCallback (response)
		{
			alert ('Спасибо за отзыв. Ваш отзыв опубликован.');
			$scope.canReview();
			$scope.loadReviews();
		},
		function failureCallback (response)
		{
			alert ('К сожалению, ваш отзыв опубликовать не удалось.');
		});
	}
//----------------------------------------------------------------------- условия
	$scope.canReview = function ()
	{
		if (!$rootScope.isUserLoggedIn())
		{
			$scope.showReviewForm = false;
		}
		else
		{
			$http.get (contextProductPath + '/can_review/' + $routeParams.pid)
			.then (
			function successCallback (response)
			{
				$scope.showReviewForm = response.data;
			},
			function failureCallback (response)
			{
				$scope.showReviewForm = false;
			});
		}
	}
//----------------------------------------------------------------------- вызовы
	$scope.loadProductProperties();
	$scope.loadReviews();
	$scope.canReview();
});