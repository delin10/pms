jQuery.extend({
	nameget : function(input) {
		return input[0].attributes['textboxname'].value;
	}
});

(function(JQuery) {
	JQuery.window = function(selector, title, width, height) {
		if (!width)
			width = "350px";
		if (!height)
			height = "240px";
		JQuery(selector).window({
			title : title,
			width : width,
			height : height,
			minimizable : false,
			maximizable : false,
			collapsible : false,
			resizable : false,
			modal : true,
			onClose : function() {
				//$(selector + " form input").val(null);
			}
		});
	};
})($);

$.extend($.fn.validatebox.defaults.rules, {
	equals : {
		validator : function(value, param) {
			return value == $(param[0]).val();
		},
		message : "两次密码输入不一致"
	}
});
