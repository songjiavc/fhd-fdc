Ext.define('FHD.view.event.eventDemo', {
    extend: 'FHD.ux.layout.GridPanel',
    alias: 'widget.eventDemo',
    initComponent: function() {
    	 var me = this;
    	 me.id = 'eventDemo';
    	 var cols = [
    	 			{
    	 				header: "id",
    	 				dataIndex:'id',
    	 				hidden:true
    	 			},
    	 	        {
    	 	            header: "事件名称",
    	 	            dataIndex: 'eventName',
    	 	            sortable: true,
    	 	            flex: 1
    	 	        },{
    	 	            header: "事件描述",
    	 	            dataIndex: 'eventDescription',
    	 	            sortable: true,
    	 	            flex: 2,
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
    	 	        },{
    	 	            header: "发生时间",
    	 	            dataIndex: 'happenTime',
    	 	            sortable: true,
    	 	            flex: 1
    	 	        },{
    	 	            header: "处理状态",
    	 	            dataIndex: 'status',
    	 	            sortable: true,
    	 	            flex: 1
    	 	        }];
    	   var btns = [{
   			btype:'add',
   			id:'event',
   			handler:function(){
   			}
			},{
   			btype:'edit',
   			disabled:true,
   			id : 'eventgrid_editbtnId',
   			handler:function(){		
   			}
			},{
   			btype:'delete',
   			disabled:true,
   			id : 'eventgrid_deletebtnId',
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
         Ext.apply(me,{
         	cols:cols,
         	url: __ctxPath +'/app/view/event/eventDemo.json',
         	btns: btns,
 		    border: false,
 		    checked : true,
 		    pagable : true
         });
         me.callParent(arguments);
         me.on('selectionchange',function(){me.setstatus(me)});
         me.on('resize',function(p){
     		me.setHeight(FHD.getCenterPanelHeight() - 35);
     	});
    },
    setstatus : function(me){//设置按钮可用状态
    	Ext.getCmp('eventgrid_editbtnId').setDisabled(me.getSelectionModel().getSelection().length === 0);
    	Ext.getCmp('eventgrid_deletebtnId').setDisabled(me.getSelectionModel().getSelection().length === 0);
    },
})
