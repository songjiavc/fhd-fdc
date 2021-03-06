/**
 * 
 * 计划制定表单(审批)
 */

Ext.define('FHD.view.risk.assess.formulatePlan.FormulateApproverSubmitDownMain', {
    extend: 'Ext.form.Panel',
    alias: 'widget.formulateApproverSubmitDownMain',
    requires: [
               
	],
   
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
        
        me.downgrid = Ext.create('FHD.view.risk.assess.formulatePlan.FormulateApproverSubmitGridPanelNew',{flex:1,margin:2,columnWidth :1,businessId:me.businessId});

        me.downgrid.store.proxy.url = __ctxPath + '/access/formulateplan/queryscoredeptsandcbrgrid.f';
	    me.downgrid.store.proxy.extraParams.planId = me.businessId;
	    me.downgrid.store.load();
        me.fieldSet3 = Ext.create('Ext.form.FieldSet',{
			layout:{
     	        type: 'column'
     	    },
			title:'评估范围',
			collapsible: true,
			margin: '5 5 0 5',
			items:[me.downgrid]
	  	});
	  	
	  	//审批意见
		me.ideaApproval=Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
			columnWidth:1/1,
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
	  	
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.fieldSet, me.fieldSet3, me.fieldSet4]
        });

        
        me.callParent(arguments);
        
        me.form.load({
	        url: __ctxPath + '/access/formulateplan/queryassessplanbyplanId.f',
	        params:{businessId:me.businessId},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });
       
	    if(Ext.getCmp(me.winId)){
	    	me.downgrid.on('resize',function(p){
	    		me.downgrid.setHeight(Ext.getCmp(me.winId).getHeight()-420);
			});
	    }
    }
   
});