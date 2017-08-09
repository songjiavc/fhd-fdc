Ext.define('FHD.view.response.responseDemo', {
    extend: 'FHD.ux.layout.GridPanel',
    alias: 'widget.responseDemo',
    requires: [
        		'FHD.view.risk.assess.utils.GridCells'
       	], 	   	
    border:false,    
    initComponent: function() {
    	 var me = this;
    	 me.id = 'responseDemo';
    	 var cols = [
    	 			{
    	 				header: "id",
    	 				dataIndex:'id',
    	 				hidden:true
    	 			},
    	 	        {
    	 	            header: "风险",
    	 	            dataIndex: 'risk',
    	 	            sortable: true,
    	 	            flex: 2
    	 	        },{
    	 	            header: "应对措施",
    	 	            dataIndex: 'dealMethod',
    	 	            sortable: true,
    	 	            flex: 3,
    	 	            renderer:function(dataIndex) { 
    	     				  return "<a href='javascript:void(0)'>" + dataIndex + "</a>";
    	     			}
    	 	        },{
    	 	            header: "责任部门",
    	 	            dataIndex: 'respondDepartment',
    	 	            sortable: true,
    	 	            flex: 1
    	 	        },{
    	 	            header: "责任人",
    	 	            dataIndex: 'personInCharge',
    	 	            sortable: true,
    	 	            flex: 1
    	 	        }
    	 	       ,{
   	 	            header: "完成标志",
   	 	            dataIndex: 'finishSymbol',
   	 	            sortable: true,
   	 	            flex: 1.5
   	 	           },{
    	 	            header: "完成时间",
    	 	            dataIndex: 'finishTime',
    	 	            sortable: true,
    	 	            flex: 1
    	 	        },{
    	 	            header: "执行标准",
    	 	            dataIndex: 'executeCritera',
    	 	            sortable: true,
    	 	            flex: 1
    	 	        },{
    	 	            header: "执行状态",
    	 	            dataIndex: 'status',
    	 	            sortable: true,
    	 	            flex: 0.8
    	 	        }];
    	 var btns = [{
    		    btype:'add',
    			handler:function(){
    			}
 			},{
    			btype:'edit',
    			disabled:true,
    			id : 'responsegrid_editbtnId',
    			handler:function(){		
    			}
 			},{
    			btype:'delete',
    			disabled:true,
    			id : 'responsegrid_deletebtnId',
    			handler:function(){
    			
    			}
 			},{
 			    text:'关联风险',
 	            iconCls: 'icon-plugin-add',
 			    handler: function() {		       
 			    }
 			},{
 			    text:'回归分析',
 	            iconCls: 'icon-linechart',
 			    handler: function() {			        
 			    }
 			},{
 			    text:'风险承受度',
 	            iconCls: 'icon-ibm-icon-metrics-16',
 			    handler: function() {			       
 			    }
 			}];
    	 me.selModel = Ext.create('Ext.selection.CheckboxModel');
         Ext.apply(me,{
         	cols:cols,
         	url: __ctxPath +'/app/view/response/responseDemo.json',
 		    border: me.border,
 		    btns: btns,
 		    rowlines: true,
 		    columnLines: true,
 		    checked : false,
 		    pagable : true
         });
         me.callParent(arguments);
         
        me.on('resize',function(p){
     		me.setHeight(FHD.getCenterPanelHeight() - 35);
     	});
          me.on('selectionchange',function(){me.setstatus(me)});
     	me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [3]);
        });	    	
    },
    setstatus : function(me){//设置按钮可用状态
    	Ext.getCmp('responsegrid_editbtnId').setDisabled(me.getSelectionModel().getSelection().length === 0);
    	Ext.getCmp('responsegrid_deletebtnId').setDisabled(me.getSelectionModel().getSelection().length === 0);
    },  
})
