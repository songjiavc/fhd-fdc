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
		// Public global variables 各个js会用到,比如fhdsidebar.js
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
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/Sidebar.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/Toolbar.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jseditor/Dialogs.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/mxgraph-1.10/grapheditor/jscolor/jscolor.js"></script>
</head>
<body class="geEditor">
	<script type="text/javascript">
		// Extends EditorUi to update I/O action states
		(function()
		{
			//如果是IE10，让键盘按下事件和右键事件失效
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
			//覆盖双击事件，改为查看明细
			mxGraph.prototype.dblClick = function(evt, cell)
			{
				var mxe = new mxEventObject(mxEvent.DOUBLE_CLICK, 'event', evt, 'cell', cell);
				this.fireEvent(mxe);
				if(cell){
					console.log(cell.getAttribute('link',null));
					if(cell.getAttribute('link',null)){
						onItemDbClick(cell.getAttribute('link',null),cell.getAttribute('label',null));
					}else{
						//查看明细
						var id = cell.id;
						var type = id.substring(id.indexOf('@')+1,id.length);
						id = id.substring(0,id.indexOf('@'));
						viewObjectDetail(id,type);
					}
					
				}
			};
			
			//覆盖右键菜单
			Menus.prototype.createPopupMenu = function(menu, cell, evt){
				var graph = this.editorUi.editor.graph;
				menu.smartSeparators = true;
				if (graph.getSelectionCount() == 1)		//画布中图形的个数
				{
					var id = graph.getSelectionCell().id;
					var type = id.substring(id.indexOf('@')+1,id.length);
					if(type!="endpoint" && type!="startpoint" && type!="line"){
						this.addMenuItems(menu, ['viewDetail']);
					}
				}else{
					this.addMenuItems(menu, ['export']);//导出图品
					//this.addMenuItems(menu, ['edit']);
				}
			};
			
			//创建主面板。默认面板是编辑的面板，下面3段话复写面板的创建过程，只显示图形，不可编辑
			EditorUi.prototype.createUi = function(){
				this.container.appendChild(this.diagramContainer);
			};
			//创建对象
			EditorUi.prototype.createDivs = function(){
				this.diagramContainer = this.createDiv('geDiagramContainer');
				this.outlineContainer = this.createDiv('geOutlineContainer');
			};
			//设置主面板的位置
			EditorUi.prototype.refresh = function(){
				var w = this.container.clientWidth;
				var h = this.container.clientHeight;
				if (this.container == document.body)
				{
					w = document.body.clientWidth || document.documentElement.clientWidth;
					h = document.body.clientHeight || document.documentElement.clientHeight;
				}
				this.diagramContainer.style.right = '0px';
				this.diagramContainer.style.left = '0px';
				this.diagramContainer.style.top = '0px';
				this.diagramContainer.style.width = w+ 'px';
				this.diagramContainer.style.height = h + 'px';
				
			};
			//初始化编辑器
			var editorUiInit = EditorUi.prototype.init;
			EditorUi.prototype.init = function()
			{
				//继承原始的init函数操作
				editorUiInit.apply(this, arguments);
				
				//添加一些额外操作
				this.editor.graph.setGridEnabled(false);
				this.editor.graph.setCellsMovable(false);
				this.editor.graph.setCellsDeletable(false);
				this.editor.graph.setCellsEditable(false);
				this.editor.graph.setConnectable(false);
				this.editor.graph.setEdgeLabelsMovable(false);
				this.editor.graph.setCellsDisconnectable(false);
				this.editor.graph.setCellsResizable(false);
				
				this.editor.updateGraphComponents();
				var xml = "${graphContext}";
				if(xml){
					initData(this,xml);
				}else{
					initData(this,'<mxGraphModel grid="1" guides="1" tooltips="1" connect="1" fold="1" page="0" pageScale="1" pageWidth="826" pageHeight="1169"><root><mxCell id="0"/><mxCell id="1" parent="0"/></root></mxGraphModel>');
				}
			};
		})();
		new EditorUi(new Editor());
		
		//初始化画布数据
		function initData(editorUi,xml){
			try{
				var doc = mxUtils.parseXml(xml);
				var model = new mxGraphModel();
				var codec = new mxCodec(doc);
				codec.decode(doc.documentElement, model);
				var children = model.getChildren(model.getChildAt(model.getRoot(), 0));
				editorUi.editor.graph.setSelectionCells(editorUi.editor.graph.importCells(children));
			}
			catch (e){
				mxUtils.alert(mxResources.get('invalidOrMissingFile') + ': ' + e.message);
			}
		}
		
		//用作界面导航时使用
		function onItemDbClick(url1,tabTitle){
			var url = url1;
			var temp = url.split('?');//url传参，以“？”分隔
			var typeId;
			if(temp.length>1){
				url = temp[0];
				typeId = temp[1];
			}
			
			var centerPanel = parent.Ext.getCmp('center-panel');
			var tab = centerPanel.getComponent(url);
			if(tab){
				if(typeId){//如果页面url相同但参数不同，创建新的tab
					var p = isSamePanelExists(centerPanel,menu,typeId,url);
					centerPanel.setActiveTab(p);
				}else{
					centerPanel.setActiveTab(tab);
				}
			}else{
				if(url.startWith('FHD')){
					var p = centerPanel.add(parent.Ext.create(url,{
						itemId:url,
						title: tabTitle,
						tabTip:tabTitle,
						typeId:typeId,
						closable:true,
						destroy:function (){//销毁tabpanel
                                if(this.fireEvent("destroy",this)!=false){
                                    this.el.remove();
                                    p = null;
                                    if(parent.Ext.isIE){
                                        CollectGarbage();
                                    }
                             }
						}
					}));
					centerPanel.setActiveTab(p);
				} else if (url.startWith('/pages')){
					var p = centerPanel.add({
						itemId:url,
						title: tabTitle,
						tabTip:tabTitle,
						layout:'fit',
						autoWidth:true,
						border:false,
						//iconCls: 'tabs',
						closable:true,
						autoLoad :{ url: __ctxPath+url,scripts: true}
					});
					centerPanel.setActiveTab(p);
				}else{
					var p = centerPanel.add({
						itemId:url,
						title: tabTitle,
						tabTip:tabTitle,
						layout:'fit',
						autoWidth:true,
						border:false,
						closable:true,
						html : '<iframe width=\'100%\' height=\'100%\' frameborder=\'0\' src=\''+__ctxPath+url+'\'></iframe>'
					});
					centerPanel.setActiveTab(p);
				}
			}
		}
		
		//遍历centerPanel，查找相同url相同typeId的panel，如果不存在，返回新创建的panel 
		function isSamePanelExists(centerPanel,tabTitle,typeId,url){
			var isSamePanel;//存在参数相同的panel
			for(var k in centerPanel.items.items){ 
				if(typeId == centerPanel.items.items[k].typeId){
					isSamePanel = centerPanel.items.items[k];
				}
			}
			if(!isSamePanel){
				var p = centerPanel.add(parent.Ext.create(url,{
					itemId:url+typeId,
					title: tabTitle,
					tabTip:tabTitle,
					typeId:typeId,
					closable:true,
					destroy:function (){//销毁tabpanel
                            if(this.fireEvent("destroy",this)!=false){
                                this.el.remove();
                                p = null;
                                if(parent.Ext.isIE){
                                    CollectGarbage();
                                }
                         }
					}
				}));
				centerPanel.setActiveTab(p);
			}else{
				return isSamePanel;
			}
		}
		String.prototype.startWith = function(s) {
			if (s == null || s == "" || this.length == 0 || s.length > this.length)
				return false;
			if (this.substr(0, s.length) == s)
				return true;
			else
				return false;
			return true;
		}
		function viewObjectDetail(id,type){
			var graphView = parent.Ext.create('FHD.view.process.graph.GraphViewDetail');
			graphView.initParam({id:id,type:type});
			graphView.reloadData();
		}	
	
	</script>
</body>
</html>
