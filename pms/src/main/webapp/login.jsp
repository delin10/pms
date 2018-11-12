<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
<link rel="stylesheet" href="plug-ins/easyui/css/default/easyui.css" />
<link rel="stylesheet" href="plug-ins/easyui/css/icon.css" />
<link rel="stylesheet" href="plug-ins/index/css/style.css" />
</head>
<body>
	<input id="contextPath" type="hidden"
		value="${pageContext.request.contextPath}" />
	<div id="loginWindow">
		<form method="post">
			<table style="width: 80%; margin: 10px auto;">
				<tr>
					<td>用户名：</td>
					<td><input name="username" type="text" class="easyui-textbox"
						required="required" /></td>
				</tr>
				<tr>
					<td>密码：</td>
					<td><input name="password" type="password"
						class="easyui-textbox" required="required" /></td>
				</tr>
				<tr>
					<td></td>
					<td><a href="javascript:void(0)" onclick="submit();"
						class="easyui-linkbutton">登录</a></td>
				</tr>
			</table>
		</form>
	</div>
	<script src="plug-ins/jquery-3.3.1.js"></script>
	<script type="text/javascript"
		src="plug-ins/easyui/js/jquery.easyui.min.js"></script>
	<script type="text/javascript"
		src="plug-ins/easyui/js/easyui-lang-zh_CN.js"></script>
	<script type="text/javascript"
		src="plug-ins/index/js/easyui-extends.js"></script>
	<script type="text/javascript">
		$(function() {
			$.window("#loginWindow", "登录");
			$("#loginWindow").window({
				closable : false,
				draggable : false,
				resizable : false,
				height : "160px"
			});
		});

		function submit() {
			//$.messager.progress();
			var form = {
				"id" : $("form input[name='username']").val(),
				"pwd" : $("form input[name='password']").val()
			};
			$.ajax({
				url : "login",
				type : 'post',
				async : true,
				onSubmit : function() {
					var isvalid = $(this).form("validate");
					if (!isvalid) {
						$.messager.progress("close");
					}
					return isvalid;
				},
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(form),
				success : function(data) {
					//$.messager.progress("close");
					console.log(data);
					if (data.suc == 0) {
						window.location.href = "index.jsp";
					} else {
						$.messager.show({
							title : "提示信息",
							msg : "用户名或密码错误"
						})
					}
				}
			});
		}
	</script>
</body>
</html>