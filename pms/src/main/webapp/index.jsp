<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="WEB-INF/pages/setting/user.jsp"%>

<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="plug-ins/easyui/css/default/easyui.css" />
<link rel="stylesheet" href="plug-ins/easyui/css/icon.css" />
<link rel="stylesheet" href="plug-ins/index/css/style.css" />
<title>物业管理系统</title>
</head>
<body class="easyui-layout">

	<div data-options="region:'north'"
		style="height: 80px; background-image: url('plug-ins/index/images/top_bg.jpg' ) no-repeat right center; float: right; BACKGROUND: #A8D7E9; padding: 1px; overflow: hidden;">
		<img alt="logo" src="plug-ins/index/images/head.png" />
	</div>
	<div data-options="region:'west',title:'功能菜单',collapsible:false"
		style="width: 220px">
		<ul id="menu"></ul>
	</div>
	<div
		data-options="region:'east',collapsible:'true',title:'工具',collapsed:true"
		style="width: 220px">
		<div class="easyui-calendar" style="width: 100%;"></div>
	</div>
	<div data-options="region:'center'" style='border-width: 0px;'>
		<div id="tabs" class="easyui-tabs" style="width: 100%; height: 100%;">
		</div>
	</div>


	<script src="plug-ins/jquery-3.3.1.js"></script>
	<script type="text/javascript"
		src="plug-ins/easyui/js/jquery.easyui.min.js"></script>
	<script type="text/javascript"
		src="plug-ins/easyui/js/easyui-lang-zh_CN.js"></script>
	<script type="text/javascript"
		src="plug-ins/index/js/easyui-extends.js"></script>
	<script type="text/javascript">
		var tabs;
		var funcs;

		function convert_procedure(rows, fid, children) {
			for (var i = 0; i < rows.length;) {
				var row = rows[i];
				if (row.fid == fid) {
					var node = {
						id : row.id,
						text : row.name,
						url : row.url,
						fid: row.fid
					};
					children.push(node);
					rows.splice(i, 1);
					if (!node.children) {
						node.children = [];
					}
					convert_procedure(rows, row.id, node.children);
				} else {
					++i;
				}
			}

		}

		function convert(rows) {
			function children(arr, parentId) {
				var temp = [];
				for (var i = 0; i < arr.length;) {
					var row = arr[i];
					console.log(arr.length);
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
					fid: row.fid
				};
				roots.push(node);
				//console.log(node);
				if (!node.children) {
					node.children = [];
				}
				convert_procedure(swap, node.id, node.children);
			}
			return roots;
		}

		$(function() {
			$('ul.tabs').html('');
			$('#menu').tree(
					{ 
						url : 'init?comp=tree_menu',
						lines : true,
						cascadeCheck : false,
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
							console.log(data.data.resources);
							console.log(data.resources);
							return convert(data.data);
						},
						onClick : function(node) {
							if (!node.id){
								return;
							}
							addPanel(node.text,node.id+".action"+"?menu="+node.fid)
							if (node) {
								var s = node.text;
								if (node.attributes) {
									s += "," + node.attributes.p1 + ","
											+ node.attributes.p2;
								}
								console.log(s);
							}
						}
					});

			function addPanel(name, url) {

				var dd = $('#tabs').tabs('exists', name);
				if (dd) {
					$('#tabs').tabs('select', name);
				} else {
					var content = '<iframe scrolling="auto" frameborder="0"  src="'
							+ url
							+ '" style="width:100%;height:100%;"></iframe>';
					$('#tabs').tabs('add', {
						id : name,
						title : name,
						content : content,
						closable : true
					});
				}
			}

			function removeWindow() {
				$(".panel.window").remove();
				$(".window-shadow").remove();
				$(".window-mask").remove();
			}

			Date.prototype.Format = function(fmt) { //author: meizz 
				var o = {
					"M+" : this.getMonth() + 1, //月份 
					"d+" : this.getDate(), //日 
					"h+" : this.getHours(), //小时 
					"m+" : this.getMinutes(), //分 
					"s+" : this.getSeconds(), //秒 
					"q+" : Math.floor((this.getMonth() + 3) / 3),
					"S" : this.getMilliseconds()
				//毫秒 
				};
				if (/(y+)/.test(fmt))
					fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
							.substr(4 - RegExp.$1.length));
				for ( var k in o)
					if (new RegExp("(" + k + ")").test(fmt))
						fmt = fmt.replace(RegExp.$1,
								(RegExp.$1.length == 1) ? (o[k])
										: (("00" + o[k])
												.substr(("" + o[k]).length)));
				return fmt;
			}
		})
	</script>
</body>
</html>