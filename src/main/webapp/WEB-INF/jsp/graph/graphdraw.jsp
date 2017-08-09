<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/include-tagsOnly.jsp"%>

<html>
<head>
    <title>Graph</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/scripts/mxgraph-1.10/grapheditor/styles/grapheditor.css"/>
	<style>
	a:link, a:visited {
	    color:#15498b;
	    text-decoration: none;
	}
	a:hover {
	    color:#ff9900;
	    text-decoration: none;
	}
	</style>
	<script type="text/javascript">
		// Public global variables
		var MAX_REQUEST_SIZE = 10485760;
		var MAX_WIDTH = 6000;
		var MAX_HEIGHT = 6000;
		// URLs for save and export
		var EXPORT_URL = '${ctx}/graph/imageExport.f';
		var SAVE_URL = '${ctx}/scripts/mxgraph-1.10/grapheditor/save';
		var OPEN_URL = '${ctx}/scripts/mxgraph-1.10/grapheditor/open';
		var RESOURCES_PATH = '${ctx}/scripts/mxgraph-1.10/grapheditor/resources';
		var RESOURCE_BASE = RESOURCES_PATH + '/grapheditor';
		var STENCIL_PATH = '${ctx}/scripts/mxgraph-1.10/grapheditor/stencils';
		var IMAGE_PATH = '${ctx}/scripts/mxgraph-1.10/grapheditor/images';
		var STYLE_PATH = '${ctx}/scripts/mxgraph-1.10/grapheditor/styles';
		var CSS_PATH = '${ctx}/scripts/mxgraph-1.10/grapheditor/styles';
		var OPEN_FORM = '';//'${ctx}/scripts/mxgraph-1.10/grapheditor/open.html';
	
		// Specifies connection mode for touch devices (at least one should be true)
		var tapAndHoldStartsConnection = true;
		var showConnectorImg = true;

		// Parses URL parameters. Supported parameters are:
		// - lang=xy: Specifies the language of the user interface.
		// - touch=1: Enables a touch-style user interface.
		// - storage=local: Enables HTML5 local storage.
		
		//解析url,讲参数转换成json格式存在uroParams变量中
		var urlParams = (function(url)
		{
			var result = new Object();
			var idx = url.lastIndexOf('?');
	
			if (idx > 0)
			{
				var params = url.substring(idx + 1).split('&');
				
				for (var i = 0; i < params.length; i++)
				{
					idx = params[i].indexOf('=');
					
					if (idx > 0)
					{
						result[params[i].substring(0, idx)] = params[i].substring(idx + 1);
					}
				}
			}
			
			return result;
		})(window.location.href);

		// Sets the base path, the UI language via URL param and configures the
		// supported languages to avoid 404s. The loading of all core language
		// resources is disabled as all required resources are in grapheditor.
		// properties. Note that in this example the loading of two resource
		// files (the special bundle and the default bundle) is disabled to
		// save a GET request. This requires that all resources be present in
		// each properties file since only one file is loaded.
		mxLoadResources = false;
		mxBasePath = '${ctx}/scripts/mxgraph-1.10/';
		mxLanguage = urlParams['lang'];
		mxLanguages = ['de'];
		
	</script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/js/mxClient.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/Editor.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/Graph.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/Shapes.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/EditorUi.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/Actions.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/Menus.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/FHDSidebar.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/Toolbar.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/Dialogs.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jscolor/jscolor.js"></script>
