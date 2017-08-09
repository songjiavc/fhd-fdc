/*
 * 图形关联记分卡Panel
 * 
 * @author 张雷
 * */
Ext.define('FHD.view.comm.graph.GraphPanel', {
 	extend: 'Ext.container.Container',
 	alias: 'widget.graphpanel',
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
		    }],
		 	maxHeight: 155,
        	split: true, 
        	collapsible: true, 
        	collapseMode:'mini',
			cols: [
				{dataIndex:'id',hidden:true},
				{ header: '图形名称',  dataIndex: 'name' ,flex: 3 ,sortable:false,
					renderer:function(value, metaData, record, rowIndex, colIndex, store, view) {
						metaData.tdAttr = 'data-qtip="'+value+'"';
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').editOrViewGraph('" + record.get('id') + "','graphdrawview')\" >" + value + "</a>"; 
					}
				},
				{ header: '更新日期', dataIndex: 'updateDate', width:90, sortable:false},
				{ header: '操作', dataIndex: 'type', width:120, sortable:false,
					renderer:function(value, metaData, record, rowIndex, colIndex, store, view) {
						hrefUrl = "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('"+me.id+"').editOrViewGraph('"+record.get('id')+"','graphdraw')\">编辑</a>"
							+" | <a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('"+me.id+"').removeGraph('"+record.get('id')+"')\">删除</a>";
						return hrefUrl;
					}
				}
			],
			url: __ctxPath+'/comm/graph/findGraphPageBySome.f',
			extraParams:me.extraParams,
			checked:false,
			searchable:true,
			pagable : true
		});		
		me.grapheditor = Ext.create('Ext.panel.Panel',{
    	 	region:'center',
    	 	border:false,
            html:"<iframe src='"+__ctxPath+"/comm/graph/findGraph.f?panelId="+me.id+"' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>"
         });
        me.add(me.grapheditor);
        me.add(me.grid);
    },
    /*
     * 重新加载
     */
    reloadData:function(){
    	var me=this,graphId;
    	me.grid.store.proxy.extraParams = me.extraParams;
    	me.grid.store.load();
    	if(me.extraParams.graphId && me.extraParams.graphId!=''){
    		graphId = me.extraParams.graphId;
    	}else{
    		graphId = '';
    	}
    	var html = "<iframe src='"+__ctxPath+"/comm/graph/findGraph.f?panelId="+me.id+"&viewType=graphdrawview&id="+graphId+"' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>";
		me.grapheditor.update(html);
    },
    /*
     * 显示图形
     */
    editOrViewGraph:function(id,viewType){
    	var me = this;
    	var html = "<iframe src='"+__ctxPath+"/comm/graph/findGraph.f?panelId="+me.id+"&viewType="+viewType+"&id="+id+"' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>";
		me.grapheditor.update(html);
    },
    /*
     * 新增一个图形
     */
    addGraph:function(){
    	var me = this;
    	var html = "<iframe src='"+__ctxPath+"/comm/graph/findGraph.f?panelId="+me.id+"&viewType=graphdraw' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>";
		me.grapheditor.update(html);
    },
	/*
	 * 删除一个图形及关联关系
	 * */
	removeGraph:function(id){
		var me = this;
       Ext.MessageBox.confirm('提示', '本操作将删除该图形及所有关联关系,且无法恢复，您确定吗?', function(btn){
			if('yes'==btn){
				FHD.ajax({
					url: __ctxPath + '/comm/graph/removeGraph.f',
					params: {graphId:id},
					callback: function(data){
						if(data){
							FHD.notification('操作成功!','提示');
							me.extraParams.graphId = '';
							me.reloadData();
						}else{
							Ext.MessageBox.alert('提示', '操作失败');
						}
					}
				}); 
			}
	    });
	}
});