Ext.define('FHD.view.bpm.processinstance.ProcessInstanceView',{
	extend: 'Ext.container.Container',
    alias: 'widget.processtnstanceview',
    businessId:"",
    initComponent: function(){
    	var me = this;
    	
    	Ext.applyIf(me,{
    		layout:'fit'
    	});
    	me.callParent(arguments);
    	
    },
    reloadData:function(){
    	var me = this;
    	if(me.businessId){
    		FHD.ajax({
				url : __ctxPath + '/jbpm/processInstance/findBusinessWorkFlowBySome.f',//判断是否发起工作流
				params : {
					businessId : me.businessId
				},
				callback: function (data) {
		           	me.processInstanceTab = Ext.create('FHD.view.bpm.processinstance.ProcessInstanceTab',{
		           		processInstanceId:data.processInstanceId,
		           		jbpmHistProcinstId:data.jbpmHistProcinstId,
						businessId:me.businessId,
						model:"show"
			    	});
			    	me.add(me.processInstanceTab);
		        }
			});
    	}
    }
});