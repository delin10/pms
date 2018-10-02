<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/pages/common.jspf"%>
<c:set var="postPath" value="${contextPath }/stock/stock"></c:set>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
	<table id="stock_datagrid"></table>
	<div id="stock_datagridToolbar" style="padding: 2px 5px;">
		<label for="">
			货品名称：
			<input id="stock_goodsNameFilter" type="text" class="easyui-textbox" />
		</label>
		<label>
			货品类型：
			<select id="stock_goodsTypeFilter" class="easyui-combobox"></select>
		</label>
		<div style="float: right; margin-right: 10px;">
			<a href="javascript:void(0)" onclick="reloadStockDatagrid();" class="easyui-linkbutton"
				iconCls="icon-search" plain="true">搜索</a>
			<a href="javascript:void(0)" onclick="reloadStockDatagrid(true);" class="easyui-linkbutton"
				iconCls="icon-reload" plain="true">重置</a>
		</div>
	</div>
	<script type="text/javascript">
		var stock_datagrid;
		var stock_goodsTypeData;
		$(function() {
			stock_datagrid = $("#stock_datagrid");
			$.post("${contextPath}/ajax", {
				method : "gettypes",
				groupCode : "goodsType"
			}, function(data) {
				var json = eval("(" + data + ")");
				if (!json.status) {
					json[json.length] = {
						groupId : "",
						groupName : "全部",
						selected : true
					};
					$("#stock_goodsTypeFilter").combobox({
						valueField : "groupId",
						textField : "groupName",
						data : json,
						editable : false,
						width : "70px"
					});
					stock_goodsTypeData = eval("(" + data + ")");
					reloadStockDatagrid();
				} else {
					$.messager.show("提示信息", json.message);
				}
			});
			stock_datagrid.datagrid({
				idField : "id",
				pagination : true,
				striped : true,
				rownumbers : true,
				singleSelect : true,
				height : "100%",
				columns : [ [ {
					title : "货品名称",
					field : "goodsName",
					width : "30%"
				}, {
					title : "库存",
					field : "totalCount",
					width : "30%"
				} ] ],
				toolbar : "#stock_datagridToolbar"
			});

		});

		function reloadStockDatagrid(noQuery) {
			stock_datagrid.datagrid({
				url : "${postPath}"
			});
			var params = {
				method : "get",
				goodsTypeFilter : "",
				goodsNameFilter : ""
			};
			if (!noQuery) {
				params.goodsNameFilter = $("#stock_goodsNameFilter").val();
				params.goodsTypeFilter = $("#stock_goodsTypeFilter").combobox(
						"getValue");
			}
			stock_datagrid.datagrid("reload", params);
		}
	</script>
</body>
</html>