Ext.define('FHD.view.risk.contingencyPlan.ContingencyPlanDown', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contingencyplandown',
   
    showStatus: false,
    type : 'all',
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.fieldSet = {
            xtype:'fieldset',
            title: '基础信息',
            collapsible: true,
            collapsed : true,//初始化收缩
            margin: '5 5 0 5',
            defaults: {
                    columnWidth : 1 / 2,
                    margin: '7 30 3 30',
                    labelWidth: 95
                },
            layout: {
     	        type: 'column'
     	    },
     	    items : [ 	{xtype:'displayfield', fieldLabel : '计划名称', name:'planName'},
						{xtype:'displayfield', fieldLabel : '起止日期', name : 'beginendDateStr'},
						{xtype:'displayfield', fieldLabel : '联系人', name : 'contactName'},
						{xtype:'displayfield', fieldLabel : '负责人', name : 'responsName'}]
        };
        
        me.contingencyPlanGridPanel  = Ext.create('FHD.view.risk.contingencyPlan.ContingencyPlanGridPanel',{
        	showStatus : me.showStatus,
        	type : me.type,
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
			border : false,
			showbar : false
		});
        
        me.fieldSet3 = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				  title:'范围',
				  //minHeight: 250,
				  collapsible: true,
				  margin: '5 5 0 5',
				  items:[me.contingencyPlanGridPanel]
	  	});
	  	
	  	//审批意见
		me.ideaApproval=Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
			columnWidth:1,
			executionId:me.executionId
		});
	  	
	  	me.fieldSet4 = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				title:'审批意见',
				collapsible: true,
				margin: '5 5 0 5',
				items:[me.ideaApproval]
	  	});
	  	if(me.showStatus){
	        Ext.apply(me, {
	        	autoScroll: true,
	        	border:false,
	            items : [me.fieldSet, me.fieldSet3]
	        });
	  	}else{
	        Ext.apply(me, {
	        	autoScroll: true,
	        	border:false,
	            items : [me.fieldSet, me.fieldSet3, me.fieldSet4]
	        });
	  	}

        
        me.callParent(arguments);
        me.contingencyPlanGridPanel.reloadData(me.executionId,me.businessId);
        me.form.load({
    	        url: __ctxPath + '/access/formulateplan/queryassessplanbyplanId.f',
    	        params:{businessId: me.businessId},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        }
    	    });
       
       /*me.downgrid.on('resize',function(p){
    		me.downgrid.setHeight(Ext.getCmp(me.winId).getHeight()-420);
	   });*/
    }
});