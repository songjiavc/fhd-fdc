Ext.define('FHD.view.response.responseplan.bpm.ResponsePlanApproveForBpm', {
	extend:'Ext.panel.Panel',
	aligs:'widget.responseplanapproveforbpm',
	layout : {
		type : 'vbox',
		align : 'stretch'
	},
	requires: [
      'FHD.ux.icm.common.FlowTaskBar',
      'FHD.view.response.responseplan.form.ResponsePlanRangeFormForView'
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
        	title: "风险应对计划-计划审批",
            region:'north',
            collapsed:true,
            collapsible: true,
            maxHeight:200,
            split: true,
            border: false,
        	items:[
	        	Ext.widget('flowtaskbar',{
		    		jsonArray:[
			    		{index: 1, context:'1.计划编辑',status:'done'},
			    		{index: 2, context:'2.计划审批',status:'current'}
			    		
			    	]
		    	})
        	]
        });
		var responseplanrangeformforview = Ext.widget('responseplanrangeformforview',{flex : 1});
		Ext.applyIf(me, {
        	items:[me.flowtaskbar,responseplanrangeformforview]
		});
		me.callParent(arguments);
	},
	reloadData:function(){
		var me=this;
	}
});