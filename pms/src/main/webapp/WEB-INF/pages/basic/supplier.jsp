<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/pages/common.jspf"%>
<c:set var="postPath" value='${contextPath }/basic/supplier'></c:set>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
	<table id="supplier_datagrid" title="供应商列表">

	</table>

	<div id="supplier_datagridToolbar" style="padding: 2px 5px;">
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="supplier_add();">添加</a>
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="supplier_edit();">编辑</a>
		<div>
			<label>
				供应商名称：
				<input type="text" id="supplier_nameFilter" class="easyui-textbox" />
			</label>
			<label>
				企业注册号：
				<input type="text" id="supplier_numFilter" class="easyui-textbox" />
			</label>
			<div style="float: right;">
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="reloadSupplierDatagrid();">搜索</a>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-undo" plain="true" onclick="reloadSupplierDatagrid(true);">重置</a>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		var supplier_datagrid;
		var supplier_editIndex = -1;

		$(function() {
			supplier_datagrid = $("#supplier_datagrid");
			supplier_datagrid.datagrid({
				idField : "id",
				pagination : true,
				striped : true,
				rownumbers : true,
				singleSelect : true,
				height : "100%",
				onAfterEdit : function(rowIndex, rowData, changes) {
					if (rowData) {
						console.log(rowIndex);
						console.log(rowData);
						console.log(changes);
						var postUrl = "${postPath}?method=";
						if (rowData.id) {
							postUrl += "update";
						} else {
							postUrl += "add";
						}

						$.post(postUrl, rowData, function(data) {
							var json = eval("(" + data + ")");
							console.log(json);
							if (json.status != "success") {
								supplier_datagrid.datagrid("rejectChanges");
								/* if (rowData.id) {
									
								} else {
									supplier_datagrid.datagrid(
											"deleteRow", rowIndex);
								} */
							}
							$.messager.show({
								title : "提示消息",
								msg : json.message
							});
							supplier_endEdit();
						});
					}
				},
				columns : [ [ {
					title : "供应商名称",
					field : "supplierName",
					width : "20%",
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
					title : "供应商地址",
					field : "supplierAddr",
					width : "15%",
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
					title : "邮箱地址",
					field : "supplierEmail",
					width : "10%",
					editor : {
						type : "validatebox",
						options : {
							required : true,
							validType : "email"
						}
					}
				}, {
					title : "联系电话",
					field : "supplierTel",
					width : "10%",
					editor : {
						type : "textbox"
					}
				}, {
					title : "企业注册号",
					field : "supplierNum",
					width : "10%",
					editor : {
						type : "numberbox"
					}
				}, {
					title : "联系人",
					field : "contactPerson",
					width : "10%",
					editor : {
						type : "validatebox",
						options : {
							required : true,
							validType : {
								length : [ 2, 20 ]
							}
						}
					}
				}, {
					title : "联系人电话",
					field : "contactTel",
					width : "10%",
					editor : {
						type : "numberbox",
						validType : {
							required : true
						}
					}
				}, {
					title : "操作",
					field : "id",
					width : "10%",
					formatter : function(value, row, index) {
						return '<a href="javascript:void(0);" onclick="supplier_delete(\'' + value + '\',\'' + index + '\')">{删除}</a>';
					}
				} ] ],
				toolbar : "#supplier_datagridToolbar"
			});
			reloadSupplierDatagrid();
		});

		function supplier_delete(id, index) {
			$.post("${postPath}", {
				method : "delete",
				id : id
			}, function(data) {
				var json = eval("(" + data + ")");
				if (json.status == "success") {
					supplier_datagrid.datagrid("deleteRow", index);
				}
				$.messager.show({
					title : "提示信息",
					msg : json.message
				});
			});
		}

		function supplier_add() {
			supplier_datagrid.datagrid("appendRow", {});
			var rowIndex = supplier_datagrid.datagrid("getRows").length - 1;
			supplier_datagrid.datagrid("selectRow", rowIndex)
			supplier_datagrid.datagrid("beginEdit", rowIndex);
			supplier_editIndex = rowIndex;
			supplier_beginEdit();
		}

		function supplier_edit() {
			var selrow = supplier_datagrid.datagrid("getSelected");
			if (selrow) {
				if (supplier_editIndex != -1) {
					supplier_endEdit();
				}
				var rowIndex = supplier_datagrid.datagrid("getRowIndex", selrow);
				supplier_datagrid.datagrid("beginEdit", rowIndex);
				supplier_beginEdit();
				supplier_editIndex = rowIndex;

			}
		}

		function supplier_endEdit() {
			//goods_datagrid.datagrid("cancelEdit", goods_editingIndex);

			supplier_datagrid.datagrid("rejectChanges", supplier_editIndex);
			supplier_editIndex = -1;
			//supplier_datagrid.datagrid("acceptChanges");
			var pager = supplier_datagrid.datagrid("getPager");
			pager.pagination({
				buttons : null
			});
		}

		function supplier_beginEdit() {
			if (supplier_editIndex != -1) {
				supplier_endEdit();
			}
			var pager = supplier_datagrid.datagrid("getPager");
			pager.pagination({
				buttons : [ {
					iconCls : 'icon-save',
					text : "提交",
					handler : function() {
						supplier_datagrid.datagrid("endEdit", supplier_editIndex);
					}
				}, {
					iconCls : "icon-cancel",
					text : "取消",
					handler : function() {

						supplier_endEdit();
					}
				} ]
			});
		}

		function reloadSupplierDatagrid(noQuery) {
			supplier_datagrid.datagrid("options").url = "${postPath}";
			var queryParam = {
				method : "get",
				nameFilter : "",
				numFilter : ""
			};
			if (!noQuery) {
				var nameFilter = $("#supplier_nameFilter").val();
				var numFilter = $("#supplier_numFilter").val();
				queryParam.nameFilter = nameFilter;
				queryParam.numFilter = numFilter;
			}
			supplier_datagrid.datagrid("load", queryParam);
		}
	</script>
</body>
</html>