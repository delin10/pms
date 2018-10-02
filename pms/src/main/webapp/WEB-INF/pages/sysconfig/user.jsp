<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/pages/common.jspf"%>
<c:set var="postPath" value="${contextPath }/sysconfig/user"></c:set>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
	<table id="user_datagrid"></table>
	<div id="user_editWindow" style="display: none;">
		<form method="post">
			<table style="width: 80%; margin: 10px auto;">
				<tr>
					<td>用户名:</td>
					<td>
						<input id="user_username" name="username" type="text" class="easyui-textbox" data-options="required:true,width:'170px'" />
					</td>
				</tr>
				<tr>
					<td>真实姓名:</td>
					<td>
						<input id="user_realName" name="realName" type="text" class="easyui-textbox" data-options="required:true,width:'170px'" />
					</td>
				</tr>
				<tr>
					<td>邮箱:</td>
					<td>
						<input id="user_email" name="email" type="text" class="easyui-textbox" data-options="required:true,validType:'email',width:'170px'" />
					</td>
				</tr>
				<tr>
					<td>密码:</td>
					<td>
						<input id="user_password" name="password" type="password" class="easyui-validatebox" data-options="required:true,width:'170px'" />
					</td>
				</tr>
				<tr>
					<td>确认密码:</td>
					<td>
						<input name="passwordAgain" type="password" class="easyui-validatebox" data-options="required:true,width:'170px'" validType="equals['#user_password']" />
					</td>
				</tr>
				<tr>
					<td>部门:</td>
					<td>
						<select id="user_department" name="departId" class="easyui-combobox" data-options="width:'170px'"></select>
					</td>
				</tr>
				<tr>
					<td>角色:</td>
					<td>
						<input id="user_role" name="role" type="text" class="easyui-combo" data-options="width:'170px'" />
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<a href="javascript:void(0);" class="easyui-linkbutton" onclick="user_submit();">提交</a>
						<a href="javascript:void(0);" class="easyui-linkbutton" onclick="$('#user_editWindow').window('close');">取消</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<script type="text/javascript">
		var user_datagrid;
		var user_editId = -1;

		$(function() {
			user_datagrid = $("#user_datagrid");

			$.post("${contextPath}/sysconfig/department", {
				method : "get"
			}, function(data) {
				var json = eval("(" + data + ")");
				if (json.status) {
					$.messager.show("数据记载出现错误，请刷新重试");
					return;
				}
				json[0].selected = true;
				$("#user_department").combobox({
					data : json,
					editable : false,
					valueField : "id",
					textField : "departName"
				});
			});

			$.post("${contextPath}/sysconfig/role", {
				method : "get"
			}, function(data) {
				var json = eval("(" + data + ")");
				if (json.status) {
					$.messager.show("数据记载出现错误，请刷新重试");
					return;
				}
				json[0].selected = true;
				$("#user_role").combobox({
					data : json,
					editable : false,
					valueField : "id",
					textField : "roleName",
					multiple : true
				});
			});

			user_datagrid.datagrid({
				idField : "id",
				pagination : true,
				striped : true,
				rownumbers : true,
				singleSelect : true,
				height : "100%",
				columns : [ [ {
					title : "用户名",
					field : "username",
					width : "10%"
				}, {
					title : "邮箱",
					field : "email",
					width : "10%"
				}, {
					title : "真实姓名",
					field : "realName",
					width : "10%"
				}, {
					title : "部门",
					field : "departId",
					width : "10%"
				}, {
					title : "角色",
					field : "role",
					width : "10%",
					formatter : function(value, rows, index) {
						var result = "";
						for ( var index in value) {
							result += value[index] + ",";
						}
						result = result.substring(0, result.length - 1);
						return result;
					}
				} ] ],
				toolbar : [ {
					iconCls : "icon-add",
					text : "新增",
					handler : function() {
						$.window("#user_editWindow", "新增用户", "320px", "330px");
						user_editId = -1;
					}
				}, {
					iconCls : "icon-edit",
					text : "编辑",
					handler : function() {
						var selrow = user_datagrid.datagrid("getSelected");
						if (selrow) {
							user_editId = selrow.id;
							$("#user_username").textbox("setValue",selrow.username);
							$("#user_realName").textbox("setValue",selrow.realName);
							$("#user_email").textbox("setValue",selrow.email);
							$.window("#user_editWindow", "编辑用户", "320px", "330px");
						}

					}
				} ]
			});
			reloadUserdatagrid();
		});

		function user_submit() {
			$.messager.progress();
			var method = "add";
			if (user_editId != -1) {
				method = "update";
			}
			$("#user_editWindow form").form("submit", {
				url : "${postPath}",
				queryParams : {
					method : method,
					id : user_editId
				},
				onSubmit : function() {
					var isValid = $(this).form("validate");
					if (!isValid) {
						$.messager.progress("close");
					}
					return isValid;
				},
				success : function(data) {
					$.messager.progress("close");
					var json = eval("(" + data + ")");
					if (json.status == "success") {
						reloadUserdatagrid();
					}
					$.messager.show({
						title : "操作成功",
						msg : json.message
					});
				}
			});
		}

		function reloadUserdatagrid() {
			user_datagrid.datagrid("options").url = "${postPath}";
			user_datagrid.datagrid("reload", {
				method : "get"
			});
		}
	</script>
</body>
</html>