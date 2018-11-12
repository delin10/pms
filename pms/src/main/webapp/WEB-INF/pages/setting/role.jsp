<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<link rel="stylesheet" href="plug-ins/easyui/css/default/easyui.css" />
<link rel="stylesheet" href="plug-ins/easyui/css/icon.css" />
<link rel="stylesheet" href="plug-ins/index/css/style.css" />
<title>Insert title here</title>
</head>
<body class="easyui-layout">
	<div id="button-group" style="padding: 5px 0; margin-left: 20px">
		<a id="addRole" href="javascript:void(0);" class="easyui-linkbutton"
			data-options="iconCls:'icon-add',plain:true"
			style="width: 80px; float: left; margin-right: 20px;"><span
			style="line-height: 22px;" onclick="showAddForm()">新增角色</span></a> <a
			href="#" class="easyui-menubutton"
			data-options="menu:'#edit-item',iconCls:'icon-edit'">编辑角色</a><a
			id="addRole" href="javascript:void(0);" class="easyui-linkbutton"
			data-options="iconCls:'icon-cancel',plain:true"
			style="width: 80px; float: left; margin-right: 20px;"><span
			style="line-height: 22px;" onclick="delRole()">删除角色</span></a>
	</div>
	<table id="roles" class="easyui-datagrid"
		style="width: 100%; height: 100%" toolbar="#tb"
		data-options="fit:false,fitColumns:true,singleSelect:true">
	</table>
	<div id="tb" style="padding: 3px">
		<span>搜索项目:</span><input class="easyui-combobox" id="search-keys"
			name="key" /><span>关键字:</span> <input id="key-word"
			style="line-height: 26px; border: 1px solid #ccc"> <a
			href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true"
			onclick="doSearch()">Search</a><a href="#" class="easyui-linkbutton"
			iconCls="icon-reload" plain="true" onclick="refresh('setting?action=roles&resource_id=setRole')"
			style="display: block; float: right; margin-right: 20px;">刷新</a>
	</div>
	<div id="edit-item" style="width: 80px">
		<div data-options="iconCls:'icon-auth'"
			onclick="showChooseResourcesForm()">授予资源</div>
		<div data-options="iconCls:'icon-change'"
			onclick="showUpdateRolesForm()">修改角色</div>
	</div>
	<div id="add-role-win" class="easyui-window" title="添加角色"
		style="width: 100%; height: 800px"
		data-options="iconCls:'icon-save',modal:true,closed:true">
		<form id="add-role-form" class="easyui-form" method="post"
			data-options="novalidate:true">
			<table cellpadding="5">
				<tr>
					<td>角色名字:</td>
					<td><input class="easyui-textbox needed" type="text"
						name="name" field="name" data-options="required:true"></input></td>
				</tr>
				<tr>
					<td>角色描述:</td>
					<td><input class="easyui-textbox  needed" type="text"
						name="description" data-options="required:true,validType:'email'"></input></td>
				</tr>
			</table>
		</form>
		<div style="text-align: center; padding: 5px">
			<a href="javascript:void(0)" class="easyui-linkbutton"
				onclick="submitForm()">添加</a> <a href="javascript:void(0)"
				class="easyui-linkbutton" onclick="cancel('add-role-win')">取消</a>
		</div>
	</div>

	<div id="update-role-win" class="easyui-window" title="My Window"
		data-options="iconCls:'icon-save',modal:true,closed:true">
		<form id="update-role-form" class="easyui-form" method="post"
			data-options="novalidate:true">
			<table cellpadding="5">
				<tr>
					<td><input class="easyui-textbox needed" type="hidden"
						name="id" data-options="required:true"></input></td>
				</tr>
				<tr>
					<td>角色名字:</td>
					<td><input class="easyui-textbox needed" type="text"
						name="name" data-options="required:true"></input></td>
				</tr>

				<tr>
					<td>角色描述:</td>
					<td><input class="easyui-textbox  needed" type="text"
						name="description" data-options="required:true"></input></td>
				</tr>
				<tr>
					<td>是否可用:</td>
					<td><select class="easyui-combobox needed" name="available"
						style="width: 100px;">
							<option value="1">可用</option>
							<option value="0">不可用</option>
					</select></td>
				</tr>
			</table>
		</form>
		<div style="text-align: center; padding: 5px">
			<a href="javascript:void(0)" class="easyui-linkbutton"
				onclick="updateRole()">修改</a> <a href="javascript:void(0)"
				class="easyui-linkbutton" onclick="cancel('update-role-win')">取消</a>
		</div>
	</div>

	<div id="auth-win" class="easyui-window" title="My Window"
		style="width: 100%; height: 800px; position: relative"
		data-options="iconCls:'icon-save',modal:true,closed:true">
		<div data-options="region:'west',title:'资源列表',collapsible:false"
			style="width: 220px">
			<ul id="resource-list" data-options="checkbox:true,cascadecheck:true"></ul>
		</div>
		<div
			style="text-align: center; padding: 5px; position: absolute; bottom: 10px; left: 35%;">
			<a href="javascript:void(0)" class="easyui-linkbutton"
				onclick="auth()">提交</a> <a href="javascript:void(0)"
				class="easyui-linkbutton" onclick="cancel('add-role-win')">取消</a>
		</div>
	</div>
	<script src="plug-ins/jquery-3.3.1.js" charset="utf-8"></script>
	<script type="text/javascript"
		src="plug-ins/easyui/js/jquery.easyui.min.js" charset="utf-8"></script>
	<script type="text/javascript"
		src="plug-ins/easyui/js/easyui-lang-zh_CN.js" charset="utf-8"></script>
	<script type="text/javascript"
		src="plug-ins/index/js/easyui-extends.js"></script>
	<script type="text/javascript">
		function contains(own, id) {
			for (var i = 0; i < own.length; ++i) {
				if (own[i].id == id) {
					return true;
				}
			}
			return false;
		}

		function check(children) {
			if (!children || children.length == 0) {
				return;
			}
			for (var i = 0; i < children.length; ++i) {
				if (children[i].isChecked) {
					console.log(children[i].id);
					var node = $('#resource-list').tree('find', children[i].id);
					$("#resource-list").tree('check', node.target);
				}
				check(children[i].children);
			}
		}
		function convert_procedure(own, rows, fid, children) {
			for (var i = 0; i < rows.length;) {
				var row = rows[i];
				if (row.fid == fid) {
					var node = {
						id : row.id,
						text : row.name,
						url : row.url,
						fid : row.fid
					};
					if (contains(own, row.id)) {
						node.isChecked = true;
					} else {
						node.isChecked = false;
					}
					children.push(node);
					rows.splice(i, 1);
					if (!node.children) {
						node.children = [];
					}
					convert_procedure(own, rows, row.id, node.children);
				} else {
					++i;
				}
			}

		}
		var checked = [];
		function convert(rows, own) {
			function children(arr, parentId) {
				var temp = [];
				for (var i = 0; i < arr.length;) {
					var row = arr[i];
					//console.log(arr.length);
					if (row.fid == parentId) {
						temp.push(row);
						arr.splice(i, 1);
					} else {
						i++;
					}
				}
				return temp;
			}
			var swap = [];
			for (var i = 0; i < rows.length; i++) {
				swap.push(rows[i]);
			}
			var nodes = children(swap, '');
			var roots = [];
			for (var i = 0; i < nodes.length; ++i) {
				var row = nodes[i];
				var node = {
					id : row.id,
					text : row.name,
					url : row.url,
					fid : row.fid
				};
				if (contains(own, row.id)) {
					node.isChecked = true;
					//console.log(node);
				} else {
					node.isChecked = false;
				}
				roots.push(node);
				//console.log(node);
				if (!node.children) {
					node.children = [];
				}
				convert_procedure(own, swap, node.id, node.children);
			}
			return roots;
		}

		function showChooseResourcesForm() {
			var row = $('#roles').datagrid('getSelected');
			if (row) {
				$("#auth-win").window({
					title : "资源列表",
					width : 300,
					height : 300,
					top : 100,
					left : 600,
					collapsible : false,
					minimizable : false,
					maximizable : false,
					closable : true,
					draggable : false,
					resizable : false,
					shadow : true,
					modal : true,
					closed : false
				});

				$('#resource-list')
						.tree(
								{
									url : 'setting?action=resources&resource_id=resources&role_id='
											+ row.id,
									lines : true,
									cascadeCheck : false,
									fit : true,
									loadFilter : function(data) {
										$('#cz').datagrid({
											checkOnSelect : true,
											rownumbers : true,
											pagination : true,
											striped : true,
											data : data,
											columns : [ [ {
												field : 'xxx',
												title : '选择',
												align : 'center',
												width : 20,
												checkbox : 'true'
											}, {
												field : 'name',
												title : '菜单名称',
												align : 'center',
												width : 200
											}, {
												field : 'url',
												title : 'URL',
												align : 'center',
												width : 200
											}, {
												field : '_operate',
												title : '操作',
												align : 'center',
												width : 100,
												formatter : 'compile'
											}, ] ]
										});
										return convert(data.data.all,
												data.data.own);
									},
									onClick : function(node) {
										if (!node.id) {
											return;
										}
										if (node) {
											var s = node.text;
											if (node.attributes) {
												s += "," + node.attributes.p1
														+ ","
														+ node.attributes.p2;
											}
											console.log(s);
										}
									},
									onCheck : function(node, checked) {
										if (checked) {
											var parentNode = $("#resource-list")
													.tree('getParent',
															node.target);
											if (parentNode != null) {
												$("#resource-list").tree(
														'check',
														parentNode.target);
											}
										} else {
											var childNode = $("#resource-list")
													.tree('getChildren',
															node.target);
											if (childNode.length > 0) {
												for (var i = 0; i < childNode.length; i++) {
													$("#resource-list")
															.tree(
																	'uncheck',
																	childNode[i].target);
												}
											}
										}
									},
									onLoadSuccess : function(node, data) {
										check(data);
									}
								});
				//check();
			}
		}

		function auth() {
			var row = $('#roles').datagrid('getSelected');
			if (row) {
				console.log($('#resource-list').tree('getChecked'));
				let resources = [];
				$('#resource-list').tree('getChecked').forEach(function(e) {
					resources.push(e.id);
				});
				if (resources.length == 0) {
					return;
				}
				$.ajax({
					url : "setting?action=auth&resource_id=auth&role_id="
							+ row.id,
					type : 'post',
					async : true,
					contentType : "application/json; charset=utf-8",
					data : JSON.stringify(resources),
					dataType : 'json',
					success : function(data) {
						$.messager.alert('信息', data.msg);
						if (data.suc == 0) {
							cancel("auth-win");
							refresh('setting?action=roles&resource_id=setRole');
						}
					}
				});
			}
		}

		function delRole() {
			$.messager
					.confirm(
							'删除角色',
							'你确定要删除角色?(删除角色会删除其与资源的关联、对应的用户设置为默认角色)',
							function(r) {
								if (r) {
									var row = $('#roles').datagrid(
											'getSelected');
									if (row) {
										$
												.ajax({
													url : "setting?action=delRole&resource_id=delRole&role_id="
															+ row.id,
													type : 'get',
													async : true,
													dataType : 'json',
													success : function(data) {
														$.messager.alert('信息',
																data.msg);
														refresh("setting?action=roles&resource_id=setRole");
													}
												});
									}
								}
							});

		}

		function showUpdateRolesForm() {
			var row = $('#roles').datagrid('getSelected');

			if (row) {
				var nodes = $("#update-role-form .needed");
				//console.log($(nodes[0]));
				$(nodes[0]).textbox('setValue', row.id);
				$(nodes[1]).textbox('setValue', row.name);
				$(nodes[2]).textbox('setValue', row.description);
				$(nodes[3]).val(row.available);
				$("#update-role-win").window({
					title : "添加角色",
					width : 300,
					height : 250,
					top : 100,
					left : 600,
					collapsible : false,
					minimizable : false,
					maximizable : false,
					closable : true,
					draggable : false,
					resizable : false,
					shadow : true,
					modal : true,
					closed : false
				});
			}
		}

		function updateRole() {
			var data = {};
			$("#update-role-form .needed").each(function() {
				data[$.nameget($(this))] = $(this).val();
			});
			$.ajax({
				url : "setting?action=updateRole&resource_id=updateRole",
				type : 'post',
				async : true,
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(data),
				dataType : 'json',
				success : function(data) {
					$.messager.alert('信息', data.msg);
					if (data.suc == 0) {
						cancel("update-role-win");
						refresh("setting?action=roles&resource_id=setRole");
					}
				}
			});
		}

		function showAddForm() {
			$("#add-role-win").window({
				title : "添加角色",
				width : 300,
				height : 150,
				top : 100,
				left : 600,
				collapsible : false,
				minimizable : false,
				maximizable : false,
				closable : true,
				draggable : false,
				resizable : false,
				shadow : true,
				modal : true,
				closed : false
			});
		}
		function submitForm() {
			var data = {};
			console.log($("#add-role-form input.needed"));
			$("#add-role-form input.needed").each(function() {
				data[$.nameget($(this))] = $(this).val();
			});
			$.ajax({
				url : "setting?action=addRole&resource_id=addRole",
				type : 'post',
				async : true,
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(data),
				dataType : 'json',
				success : function(data) {
					$.messager.alert('信息', data.msg);
					if (data.suc == 0) {
						cancel("add-role-win");
						refresh("setting?action=roles&resource_id=setRole");
					}
				}
			});
		}

		function cancel(id) {
			$("#" + id).window({
				closed : true
			});
		}
		function doSearch() {
			var key = $('#search-keys').combobox('getValue');
			var word = $('#key-word').val();
			if (key && word){
				refresh("setting?action=search&subject=roles&key="+key+"&word="+word+"&resource_id=search");
			}
		}

		function setRole() {
			var row = $('#users').datagrid('getSelected');
			var selected_role = $('#roles').combobox('getValue');
			if (row && selected_role) {
				$.ajax({
					url : "setting?action=setRole&user_id=" + row.userid
							+ "&role_id=" + selected_role
							+ "&resource_id=setRole",
					type : 'get',
					async : true,
					dataType : 'json',
					success : function(data) {
						$.messager.alert('信息', data.msg);
					}
				});
			}
		}

		function refresh(url) {
			$('#roles').datagrid({
				url : url,
				type : 'get',
				loadFilter : function(data) {
					return data.data;
				}
			});
		}
		$(function() {
			$('#roles').datagrid({
				url : "setting?action=roles&resource_id=setRole",
				title : '角色信息查询结果', //表格标题  	
				columns : [ [ {
					field : 'id',
					title : '角色标识符',
					width : 100
				}, {
					field : 'name',
					title : '角色名字',
					width : 100
				}, {
					field : 'description',
					title : '角色描述',
					width : 100
				}, {
					field : 'available',
					title : '是否可用',
					width : 100
				}, ] ],
				loadFilter : function(data) {
					return data.data;
				}
			});
			console.log($('#search-keys'));
			$('#search-keys').combobox({
				valueField : 'field',
				textField : 'title',
				panelHeight : 'auto',
				data : $('#roles').datagrid('options').columns[0]
			});
			console.log($('#search-keys'));
			//refresh();
			$.ajax({
				url : "init?comp=page_menu&id=role",
				type : 'get',
				async : true,
				dataType : 'json',
				success : function(data) {
					var funcs = data.data;
					//console.log(funcs);
					for (var i = 0; i < funcs.length; ++i) {
						var node = $("#" + funcs[i].id);
						//console.log(node);
						if (node) {
							node.css("display", "block");
						}
					}
				}
			});
		})
	</script>
</body>
</html>