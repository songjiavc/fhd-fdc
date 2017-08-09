Ext.define('FHD.view.risk.riskidentify.tidy.IdentifyTidyAssessGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.identifyTidyAssessGrid',
    
    reloadData: function(scoreObjectId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/assess/riskTidy/findRiskByAssessPlanIdAndRiskId.f';//查询列表
	    me.store.proxy.extraParams.scoreObjectId = scoreObjectId;
	    me.store.load();
    },
    
    getColsShow : function(){
    	var me = this;
    	var cols = [
    				{
    					dataIndex:'rangId',
    					hidden:true
    				},
    		        {
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            hidden:true,
    		            flex:1,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
    		            	return value;
    	     			}
    		        },
    		        {
    		            header: "辨识人",
    		            dataIndex: 'empName',
    		            sortable: true,
    		            flex:.2
    		        },
    		        {
    		            header: "部门",
    		            dataIndex: 'orgName',
    		            sortable: true,
    		            flex:.2,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
    		            	return value;
    	     			}
    		        },
    		        {
    		            header: "类型",
    		            dataIndex: 'typeName',
    		            sortable: true,
    		            flex:.2
    		        },
    		        {
    		        	dataIndex:'idea', header:'修改意见', sortable : true, flex:1,
			            renderer:function(value,metaData,record,colIndex,store,view) {
			            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+200+'" ';
			            	return value;
			 			}
			 		},
			 		{
			 			dataIndex:'response', header:'应对措施', sortable : true, flex:1,
			            renderer:function(value,metaData,record,colIndex,store,view) {
			            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+200+'" ';
			            	return value;
			 			}
			 		}
    	        ];
    	
    	return cols;
    },
    
    // 初始化方法
    initComponent: function(){
        var me = this;
        var cols = me.getColsShow();
    	Ext.apply(me,{
        	region:'center',
        	margin : '5 5 5 5',
        	cols:cols,
		    border: true,
		    checked: false,
		    pagable : false,
		    searchable : false,
		    columnLines: true,
		    isNotAutoload : false
        });
        me.callParent(arguments);
    	/*
    	me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [1,2]);
        });
        */
    }
});