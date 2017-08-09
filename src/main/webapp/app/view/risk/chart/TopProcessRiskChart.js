Ext.define('FHD.view.risk.chart.TopProcessRiskChart', {
    extend: 'Ext.container.Container',
    alias: 'widget.topprocessriskchart',
    
    border:false,
    toolRegion:'west',
    url: __ctxPath + '/app/view/risk/chart/data/topProcessRiskDataJson.json',
	extraParams:{
		orgId:''
	},
	//默认显示图表类型--可选
	myType:'msColumnChart',
	//默认系列与图例选项--可选
	myXCol:{header:'流程',dataIndex:'processName'},
	myYCol:{header:'风险等级',dataIndex:'riskLevel'},
	
	initComponent: function() {
    	var me = this;

    	me.multiColumns =[
      	    {dataIndex:'id',hidden:true},
      	    {dataIndex:'orgId',hidden:true},
      		{
          		header: '部门', 
          		dataIndex: 'orgName', 
          		sortable: true, 
          		flex : 1,
          		renderer:function(value,metaData,record,colIndex,store,view) { 
  		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
  		      	    return value;
  		      	}
          	},
          	{dataIndex:'processId',hidden:true},
      		{
          		header: '流程', 
          		dataIndex: 'processName', 
          		sortable: true, 
          		flex : 1,
          		renderer:function(value,metaData,record,colIndex,store,view) { 
  		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
  		      	    return value;
  		      	}
          	},
          	{
          		header: '风险名称', 
          		dataIndex: 'riskName', 
          		sortable: true, 
          		flex : 1,
          		renderer:function(value,metaData,record,colIndex,store,view) { 
  		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
  		      	    return value;
  		      	}
          	},	
          	{
          		header: '风险分类', 
          		dataIndex: 'parentName', 
          		sortable: true, 
          		flex : 1,
          		renderer:function(value,metaData,record,colIndex,store,view) { 
  		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
  		      	    return value;
  		      	}
          	},
          	{
          		header: '风险等级', 
          		dataIndex: 'riskLevel', 
          		sortable: true, 
          		flex : 1,
          		renderer:function(value,metaData,record,colIndex,store,view) { 
  		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
  		      	    return value;
  		      	}
          	},
  	      	{
          		header: '数量', 
          		dataIndex: 'riskCount',
          		sortable: true, 
          		flex : 1,
          		renderer:function(value,metaData,record,colIndex,store,view) { 
  		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
  		      	    return value;  
  		      	}
          	}
        	];
      	
      	me.chartPanel = Ext.create('FHD.view.comm.chart.ChartDashboardPanel',{
      		//图表标题--可选
      		caption:'流程风险TOP5',
      		//查询条件自定义位置--可选
      		toolRegion:me.toolRegion,
      		//数据类型--必选
      		dataType:'baseData',
      		flex:1,
      		//系列与图例下拉选项--必选
      		storeItem:[
  	    	    {'dataIndex':'', 'header':'请选择'},
  	      	    {'dataIndex':'processName', 'header':'部门'},
  	          	{'dataIndex':'riskLevel', 'header':'风险等级'}
          	],
          	//默认显示图表类型--可选
          	type:me.myType,
          	//默认系列与图例选项--可选
          	xCol:me.myXCol,
      		yCol:me.myYCol,
      		//值选项--必选
          	valueCol:{header:'数量',dataIndex:'riskCount'},
          	//grid列表columns--必选
      		multiColumns:me.multiColumns,
      		//grid列表url--必选
      		url:me.url,
      		//grid列表url参数--可选
      		extraParams:me.extraParams
      	});
    	          	
    	Ext.applyIf(me, {
        	layout: {
				type: 'vbox',
	        	align:'stretch'
	        },
        	items:[
        	    me.chartPanel
        	]
		});
    	me.callParent(arguments);
	},
	//重新加载数据
    reloadData: function() {
    	var me = this;
    	me.chartPanel.reloadData();
    }
});