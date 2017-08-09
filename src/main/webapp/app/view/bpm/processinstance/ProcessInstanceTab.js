Ext.define('FHD.view.bpm.processinstance.ProcessInstanceTab', {
    extend: 'Ext.tab.Panel',
	alias: 'widget.ProcessInstanceTab',
	
	activeTab: 0,
	width:'auto',
	autoScroll:true,
	jbpmHistProcinstId:null,
	processInstanceId:null,
	url:null,
	businessId:null,
    model:"show",
    BusinessInfo:null,
    processInstanceInfo:null,
    flowChartPanel:null,
    reloadParentData:function(){
    	
    },
    initComponent: function() {
        var me = this;
        var items=new Array();
        var processInstanceInfo=Ext.create("FHD.view.bpm.processinstance.ProcessInstanceTabInfo",{
        	title: "工作流日志",
        	jbpmHistProcinstId:me.jbpmHistProcinstId,
        	processInstanceId:me.processInstanceId,
        	model:me.model,
        	reloadParentData:function(){
        		me.reloadParentData()
        	}
        });
        items.push(processInstanceInfo);
        if(me.url){
	        var BusinessInfo=Ext.create(me.url,{
	        	title: "业务信息",
	        	id:me.businessId,
	        	model:me.model
	        });
	        items.push(BusinessInfo);
        }
        var flowChartPanel=Ext.create("FHD.view.bpm.FlowChartPanel",{
        	title: "工作流状态",
        	jbpmHistProcinstId:me.jbpmHistProcinstId,
        	processInstanceId:me.processInstanceId
        });
        items.push(flowChartPanel);
        Ext.applyIf(me, {
            items: items
        });
        me.callParent(arguments);
    }

});