Ext.define('FHD.view.risk.riskidentify.tidy.IdentifyTidyDeptApproveGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.identifyTidyDeptApproveGrid',
    
    reloadData: function(scoreObjectId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/assess/riskTidy/findRiskByDeptRiskIdea.f';//查询列表
	    me.store.proxy.extraParams.scoreObjectId = scoreObjectId;
	    me.store.load();
    },
    
    getColsShow : function(){
    	var me = this;
    	var array = null;
    	var cols = [
    				{
    					dataIndex : 'circuseeId',
    					hidden:true
    				},{
    		            header: "部门风险管理员",
    		            dataIndex: 'empName',
    		            flex:.2
    		        },{
    		        	header: "所属部门",
    		            dataIndex : "orgName",
    		            flex:.2
    		        },{
    		            header: "修改意见汇总",
    		            dataIndex: 'hIdea',
    		            flex:.3,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
    		            	return value;
    	     			}
    		        },{
    		            header: "应对意见汇总",
    		            dataIndex: 'hResponse',
    		            flex:.3,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
    		            	return value;
    	     			}
    		        }
    	        ];
    	return cols;
    },
    
    // 初始化方法
    initComponent: function() {
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
    	me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [1,2]);
        });
    }
});