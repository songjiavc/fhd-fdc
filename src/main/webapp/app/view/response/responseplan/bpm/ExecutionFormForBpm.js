Ext.define('FHD.view.response.responseplan.bpm.ExecutionFormForBpm', {
	extend:'Ext.panel.Panel',
	aligs:'widget.executionformforbpm',
	layout : {
		type : 'vbox',
		align : 'stretch'
	},
	requires: [
      'FHD.ux.icm.common.FlowTaskBar',
      'FHD.view.response.ExecutionForm'
    ],
	autoScroll:true,
//	bodyPadding:'0 3 3 3',
	initComponent : function() {
		var me=this;
		
		me.bbar={
			items: [
				'->',{
					text: '保存',
    				iconCls: 'icon-control-stop-blue',
    				handler: function () {
					    me.diagnosesEditGrid.saveData();
    				} 
				},{
       				text: '提交',
    				iconCls: 'icon-operator-submit',
    				handler: function () {
						if(!me.diagnosesEditGrid.saveSubmitData()){
							
						}else{
							var jsonArray=[];
							var rows = me.diagnosesEditGrid.store.data;
							Ext.each(rows.items,function(item){
								jsonArray.push(item.data);
							});
							if(jsonArray.length>0){
								 FHD.ajax({
					    		     url : __ctxPath+ '/icm/icsystem/diagnosessubmit.f',
					    		     params : {
					    		    	 modifiedRecord:Ext.encode(jsonArray),
					    		    	 constructPlanId : me.businessId,
					    				 executionId : me.executionId
					    			 },
					    			 callback : function(data) {
					    				if(me.winId){
											Ext.getCmp(me.winId).close();
										}
					    			 }
					    		});
							}
						}
    				} 
    			}
    		]
		};
		
		me.flowtaskbar=Ext.widget('panel',{
        	title: "风险应对方案-方案执行",
            region:'north',
            collapsed:true,
            collapsible: true,
            maxHeight:200,
            split: true,
            border: false,
        	items:[
	        	Ext.widget('flowtaskbar',{
	    		jsonArray:[
		    		{index: 1, context:'1.指定方案制定人',status:'done'},
		    		{index: 2, context:'2.方案制定',status:'done'},
		    		{index: 3, context:'3.方案审批',status:'done'},
		    		{index: 4, context:'4.方案执行',status:'current'}
		    	]
	    		})
        	]
        });
		
		me.executionform = Ext.widget('executionform',{flex:1});
		Ext.applyIf(me, {
        	items:[me.flowtaskbar,me.executionform]
		});
		me.callParent(arguments);
	},
	reloadData:function(){
		var me=this;
	}
});