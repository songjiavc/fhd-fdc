Ext.define('FHD.view.icm.import.ControlStandardImportPreviewGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.controlstandardimportpreviewgrid',
    
    url : __ctxPath + '/icm/process/import/findControlStandardPreviewListBySome.f',
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
	            header: '上级控制标准(要求)编号',
	            dataIndex: 'parentControlStandardCode',
	            flex: 4
            },
            {
                header: '上级控制标准(要求)名称',
                dataIndex: 'parentControlStandardName',
                flex: 4,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
	            header: '控制标准(要求)编号',
	            dataIndex: 'controlStandardCode',
	            flex: 3
            },
            {
                header: '控制标准(要求)名称',
                dataIndex: 'controlStandardName',
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
                header: '内控层级',
                dataIndex: 'controlLevel',
                flex: 2,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
	            header: '内控要素',
	            dataIndex: 'controlElements',
	            flex: 2,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
                header: '是否分类',
                dataIndex: 'isClass',
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