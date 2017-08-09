/*
 * 体系建设的合规诊断数量统计
 * */
Ext.define('FHD.view.icm.statics.ConstructProcessCountChart', {
    extend: 'Ext.container.Container',
    alias: 'widget.constructprocesscountchart',
    border:false,
	url: __ctxPath + '/icm/statics/findconstructprocesscountbysome.f',
	toolRegion:'west',
	extraParams:{
		orgId:'',
		processId:'',
		planId:''
	},
	initComponent: function() {
    	var me = this;
    	me.multiColumns =[
    	    {dataIndex:'id',hidden:true},
        	{
        		header: '年份', 
        		dataIndex: 'createYear', 
        		sortable: true, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;
		      	}
        	},
        	{
        		header: '流程分类', 
        		dataIndex: 'parentProcessName', 
        		sortable: true, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;
		      	}
        	},
        	{
        		header: '责任部门', 
        		dataIndex: 'orgName', 
        		sortable: true, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;
		      	}
        	},
        	{
        		header: '建设责任人', 
        		dataIndex: 'empName', 
        		sortable: true, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;
		      	}
        	},
	      	{
        		header: '流程梳理数量', 
        		dataIndex: 'constructProcessCount',
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
    		caption:'流程梳理统计',
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
	          	{'dataIndex':'parentProcessName', 'header':'流程分类'},
	          	{'dataIndex':'orgName', 'header':'责任部门'},
	          	{'dataIndex':'planName', 'header':'体系建设计划'},
	          	{'dataIndex':'empName', 'header':'建设责任人'},
	          	{'dataIndex':'constructProcessCount', 'header':'流程梳理数量'}
        	],
        	//默认显示图表类型--可选
        	type:'msColumnChart',
        	//默认系列与图例选项--可选
        	xCol:{header:'年份',dataIndex:'createYear'},
    		yCol:{header:'流程分类',dataIndex:'parentProcessName'},
    		//值选项--必选
        	valueCol:{header:'流程梳理数量',dataIndex:'constructProcessCount'},
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