</head>
<body class="geEditor">
	<script type="text/javascript">
		// Extends EditorUi to update I/O action states
		(function()
		{
			if(parent.Ext.isIE10){
				document.onkeydown = function() {  
				    if (event.keyCode == 116) {  
				        event.keyCode = 0;  
				        event.returnValue = false;  
				    }  
				}  
				document.oncontextmenu = function() {  
				    event.returnValue = false;  
				} 
			}
			/**
			 * Specifies the position of the horizontal split bar. Default is 190.
			 */
			EditorUi.prototype.hsplitPosition = 90;
			// Defines an icon for creating new connections in the connection handler.
			// This will automatically disable the highlighting of the source vertex.
			mxConnectionHandler.prototype.connectImage = new mxImage(IMAGE_PATH+'/connector.gif', 16, 16);
			/**
			 * Function: clone
			 *
			 * Returns a clone of the cell. Uses <cloneValue> to clone
			 * the user object. All fields in <mxTransient> are ignored
			 * during the cloning.
			 */
			mxCell.prototype.clone = function()
			{
				var clone = mxUtils.clone(this, this.mxTransient);
				clone.setValue(this.cloneValue());
				//添加使ID不发生变化，为查看明细做准备
				clone.setId(this.getId());
				return clone;
			};
			
			/**
			 * Creates a drop handler for inserting the given cells.
			 */
			Sidebar.prototype.createDropHandler = function(cells, allowSplit)
			{
				return function(graph, evt, target, x, y)
				{
					cells = graph.getImportableCells(cells);
					if (cells.length > 0)
					{
						var validDropTarget = (target != null) ?
							graph.isValidDropTarget(target, cells, evt) : false;
						var select = null;
						
						if (target != null && !validDropTarget)
						{
							target = null;
						}
						
						// Splits the target edge or inserts into target group
						if (allowSplit && graph.isSplitEnabled() && graph.isSplitTarget(target, cells, evt))
						{
							graph.splitEdge(target, cells, null, x, y);
							select = cells;
						}
						else if (cells.length > 0)
						{
							var icon_user = 'image;image=' + STENCIL_PATH + '/images/icon_user.png';
							var icon_kpi = 'image;image=' + STENCIL_PATH + '/images/icon_kpi.png';
							var icon_risk = 'image;image=' + STENCIL_PATH + '/images/icon_risk.png';
							var icon_control = 'image;image=' + STENCIL_PATH + '/images/icon_control.png';
							var icon_document = 'image;image=' + STENCIL_PATH + '/images/icon_document.png';
							
							if(cells[0].getValue()=='流程'){
								parent.Ext.create('FHD.ux.process.ProcessSelectorWindow', {
									extraParams : {smIconType:'display',canChecked:true},
									single : true,
									parent:true,
									onSubmit : function(values) {
										values.each(function(value) {
											var cellValue = cells[0].getValue();
											var cellStyle = cells[0].getStyle();
											parent.FHD.ajax({
												url: '${ctx}/process/graph/showstatus.f',
												params: {id:value.data.id},
												callback: function(data){
													if(data.sucess){
														parent.FHD.notification('操作成功!','提示');
														cells[0].setId(value.data.id+'@process');
														cells[0].valueChanged(value.data.text);
														if(data.riskLevel=='green'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/flow_green.png');
														}else if(data.riskLevel=='red'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/flow_red.png');
														}else if(data.riskLevel=='yellow'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/flow_yellow.png');
														}
														select = graph.importCells(cells, x, y, target);
														cells[0].setValue(cellValue);
														cells[0].setStyle(cellStyle);
													}else{
														parent.Ext.MessageBox.alert('提示', '操作失败');
													}
												}
											}); 
										});
									}
								}).show();
							}else if(cells[0].getValue()=='组织'){
								parent.Ext.create('FHD.ux.org.DeptSelectorWindow',{
									multiSelect:false,
			        				subCompany: false,
			        				companyOnly: false,
			        				rootVisible: true,
									onSubmit:function(win){
										win.selectedgrid.store.each(function(value) {
											var cellValue = cells[0].getValue();
											var cellStyle = cells[0].getStyle();
											parent.FHD.ajax({
												url: '${ctx}/organization/graph/showstatus.f',
												params: {id:value.data.id},
												callback: function(data){
													if(data.sucess){
														parent.FHD.notification('操作成功!','提示');
														cells[0].setId(value.data.id+'@org');
														cells[0].valueChanged(value.data.deptname);
														if(data.riskLevel=='green'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/org_green.png');
														}else if(data.riskLevel=='red'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/org_red.png');
														}else if(data.riskLevel=='yellow'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/org_yellow.png');
														}
														select = graph.importCells(cells, x, y, target);
														cells[0].setValue(cellValue);
														cells[0].setStyle(cellStyle);
													}else{
														parent.Ext.MessageBox.alert('提示', '操作失败');
													}
												}
											}); 
										});
									}
								}).show();
							}else if(cells[0].getValue()=='目标'){
								parent.Ext.create('FHD.ux.kpi.KpiStrategyMapSelectorWindow',{
									extraParams : {
										smIconType : 'display',
										canChecked : true
									},
									single:true,
									//是否显示目标树
									smTreeVisible : true,
									//设置目标树图标
									mineSmTreeIcon : 'icon-orgsub',
									onSubmit:function(values){
										values.each(function(value) {
											var cellValue = cells[0].getValue();
											var cellStyle = cells[0].getStyle();
											parent.FHD.ajax({
												url: '${ctx}/smgraphdraw/graph/showstatus.f',
												params: {smId:value.data.id},
												callback: function(data){
													if(data.sucess){
														parent.FHD.notification('操作成功!','提示');
														cells[0].setId(value.data.id+'@sm');
														cells[0].valueChanged(value.data.text);
														if(data.riskLevel=='green'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/sm_green.png');
														}else if(data.riskLevel=='red'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/sm_red.png');
														}else if(data.riskLevel=='yellow'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/sm_yellow.png');
														}
														select = graph.importCells(cells, x, y, target);
														cells[0].setValue(cellValue);
														cells[0].setStyle(cellStyle);
													}else{
														parent.Ext.MessageBox.alert('提示', '操作失败');
													}
												}
											}); 
										});
									}
								}).show();
							}else if(cells[0].getValue()=='指标'){
								var kpiselectwindow = parent.Ext.create('FHD.ux.kpi.opt.KpiSelectorWindow',{
			                    	multiSelect:false,
			                    	onSubmit:function(store){
			                    		var items = store.data.items;
			                    		parent.Ext.Array.each(items,function(item){
			                    			var cellValue = cells[0].getValue();
			                    			var cellStyle = cells[0].getStyle();
			                    			parent.FHD.ajax({
												url: '${ctx}/kpigraphdraw/graph/showstatus.f',
												params: {kpiId:item.data.id},
												callback: function(data){
													if(data.sucess){
														parent.FHD.notification('操作成功!','提示');
														cells[0].setId(item.data.id+'@kpi');
														cells[0].valueChanged(item.data.name);
														if(data.riskLevel=='green'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/kpi_green.png');
														}else if(data.riskLevel=='red'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/kpi_red.png');
														}else if(data.riskLevel=='yellow'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/kpi_yellow.png');
														}
														select = graph.importCells(cells, x, y, target);
														cells[0].setValue(cellValue);
														cells[0].setStyle(cellStyle);
													}else{
														parent.Ext.MessageBox.alert('提示', '操作失败');
													}
												}
											}); 
			                        	});
			                    	}
			                    }).show();
								kpiselectwindow.addComponent();
							}else if(cells[0].getValue()=='风险'){
								parent.Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow',{
									multiSelect:false,
									modal: true,
									showLight: true,
									riskcatalogtreevisable:true,
									onSubmit:function(win){
										win.selectedgrid.store.each(function(value) {
											var cellValue = cells[0].getValue();
											var cellStyle = cells[0].getStyle();
											parent.FHD.ajax({
												url: '${ctx}/risk/graph/showstatus.f',
												params: {id:value.data.id},
												callback: function(data){
													if(data.sucess){
														parent.FHD.notification('操作成功!','提示');
														cells[0].setId(value.data.id+'@risk');
														cells[0].valueChanged(value.data.name);
														if(data.riskLevel=='green'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/risk_green.png');
														}else if(data.riskLevel=='red'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/risk_red.png');
														}else if(data.riskLevel=='yellow'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/risk_yellow.png');
														}
														select = graph.importCells(cells, x, y, target);
														cells[0].setValue(cellValue);
														cells[0].setStyle(cellStyle);
													}else{
														parent.Ext.MessageBox.alert('提示', '操作失败');
													}
												}
											}); 
										});
									}
								}).show();
							}else if(cells[0].getValue()=='人员'){
								parent.Ext.create('FHD.ux.org.EmpSelectorWindow',{
									multiSelect:false,
									subCompany: false,
									onSubmit:function(win){
										win.selectedgrid.store.each(function(value) {
											var cellValue = cells[0].getValue();
											cells[0].setId(value.data.id+'@emp');
											cells[0].valueChanged(value.data.empname);
											select = graph.importCells(cells, x, y, target);
											cells[0].setValue(cellValue);
										});
									}
								}).show();
							}else if(cells[0].getValue()=='记分卡'){
								parent.Ext.create('FHD.ux.kpi.ScorecardSelectorWindow',{
									single:true,
									extraParams : {
										smIconType : 'display',
										canChecked : true
									},
									categorytreevisable:true,
//									//设置指标树图标
									onSubmit:function(values){
										values.each(function(value) {
											var cellValue = cells[0].getValue();
											var cellStyle = cells[0].getStyle();
											parent.FHD.ajax({
												url: '${ctx}/scgraphdraw/graph/showstatus.f',
												params: {scId:value.data.id},
												callback: function(data){
													if(data.sucess){
														parent.FHD.notification('操作成功!','提示');
														cells[0].setId(value.data.id+'@sc');
														cells[0].valueChanged(value.data.text);
														if(data.riskLevel=='green'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/sc_green.png');
														}else if(data.riskLevel=='red'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/sc_red.png');
														}else if(data.riskLevel=='yellow'){
															cells[0].setStyle('label;image=' + STENCIL_PATH + '/images/sc_yellow.png');
														}
														select = graph.importCells(cells, x, y, target);
														cells[0].setValue(cellValue);
														cells[0].setStyle(cellStyle);
													}else{
														parent.Ext.MessageBox.alert('提示', '操作失败');
													}
												}
											}); 
										});
									}
								}).show();
							}else if(cells[0].getValue()=='控制措施'){
								parent.Ext.create('FHD.ux.icm.control.MeasureSelectorWindow',{
									multiSelect:false,
									onSubmit:function(win){
										win.selectedGrid.store.each(function(value) {
											var cellValue = cells[0].getValue();
											cells[0].setId(value.data.measureId+'@controlMeasure');
											cells[0].valueChanged(value.data.measureName);
											select = graph.importCells(cells, x, y, target);
											cells[0].setValue(cellValue);
										});
									}
								}).show();
							}else if(cells[0].getValue()=='文档'){
								parent.Ext.create('FHD.view.sys.documentlib.documentWindow.DocumentSelectWindow',{
									multiSelect:false,
									modal: true,
									onSubmit:function(win){
										win.selectedgrid.store.each(function(value) {
											var cellValue = cells[0].getValue();
											cells[0].setId(value.data.id+'@document');
											cells[0].valueChanged(value.data.documentName);
											select = graph.importCells(cells, x, y, target);
											cells[0].setValue(cellValue);
										});
									}
								}).show();
							}//TODO 需要添加新的形状
							//TODO 在这里添加其他对象的支持
							else if(cells[0].getStyle()==icon_kpi){
								var kpiselectwindow = parent.Ext.create('FHD.ux.kpi.opt.KpiSelectorWindow',{
			                    	multiSelect:false,
			                    	onSubmit:function(store){
			                    		var items = store.data.items;
			                    		parent.Ext.Array.each(items,function(item){
			                    			var cellValue = cells[0].getValue();
			                    			var cellStyle = cells[0].getStyle();
			                    			parent.FHD.ajax({
												url: '${ctx}/kpigraphdraw/graph/showstatus.f',
												params: {kpiId:item.data.id},
												callback: function(data){
													if(data.sucess){
														parent.FHD.notification('操作成功!','提示');
														cells[0].setId(item.data.id+'@kpi');
														if(data.riskLevel=='green'){
															cells[0].setStyle('image;image=' + STENCIL_PATH + '/images/icon_kpi_green.png');
														}else if(data.riskLevel=='red'){
															cells[0].setStyle('image;image=' + STENCIL_PATH + '/images/icon_kpi_red.png');
														}else if(data.riskLevel=='yellow'){
															cells[0].setStyle('image;image=' + STENCIL_PATH + '/images/icon_kpi_yellow.png');
														}
														select = graph.importCells(cells, x, y, target);
														cells[0].setStyle(cellStyle);
													}else{
														parent.Ext.MessageBox.alert('提示', '操作失败');
													}
												}
											}); 
			                        	});
			                    	}
			                    }).show();
								kpiselectwindow.addComponent();
							}else if(cells[0].getStyle()==icon_risk){
								parent.Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow',{
									multiSelect:false,
									modal: true,
									onSubmit:function(win){
										win.selectedgrid.store.each(function(value) {
											var cellValue = cells[0].getValue();
											var cellStyle = cells[0].getStyle();
											parent.FHD.ajax({
												url: '${ctx}/risk/graph/showstatus.f',
												params: {id:value.data.id},
												callback: function(data){
													if(data.sucess){
														parent.FHD.notification('操作成功!','提示');
														cells[0].setId(value.data.id+'@risk');
														if(data.riskLevel=='green'){
															cells[0].setStyle('image;image=' + STENCIL_PATH + '/images/icon_risk_green.png');
														}else if(data.riskLevel=='red'){
															cells[0].setStyle('image;image=' + STENCIL_PATH + '/images/icon_risk_red.png');
														}else if(data.riskLevel=='yellow'){
															cells[0].setStyle('image;image=' + STENCIL_PATH + '/images/icon_risk_yellow.png');
														}
														select = graph.importCells(cells, x, y, target);
														cells[0].setStyle(cellStyle);
													}else{
														parent.Ext.MessageBox.alert('提示', '操作失败');
													}
												}
											}); 
										});
									}
								}).show();
							}else if(cells[0].getStyle()==icon_user){
								parent.Ext.create('FHD.ux.org.EmpSelectorWindow',{
									multiSelect:false,
									subCompany: false,
									onSubmit:function(win){
										win.selectedgrid.store.each(function(value) {
											var cellValue = cells[0].getValue();
											cells[0].setId(value.data.id+'@emp');
											cells[0].valueChanged(value.data.empname);
											select = graph.importCells(cells, x, y, target);
											cells[0].setValue(cellValue);
										});
									}
								}).show();
							}else if(cells[0].getStyle()==icon_control){
								parent.Ext.create('FHD.ux.icm.control.MeasureSelectorWindow',{
									multiSelect:false,
									onSubmit:function(win){
										win.selectedGrid.store.each(function(value) {
											var cellValue = cells[0].getValue();
											cells[0].setId(value.data.measureId+'@controlMeasure');
											select = graph.importCells(cells, x, y, target);
										});
									}
								}).show();
							}else if(cells[0].getStyle()==icon_document){
								parent.Ext.create('FHD.view.sys.documentlib.documentWindow.DocumentSelectWindow',{
									multiSelect:false,
									modal: true,
									onSubmit:function(win){
										win.selectedgrid.store.each(function(value) {
											var cellValue = cells[0].getValue();
											cells[0].setId(value.data.id+'@document');
											select = graph.importCells(cells, x, y, target);
										});
									}
								}).show();
							}
							else if(cells[0].getValue() && cells[0].getValue().indexOf("风险") != -1){
								parent.Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow',{
									multiSelect:true,
									modal: true,
									onSubmit:function(win){
										var riskIds = new Array(),id = new Array();
										win.selectedgrid.store.each(function(value) {
											riskIds.push(value.data.id);
											id.push(value.data.id + "@risk");
										});
										var cellValue = cells[0].getValue();
										var cellStyle = cells[0].getStyle();
										cells[0].setId(id.join('|'));
										parent.FHD.ajax({
											url: '${ctx}/risk/graph/showstatusbatch.f',
											params: {riskIds:riskIds.join(',')},
											callback: function(data){
												if(data.sucess){
													parent.FHD.notification('操作成功!','提示');
													var value = '<table cellpadding="5" style="font-size:9pt;border:none;border-collapse:collapse;width:100%;">' +
									    			'<tr><td colspan="2" style="border:1px solid gray;background:#e4e4e4;">风险</td></tr>';
													
													parent.Ext.Array.each(data.datas, function(name, index, countriesItSelf) {
													    value += '<tr><td style="border:1px solid gray;width:15px;">' +
														'<div style="display: inline-block;font-size:20pt;text-align:center;color:' +
														name.riskLevel +
														';text-decoration:none">●</div>' +
														'</td><td style="border:1px solid gray;"><a href="#" onclick="viewObjectDetail(\''+name.id+'\',\'risk\')">' +
														name.name + 
														'</a></td></tr>';
													});
													value += '</table>';
													cells[0].setValue(value);
													select = graph.importCells(cells, x, y, target);
													cells[0].setValue(cellValue); 
													/* cells[0].setId(value.data.id+'@risk');
													if(data.riskLevel=='green'){
														cells[0].setStyle('image;image=' + STENCIL_PATH + '/images/icon_risk_green.png');
													}else if(data.riskLevel=='red'){
														cells[0].setStyle('image;image=' + STENCIL_PATH + '/images/icon_risk_red.png');
													}else if(data.riskLevel=='yellow'){
														cells[0].setStyle('image;image=' + STENCIL_PATH + '/images/icon_risk_yellow.png');
													}
													
													select = graph.importCells(cells, x, y, target);
													cells[0].setStyle(cellStyle); */
												}else{
													parent.Ext.MessageBox.alert('提示', '操作失败');
												}
											}
										});
									}
								}).show(); 
							}
							//TODO 需要添加新的形状
							//TODO 在这里添加其他对象的支持
							else{
								select = graph.importCells(cells, x, y, target);
							}
						}
						
						if (select != null && select.length > 0)
						{
							graph.scrollCellToVisible(select[0]);
							graph.setSelectionCells(select);
						}
					}
				};
			};
		
			//菜单栏添加自定义的保存和刷新按钮
			var actionsInit = Actions.prototype.init;
			Actions.prototype.init = function(){
				actionsInit.apply(this, arguments);
				var editorUi = this.editorUi;
				var graphId = "${graphId}";
				var graphName = "${graphName}";
				//添加保存的方法
				this.addAction('save', function() { 
					saveGraph(editorUi,graphId,graphName);
				}, null, null,"Ctrl+S");
				this.addAction('refresh', function() { 
					var url = "${ctx}/comm/graph/refreshgraph.f";
					var data = {
						graphId:graphId
					}
					parent.Ext.MessageBox.show({
		    			title : '确认',
		    			width : 260,
		    			msg : '您确认已经保存当前的修改并更新状态吗？已保存，请点“是”，否则，请点“否”。',
		    			buttons : parent.Ext.MessageBox.YESNO,
		    			icon : parent.Ext.MessageBox.QUESTION,
		    			fn : function(btn) {
							if (btn == 'yes') {//确认
								parent.FHD.ajax({
									url: url,
									params: data,
									callback: function(data){
										if(data){
											parent.FHD.notification('操作成功!','提示');
											var parentPanel = parent.Ext.getCmp('${panelId}');//程序约定的
											if(parentPanel){
												parentPanel.reloadData();
											}
										}else{
											parent.Ext.MessageBox.alert('提示', '操作失败');
										}
									}
								}); 
							}else{
								saveGraph(editorUi,graphId,graphName);
							}
						}
					});	
				}, null, null);
				/* this.addAction('preview', function() {
					//如果通过流程ID或者图形关联流程ID获得图的预览
					if(("${graphRelaProcessId}"!=null || "${processId}"!=null) || ("${graphRelaProcessId}"!="" || "${processId}"!="")){
						window.location.href="${ctx}/comm/graph/findGraphRelaProcess.f?viewType=graphdrawview&processId=${processId}&graphRelaProcessId=${graphRelaProcessId}";
					}else if("${graphRelaRiskId}"!=null || "${riskId}"!=null || "${graphRelaRiskId}"!="" || "${riskId}"!=""){
						window.location.href="${ctx}/comm/graph/findGraphRelaRisk.f?viewType=graphdrawview&riskId=${riskId}&graphRelaRiskId=${graphRelaRiskId}";
					}else if("${graphRelaKpiId}"!=null || "${kpiId}"!=null || "${graphRelaKpiId}"!="" || "${kpiId}"!=""){
						window.location.href="${ctx}/comm/graph/findGraphRelaKpi.f?viewType=graphdrawview&kpiId=${kpiId}&graphRelaKpiId=${graphRelaKpiId}";
					}else if("${graphRelaStrategyMapId}"!=null || "${strategyMapId}"!=null || "${graphRelaStrategyMapId}"!="" || "${strategyMapId}"!=""){
						window.location.href="${ctx}/comm/graph/findGraphRelaStrategyMap.f?viewType=graphdrawview&strategyMapId=${strategyMapId}&graphRelaStrategyMapId=${graphRelaStrategyMapId}";
					}else if("${graphRelaCategoryId}"!=null || "${categoryId}"!=null || "${graphRelaCategoryId}"!="" || "${categoryId}"!=""){
						window.location.href="${ctx}/comm/graph/findGraphRelaCategory.f?viewType=graphdrawview&categoryId=${categoryId}&graphRelaCategoryId=${graphRelaCategoryId}";
					}else if("${graphRelaOrgId}"!=null || "${oryId}"!=null || "${graphRelaOrgId}"!="" || "${oryId}"!=""){
						window.location.href="${ctx}/comm/graph/findGraphRelaOrg.f?viewType=graphdrawview&oryId=${oryId}&graphRelaOrgId=${graphRelaOrgId}";
					}//TODO 在这里添加其他对象的支持
					else{
						window.location.href="${ctx}/comm/graph/findGraph.f?viewType=graphdrawview&id="+graphId;
					}
					
				}, null, null); */
			}
			
			var editorUiInit = EditorUi.prototype.init;
			EditorUi.prototype.init = function() {
				editorUiInit.apply(this, arguments);
				this.editor.graph.setEdgeLabelsMovable(false);
				this.editor.graph.setCellsCloneable(false);
				var xml = "${graphContext}";
				if (xml) {
					initData(this, xml);
				} else {
					initData(
							this,
							'<mxGraphModel grid="1" guides="1" tooltips="1" connect="1" fold="1" page="0" pageScale="1" pageWidth="826" pageHeight="1169"><root><mxCell id="0"/><mxCell id="1" parent="0"/></root></mxGraphModel>');
				}
			};
			
			
			
		})();
		new EditorUi(new Editor());
		function initData(editorUi, xml) {
			try {
				var doc = mxUtils.parseXml(xml);
				var model = new mxGraphModel();
				var codec = new mxCodec(doc);
				codec.decode(doc.documentElement, model);
				var children = model.getChildren(model.getChildAt(model
						.getRoot(), 0));
				editorUi.editor.graph.setSelectionCells(editorUi.editor.graph
						.importCells(children));
			} catch (e) {
				mxUtils.alert(mxResources.get('invalidOrMissingFile') + ': '
						+ e.message);
			}
		}
		
		//保存图形的处理函数
		function saveGraph(editorUi,graphId,graphName){
			var xml = encodeURI(mxUtils.getPrettyXml(editorUi.editor.getGraphXml()));
			parent.Ext.MessageBox.prompt('保存', '请输入图形名称:', 
				function(btn, text){
					if("ok" == btn){
						var url,data;
						//如果流程ID不为空，保存流程相关的图
						if("${processId}" != null && "${processId}" != ""){
							url = "${ctx}/comm/graph/saveGraphRelaProcess.f";
							data = {
								processId:'${processId}',
								graphId:graphId,
								graphName:text,
								graphContext:xml
							}
						}else if("${riskId}" != null && "${riskId}" != ""){
							url = "${ctx}/comm/graph/saveGraphRelaRisk.f";
							data = {
								riskId:'${riskId}',
								graphId:graphId,
								graphName:text,
								graphContext:xml
							}
						}else if("${kpiId}" != null && "${kpiId}" != ""){
							url = "${ctx}/comm/graph/saveGraphRelaKpi.f";
							data = {
								kpiId:'${kpiId}',
								graphId:graphId,
								graphName:text,
								graphContext:xml
							}
						}else if("${strategyMapId}" != null && "${strategyMapId}" != ""){
							url = "${ctx}/comm/graph/saveGraphRelaStrategyMap.f";
							data = {
								strategyMapId:'${strategyMapId}',
								graphId:graphId,
								graphName:text,
								graphContext:xml
							}
						}else if("${categoryId}" != null && "${categoryId}" != ""){
							url = "${ctx}/comm/graph/saveGraphRelaCategory.f";
							data = {
								categoryId:'${categoryId}',
								graphId:graphId,
								graphName:text,
								graphContext:xml
							}
						}else if("${orgId}" != null && "${orgId}" != ""){
							url = "${ctx}/comm/graph/saveGraphRelaOrg.f";
							data = {
								orgId:'${orgId}',
								graphId:graphId,
								graphName:text,
								graphContext:xml
							}
						}//TODO 在这里添加其他对象的支持
						else{
							url = "${ctx}/comm/graph/mergeGraph.f";
							data = {
								graphId:graphId,
								graphName:text,
								graphContext:xml
							}
							parent.FHD.ajax({
								url: url,
								params: data,
								callback: function(data){
									if(data.success){
										parent.FHD.notification('操作成功!','提示');
										var parentPanel = parent.Ext.getCmp('${panelId}');//程序约定的
										if(parentPanel){
											parentPanel.extraParams = {graphId : data.id};
											parentPanel.reloadData();
										}
									}else{
										parent.Ext.MessageBox.alert('提示', '操作失败');
									}
								}
							}); 
							return;
						}
						parent.FHD.ajax({
							url: url,
							params: data,
							callback: function(data){
								if(data){
									parent.FHD.notification('操作成功!','提示');
									var parentPanel = parent.Ext.getCmp('${panelId}');//程序约定的
									if(parentPanel){
										parentPanel.reloadData();
									}
								}else{
									parent.Ext.MessageBox.alert('提示', '操作失败');
								}
							}
						}); 
					}
			    },
			    this,
			    false,
			    graphName
			);
		}
	</script>
</body>
</html>
