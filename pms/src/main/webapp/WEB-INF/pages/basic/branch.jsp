<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/pages/common.jspf"%>
<
<c:set var="postPath" value="${contextPath }/basic/branch"></c:set>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
	<table id="branch_datagrid" title="分公司列表">

	</table>

	<div id="branch_datagridToolbar" style="padding: 2px 5px;">
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="branch_add();">添加</a>
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="branch_edit();">编辑</a>

	</div>
	<script type="text/javascript">
		var branch_datagrid;
		var branch_editIndex = -1;

		$(function() {
			branch_datagrid = $("#branch_datagrid");
			branch_datagrid.datagrid({
				idField : "id",
				pagination : true,
				striped : true,
				rownumbers : true,
				singleSelect : true,
				height : "100%",
				onAfterEdit : function(rowIndex, rowData, changes) {
					if (rowData) {
						var postUrl = "${postPath}?method=";
						if (rowData.id) {
							postUrl += "update";
						} else {
							postUrl += "add";
						}

						$.post(postUrl, rowData, function(data) {
							var json = eval("(" + data + ")");
							if (json.status != "success") {
								branch_datagrid.datagrid("rejectChanges");
							} else {
								branch_datagrid.datagrid("acceptChanges");
							}
							$.messager.show({
								title : "提示消息",
								msg : json.message
							});
							branch_endEdit();
						});
					}
				},
				columns : [ [ {
					title : "分公司地址",
					field : "branchAddr",
					width : "30%",
					editor : {
						type : "validatebox",
						options : {
							required : true,
							validType : {
								length : [ 2, 30 ]
							}

						}
					}
				}, {
					title : "分公司电话",
					field : "branchTel",
					width : "15%",
					editor : {
						type : "numberbox",
						options : {
							required : true,
							validType : {
								length : [ 2, 30 ]
							}
						}
					}
				}, {
					title : "负责人",
					field : "leaderName",
					width : "10%",
					editor : {
						type : "validatebox",
						options : {
							required : true
						}
					}
				}, {
					title : "负责人电话",
					field : "leaderTel",
					width : "10%",
					editor : {
						type : "numberbox"
					}
				}, {
					title : "邮编",
					field : "zipcode",
					width : "10%",
					editor : {
						type : "numberbox"
					}
				}, {
					title : "操作",
					field : "id",
					width : "10%",
					formatter : function(value, row, index) {
						return '<a href="javascript:void(0);" onclick="branch_delete(\'' + value + '\',\'' + index + '\')">{删除}</a>';
					}
				} ] ],
				toolbar : "#branch_datagridToolbar"
			});
			reloadBranchDatagrid();
		});

		function branch_delete(id, index) {
			$.post("${postPath}", {
				method : "delete",
				id : id
			}, function(data) {
				var json = eval("(" + data + ")");
				if (json.status == "success") {
					branch_datagrid.datagrid("deleteRow", index);
				}
				$.messager.show({
					title : "提示信息",
					msg : json.message
				});
			});
		}

		function branch_add() {
			branch_datagrid.datagrid("appendRow", {});
			var rowIndex = branch_datagrid.datagrid("getRows").length - 1;
			branch_datagrid.datagrid("selectRow", rowIndex)
			branch_datagrid.datagrid("beginEdit", rowIndex);
			branch_editIndex = rowIndex;
			branch_beginEdit();
		}

		function branch_edit() {
			var selrow = branch_datagrid.datagrid("getSelected");
			if (selrow) {
				if (branch_editIndex != -1) {
					branch_endEdit();
				}
				var rowIndex = branch_datagrid.datagrid("getRowIndex", selrow);
				branch_datagrid.datagrid("beginEdit", rowIndex);
				branch_beginEdit();
				branch_editIndex = rowIndex;

			}
		}

		function branch_endEdit() {
			//goods_datagrid.datagrid("cancelEdit", goods_editingIndex);

			branch_datagrid.datagrid("rejectChanges", branch_editIndex);
			branch_editIndex = -1;
			//branch_datagrid.datagrid("acceptChanges");
			var pager = branch_datagrid.datagrid("getPager");
			pager.pagination({
				buttons : null
			});
		}

		function branch_beginEdit() {
			if (branch_editIndex != -1) {
				branch_endEdit();
			}
			var pager = branch_datagrid.datagrid("getPager");
			pager.pagination({
				buttons : [ {
					iconCls : 'icon-save',
					text : "提交",
					handler : function() {
						branch_datagrid.datagrid("endEdit", branch_editIndex);
					}
				}, {
					iconCls : "icon-cancel",
					text : "取消",
					handler : function() {
						branch_endEdit();
					}
				} ]
			});
		}

		function reloadBranchDatagrid() {
			branch_datagrid.datagrid("options").url = "${postPath}";
			var queryParam = {
				method : "get"
			};
			branch_datagrid.datagrid("load", queryParam);
		}
	</script>
</body>
</html>