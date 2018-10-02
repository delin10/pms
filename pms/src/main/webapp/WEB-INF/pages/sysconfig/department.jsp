<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/pages/common.jspf"%>
<c:set var="postPath" value="${contextPath }/sysconfig/department"></c:set>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
	<table id="department_datagrid"></table>
	<div id="department_datagridFooter" style="display: none;">
		<a href="javascript:void(0)" class="easyui-linkbutton" plain="true" onclick="department_submit();" iconCls="icon-add">提交</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" plain="true" onclick="department_cancelEdit();" iconCls="icon-undo">取消</a>
	</div>
	<script type="text/javascript">
		var department_datagrid;
		var department_editIndex = -1;

		$(function() {
			department_datagrid = $("#department_datagrid");
			department_datagrid.datagrid({
				idField : "id",
				striped : true,
				rownumbers : true,
				singleSelect : true,
				height : "100%",
				footer : "#department_datagridFooter",
				columns : [ [ {
					title : "部门名称",
					field : "departName",
					width : "40%",
					editor : {
						type : "validatebox",
						validType : {
							required : true
						}
					}
				}, {
					title : "部门描述",
					field : "departDescription",
					width : "40%",
					editor : {
						type : "textarea"
					}
				}, {
					title : "操作",
					field : "id",
					width : "20%",
					formatter : function(value, rows, index) {
						if (value) {
							return '<a href="javascript:void(0)" onclick="department_delete(\'' + value + '\')">{删除}</a>';
						}
					}
				} ] ],
				toolbar : [ {
					text : "新增",
					iconCls : "icon-add",
					handler : function() {
						department_cancelEdit();
						department_datagrid.datagrid("appendRow", {});
						var index = department_datagrid.datagrid("getRows").length - 1;
						department_beginEdit(index);
					}
				}, {
					text : "修改",
					iconCls : "icon-edit",
					handler : function() {
						var selrow = department_datagrid.datagrid("getSelected");
						if (selrow) {
							department_cancelEdit();
							var index = department_datagrid.datagrid("getRowIndex", selrow);
							department_beginEdit(index);
						}
					}
				} ],
				onAfterEdit : function(rowIndex, rowData, rowChanges) {
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
							department_datagrid.datagrid("acceptChanges");
							department_cancelEdit();
						} else {
							department_cancelEdit();
						}
						$.messager.show({
							title : "提示信息",
							msg : json.message
						});
					});
					
				}
			});
			reloadDepartmentDatagrid();
		});

		function reloadDepartmentDatagrid() {
			department_datagrid.datagrid("options").url = "${postPath}";
			department_datagrid.datagrid("reload", {
				method : "get"
			});
		}

		function department_delete(id) {
			$.post("${postPath}", {
				method : "delete",
				id : id
			}, function(data) {
				var json = eval("(" + data + ")");
				if (json.status == "success") {
					reloadDepartmentDatagrid();
				}
				$.messager.show({
					title : "提示信息",
					msg : json.message
				});
			});
		}

		function department_beginEdit(index) {
			if (department_editIndex != -1) {
				department_cancelEdit();
			}
			$("#department_datagridFooter").show();
			department_editIndex = index;
			department_datagrid.datagrid("beginEdit", index);
		}

		function department_cancelEdit() {
			if (department_editIndex != -1) {
				department_datagrid.datagrid("rejectChanges", department_editIndex);
				department_editIndex = -1;
				$("#department_datagridFooter").hide();
			}
		}

		function department_submit() {
			department_datagrid.datagrid("endEdit", department_editIndex);
		}
	</script>
</body>
</html>