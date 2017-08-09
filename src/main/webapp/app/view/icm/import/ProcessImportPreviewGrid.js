Ext.define('FHD.view.icm.import.ProcessImportPreviewGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.processimportpreviewgrid',
    
    url : __ctxPath + '/icm/process/import/findProcessPreviewListBySome.f',
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
	            header: '上级流程编号',
	            dataIndex: 'parentProcessureCode',
	            flex: 3
            },
            {
                header: '上级流程名称',
                dataIndex: 'parentProcessureName',
                flex: 3,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
	            header: '流程编号',
	            dataIndex: 'processureCode',
	            flex: 2
            },
            {
                header: '流程名称',
                dataIndex: 'processureName',
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
                header: '相关部门',
                dataIndex: 'relateOrg',
                flex: 3,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
	            header: '发生频率',
	            dataIndex: 'frequency',
	            flex: 2
            },
            {
                header: '影响的财报科目',
                dataIndex: 'affectSubjects',
                flex: 3,
                renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
            },
            {
	            header: '责任人',
	            dataIndex: 'responsibleEmp',
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