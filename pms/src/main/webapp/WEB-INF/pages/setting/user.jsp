<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<link rel="stylesheet" href="plug-ins/easyui/css/default/easyui.css" />
<link rel="stylesheet" href="plug-ins/easyui/css/icon.css" />
<link rel="stylesheet" href="plug-ins/index/css/style.css" />
<title>Insert title here</title>
</head>
<body class="easyui-layout">
	<div id="button-group" style="padding: 5px 0; margin-left: 20px">
		<input id="roles" style="width: 100px; float: left" valueField="id"
			textField="text"/>  <a id="setRole" href="javascript:void(0);"
			class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
			style="width: 80px; margin-right: 20px; display: none; float: left"
			onclick="setRole()"><span style="line-height: 22px">设置角色</span></a>
	</div>
	<table id="users" class="easyui-datagrid"
		style="width: 100%; height: 800px" toolbar="#tb"
		data-options="fitColumns:true,singleSelect:true">
	</table>
	<div id="tb" style="padding: 3px">
		<span>搜索项目:</span><input class="easyui-combobox" id="search-keys"
			name="key" /><span>关键字:</span> <input id="key-word"
			style="line-height: 26px; border: 1px solid #ccc"> <a
			href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true"
			onclick="doSearch()">搜索</a><a href="#" class="easyui-linkbutton"
			iconCls="icon-reload" plain="true"
			onclick="refresh('setting?action=showUser&resource_id=showUser')"
			style="display: block; float: right; margin-right: 20px;">刷新</a>
	</div>
	<script src="plug-ins/jquery-3.3.1.js" charset="utf-8"></script>
	<script type="text/javascript"
		src="plug-ins/easyui/js/jquery.easyui.min.js" charset="utf-8"></script>
	<script type="text/javascript"
		src="plug-ins/easyui/js/easyui-lang-zh_CN.js" charset="utf-8"></script>
	<script type="text/javascript"
		src="plug-ins/index/js/easyui-extends.js"></script>
	<script type="text/javascript">
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
		function doSearch() {
			var key = $('#search-keys').combobox('getValue');
			var word = $('#key-word').val();
			if (key && word) {
				refresh("setting?action=search&subject=users&key=" + key
						+ "&word=" + word + "&resource_id=search");
			}
		}
		
		function refresh(url) {
			$('#users').datagrid({
				url : url,
				type : 'get',
				loadFilter : function(data) {
					return data.data;
				}
			});
		}
		$(function() {
			$('#users').datagrid({
				url : 'setting?action=showUser&resource_id=showUser',
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
				}, ] ],
				loadFilter : function(data) {
					return data.data;
				}
			});
			$('#search-keys').combobox({
				valueField : 'field',
				textField : 'title',
				panelHeight : 'auto',
				data : $('#users').datagrid('options').columns[0]
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
						panelHeight : 'auto',
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