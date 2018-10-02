<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/pages/common.jspf"%>
<c:set var="postPath" value="${contextPath }/stock/ship"></c:set>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
	<table id="ship_datagrid"></table>
	<div id="ship_datagridToolbar" style="padding: 2px 5px;">
		<label for="">
			货品名称:
			<input id="ship_goodsNameFilter" class="easyui-textbox" />
		</label>
		<label>
			出库时间：
			<input id="ship_beginDateFilter" type="text" class="easyui-datebox" data-options="width:'100px'" />
			-
			<input id="ship_endDateFilter" type="text" class="easyui-datebox" data-options="width:'100px'" />
		</label>
		<div style="margin-top: 5px; margin-bottom: 5px;">
			<label for="">
				分部地址:
				<input id="ship_branchFilter" type="text" class="easyui-textbox" />
			</label>
			<label for="">
				状态：&nbsp;&nbsp;
				<select id="ship_statusFilter" class="easyui-combobox">

				</select>
			</label>
			<div style="float: right; margin-right: 10px;">
				<a href="javascript:void(0)" onclick="reloadShipDatagrid();" class="easyui-linkbutton"
					iconCls="icon-search" plain="true">搜索</a>
				<a href="javascript:void(0)" onclick="reloadShipDatagrid(true);" class="easyui-linkbutton"
					iconCls="icon-reload" plain="true">重置</a>
				<a href="javascript:void(0)" onclick="ship_onship();" class="easyui-linkbutton"
					iconCls="icon-add" plain="true">货品出库</a>
			</div>
		</div>
	</div>
	<div id="ship_shipWindow" style="display: none;">
		<form id="ship_form" method="post">
			<table style="width: 90%; margin: 10px auto;">
				<tr>
					<td>
						货品：
						<br />
						<label>&nbsp;</label>
						<br />
						<label>&nbsp;</label>
					</td>
					<td>
						<input type="text" id="ship_goodsName" class="easyui-validatebox"
							data-options="required:true,width:'166px'" readonly="readonly" />
						<input type="hidden" id="shipGoods" name="shipGoods" />
						<br />
						<a href="javascript:void(0);" onclick="ship_choseGoods();" plain="true"
							class="easyui-linkbutton" iconCls="icon-search">选择</a>
						<a href="javascript:void(0);" onclick="ship_resetGoods();" plain="true"
							class="easyui-linkbutton" iconCls="icon-undo">重置</a>
					</td>
				</tr>
				<tr>
					<td>出库数量：</td>
					<td>
						<input id="ship_shipCount" name="shipCount" type="text" class="easyui-numberbox"
							data-options="min:0,width:'166px',readonly:true" />
					</td>
				</tr>
				<tr>
					<td>
						分公司：
						<br />
						<label>&nbsp;</label>
						<br />
						<label>&nbsp;</label>
					</td>
					<td>
						<input type="text" id="ship_BranchName" class="easyui-validatebox"
							data-options="required:true,width:'166px'" readonly="readonly" />
						<input type="hidden" name="branch" id="branch" />
						<br />
						<a href="javascript:void(0);" onclick="ship_choseBranch();" plain="true"
							class="easyui-linkbutton" iconCls="icon-search">选择</a>
						<a href="javascript:void(0);" onclick="ship_resetBranch();" plain="true"
							class="easyui-linkbutton" iconCls="icon-undo">重置</a>
					</td>
				</tr>
				<tr>
					<td>出库时间：</td>
					<td>
						<input name="shipDate" type="text" class="easyui-datebox" required="required"
							data-options="width:'166px'" />
					</td>
				</tr>
				<tr>
					<td>备注：</td>
					<td>
						<input name="remark" type="text" class="easyui-textbox"
							data-options="multiline:true,width:'166px'" />
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<a href="javascript:void(0);" onclick="ship_submit();" class="easyui-linkbutton">提交</a>
						<a href="javascript:void(0);" onclick="ship_resetForm();" class="easyui-linkbutton"">重置</a>
						<a href="javascript:void(0);" onclick="$('#ship_shipWindow').window('close');"
							class="easyui-linkbutton">取消</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id="ship_choseGoodsWindow" style="display: none;">
		<table id="ship_choseGoodsData"></table>
	</div>
	<div id="ship_choseBranchWindow" style="display: none;">
		<table id="ship_choseBranchData"></table>
	</div>

	<script type="text/javascript">
		var ship_datagrid;
		$(function() {
			ship_datagrid = $("#ship_datagrid");

			$.messager.progress();
			$
					.post(
							"${contextPath}/ajax",
							{
								method : "getTypes",
								groupCode : "status"
							},
							function(data) {
								$.messager.progress("close");
								var json = eval("(" + data + ")");
								if (json.status) {
									$.messager.show({
										title : "提示信息",
										msg : "加载数据错误，请刷新重试"
									});
								} else {
									json[json.length] = {
										groupCode : "",
										groupName : "全部",
										selected : true
									};
									$("#ship_statusFilter").combobox({
										valueField : "groupCode",
										textField : "groupName",
										data : json,
										editable : false,
										width : "100px"
									});
									ship_datagrid
											.datagrid({
												idField : "id",
												pagination : true,
												striped : true,
												rownumbers : true,
												singleSelect : true,
												toolbar : "#ship_datagridToolbar",
												height : "100%",
												columns : [ [
														{
															title : "货品名称",
															field : "shipGoods",
															width : "20%"
														},
														{
															title : "出库数量",
															field : "shipCount",
															width : "10%"
														},
														{
															title : "状态",
															field : "status",
															width : "6%",
															formatter : function(
																	value, row,
																	index) {
																switch (value) {
																case "1":
																	return "待审核";
																case "2":
																	return "通过";
																case "3":
																	return "驳回";
																case "4":
																	return "已完成"
																}
															}
														},
														{
															title : "分公司地址",
															field : "branch",
															width : "25%"
														},
														{
															title : "出库时间",
															field : "shipDate",
															width : "10%",
															formatter : function(
																	value, row,
																	index) {
																return new Date(
																		value)
																		.Format("yyyy-MM-dd");
															}
														},
														{
															title : "备注",
															field : "remark",
															width : "15%"
														},
														{
															title : "操作",
															field : "id",
															width : "15%",
															formatter : function(
																	value, row,
																	index) {
																var passLink = '<a href="javascript:void(0)" onclick="ship_setStatus(\''
																		+ value
																		+ '\',\'pass\')">{通过}</a>';
																var rejectLink = '<a href="javascript:void(0)" onclick="ship_setStatus(\''
																		+ value
																		+ '\',\'reject\')">{驳回}</a>';
																var deleteLink = '<a href="javascript:void(0)" onclick="ship_setStatus(\''
																		+ value
																		+ '\',\'delete\')">{删除}</a>';
																var doneLink = '<a href="javascript:void(0)" onclick="ship_setStatus(\''
																		+ value
																		+ '\',\'done\')">{完成}</a>';
																if (row.status == "1") {
																	return passLink
																			+ rejectLink
																			+ deleteLink;
																}
																if (row.status == "2") {
																	return doneLink
																			+ deleteLink;
																}
																if (row.status == "3") {
																	return deleteLink;
																}
															}
														}, ] ]
											});
									reloadShipDatagrid();
								}
							});

		});

		function reloadShipDatagrid(noQuery) {
			ship_datagrid.datagrid({
				url : "${postPath}"
			});
			var params = {
				method : "get"
			};
			if (!noQuery) {
				params.goodsNameFilter = $("#ship_goodsNameFilter").val();
				params.branchFilter = $("#ship_branchFilter").val();
				params.statusFilter = $("#ship_statusFilter").combobox(
						"getValue");
				params.beginDateFilter = $("#ship_beginDateFilter").datebox(
						"getValue");
				params.endDateFilter = $("#ship_endDateFilter").datebox(
						"getValue");
			}
			ship_datagrid.datagrid("reload", params);
		}

		function ship_setStatus(id, status) {
			var params = {
				id : id,
				method : status
			};
			$.post("${postPath}", params, function(data) {
				var json = eval("(" + data + ")");
				if (json.status == "success") {
					reloadShipDatagrid();
				}
				$.messager.show({
					title : "提示信息",
					msg : json.message
				})
			});
		}

		function ship_onship() {
			$.window("#ship_shipWindow", "货品出库", "340px", "310px");
		}

		function ship_submit() {
			$.messager.progress();
			$("#ship_form").form("submit", {
				url : "${postPath}",
				queryParams : {
					method : "add"
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
						reloadShipDatagrid(true);
					}
					$.messager.show({
						title : "提示信息",
						msg : json.message
					})
				}
			});
		}

		function ship_resetForm() {

		}

		function ship_choseGoods() {
			var datagrid = $("#ship_choseGoodsData");
			$.window("#ship_choseGoodsWindow", "选择货品", "360px", "320px");
			var opts = $("#ship_shipWindow").window("options");
			$("#ship_choseGoodsWindow").window({
				left : opts.left + opts.width,
				top : opts.top
			});
			datagrid.datagrid({
				idField : "id",
				url : "${contextPath}/stock/stock?method=get",
				pagination : true,
				striped : true,
				rownumbers : true,
				singleSelect : true,
				height : "100%",
				columns : [ [ {
					title : "商品名称",
					field : "goodsName",
					width : "60%"
				}, {
					title : "库存",
					field : "totalCount",
					width : "60%"
				} ] ]
			});
			var pager = datagrid.datagrid("getPager");
			pager.pagination({
				buttons : [ {
					text : "确定",
					iconCls : "icon-add",
					handler : function() {
						var selrow = datagrid.datagrid("getSelected");
						if (selrow) {
							if (selrow.totalCount > 0) {
								$("#ship_goodsName").val(selrow.goodsName);
								$("#shipGoods").val(selrow.goodsId);
								$("#ship_choseGoodsWindow").window("close");
								$("#ship_shipCount").numberbox({
									readonly : false,
									min : 0,
									max : selrow.totalCount
								});
								$("#ship_goodsName").validatebox("validate");
							} else {
								$.messager.show({
									title : "提示信息",
									msg : "该货品没有库存"
								});
							}
						}
					}
				} ]
			});
		}

		function ship_choseBranch() {
			var datagrid = $("#ship_choseBranchData");
			$.window("#ship_choseBranchWindow", "选择分公司", "360px", "320px");
			var opts = $("#ship_shipWindow").window("options");
			$("#ship_choseBranchWindow").window({
				left : opts.left + opts.width,
				top : opts.top
			});
			datagrid.datagrid({
				idField : "id",
				url : "${contextPath}/basic/branch?method=get",
				pagination : true,
				striped : true,
				rownumbers : true,
				singleSelect : true,
				height : "100%",
				columns : [ [ {
					title : "分公司地址",
					field : "branchAddr",
					width : "100%"
				} ] ]
			});
			var pager = datagrid.datagrid("getPager");
			pager.pagination({
				buttons : [ {
					text : "确定",
					iconCls : "icon-add",
					handler : function() {
						var selrow = datagrid.datagrid("getSelected");
						if (selrow) {
							$("#ship_BranchName").val(selrow.branchAddr);
							$("#branch").val(selrow.id);
							$("#ship_choseBranchWindow").window("close");
							$("#ship_BranchName").validatebox("validate");
						}
					}
				} ]
			});
		}
	</script>
</body>

</html>