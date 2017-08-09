<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/include-tagsOnly.jsp"%>

<html>
<head>
    <title>Graph</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/scripts/mxgraph-1.10/grapheditor/styles/grapheditor.css"/>
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
			
			var actionsInit = Actions.prototype.init;
			Actions.prototype.init = function(){
				actionsInit.apply(this, arguments);
				var editorUi = this.editorUi;
				//添加保存的方法
				this.addAction('save', function() { 
					var xml = encodeURI(mxUtils.getPrettyXml(editorUi.editor.getGraphXml()));
					
					var graph = editorUi.editor.graph;
					var bounds = graph.getGraphBounds();
					var scale = graph.view.scale;
					var width = Math.ceil(bounds.width / scale);
					var height = Math.ceil(bounds.height / scale);
					
					var widthInput = document.createElement('input');
					widthInput.setAttribute('value', width);
					widthInput.style.width = '180px';

					var heightInput = document.createElement('input');
					heightInput.setAttribute('value', height);
					heightInput.style.width = '180px';
					
					var borderInput = document.createElement('input');
					borderInput.setAttribute('value', width);
					borderInput.style.width = '180px';
					borderInput.value = '0';
					
					
					function checkValues()
					{
						if (widthInput.value > MAX_WIDTH || widthInput.value < 0)
						{
							widthInput.style.backgroundColor = 'red';
						}
						else
						{
							widthInput.style.backgroundColor = '';
						}
						
						if (heightInput.value > MAX_HEIGHT || heightInput.value < 0)
						{
							heightInput.style.backgroundColor = 'red';
						}
						else
						{
							heightInput.style.backgroundColor = '';
						}
					};

					mxEvent.addListener(widthInput, 'change', function()
					{
						if (width > 0)
						{
							heightInput.value = Math.ceil(parseInt(widthInput.value) * height / width);
						}
						else
						{
							heightInput.value = '0';
						}
						
						checkValues();
					});

					mxEvent.addListener(heightInput, 'change', function()
					{
						if (height > 0)
						{
							widthInput.value = Math.ceil(parseInt(heightInput.value) * width / height);
						}
						else
						{
							widthInput.value = '0';
						}
						
						checkValues();
					});

					// Reusable image export instance
					var imgExport = new mxImageExport();
					
					
					var border = Math.max(0, parseInt(borderInput.value)) + 1;
					var scale = parseInt(widthInput.value) / width;
					var bounds = graph.getGraphBounds();
					
		        	// New image export
					var xmlDoc = mxUtils.createXmlDocument();
					var root = xmlDoc.createElement('output');
					xmlDoc.appendChild(root);
					var xmlCanvas = new mxXmlCanvas2D(root);
					
					// Render graph
					xmlCanvas.scale(scale);
					xmlCanvas.translate(Math.floor(-bounds.x * scale) + border, Math.floor(-bounds.y * scale) + border);
					imgExport.drawState(graph.getView().getState(graph.model.root), xmlCanvas);
		
					// Puts request data together
					var w = Math.ceil(bounds.width * scale) + 2 * border;
					var h = Math.ceil(bounds.height * scale) + 2 * border;
					var xmlImage = mxUtils.getXml(root);
					
					// Requests image if request is valid
					if (xmlImage.length <= MAX_REQUEST_SIZE && width < MAX_WIDTH && width > 0 &&
						height < MAX_HEIGHT && height > 0)
					{
						var bg = graph.background || '#ffffff';
						parent.FHD.ajax({
							url: "${ctx}/graph/mergeprocessgraph.f",
							params: {
								processId:'${processId}',
								graphName:'${graphName}',
								graphContext:xml,
								format:'png',
								bg:bg,
								w:w,
								h:h,
								plain:encodeURIComponent(xmlImage)
							},
							callback: function(data){
								if(data){
									parent.FHD.notification('操作成功!','提示');
								}else{
									parent.Ext.MessageBox.alert('提示', '操作失败');
								}
							}
						}); 
					}
					else
					{
						mxUtils.alert(mxResources.get('drawingTooLarge'));
					}
					
					
				}, null, null,"Ctrl+S");
				this.addAction('rebuild', function() { 
					parent.FHD.ajax({
						url: "${ctx}/graph/refreshprocessgraph.f",
						params: {processId:'${processId}'},
						callback: function(data){
							if(data){
								parent.FHD.notification('操作成功!','提示');
								window.location.reload();
							}else{
								parent.Ext.MessageBox.alert('提示', '操作失败');
							}
						}
					}); 
				}, null, null);
				this.addAction('preview', function() {
					window.location.href="${ctx}/graph/findprocessgraph.f?viewType=graphview&processId=${processId}"
				}, null, null);
			}
			/**
			 * Function: cellsRemoved
			 * 
			 * Removes the given cells from the model. This method fires
			 * <mxEvent.CELLS_REMOVED> while the transaction is in progress.
			 * 
			 * Parameters:
			 * 
			 * cells - Array of <mxCells> to remove.
			 */
			mxGraph.prototype.cellsRemoved = function(cells)
			{
				if (cells != null && cells.length > 0)
				{
					var scale = this.view.scale;
					var tr = this.view.translate;
					
					this.model.beginUpdate();
					try
					{
						// Creates hashtable for faster lookup
						var hash = new Object();
						
						for (var i = 0; i < cells.length; i++)
						{
							var id = mxCellPath.create(cells[i]);
							hash[id] = cells[i];
						}
						
						for (var i = 0; i < cells.length; i++)
						{
							// Disconnects edges which are not in cells
							var edges = this.getConnections(cells[i]);
							
							for (var j = 0; j < edges.length; j++)
							{
								var id = mxCellPath.create(edges[j]);
								
								if (hash[id] == null)
								{
									var geo = this.model.getGeometry(edges[j]);

									if (geo != null)
									{
										var state = this.view.getState(edges[j]);
												
										if (state != null)
										{
											geo = geo.clone();
											var source = state.getVisibleTerminal(true) == cells[i];
											var pts = state.absolutePoints;
											var n = (source) ? 0 : pts.length - 1;

											geo.setTerminalPoint(
													new mxPoint(pts[n].x / scale - tr.x,
														pts[n].y / scale - tr.y), source);
											this.model.setTerminal(edges[j], null, source);
											this.model.setGeometry(edges[j], geo);
										}
									}
								}
							}
							var reg = /(\@)/;
							if(reg.test(cells[i].getId())){
								parent.FHD.alert("不允许在这里删除业务数据！");
							}else{
								this.model.remove(cells[i]);
							}
							
						}
						
						this.fireEvent(new mxEventObject(mxEvent.CELLS_REMOVED,
								'cells', cells));
					}
					finally
					{
						this.model.endUpdate();
					}
				}
			};
			
			/**
			 * Refreshes the viewport.
			 */
			EditorUi.prototype.refresh = function()
			{
				var quirks = mxClient.IS_IE && (document.documentMode == null || document.documentMode == 5);
				var w = this.container.clientWidth;
				var h = this.container.clientHeight;

				if (this.container == document.body)
				{
					w = document.body.clientWidth || document.documentElement.clientWidth;
					h = (quirks) ? document.body.clientHeight || document.documentElement.clientHeight : document.documentElement.clientHeight;
				}
				
				var effHsplitPosition = Math.max(0, Math.min(this.hsplitPosition, w - this.splitSize - 20));
				var effVsplitPosition = Math.max(0, Math.min(this.vsplitPosition, h - this.menubarHeight - this.toolbarHeight - this.footerHeight - this.splitSize - 1));
				this.menubarContainer.style.height = this.menubarHeight + 'px';
				this.toolbarContainer.style.top = this.menubarHeight + 'px';
				this.toolbarContainer.style.height = this.toolbarHeight + 'px';
				this.outlineContainer.style.width = effHsplitPosition + 'px';
				this.outlineContainer.style.height = effVsplitPosition + 'px';
				this.outlineContainer.style.bottom = this.footerHeight + 'px';
				this.diagramContainer.style.left = '0px';//(effHsplitPosition + this.splitSize) + 'px';
				this.diagramContainer.style.top = (this.menubarHeight + this.toolbarHeight) + 'px';//this.sidebarContainer.style.top;
				this.footerContainer.style.height = this.footerHeight + 'px';
				
				if (quirks)
				{
					this.menubarContainer.style.width = w + 'px';
					this.toolbarContainer.style.width = this.menubarContainer.style.width;
					var sidebarHeight = (h - effVsplitPosition - this.splitSize - this.footerHeight - this.menubarHeight - this.toolbarHeight);
					this.diagramContainer.style.width = w+ 'px';//(w - effHsplitPosition - this.splitSize) + 'px';
					var diagramHeight = (h - this.footerHeight - this.menubarHeight - this.toolbarHeight);
					this.diagramContainer.style.height = diagramHeight + 'px';
					this.footerContainer.style.width = this.menubarContainer.style.width;
				}
				else
				{
					this.diagramContainer.style.bottom = this.outlineContainer.style.bottom;
				}
			};
			
			/**
			 * Creates the required containers.
			 */
			EditorUi.prototype.createDivs = function()
			{
				this.menubarContainer = this.createDiv('geMenubarContainer');
				this.toolbarContainer = this.createDiv('geToolbarContainer');
				this.outlineContainer = this.createDiv('geOutlineContainer');
				this.diagramContainer = this.createDiv('geDiagramContainer');
				this.footerContainer = this.createDiv('geFooterContainer');

				// Sets static style for containers
				this.menubarContainer.style.top = '0px';
				this.menubarContainer.style.left = '0px';
				this.menubarContainer.style.right = '0px';
				this.toolbarContainer.style.left = '0px';
				this.toolbarContainer.style.right = '0px';
				this.outlineContainer.style.left = '0px';
				this.diagramContainer.style.right = '0px';
				this.footerContainer.style.left = '0px';
				this.footerContainer.style.right = '0px';
				this.footerContainer.style.bottom = '0px';
			};

			/**
			 * Creates the required containers.
			 */
			EditorUi.prototype.createUi = function()
			{
				// Creates menubar
				this.menubar = this.menus.createMenubar(this.createDiv('geMenubar'));
				this.menubarContainer.appendChild(this.menubar.container);
				
				// Creates toolbar
				this.toolbar = this.createToolbar(this.createDiv('geToolbar'));
				this.toolbarContainer.appendChild(this.toolbar.container);
				
				// Creates the footer
				this.footerContainer.appendChild(this.createFooter());

				// Adds status bar in menubar
				this.statusContainer = this.createStatusContainer();

				// Connects the status bar to the editor status
				this.editor.addListener('statusChanged', mxUtils.bind(this, function()
				{
					this.setStatusText(this.editor.getStatus());
				}));
				
				this.setStatusText(this.editor.getStatus());
				this.menubar.container.appendChild(this.statusContainer);
				
				// Inserts into DOM
				this.container.appendChild(this.menubarContainer);
				this.container.appendChild(this.toolbarContainer);
				this.container.appendChild(this.outlineContainer);
				this.container.appendChild(this.diagramContainer);
				this.container.appendChild(this.footerContainer);
			};
			
			var editorUiInit = EditorUi.prototype.init;
			EditorUi.prototype.init = function() {
				editorUiInit.apply(this, arguments);
				this.editor.graph.setEdgeLabelsMovable(false);
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
	</script>
</body>
</html>
