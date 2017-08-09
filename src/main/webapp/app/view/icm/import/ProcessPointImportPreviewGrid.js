Ext.define('FHD.view.icm.import.ProcessPointImportPreviewGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.processpointimportpreviewgrid',
    
    url : __ctxPath + '/icm/process/import/findProcessPointPreviewListBySome.f',
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
	            flex: 3
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
	            header: '节点类型',
	            dataIndex: 'type',
	            flex: 2
            },
            {
                header: '节点描述',
                dataIndex: 'processPointDesc',
                flex: 3,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
	            header: '责任部门',
	            dataIndex: 'responsibleOrg',
	            flex: 2
            },
            {
	            header: '责任人',
	            dataIndex: 'responsibleEmp',
	            flex: 2
            },
            {
                header: '输入信息',
                dataIndex: 'inputInfo',
                flex: 3,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
                header: '输出信息',
                dataIndex: 'outputInfo',
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