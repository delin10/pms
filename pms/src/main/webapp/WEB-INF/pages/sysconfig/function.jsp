<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/pages/common.jspf"%>
<c:set var="postPath" value="${contextPath }/sysconfig/function"></c:set>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
	<div class="easyui-layout" data-options="fit:true">
		<div data-options="region:'center',title:'菜单列表',width:'80%',height:'100%'">
			<table id="function_datagrid"></table>
			<div id="function_addWindow" style="display: none;">
				<form id="function_addForm" method="post">
					<table style="width: 80%; margin: 10px auto;">
						<tr>
							<td>菜单名称:</td>
							<td>
								<input id="function_name" name="functionName" type="text" class="easyui-textbox"
									data-options="required:true,width:'166px'" />
							</td>
						</tr>
						<tr>
							<td>菜单URL:</td>
							<td>
								<input id="function_url" name="functionUrl" type="text" class="easyui-textbox"
									data-options="required:true,width:'166px'" />
							</td>
						</tr>
						<tr>
							<td>菜单级别:</td>
							<td>
								<select id="function_level" name="functionLevel" data-options="editable:false"
									class="easyui-combobox">
									<option value="0">父级菜单</option>
									<option value="1">子级菜单</option>
								</select>
							</td>
						</tr>
						<tr style="display: none;">
							<td>父级菜单:</td>
							<td>
								<select id="function_parent" name="parentFunction"
									data-options="editable:false,valueField:'id',textField:'functionName'"
									class="easyui-combobox">
								</select>
							</td>
						</tr>
						<tr>
							<td>排序编号:</td>
							<td>
								<input id="function_order" name="functionOrder" type="text" class="easyui-numberbox"
									data-options="required:true,width:'166px'" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td>
								<a href="javascript:void(0)" class="easyui-linkbutton" onclick="function_submit();">提交</a>
								<a href="javascript:void(0)" class="easyui-linkbutton"
									onclick="$('#function_addWindow').window('close');">取消</a>
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
		<div data-options="region:'east',title:'按钮参数',width:'20%',height:'100%',split:true">
			<table id="function_paramDatagrid"></table>
			<div id="params_footer" style="display: none;">
				<a href="javascript:void(0)" onclick="submitParam();" class="easyui-linkbutton"
					iconCls="icon-add" plain="true">提交</a>
				<a href="javascript:void(0)" onclick="canelParamEdit();" class="easyui-linkbutton"
					iconCls="icon-undo" plain="true">取消</a>
			</div>
		</div>
	</div>


	<script type="text/javascript">
		var function_datagrid;
		var params_datagrid;
		var function_editId = "";
		var params_functionId;
		var params_editIndex = -1;

		$(function() {
			$("#function_level").combobox({
				onChange : function(record) {
					$("#function_parent").parent().parent().toggle();
					if (record == 1) {
						$.post("${postPath}?method=getparent", function(data) {
							var json = eval("(" + data + ")");
							if (json.length > 0) {
								json[0].selected = true;
							}
							$("#function_parent").combobox({
								data : json
							});
						});
					}
				}
			});
			function_datagrid = $("#function_datagrid");
			function_datagrid
					.treegrid({
						idField : "id",
						treeField : "functionName",
						striped : true,
						rownumbers : true,
						height : "100%",
						animate : true,
						toolbar : [
								{
									text : "新增",
									iconCls : "icon-add",
									handler : function() {
										function_editId = "";
										$.window("#function_addWindow", "添加菜单",
												"320px", "280px");
									}
								},
								{
									text : "修改",
									iconCls : "icon-edit",
									handler : function() {
										var selRow = function_datagrid
												.treegrid("getSelected");
										function_editId = "";
										if (selRow) {
											function_editId = selRow.id;
											$("#function_name").textbox(
													"setValue",
													selRow.functionName);
											$("#function_url").textbox(
													"setValue",
													selRow.functionUrl);
											$("#function_level").combobox(
													"setValue",
													selRow.functionLevel);
											$("#function_parent").combobox(
													"setValue",
													selRow.parentFunction);
											$("#function_order").textbox(
													"setValue",
													selRow.functionOrder);
											$.window("#function_addWindow",
													"修改菜单", "320px", "280px");
										}

									}
								} ],
						columns : [ [
								{
									title : "菜单名称",
									field : "functionName",
									width : "15%"
								},
								{
									title : "菜单路径",
									field : "functionUrl",
									width : "30%"
								},
								{
									title : "菜单顺序",
									field : "functionOrder",
									width : "30%"
								},
								{
									title : "操作",
									field : "id",
									width : "15%",
									formatter : function(value, row, index) {
										var deleteLink = '<a href="javascript:void(0)" onclick="function_delete(\''
												+ value + '\')">{删除}</a>';
										var paramLink = '<a href="javascript:void(0)" onclick="function_param(\''
												+ value + '\')">{按钮参数}</a>';
										return deleteLink + paramLink;
									}
								} ] ]
					});
			reloadFunctionDataGrid();

			params_datagrid = $("#function_paramDatagrid");
			params_datagrid.datagrid({
				idField : "id",
				striped : true,
				rownumbers : true,
				singleSelect : true,
				height : "100%",
				columns : [ [ {
					title : "参数名",
					field : "paramName",
					width : "50%",
					editor : {
						type : "validatebox",
						validType : {
							required : true
						}
					}
				}, {
					title : "参数代码",
					field : "paramCode",
					width : "50%",
					editor : {
						type : "validatebox",
						validType : {
							required : true
						}
					}
				} ] ],
				toolbar : [
						{
							text : "新增",
							iconCls : "icon-add",
							handler : function() {
								if (params_functionId) {
									if (params_editIndex != -1) {
										canelParamEdit();
									}
									params_datagrid.datagrid("appendRow", {});
									var index = params_datagrid
											.datagrid("getRows").length - 1;
									beginParamEdit(index);
								}
							}
						},
						{
							text : "修改",
							iconCls : "icon-edit",
							handler : function() {
								var selrow = params_datagrid
										.datagrid("getSelected");
								if (selrow) {
									if (params_editIndex != -1) {
										canelParamEdit();
									}
									var index = params_datagrid.datagrid(
											"getRowIndex", selrow);
									beginParamEdit(index);
								}
							}
						},
						{
							text : "删除",
							iconCls : "icon-remove",
							handler : function() {
								var selrow = params_datagrid
										.datagrid("getSelected");
								if (selrow) {
									$.post("${postPath}", {
										method : "deleteparam",
										id : selrow.id
									}, function(data) {
										var json = eval("(" + data + ")");
										if (json.status == "success") {
											function_param(params_functionId);
										}
										$.messager.show({
											title : "提示信息",
											msg : json.message
										});
									});
								}
							}
						} ],
				footer : "#params_footer",
				onAfterEdit : function(rowIndex, rowData, changes) {
					method = "addparam";
					if (rowData.id) {
						method = "updateparam";
					}
					rowData.method = method;
					rowData.functionId = params_functionId;
					$.post("${postPath}", rowData, function(data) {
						params_editIndex = -1;
						var json = eval("(" + data + ")");
						if (json.status == "success") {
							function_param(params_functionId);
						} else {
							params_datagrid.datagrid("rejectChanges");
						}
						$.messager.show({
							title : "提示信息",
							msg : json.message
						});
					});
				}
			});
		});

		function function_submit() {
			var method = "update";
			if (function_editId == null || function_editId == ""
					|| function_editId == undefined) {
				method = "add"
			}
			$.messager.progress();
			$("#function_addForm").form("submit", {
				url : "${postPath}",
				queryParams : {
					method : method,
					id : function_editId
				},
				onSubmit : function() {
					var isValid = $("#function_addForm").form("validate");
					console.log(isValid);
					if (!isValid) {
						$.messager.progress("close");
					}
					return isValid;
				},
				success : function(data) {
					$.messager.progress("close");
					var json = eval("(" + data + ")");
					if (json.status == "success") {
						reloadFunctionDataGrid();
					}
					$.messager.show({
						title : "提示信息",
						msg : json.message
					});
				}
			});
		}

		function function_delete(id) {
			$.post("${postPath}", {
				method : "delete",
				id : id
			}, function(data) {
				var json = eval("(" + data + ")");
				if (json.status == "success") {
					reloadFunctionDataGrid();
				}
				$.messager.show({
					title : "提示消息",
					msg : json.message
				});
			});
		}

		function function_param(id) {
			params_functionId = id;
			params_datagrid.datagrid({
				url : "${postPath}"
			});
			params_datagrid.datagrid("reload", {
				method : "getparam",
				functionId : id
			});
		}

		function reloadFunctionDataGrid() {
			function_datagrid.treegrid("options").url = "${postPath}";
			function_datagrid.treegrid("reload", {
				method : "get"
			});
		}

		function canelParamEdit() {
			params_datagrid.datagrid("rejectChanges", params_editIndex);
			params_editIndex = -1;
			$("#params_footer").hide();
		}

		function submitParam() {
			params_datagrid.datagrid("endEdit", params_editIndex);
		}

		function beginParamEdit(index) {
			params_datagrid.datagrid("beginEdit", index);
			params_editIndex = index;
			$("#params_footer").show();
		}
	</script>
</body>
</html>