/*
 * 体系建设的合规诊断数量统计
 * */
Ext.define('FHD.view.icm.statics.ProcessCountChart', {
    extend: 'Ext.container.Container',
    alias: 'widget.processcountchart',
    border:false,
	url: __ctxPath + '/icm/statics/findprocesscountbysome.f',
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
        		header: '一级分类', 
        		dataIndex: 'levelOneName', 
        		sortable: false, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;
		      	}
        	},
        	{
        		header: '二级分类', 
        		dataIndex: 'levelTwoName', 
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
        		header: '发生频率', 
        		dataIndex: 'processClass', 
        		sortable: false, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;
		      	}
        	},
	      	{
        		header: '流程数量', 
        		dataIndex: 'processCount',
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
    		caption:'流程统计',
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
	          	{'dataIndex':'levelOneName', 'header':'一级分类'},
	          	{'dataIndex':'levelTwoName', 'header':'二级分类'},
	          	{'dataIndex':'orgName', 'header':'责任部门'},
	          	{'dataIndex':'processClass', 'header':'发生频率'},
	          	{'dataIndex':'processCount', 'header':'流程数量'}
        	],
        	//默认显示图表类型--可选
        	type: me.myType,
        	//默认系列与图例选项--可选
        	xCol: me.myXCol,
    		yCol: me.myYCol,
    		//值选项--必选
        	valueCol:{header:'流程数量',dataIndex:'processCount'},
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