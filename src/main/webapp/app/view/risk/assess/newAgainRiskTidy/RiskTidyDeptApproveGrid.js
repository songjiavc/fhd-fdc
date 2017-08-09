

Ext.define('FHD.view.risk.assess.againRiskTidy.RiskTidyDeptApproveGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskTidyDeptApproveGrid',
    
    getColsShow : function(){
    	var me = this;
    	var array = null;
    	var cols = [
    				{
    					dataIndex:'riskId',
    					hidden:true
    				},
    		        {
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            hidden:true,
    		            flex:.3,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
    		            	return value;
    	     			}
    		        },
    		        {
    		            header: "部门风险管理员",
    		            dataIndex: 'empName',
    		            sortable: true,
    		            flex:.2
    		        },
    		        {
    		            header: "审批意见",
    		            dataIndex: 'editIdeaContent',
    		            sortable: true,
    		            flex:1
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
		    storeAutoLoad:false,
		    isNotAutoload : false
        });
        
        me.callParent(arguments);
    	
    	me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [1,2]);
        });
    }
});