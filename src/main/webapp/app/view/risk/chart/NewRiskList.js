Ext.define('FHD.view.risk.chart.NewRiskList', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.newrisklist',
    
    border:false,
    checked: false,
    pagable:false,
    destoryflag:'true',
	url: __ctxPath + '/app/view/risk/chart/data/newRiskListDataJson.json',
	extraParams:{
		orgId:''
	},
	
	initComponent: function() {
    	var me = this;

    	me.cols = [
    	    {dataIndex:'id',hidden:true},
    	    {dataIndex:'orgId',hidden:true},
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
        		header: '更新时间', 
        		dataIndex: 'updateDate',
        		sortable: true, 
        		flex : 1,
        		renderer:function(value,metaData,record,colIndex,store,view) { 
		      		metaData.tdAttr = 'data-qtip="'+value+'"'; 
		      	    return value;  
		      	}
        	}
      	];
    	
    	Ext.applyIf(me, {
            cols: me.cols,
            border: me.border,
            checked: me.checked
        });

    	me.callParent(arguments);
	},
	//重新加载数据
    reloadData: function() {
    	var me = this;
    	
    	me.store.load();
    }
});