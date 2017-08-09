Ext.define('FHD.view.icm.import.RiskProcessPointMeasureRelationImportPreviewGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskprocesspointmeasurerelationimportpreviewgrid',
    
    url : __ctxPath + '/icm/process/import/findRiskProcessPointMeasureRelationPreviewListBySome.f',
    extraParams:{
    	fileId: ''
    },
    cols: [],
    tbarItems: [],
    border: false,
    checked: false,
    pagable:false,
    destoryflag:'true',

    initComponent: function () {
        var me = this;
        
        me.cols = [
	        {
	            header: '行号',
	            dataIndex: 'eindex',
	            flex: 1
            },
            {
                header: '错误提示',
                dataIndex: 'errorTip',
                flex: 4,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		if(value){
		    			metaData.tdAttr = 'data-qtip="'+value+'" style="background-color:#FF0000"';
		    		}
					return value; 
				}
            },
            {
	            header: '风险编号',
	            dataIndex: 'riskCode',
	            flex: 2
            },
            {
                header: '风险名称',
                dataIndex: 'riskName',
                flex: 3,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
	            header: '流程编号',
	            dataIndex: 'processCode',
	            flex: 2
            },
            {
                header: '流程名称',
                dataIndex: 'processName',
                flex: 3,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
	            header: '流程节点编号',
	            dataIndex: 'processPointCode',
	            flex: 2
            },
            {
                header: '流程节点名称',
                dataIndex: 'processPointName',
                flex: 3,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
	            header: '控制措施编号',
	            dataIndex: 'controlMeasureCode',
	            flex: 2
            },
            {
                header: '控制措施名称',
                dataIndex: 'controlMeasureName',
                flex: 3,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
            	dataIndex:'id',
            	hidden:true
            }
        ];

        Ext.applyIf(me, {
        	viewConfig: {
                getRowClass: function (record, rowIndex, rowParams, store) {
                    return record.get("errorTip") ? "row-s" : "";
                }
            },
            cols: me.cols,
            tbarItems: me.tbarItems,
            border: me.border,
            checked: me.checked
        });

        me.callParent(arguments);
    },
	reloadData:function (){
		var me = this;
		
		me.store.reload();
	}
});