<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/pages/common.jspf"%>
<c:set var="postPath" value="${contextPath }/stock/purchase"></c:set>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
	<table id="purchase_datagrid"></table>
	<div id="purchase_datagridToolbar" style="padding: 2px 5px;">
		<div>
			<label for="">
				单号：
				<input type="text" id="purchase_numFilter" class="easyui-textbox" />
			</label>
			<label for="">
				货品名称：
				<input type="text" id="purchase_goodsNameFilter" class="easyui-textbox" />
			</label>
			<label for="">
				供应商名称：
				<input type="text" id="purchase_supplierFilter" class="easyui-textbox" />
			</label>

		</div>
		<div style="margin-top: 5px;">
			<label for="">
				状态：
				<select class="easyui-combobox" id="purchase_combostatus"></select>
			</label>
			<div style="float: right">
				<a href="javascript:void(0);" onclick="reloadPurchaseDatagrid();" class="easyui-linkbutton"
					iconCls="icon-search" plain="true">搜索</a>
				<a href="javascript:void(0);" onclick="reloadPurchaseDatagrid(true);" class="easyui-linkbutton"
					iconCls="icon-reload" plain="true">重置</a>
			</div>
		</div>
		<div>
			<a href="javascript:void(0);" onclick="purchase_onPurchase();" class="easyui-linkbutton"
				iconCls="icon-add" plain="true">货品入库</a>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true">查看详细</a>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
		</div>
	</div>
	<div id="purchase_editWindow" style="display: none;">
		<form id="purchase_editForm" method="post">
			<table style="width: 80%; margin: auto;">
				<tr>
					<td>单号：</td>
					<td>
						<input name="purchaseNum" type="text" class="easyui-validatebox"
							data-options="required:true,validType:['length[1,16]']" />
					</td>
				</tr>
				<tr>
					<td>
						货品：
						<br />
						<label>&nbsp;</label>
						<br />
						<label>&nbsp;</label>
					</td>
					<td>
						<input id="purchase_goodsName" name="goodsName" type="text" class="easyui-validatebox"
							data-options="required:true,readonly:true,validType:['length[1,16]'],width:'170px'" />
						<input type="hidden" id="purchase_goodsId" name="purchaseGoods" />
						<br />
						<a href="javascript:void(0)" onclick="purchase_choseGoods();" class="easyui-linkbutton"
							iconCls="icon-search" plain="true">选择</a>
						<a href="javascript:void(0)" onclick="setPurchaseGoods();" class="easyui-linkbutton"
							iconCls="icon-undo" plain="true">重置</a>
					</td>
				</tr>
				<tr>
					<td>数量：</td>
					<td>
						<input name="purchaseCount" type="text" class="easyui-numberbox"
							data-options="required:true,min:0,width:'170px'" />
					</td>
				</tr>
				<tr>
					<td>价格：</td>
					<td>
						<input name="price" type="text" class="easyui-numberbox"
							data-options="required:true,min:0,precision:2,width:'170px'" />
					</td>
				</tr>
				<tr>
					<td>
						供应商：
						<br />
						<label>&nbsp;</label>
						<br />
						<label>&nbsp;</label>
					</td>
					<td>
						<input id="purchase_supplierName" name="supplierName" type="text" class="easyui-validatebox"
							data-options="required:true,readonly:true,validType:['length[1,16]'],width:'170px'" />
						<input type="hidden" id="purchase_supplierId" name="supplier" />
						<br />
						<a href="javascript:void(0)" onclick="purchase_choseSupplier();" class="easyui-linkbutton"
							iconCls="icon-search" plain="true">选择</a>
						<a href="javascript:void(0)" onclick="setPurchaseSupplier();" class="easyui-linkbutton"
							iconCls="icon-undo" plain="true">重置</a>
					</td>
				</tr>
				<tr>
					<td>联系人：</td>
					<td>
						<input name="contactName" type="text" class="easyui-validatebox"
							data-options="required:true,validType:['length[1,16]'],width:'170px'" />
					</td>
				</tr>
				<tr>
					<td>联系人电话：</td>
					<td>
						<input name="contactTel" type="text" class="easyui-numberbox"
							data-options="required:true,min:11,max:11,width:'170px'" />
					</td>
				</tr>
				<tr>
					<td>备注：</td>
					<td>
						<input name="remark" type="text" class="easyui-textbox"
							data-options="multiline:true,width:'170px'" />
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<a href="javascript:void(0)" onclick="purchase_submit();" class="easyui-linkbutton">提交</a>
						<a href="javascript:void(0)" onclick="purchase_resetForm();" class="easyui-linkbutton">重置</a>
						<a href="javascript:void(0)" onclick="$('#purchase_editWindow').window('close');"
							class="easyui-linkbutton">取消</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id="purchase_choseGoodsWindow" style="display: none;">
		<table id="purchase_choseGoodsDatagrid"></table>
	</div>
	<div id="purchase_choseSupplierWindow" style="display: none;">
		<table id="purchase_choseSupplierDatagrid"></table>
	</div>
	<script type="text/javascript">
		var purchase_datagrid;
		var purchase_status;
		var purchase_editWindow;

		$(function() {
			purchase_datagrid = $("#purchase_datagrid");

			$
					.post(
							"${contextPath}/ajax",
							{
								method : "gettypes",
								groupCode : "status"
							},
							function(data) {
								var json = eval("(" + data + ")");
								if (json.status) {
									$.messager.show({
										title : "提示信息",
										msg : "获取信息失败，请刷新重试"
									});
								} else {
									purchase_status = eval("(" + data + ")");
									json[json.length] = {
										groupCode : "",
										groupName : "全部",
										selected : "true"
									};
									$("#purchase_combostatus").combobox({
										valueField : "groupCode",
										textField : "groupName",
										data : json,
										editable : false,
										width : "80px"
									});
									purchase_datagrid
											.datagrid({
												idField : "id",
												pagination : true,
												striped : true,
												rownumbers : true,
												singleSelect : true,
												height : "100%",
												columns : [ [
														{
															title : "单号",
															field : "purchaseNum",
															width : "10%"
														},
														{
															title : "货品名称",
															field : "goodsName",
															width : "15%"
														},
														{
															title : "数量",
															field : "purchaseCount",
															width : "5%"
														},
														{
															title : "价格",
															field : "price",
															width : "5%"
														},
														{
															title : "状态",
															field : "status",
															width : "6%",
															formatter : function(
																	value, row,
																	index) {
																for ( var index in purchase_status) {
																	if (value == purchase_status[index].groupCode) {
																		return purchase_status[index].groupName;
																	}
																}
															}
														},
														{
															title : "供应商",
															field : "supplierName",
															width : "15%"
														},
														{
															title : "联系人",
															field : "contactName",
															width : "10%"
														},
														{
															title : "联系人电话",
															field : "purchaseTel",
															width : "10%"
														},
														{
															title : "备注",
															field : "remark",
															width : "10%"
														},
														{
															title : "操作",
															field : "id",
															width : "17%",
															formatter : function(
																	value, row,
																	index) {
																var passLink = '<a href="javascript:void(0)" onclick="purchase_setStatus(\''
																		+ value
																		+ '\',\'pass\')">{通过}</a>';
																var rejectLink = '<a href="javascript:void(0)" onclick="purchase_setStatus(\''
																		+ value
																		+ '\',\'reject\')">{驳回}</a>';
																var deleteLink = '<a href="javascript:void(0)" onclick="purchase_setStatus(\''
																		+ value
																		+ '\',\'delete\')">{删除}</a>';
																var doneLink = '<a href="javascript:void(0)" onclick="purchase_setStatus(\''
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
														} ] ],
												toolbar : "#purchase_datagridToolbar"
											});
									reloadPurchaseDatagrid();
								}
							});
		});

		function initPurchase_choseGoodsDatagrid() {
			var datagrid = $("#purchase_choseGoodsDatagrid");
			datagrid.datagrid({
				idField : "id",
				pagination : true,
				striped : true,
				rownumbers : true,
				singleSelect : true,
				url : "${contextPath}/basic/goods?method=get",
				columns : [ [ {
					title : "货品名称",
					field : "goodsName",
					width : "60%"
				}, {
					title : "成本价",
					field : "costPrice",
					width : "40%"
				} ] ]
			});
			var pager = datagrid.datagrid("getPager");
			pager.pagination({
				buttons : [ {
					text : "确定",
					iconCls : "icon-add",
					handler : function() {
						var selrow = datagrid.datagrid("getSelected");
						if (!selrow) {
							$.messager.show({
								title : "提示信息",
								msg : "请选择一个货品"
							});
						} else {
							setPurchaseGoods(selrow.goodsName, selrow.id);
							$("#purchase_choseGoodsWindow").window("close");
						}
					}
				} ]
			});
		}

		function initPurchase_choseSupplierDatagrid() {
			var datagrid = $("#purchase_choseSupplierDatagrid");
			datagrid.datagrid({
				idField : "id",
				pagination : true,
				striped : true,
				rownumbers : true,
				singleSelect : true,
				url : "${contextPath}/basic/supplier?method=get",
				columns : [ [ {
					title : "供应商名称",
					field : "supplierName",
					width : "100%"
				} ] ]
			});
			var pager = datagrid.datagrid("getPager");
			pager
					.pagination({
						buttons : [ {
							text : "确定",
							iconCls : "icon-add",
							handler : function() {
								var selrow = datagrid.datagrid("getSelected");
								if (!selrow) {
									$.messager.show({
										title : "提示信息",
										msg : "请选择一个供应商"
									});
								} else {
									setPurchaseSupplier(selrow.supplierName,
											selrow.id);
									$("#purchase_choseSupplierWindow").window(
											"close");
								}
							}
						} ]
					});
		}

		function setPurchaseGoods(goodsName, goodsId) {
			$("#purchase_goodsName").val(goodsName);
			$("#purchase_goodsName").validatebox("validate");
			$("#purchase_goodsId").val(goodsId);
		}

		function setPurchaseSupplier(supplierName, id) {
			$("#purchase_supplierName").val(supplierName);
			$("#purchase_supplierName").validatebox("validate");
			$("#purchase_supplierId").val(id);
		}

		function purchase_choseSupplier() {
			initPurchase_choseSupplierDatagrid();
			$
					.window("#purchase_choseSupplierWindow", "选择供应商", "380px",
							"400px");
			var opts = $("#purchase_editWindow").window("options");
			$("#purchase_choseSupplierWindow").window({
				left : opts.left + opts.width
			});
		}

		function purchase_choseGoods() {
			initPurchase_choseGoodsDatagrid();
			$.window("#purchase_choseGoodsWindow", "选择货品", "380px", "400px");
			var opts = $("#purchase_editWindow").window("options");
			$("#purchase_choseGoodsWindow").window({
				left : opts.left + opts.width
			});

		}

		function purchase_onPurchase() {
			$.window("#purchase_editWindow", "货品入库", "360px", "400px");
		}

		function purchase_submit() {
			$("#purchase_editForm").form("submit", {
				url : "${postPath}?method=add",
				onSubmit : function() {
					$.messager.progress();
					var isvalid = $(this).form("validate");
					if (!isvalid) {
						$.messager.progress("close");
					}
					return isvalid;
				},
				success : function(data) {
					var json = eval("(" + data + ")");
					if (json.status == "success") {
						reloadPurchaseDatagrid();
					}
					$.messager.progress("close");
					$.messager.show({
						title : "提示信息",
						msg : json.message
					});
				}
			});
		}

		function purchase_resetForm() {
			$("#purchase_editForm input").each(function() {
				$(this).val("");
			});
			$("#purchase_editForm textarea").val("");

		}

		function purchase_setStatus(id, status) {
			$.post("${postPath}", {
				method : status,
				id : id
			}, function(data) {
				var json = eval("(" + data + ")");
				if (json.status == "success") {
					reloadPurchaseDatagrid();
				}
				$.messager.show({
					title : "提示信息",
					msg : json.message
				});
			});
		}

		function reloadPurchaseDatagrid(noQuery) {
			purchase_datagrid.datagrid("options").url = "${postPath}";
			var params = {
				method : "get"
			};
			if (!noQuery) {
				params.statusFilter = $("#purchase_combostatus").combobox(
						"getValue");
				params.purchaseNumFilter = $("#purchase_numFilter").val();
				params.goodsNameFilter = $("#purchase_goodsNameFilter").val();
				params.supplierFilter = $("#purchase_supplierFilter").val();
			}
			console.log(params);
			purchase_datagrid.datagrid("reload", params);
		}
	</script>
</body>
</html>