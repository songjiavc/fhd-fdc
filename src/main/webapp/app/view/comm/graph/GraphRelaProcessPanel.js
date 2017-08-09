/*
 * 图形关联流程Panel
 * 
 * @author 张雷
 * */
Ext.define('FHD.view.comm.graph.GraphRelaProcessPanel', {
 	extend: 'Ext.container.Container',
 	alias: 'widget.graphrelaprocesspanel',
 	overflowX: 'hidden',
	overflowY: 'auto',
	layout: {
        type: 'border'
    },
    requires:[
    	'FHD.ux.ActionTextColumn'
    ],
    initParam:function(extraParams){
	   	var me = this;
	   	me.extraParams = extraParams;
    },
    // 初始化方法
    initComponent: function() {
    	 var me = this;
    	 me.callParent(arguments);
		 me.grid = Ext.create('FHD.ux.GridPanel', {
		 	region:'south',
		 	border:false,
		 	tools: [{
				type: 'plus',
				tooltip: '新增一个图形',
				style: {
		            marginLeft: '10px'
		        },
				handler: me.addGraph,
				scope: this
		    },
		    {
				type: 'search',
				tooltip: '选择引用一个图形',
				style: {
		            marginLeft: '10px'
		        },
				handler: me.mergeGraphRelaProcess,
				scope: this
		    }],
		 	maxHeight: 155,
        	split: true, 
        	collapsible: true, 
        	collapseMode:'mini',
			cols: [
				{dataIndex:'graphRelaProcessId',hidden:true},
				{dataIndex:'graphId',hidden:true},
				{ header: '图形名称',  dataIndex: 'name' ,flex: 3 ,sortable:false,
					renderer:function(value, metaData, record, rowIndex, colIndex, store, view) {
						metaData.tdAttr = 'data-qtip="'+value+'"';
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showGraphView('"+record.get('graphRelaProcessId')+"','"+record.get('type')+"')\" >" + value + "</a>"; 
					}
				},
				{ header: '允许操作', dataIndex: 'type', hidden:true, flex: 1,sortable:false,
					renderer:function(value, metaData, record, rowIndex, colIndex, store, view) {
						if(value=='graphdraw'){
							return '修改，查看';
						}else{
							return '查看';
						}
					}
				},
				
				{ header: '更新日期', dataIndex: 'updateDate', width:90, sortable:false},
				{ header: '操作', dataIndex: 'type', width:120, sortable:false,
					renderer:function(value, metaData, record, rowIndex, colIndex, store, view) {
						var hrefUrl = "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('"+me.id+"').removeGraphRelaProcess('"+record.get('type')+"','"+record.get('graphId')+"','"+record.get('graphRelaProcessId')+"')\">删除</a>";
						if(value=='graphdraw'){
							hrefUrl = "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('"+me.id+"').showGraphView('"+record.get('graphRelaProcessId')+"','"+record.get('type')+"')\">编辑</a>"
							+" | <a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('"+me.id+"').removeGraphRelaProcess('"+record.get('type')+"','"+record.get('graphId')+"','"+record.get('graphRelaProcessId')+"')\">删除</a>";
							return hrefUrl;
						}else{
							return hrefUrl;
						}
					}
				}
			],
			url: __ctxPath+'/comm/graph/findGraphRelaProcessList.f',
			extraParams:me.extraParams,
			checked:false,
			searchable:false,
			pagable : false,
			listeners:{
				itemdblclick:function( grid, record, item, index, e, eOpts ){
					var selection = grid.getSelectionModel().getSelection();//得到选中的记录
					if(selection[0]){
						me.showGraphView(selection[0].get('graphRelaProcessId'),'graphdrawview')
					}
				}
				
			}
		});		
		me.grapheditor = Ext.create('Ext.panel.Panel',{
    	 	region:'center',
    	 	border:false,
            html:"<iframe src='"+__ctxPath+"/comm/graph/findGraphRelaProcess.f?panelId="+me.id+"' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>"
         });
        me.add(me.grapheditor);
        me.add(me.grid);
    },
    /*
     * 重新加载
     */
    reloadData:function(){
    	var me=this;
    	me.grid.store.proxy.extraParams = me.extraParams;
    	me.grid.store.load();
    	var html = "<iframe src='"+__ctxPath+"/comm/graph/findGraphRelaProcess.f?panelId="+me.id+"&viewType=graphdrawview&processId="+me.extraParams.processId+"' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>";
		me.grapheditor.update(html);
    },
    /*
     * 显示图形
     */
    showGraphView:function(graphRelaProcessId,type){
    	var me = this;
    	var html = "<iframe src='"+__ctxPath+"/comm/graph/findGraphRelaProcess.f?panelId="+me.id+"&viewType="+type+"&graphRelaProcessId="+graphRelaProcessId+"' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>";
		me.grapheditor.update(html);
    },
    /*
     * 新增一个图形
     */
    addGraph:function(){
    	var me = this;
    	var html = "<iframe src='"+__ctxPath+"/comm/graph/findGraphRelaProcess.f?panelId="+me.id+"&&isNew=true&processId="+me.extraParams.processId+"' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>";
		me.grapheditor.update(html);
    },
    /*
     * 引用一个图形
     */
    mergeGraphRelaProcess:function(){
    	var me = this;
    	if(me.extraParams){
    		if(me.extraParams.processId){
    			Ext.create('FHD.ux.graph.GraphSelectorWindow',{
					multiSelect:false,
					modal: true,
					onSubmit:function(win){
						win.selectedGrid.store.each(function(value) {
							FHD.ajax({
								url: __ctxPath + '/comm/graph/mergeGraphRelaProcess.f',
								params: {
									graphId:value.data.id,
									processId:me.extraParams.processId
								},
								callback: function(data){
									if(data){
										FHD.notification('操作成功!','提示');
										me.reloadData();
									}else{
										Ext.MessageBox.alert('提示', '操作失败');
									}
								}
							}); 
						});
					}
				}).show();
    		}
    	}
	},
	/*
	 * 删除一个图形及关联关系
	 * */
	removeGraphRelaProcess:function(type,graphId,graphRelaProcessId){
		var me = this;
        if(type=='graphdraw'){
        	Ext.MessageBox.confirm('提示', '本操作将删除该图形及所有关联关系,且无法恢复，您确定吗?', function(btn){
				if('yes'==btn){
					FHD.ajax({
						url: __ctxPath + '/comm/graph/removeGraph.f',
						params: {graphId:graphId},
						callback: function(data){
							if(data){
								FHD.notification('操作成功!','提示');
								me.reloadData();
							}else{
								Ext.MessageBox.alert('提示', '操作失败');
							}
						}
					}); 
				}
		    });
        }else{
        	Ext.MessageBox.confirm('提示', '本操作将删除与该图形的关联关系,且无法恢复，您确定吗?', function(btn){
				if('yes'==btn){
					FHD.ajax({
						url: __ctxPath + '/comm/graph/removeGraphRelaProcess.f',
						params: {graphRelaProcessId:graphRelaProcessId},
						callback: function(data){
							if(data){
								FHD.notification('操作成功!','提示');
								me.reloadData();
							}else{
								Ext.MessageBox.alert('提示', '操作失败');
							}
						}
					}); 
				}
		    });
        }
	}
});