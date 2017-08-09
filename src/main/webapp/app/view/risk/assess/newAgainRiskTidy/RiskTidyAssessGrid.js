

Ext.define('FHD.view.risk.assess.againRiskTidy.RiskTidyAssessGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskTidyAssessGrid',
    
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
    		            flex:1,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
    		            	return value;
    	     			}
    		        },
    		        {
    		            header: "评估人",
    		            dataIndex: 'empName',
    		            sortable: true,
    		            flex:.2
    		        },
    		        {
    		            header: "部门",
    		            dataIndex: 'orgName',
    		            sortable: true,
    		            flex:.3,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
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
        
        for(var i = 0 ; i < me.array.length; i++){
        	cols.push(me.array[i]);
        }
        
        cols.push({dataIndex:'riskIcon', sortable : true, width:40,
        	cls: 'grid-icon-column-header grid-statushead-column-header',//风险状态灯
        	menuDisabled:true,
        	renderer:function(value,metaData,record,colIndex,store,view) {
            	return "<div style='width: 23px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
            	+ record.get('riskIcon') + "'/>";
 			}
        });
        
        cols.push({dataIndex:'editIdeaId', header:'修改意见', sortable : true, flex:.5,
            renderer:function(value,metaData,record,colIndex,store,view) {
            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+200+'" ';
            	return value;
 			}});
        
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