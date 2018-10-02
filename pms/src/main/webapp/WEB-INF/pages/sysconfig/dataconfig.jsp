<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@include file="/WEB-INF/pages/common.jspf"%>
<!DOCTYPE>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
	<table id="dataconfig_groupTreegrid" class="easyui-datagrid" style="height: 100%;">
	</table>
	<div id="dataconfig_addGroup" style="display: none; padding: 30px;">
		<form id="dataconfig_addGroupForm" method="post">
			<table>
				<tr>
					<td>
						<label for="groupname">字典名：</label>
					</td>
					<td>
						<input class="easyui-validatebox" data-options="required:true,validType:{length:[2,20]}"
							name="groupname" id="groupname" />
					</td>
				</tr>
				<tr>
					<td>
						<label for="groupcode">字典编码：</label>
					</td>
					<td>
						<input type="text" class="easyui-validatebox"
							data-options="required:true,validType:{length:[1,20]}" name="groupcode" id="groupcode" />
					</td>
				</tr>
				<tr>
					<td>
						<input type="hidden" name="groupoprtype" id="groupoprtype" />
						<input type="hidden" name="groupid" id="groupid" />
					</td>
					<td>
						<a onclick="submit();" class="easyui-linkbutton" data-options="iconCls:'icon-add'">提交</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<script type="text/javascript">
		var dataconfig_groupTreegrid;
		var method;

		function submit() {
			$("#dataconfig_addGroupForm").form("submit", {
				url : "${contextPath }/sysconfig/dataconfig?method=" + method,
				onSubmit : function(param) {
					var isValid = $(this).form("validate");
					if (isValid) {
						$.messager.progress();
					}
					return isValid;
				},
				success : function(data) {
					$.messager.progress("close");
					var json = eval("(" + data + ")");
					$.messager.alert("提示信息", json.message);
					if (json.status == "success") {
						$("#groupname").val("");
						$("#groupcode").val("");
						reloadGroupTypeDataGrid();
					}
				}
			});
		}

		function reloadGroupTypeDataGrid() {
			dataconfig_groupTreegrid.treegrid("reload", {
				method : "get"
			});
		}

		function deleteGroup(id, level) {
			if (level == "1") {
				$.messager.confirm("确定删除?", "这个操作将会删除该分组下的所有字典，你确定要继续？",
						function(flag) {
							if (flag) {
								$.messager.progress();
								$.post("${contextPath}/sysconfig/dataconfig", {
									method : "delete",
									groupoprtype : "group",
									groupid : id
								}, function(data) {
									var json = eval("(" + data + ")");
									$.messager.alert("提示信息", json.message);
									if (json.status == "success") {
										reloadGroupTypeDataGrid();
									}
									$.messager.progress("close");
								});
							}
						});
			} else {
				$.messager.progress();
				$.post("${contextPath}/sysconfig/dataconfig", {
					method : "delete",
					groupoprtype : "type",
					groupid : id
				}, function(data) {
					$.messager.alert("提示信息", data.message);
					if (data.status == "success") {
						reloadGroupTypeDataGrid();
					}
					$.messager.progress("close");
				});
			}
		}

		$(function() {
			dataconfig_groupTreegrid = $("#dataconfig_groupTreegrid");
			dataconfig_groupTreegrid
					.treegrid({
						url : "${contextPath}/sysconfig/dataconfig",
						queryParams : {
							method : "get"
						},
						idField : "groupId",
						treeField : "groupName",
						striped : true,
						rownumbers : true,
						height : "100%",
						columns : [ [
								{
									title : '字典名称',
									field : "groupName",
									width : "40%"
								},
								{
									title : "字典编码",
									field : "groupCode",
									width : "40%"
								},
								{
									title : "操作",
									field : "groupId",
									width : "20%",
									formatter : function(value, row, index) {
										return '<a href="javascript:void(0)" onclick="deleteGroup(\''
												+ value
												+ '\',\''
												+ row.level
												+ '\');">删除</a>';
									}

								}, {
									title : "parent",
									hidden : true,
									field : "parent"
								} ] ],
						toolbar : [
								{
									iconCls : "icon-add",
									text : "新增分组",
									handler : function() {
										method = "add";
										$("#groupoprtype").val("group");
										$.window("#dataconfig_addGroup",
												"新增分组", "350px", "220px");
										$("#dataconfig_addGroup")
												.window("open");
									}
								},
								{
									iconCls : "icon-add",
									text : "新增分组参数",
									handler : function() {
										method = "add";
										$("groupoprtype").val("type");
										var selRow = dataconfig_groupTreegrid
												.treegrid("getSelected");
										if (selRow) {
											var groupId;
											var groupName = selRow.groupName;
											if (selRow._parentId == undefined) {
												groupId = selRow.groupId;
											} else {
												groupId = selRow._parentId;
												groupName = $(
														"tr[node-id="
																+ groupId
																+ "] span.tree-title")
														.text();
											}
											$("#groupid").val(groupId);
											$
													.window(
															"#dataconfig_addGroup",
															"新增分组参数{"
																	+ groupName
																	+ "}",
															"350px");
										} else {
											$.messager.alert("提示信息",
													"请先选择要新增字典参数的分组");
											return;
										}

									}
								},
								{
									iconCls : "icon-edit",
									text : "编辑",
									handler : function() {
										method = "update";
										var selRow = dataconfig_groupTreegrid
												.treegrid("getSelected");
										if (selRow) {
											$("#groupname").val(
													selRow.groupName);
											$("#groupcode").val(
													selRow.groupCode);
											var groupoprtype;
											if (selRow.level == "1") {
												groupoprtype = "group";
											} else {
												groupoprtype = "type"
											}
											$("#groupoprtype")
													.val(groupoprtype);
											$("#groupid").val(selRow.groupId);
											$.window("#dataconfig_addGroup",
													"编辑字典{" + selRow.groupName
															+ "}");
											$("#dataconfig_addGroupForm").form(
													"validate");

										} else {
											$.messager.alert("提示信息",
													"请选择要编辑的字典");
										}
									}
								} ]
					});
		});
	</script>
</body>
</html>