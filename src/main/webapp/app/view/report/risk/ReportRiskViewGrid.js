/*
 * 部门风险概况
 * ZJ
 * */

Ext.define('FHD.view.report.risk.ReportRiskViewGrid', {
    extend: 'Ext.panel.Panel',
	alias: 'widget.reportriskviewgrid',
	
	type : '',
	
	initComponent: function () {
        var me = this;
        var cols=[
        		{dataIndex : 'id',hidden:true},
        		{dataIndex : 'companyid',hidden:true},
    			{text: '部门',dataIndex: 'orgname',flex: 2,hideable:false,sortable: false,xtype: 'treecolumn',
    			    renderer:function(value,metaData,record,colIndex,store,view) { 
    					metaData.tdAttr = 'data-qtip="'+value+'"';  
    				    return value;
    				}
    			},
    			{text: '重大风险',dataIndex: 'highrisk',flex: 1,hideable:false,sortable: false,
	    			    renderer:function(value,metaData,record,colIndex,store,view) { 
	    					metaData.tdAttr = 'data-qtip="'+value+'"';
	    					if(Number(value) != 0){
		    				    return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskWindow('" + record.data['companyid'] + "','" + record.data['id'] + "','high')\" >" + value + "</a>";
	    					}else{
	    						return value;
	    					}
	    				}
    			},
    			{text: '关注风险',dataIndex: 'attentionrisk',flex: 1,hideable:false,sortable: false,
	    			    renderer:function(value,metaData,record,colIndex,store,view) { 
	    					metaData.tdAttr = 'data-qtip="'+value+'"';  
	    					if(Number(value) != 0){
	    						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskWindow('" + record.data['companyid'] + "','" + record.data['id'] + "','attention')\" >" + value + "</a>";
	    					}else{
	    						return value;
	    					}
	    				}
    			},
    			{text: '安全风险',dataIndex: 'saferisk',flex: 1,hideable:false,sortable: false,
	    			    renderer:function(value,metaData,record,colIndex,store,view) { 
	    					metaData.tdAttr = 'data-qtip="'+value+'"';  
	    					if(Number(value) != 0){
	    						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskWindow('" + record.data['companyid'] + "','" + record.data['id'] + "','safe')\" >" + value + "</a>";
	    					}else{
	    						return value;
	    					}
	    				}
    			},
    			{text: '风险总数',dataIndex: 'riskcount',flex: 1,hideable:false,sortable: false,
	    			    renderer:function(value,metaData,record,colIndex,store,view) { 
	    					metaData.tdAttr = 'data-qtip="'+value+'"';  
	    					if(Number(value) != 0){
	    						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskWindow('" + record.data['companyid'] + "','" + record.data['id'] + "','all')\" >" + value + "</a>";
	    					}else{
	    						return value;
	    					}
	    				}
    			}
		];
		var root = me.getRootNode();
		var isGroup = false;
		if('group' == me.type){
			isGroup  = true;
		}
        me.treegrid = Ext.create('FHD.ux.TreeGridPanel',{
        	useArrows: true,
	        rootVisible: true,
	        multiSelect: false,
	        border:true,
	        rowLines:true,
	        checked: false,
	        autoScroll:true,
		    searchable : true,
		    border : false,
		    cols: cols,
            root:root,
            url : 'risk/risk/reportriskview.f',
            extraParams : {
				isGroup : isGroup
            },
            tbarItems:[{
    			text : '导出excel',
    			tooltip: '导出excel',
    			iconCls : 'icon-ibm-action-export-to-excel',
    			handler:function(){
    				 me.exportChart();
    			}
			}]
        	
        });
		Ext.applyIf(me, {
			layout : 'fit',
			border : false,
			items : me.treegrid
        });
    		
    	me.callParent(arguments);
	},
	
	reloadData : function(){
		var me = this;
//		me.treegrid.store.load();
	},
	
	getRootNode : function(){
		var me = this;
		var root;
		var isGroup = false;
		if('group' == me.type){
			isGroup  = true;
		}
		FHD.ajax({//ajax调用
			url : 'risk/risk/reportriskviewroot.f',
			async : false,
			params : {
				isGroup : isGroup
			},
			callback : function(data){
				root = data.organization;
			}
		});
		return root;
	},
	
	showRiskWindow : function(companyid,orgid,type){
		var me = this;
			me.reportriskgridpanel = Ext.create('FHD.view.report.risk.ReportRiskGridPanel',{});
			me.reportriskgridpanel.reloadData(companyid,orgid,type);
			me.reportriskgridpanelWindow = Ext.create('FHD.ux.Window', {
	            title: '风险图谱详情',
	            maximizable: true,
	            modal: true,
	            width: 800,
	            height: 500,
	            collapsible: true,
	            autoScroll: true,
	            items: me.reportriskgridpanel
	        }).show();
	},
	
    exportChart : function(){
		var me=this;
    	me.headerDatas = [];
    	var items = me.treegrid.columns;
		Ext.each(items,function(item){
			if(!item.hidden && item.dataIndex != ''){
				var value = {};
				value['dataIndex'] = item.dataIndex;
	        	value['text'] = item.text;
	        	me.headerDatas.push(value);
			}
		});
		var isGroup = false;
		if('group' == me.type){
			isGroup  = true;
		}
		window.location.href = "risk/risk/exportreportriskview.f?id="+""+"&type="+isGroup+"&exportFileName="+""+
								"&sheetName="+""+"&headerData="+Ext.encode(me.headerDatas)+"&style="+"event";
	}
	
	
});