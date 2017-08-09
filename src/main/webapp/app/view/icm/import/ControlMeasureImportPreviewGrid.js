Ext.define('FHD.view.icm.import.ControlMeasureImportPreviewGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.controlmeasureimportpreviewgrid',
    
    url : __ctxPath + '/icm/process/import/findControlMeasurePreviewListBySome.f',
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
	            header: '控制措施编号',
	            dataIndex: 'controlMeasureCode',
	            flex: 3
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
                header: '是否关键控制点',
                dataIndex: 'isKeyControlPoint',
                flex: 3
            },
            {
	            header: '控制目标',
	            dataIndex: 'controlTarget',
	            flex: 2,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
                header: '实施证据',
                dataIndex: 'implementProof',
                flex: 2,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
                header: '控制方式',
                dataIndex: 'controlMode',
                flex: 2
            },
            {
                header: '控制频率',
                dataIndex: 'controlFrequency',
                flex: 2
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