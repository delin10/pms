<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/pages/common.jspf"%>
<c:set var="postPath" value="${contextPath }/basic/goods"></c:set>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
	<table id="goods_datagrid" style="height: 98%;">

	</table>


	<div id="goods_toolbar" style="padding: 2px 5px;">
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="goods_add();">新增</a>
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="goods_edit();">编辑</a>
		<label>分类查看：</label>
		<select id="goods_typesFilter" class="easyui-combobox" style="width: 130px;">
			<option>全部</option>
		</select>
		<label for="">
			货品名称：
			<input type="text" class="easyui-textbox" id="goods_nameFilter" />
		</label>

		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="reloadGoodsDataGrid();">查询</a>
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-reload" plain="true" onclick="reloadGoodsDataGrid(true);">重置</a>
	</div>

	<div id="goods_addGoods" style="display: none;">
		<form id="goods_addGoodsForm"></form>
	</div>
	<script type="text/javascript">
		var goods_datagrid;
		var goods_editingIndex = -1;
		var goods_groupCode = "goodsType";
		var goods_typesData;

		$(function() {
			$.post("${contextPath}/ajax", {
				method : "gettypes",
				groupCode : goods_groupCode
			}, function(data) {
				var json = eval("(" + data + ")");
				if (json.status) {
					$.messager.alert("提示信息", json.message);
				} else {
					if (json.length && json.length > 0) {
						goods_typesData = eval("(" + data + ")");
						json[json.length] = {
							groupId : "all",
							groupName : "全部",
							selected : true
						};

						$("#goods_typesFilter").combobox({
							valueField : "groupId",
							textField : "groupName",
							data : json,
							editable : false,
							onChange : function(newValue, oladValue) {
								reloadGoodsDataGrid();
							}
						});
					}
				}
			});

			goods_datagrid = $("#goods_datagrid");
			goods_datagrid.datagrid({
				idField : "id",
				pagination : true,
				striped : true,
				rownumbers : true,
				singleSelect : true,
				title : "货品列表",
				onAfterEdit : function(rowIndex, rowData, changes) {
					var method = "add";
					if (rowData.id) {
						method = "update";
					}
					rowData.method = method;
					$.post("${postPath}", rowData, function(data) {
						var json = eval("(" + data + ")");
						if (json.status) {
							$.messager.alert("错误信息", json.message);
							goods_datagrid.datagrid("rejectChanges");
							goods_endEdit();
						} else {
							goods_datagrid.datagrid("updateRow", {
								index : goods_editingIndex,
								row : json
							});
							goods_endEdit();
							$.messager.show({
								title : "消息",
								msg : "操作成功",
								timeout : 2000
							});
						}
					});
				},
				columns : [ [ {
					title : "货品名称",
					field : "goodsName",
					width : "20%",
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
					title : "货品类型",
					field : "goodsType",
					width : "20%",
					formatter : function(value, row, index) {
						if (value != null && value != undefined) {
							return value.groupName;
						}
					},
					editor : {
						type : "combobox",
						options : {
							editable : false,
							valueField : "groupId",
							textField : "groupName",
							required : true
						}
					}
				}, {
					title : "成本价",
					field : "costPrice",
					width : "20%",
					editor : {
						type : "numberbox",
						options : {
							precision : "2",
							required : true
						}
					}
				}, {
					title : "备注",
					field : "remark",
					width : "30%",
					editor : {
						type : "textarea"
					}
				}, {
					title : "操作",
					field : "id",
					width : "10%",
					formatter : function(value, row, index) {
						if (value != null && value != undefined) {
							return '<a href="javascript:void(0);" onclick="deleteGoods(\'' + value + '\',' + index + ')">{删除}</a>';
						}
					}
				} ] ],
				toolbar : "#goods_toolbar"
			});
		});

		function goods_add() {
			goods_datagrid.datagrid("appendRow", {});
			var index;
			index = goods_datagrid.datagrid("getRows").length - 1;
			goods_datagrid.datagrid("selectRow", index);
			goods_datagrid.datagrid("beginEdit", index);
			goods_editingIndex = index;
			goods_beginEdit();
			var selrow = goods_datagrid.datagrid("getSelected");
			var rowIndex = goods_datagrid.datagrid("getRowIndex", selrow);
			var comboEditor = goods_datagrid.datagrid("getEditor", {
				index : rowIndex,
				field : "goodsType"
			});
			comboEditor.target.combobox({
				data : goods_typesData
			});
		}

		function goods_edit() {
			var selrow = goods_datagrid.datagrid("getSelected");
			if (selrow) {

				if (goods_editingIndex != -1) {
					cancelEdit();
				}
				var index = goods_datagrid.datagrid("getRowIndex", selrow);
				goods_editingIndex = index;
				goods_datagrid.datagrid("beginEdit", index);
				goods_datagrid.datagrid("selectRow", index);
				goods_beginEdit();
				var comboEditor = goods_datagrid.datagrid("getEditor", {
					index : index,
					field : "goodsType"
				});
				comboEditor.target.combobox({
					data : goods_typesData
				});
				if (selrow != null && selrow != undefined && selrow.goodsType != null && selrow.goodsType != undefined) {
					comboEditor.target.combo("setText", selrow.goodsType.groupName);
					comboEditor.target.combo("setValue", selrow.goodsType.groupId);

				}
			}
		}

		function deleteGoods(id, index) {
			if ($.messager.confirm("确认操作", "确定要删除该数据？", function(flag) {
				if (flag) {
					$.post("${postPath}", {
						id : id,
						method : "delete"
					}, function(data) {
						var json = eval("(" + data + ")");
						if (json.status != "success") {
							$.messager.alert("提示信息", json.message)
						} else {
							$.messager.show({
								title : "消息",
								msg : json.message,
								showType : "slide",
								timeout : 2000
							});
							goods_datagrid.datagrid("deleteRow", index);
						}
					});
				}
			}))
				;
		}

		function goods_endEdit() {
			//goods_datagrid.datagrid("cancelEdit", goods_editingIndex);

			goods_editingIndex = -1;
			goods_datagrid.datagrid("acceptChanges");
			var pager = goods_datagrid.datagrid("getPager");
			pager.pagination({
				buttons : null
			});
		}

		function goods_beginEdit() {
			var pager = goods_datagrid.datagrid("getPager");
			pager.pagination({
				buttons : [ {
					iconCls : 'icon-save',
					text : "提交",
					handler : function() {
						goods_datagrid.datagrid("endEdit", goods_editingIndex);
					}
				}, {
					iconCls : "icon-cancel",
					text : "取消",
					handler : function() {
						goods_datagrid.datagrid("rejectChanges");
						goods_endEdit();
					}
				} ]
			});
		}

		function reloadGoodsDataGrid(noQuery) {
			var data = {
				method : "get",
				type : "",
				searchKey : ""
			};
			if (!noQuery) {
				var type = $("#goods_typesFilter").combo("getValue");
				var searchKey = $("#goods_nameFilter").val();
				data.type = type;
				data.searchKey = searchKey;
			}

			goods_datagrid.datagrid("options").url = "${postPath}?method=get";
			goods_datagrid.datagrid("load", data);
		}
	</script>
</body>
</html>