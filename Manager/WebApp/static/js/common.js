$(function(){

	//底部始终固定在底部方法
	function footerPosition(){
		$(".footer").removeClass("fixed-bottom");
		var contentHeight = document.body.scrollHeight,//网页正文全文高度
		winHeight = window.innerHeight;//可视窗口高度，不包括浏览器顶部工具栏
		if ( typeof winHeight !="number"){
			if ( document.compatMode== "CSSICompat"){
				winHeight=document.documentElement.clientHeight;
			} else{
				winHeight=document.body.clientHeight;
			}
		}
		//console.log(contentHeight+" "+winHeight)
		if(!(contentHeight > winHeight)){
			//当网页正文高度小于可视窗口高度时，为footer添加类fixed-bottom
			$(".footer").addClass("fixed-bottom");
		} else {
			$(".footer").removeClass("fixed-bottom");
		}
	}
	footerPosition();
	$(window).resize(footerPosition);

	$(window).on("event.footerPosition", function () {
		footerPosition();
	});

	//下拉菜单
	/*var _timeidnav = null;
	$('.nav li').hover(function() {
		_this = $(this);
		_timeidnav = setTimeout(function(){
				_this.find('.subnav').slideDown(200);
			}, 200)
	},function() {
		$(this).find('.subnav').slideUp(100);
		if (_timeidnav) clearTimeout(_timeidnav);
	});*/
});




