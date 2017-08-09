Ext.define('FHD.view.bpm.processinstance.ProcessInstanceTabInfo', {
    extend: 'Ext.panel.Panel',
	alias: 'widget.ProcessInstanceTabInfo',
	layout:{
		type:'vbox',
		align:'stretch'
	},
	title:"工作流日志",
	jbpmHistProcinstId:"",
	processInstanceId:"",
	width:'auto',
    model:"show",
    processInstanceFieldSet:null,
	jbpmHistProcinstGrid:null,
	reloadParentData:function(){
		
	},
    initComponent: function() {
        var me = this;
        var JbpmHistProcinstForm=Ext.create("FHD.view.bpm.processinstance.JbpmHistProcinstForm",{
        	jbpmHistProcinstId:me.jbpmHistProcinstId,
        	model:me.model
        });
        me.processInstanceFieldSet=Ext.create('Ext.form.FieldSet',{
			title:'流程实例',
			margin:'5 5 5 5',
			collapsed:true,
			collapsible: true,
			items:[
				JbpmHistProcinstForm
			]
		});
		me.jbpmHistProcinstGrid=Ext.create('FHD.view.bpm.processinstance.JbpmHistActinstPage',{
			flex:1,
			jbpmHistProcinstId:me.jbpmHistProcinstId,
			processInstanceId:me.processInstanceId,
			model:me.model,
        	reloadParentData:function(){
        		me.reloadParentData()
        	}
		});
        Ext.applyIf(me, {
            items: [
            	me.processInstanceFieldSet,
            	me.jbpmHistProcinstGrid
            ]
        });
        me.callParent(arguments);
		
    }
});