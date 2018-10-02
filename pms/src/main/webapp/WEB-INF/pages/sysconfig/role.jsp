<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/pages/common.jspf"%>
<c:set var="postPath" value="${contextPath }/sysconfig/role"></c:set>
<c:set var="functionPostPath" value="${contextPath }/sysconfig/function"></c:set>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
	<div class="easyui-layout" data-options="fit:true">
		<div data-options="region:'center',title:'角色列表',width:'60%',height:'100%'">
			<table id="role_datagrid"></table>
			<div id="roleFooter" style="display: none;">
				<a href="javascript:void(0);" onclick="role_submit();" plain="true" class="easyui-linkbutton" iconcls="icon-add" palin="true">提交</a>
				<a href="javascript:void(0);" onclick="role_cancelEdit();" plain="true" class="easyui-linkbutton" iconcls="icon-undo" palin="true">取消</a>
			</div>
		</div>
		<div class="easyui-layout" data-options="region:'east',title:'权限列表',width:'40%',height:'100%',collapsible:false">
			<div id="role_functionListPanel" data-options="region:'center',title:'权限菜单',width:'60%',height:'100%'">
				<ul id="role_functionList">

				</ul>
			</div>
			<div id="role_functionParamList" data-options="region:'east',title:'按钮参数',width:'40%',height:'100%',collapsible:false"></div>
		</div>
	</div>
	<script type="text/javascript">
		var role_datagrid;
		var role_editIndex = -1;
		var role_currentId = null;
		var role_currentRoleParams = [];

		$(function() {
			role_datagrid = $("#role_datagrid");
			role_datagrid.datagrid({
				idField : "id",
				striped : true,
				rownumbers : true,
				singleSelect : true,
				height : "100%",
				columns : [ [ {
					title : "角色名称",
					field : "roleName",
					width : "40%",
					editor : {
						type : "validatebox",
						validType : {
							required : true
						}
					}
				}, {
					title : "角色代码",
					field : "roleCode",
					width : "40%",
					editor : {
						type : "validatebox",
						validType : {
							required : true
						}
					}
				}, {
					title : "操作",
					field : "id",
					width : "20%",
					formatter : function(value, rows, index) {
						if (value) {
							var deleteLink = '<a href="javascript:void(0)" onclick="role_delete(\'' + value + '\')">{删除}</a>';
							var roleParamsLink = '<a href="javascript:void(0)" onclick="role_editParams(\'' + value + '\')">{权限编辑}</a>';
							return deleteLink + roleParamsLink;
						}
					}
				} ] ],
				toolbar : [ {
					text : "新增角色",
					iconCls : "icon-add",
					handler : function() {
						role_datagrid.datagrid("appendRow", {});
						var index = role_datagrid.datagrid("getRows").length - 1;
						role_beginEdit(index);
					}
				}, {
					text : "编辑角色",
					iconCls : "icon-edit",
					handler : function() {
						var selrow = role_datagrid.datagrid("getSelected");
						if (selrow) {
							var index = role_datagrid.datagrid("getRowIndex", selrow);
							role_beginEdit(index);
						}
					}
				} ],
				footer : "#roleFooter",
				onAfterEdit : function(rowIndex, rowData, changes) {
					$.messager.progress();
					var method = "add";
					if (rowData.id) {
						method = "update";
					}
					rowData.method = method;
					$.post("${postPath}", rowData, function(data) {
						$.messager.progress("close");
						var json = eval("(" + data + ")");
						if (json.status == "success") {
							reloadRoleDataGrid();
						} else {
							role_cancelEdit();
						}
						$.messager.show({
							title : "提示信息",
							msg : json.message
						});
					});
				}
			});
			reloadRoleDataGrid();

			$("#role_functionListPanel").panel({
				tools : [ {
					iconCls : "icon-save",
					handler : function() {
						if (role_currentId) {
							var nodes = $("#role_functionList").tree("getChecked");
							var functionId = "";
							for ( var index in nodes) {
								if (nodes[index].functionLevel == "1") {
									functionId += nodes[index].id + ",";
								}
							}
							$.post("${contextPath}/sysconfig/role", {
								method : "addrolefun",
								role : role_currentId,
								functionId : functionId
							}, function(data) {
								$("#role_functionParamList").empty();
								var json = eval("(" + data + ")");
								if (json.status != "success") {
									role_editParams(role_currentId);
								}

								$.messager.show({
									title : "提示信息",
									msg : json.message
								});
							});

						}
					}
				} ]
			});

			$("#role_functionParamList").panel({
				tools : [ {
					iconCls : "icon-save",
					handler : function() {
						var operation = "";
						for ( var index in role_currentRoleParams) {
							var value = $(role_currentRoleParams[index]).switchbutton("options");
							if (value.checked == true) {
								operation += value.value + ",";
							}
						}
						operation = operation.substring(0, operation.length - 1);
						var node = $("#role_functionList").tree("getSelected");
						var functionId = node.id;
						$.post("${contextPath}/sysconfig/role", {
							method : "addoper",
							functionId : functionId,
							role : role_currentId,
							oper : operation
						}, function(data) {
							var json = eval("(" + data + ")");
							$.messager.show({
								title : "提示信息",
								msg : json.message
							});
						});
					}
				} ]
			});
		});

		function role_cancelEdit() {
			if (role_editIndex != -1) {
				role_datagrid.datagrid("rejectChanges", role_editIndex);
				$("#roleFooter").hide();
				role_editIndex = -1;
			}
		}

		function role_beginEdit(index) {
			if (role_editIndex != -1) {
				role_cancelEdit();
			}
			role_datagrid.datagrid("beginEdit", index);
			role_editIndex = index;
			$("#roleFooter").show();
		}

		function role_submit() {
			role_datagrid.datagrid("endEdit", role_editIndex);
		}

		function role_delete(id) {
			$.post("${postPath}", {
				method : "delete",
				id : id
			}, function(data) {
				var json = eval("(" + data + ")");
				if (json.status == "success") {
					reloadRoleDataGrid();
				}
				$.messager.show({
					title : "提示信息",
					msg : json.message
				});
			});
		}

		function role_editParams(id) {
			role_currentId = id;
			$("#role_functionParamList").empty();
			role_currentRoleParams = [];
			$("#role_functionList").tree(
					{
						url : "${functionPostPath}",
						checkbox : true,
						lines : true,
						formatter : function(node) {
							return node.functionName;
						},
						queryParams : {
							method : "get",
							roleId : id
						},
						onSelect : function(node) {
							if (node.functionLevel == "0")
								return;
							$("#role_functionList").tree("check", node.target);
							$.post("${contextPath}/sysconfig/role", {
								method : "getrolefun",
								functionId : node.id,
								roleId : role_currentId
							}, function(data) {
								$("#role_functionParamList").empty();
								role_currentRoleParams = [];
								if (!data)
									return;
								var json = eval("(" + data + ")");
								if (json.status) {
									$.messager.show({
										title : "提示信息",
										msg : json.message
									});
								}
								for ( var key in json) {
									var param = $("<input value='" + json[key].paramCode
											+ "' class='easyui-switchbutton' data-options=\"onText:'允许',width:'100%',handleWidth:'40px',offText:'禁止',handleText:'" + json[key].paramName + "'\" />");
									if (json[key].checked == true) {
										param.attr("checked", true);
									}
									role_currentRoleParams[key] = param;
									param.appendTo("#role_functionParamList");
									$.parser.parse(param.parent());

								}
							});
						}
					});
		}

		function reloadRoleDataGrid() {
			role_datagrid.datagrid("options").url = "${postPath}";
			role_datagrid.datagrid("reload", {
				method : "get"
			});
		}
	</script>
</body>
</html>