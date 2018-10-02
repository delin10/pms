<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type"  content="text/html;charset=UTF-8">
<link rel="stylesheet" href="plug-ins/easyui/css/default/easyui.css" />
<link rel="stylesheet" href="plug-ins/easyui/css/icon.css" />
<link rel="stylesheet" href="plug-ins/index/css/style.css" />
<title>Insert title here</title>
</head>
<body class="easyui-layout">
	<div id="button-group" style="padding: 5px 0; margin-left: 20px">
		<input id="roles" style="width: 100px; float: left" valueField="id"
			textField="text"> </input> <a id="setRole" href="javascript:void(0);"
			class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
			style="width: 80px; margin-right: 20px; display: none; float: left"
			onclick="setRole()"><span style="line-height: 22px">设置角色</span></a>
	</div>
	<table id="users" class="easyui-datagrid"
		style="width: 100%; height: 800px" toolbar="#tb"
		data-options="url:'datagrid_data.json',fitColumns:true,singleSelect:true">
	</table>
	<div id="tb" style="padding: 3px">
		<span>Item ID:</span> <input id="itemid"
			style="line-height: 26px; border: 1px solid #ccc"> <span>Product
			ID:</span> <input id="productid"
			style	="line-height: 26px; border: 1px solid #ccc"> <a
			href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true"
			onclick="doSearch()">Search</a>
	</div>
	<script src="plug-ins/jquery-3.3.1.js" charset="UTF-8"></script>
	<script type="text/javascript"
		src="plug-ins/easyui/js/jquery.easyui.min.js" charset="UTF-8"></script>
	<script type="text/javascript"
		src="plug-ins/easyui/js/easyui-lang-zh_CN.js" charset="UTF-8"></script>
	<script type="text/javascript"
		src="plug-ins/index/js/easyui-extends.js"></script>
	<script type="text/javascript">
		function doSearch() {
			$('#users').datagrid('load', {
				itemid : $('#itemid').val(),
				productid : $('#productid').val()
			});
		}

		function setRole() {
			var row = $('#users').datagrid('getSelected');
			var selected_role = $('#roles').combobox('getValue');
			if (row && selected_role) {
				$.ajax({
					url : "setting?action=setRole&user_id=" + row.userid
							+ "&role_id=" + selected_role
							+ "&resource_id=setRole",
					type : 'get',
					async : true,
					dataType : 'json',
					success : function(data) {
						$.messager.alert('信息', data.msg);
					}
				});
			}
		}
		$(function() {
			$('#users').datagrid({
				url : 'datagrid_data.json',
				columns : [ [ {
					field : 'userid',
					title : '账号',
					width : 100
				}, {
					field : 'roleid',
					title : '角色',
					width : 100
				}, {
					field : 'role_name',
					title : '角色名字',
					width : 100
				}, {
					field : 'role_description',
					title : '角色描述',
					width : 100
				}, ] ]
			});
			$.ajax({
				url : "setting?action=showUser&resource_id=setRole",
				type : 'get',
				async : true,
				dataType : 'json',
				success : function(data) {
					//console.log(data.data);
					$('#users').datagrid({
						data : data.data
					});
				}
			});
			$.ajax({
				url : "init?comp=page_menu&id=user",
				type : 'get',
				async : true,
				dataType : 'json',
				success : function(data) {
					var funcs = data.data;
					//console.log(funcs);
					for (var i = 0; i < funcs.length; ++i) {
						var node = $("#" + funcs[i].id);
						//console.log(node);
						if (node) {
							node.css("display", "block");
						}
					}
				}
			});
			$.ajax({
				url : "setting?action=roles&resource_id=setRole",
				dataType : "json",
				type : "GET",
				data : {
					"type" : "audit_state"
				},
				success : function(data) {
					//绑定第一个下拉框
					$('#roles').combobox({
						data : data.data,

						valueField : 'id',
						textField : 'description'
					});
				},
				error : function(error) {
					alert("初始化下拉控件失败");
				}
			});
		})
	</script>
</body>
</html>