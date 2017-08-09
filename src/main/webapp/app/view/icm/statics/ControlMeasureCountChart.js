/*
 * 体系建设的合规诊断数量统计
 * */
Ext.define('FHD.view.icm.statics.ControlMeasureCountChart', {
    extend: 'Ext.container.Container',
    alias: 'widget.controlmeasurecountchart',
    border:false,
	url: __ctxPath + '/icm/statics/findcontrolmeasurecountbysome.f',
	toolRegion:'west',
	extraParams:{
		orgId:''
	},
	//默认显示图表类型--可选
	myType:'msColumnChart',
	//默认系列与图例选项--可选
	myXCol:{header:'年份',dataIndex:'createYear'},
	myYCol:{header:'责任部门',dataIndex:'orgName'},
	initComponent: function() {
    	var me = this;
    	me.multiColumns =[
    	    {dataIndex:'id',hidden:true},
    	    {dataIndex:'orgId',hidden:true},
    	    {dataIndex:'processId',hidden:true},
        	{
        		header: '年份', 
        		dataIndex: 'createYear', 
        		sortable: false, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;
		      	}
        	},
        	{
        		header: '责任部门', 
        		dataIndex: 'orgName', 
        		sortable: false, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;
		      	}
        	},
        	{
        		header: '流程名称', 
        		dataIndex: 'processName', 
        		sortable: false, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;
		      	}
        	},
        	{
        		header: '控制方式', 
        		dataIndex: 'controlMeasureName', 
        		sortable: false, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;
		      	}
        	},
        	
        	{
        		header: '是否关键控制', 
        		dataIndex: 'isKeyControlPointName', 
        		sortable: false, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;
		      	}
        	},
	      	{
        		header: '控制措施数量', 
        		dataIndex: 'controlMeasureCount',
        		sortable: false, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      		return value;
		      	}
        	}
      	];
    	
    	me.chartPanel = Ext.create('FHD.view.comm.chart.ChartDashboardPanel',{
    		//图表标题--可选
    		caption:'控制措施统计',
    		//查询条件自定义位置--可选
    		toolRegion:me.toolRegion,
    		//数据类型--必选
    		dataType:'baseData',
    		border: me.border,
    		flex:1,
    		//系列与图例下拉选项--必选
    		storeItem:[
	    	    {'dataIndex':'', 'header':'请选择'},
	    	    {'dataIndex':'createYear', 'header':'年份'},
	    	    {'dataIndex':'orgName', 'header':'责任部门'},
	          	{'dataIndex':'processName', 'header':'流程名称'},
	          	{'dataIndex':'controlMeasureName', 'header':'控制方式'},
	          	{'dataIndex':'isKeyControlPointName', 'header':'是否关键控制'},
	          	{'dataIndex':'controlMeasureCount', 'header':'控制措施数量'}
        	],
        	//默认显示图表类型--可选
        	type: me.myType,
        	//默认系列与图例选项--可选
        	xCol: me.myXCol,
    		yCol: me.myYCol,
    		//值选项--必选
        	valueCol:{header:'控制措施数量',dataIndex:'controlMeasureCount'},
        	//可选项，排序，例如：me.cols.yCol.values=["yi","er","san+"]
        	//grid列表columns--必选
    		multiColumns:me.multiColumns,
    		//后缀
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
    	me.chartPanel.extraParams=me.extraParams;
    	me.chartPanel.reloadData();
    }
});