/**
 * 
 * 
 * @author 王再冉
 */
Ext.define('FHD.view.risk.assess.WeightAlarmPlanPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.weightAlarmPlanPanel',
    border: false,
    
   
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        
        	var Store = Ext.create('Ext.data.Store', {//
        	    fields: ['id', 'name'],
        	    proxy: {
        	         type: 'ajax',
        	         url: __ctxPath + '/sys/assess/findalarmplan.f',
        	         reader: {
        	             type: 'json',
        	             root: 'datas'
        	         }
        	     }, 
        	    autoLoad:true
        	});
        	Store.load({
  					callback : function(records, options, success) {
  						FHD.ajax({//ajax调用
    						url : __ctxPath + '/sys/assess/queryalarmplanvalue.f',//查询默认值
    						params : {
    						},
    						callback : function(data){
    							if(data){
    								me.riskAlarmplan.setValue(data[0].alarmid);
    							}
    						}
    					});
  					} });
        	//
        	me.riskAlarmplan = Ext.create('Ext.form.field.ComboBox', {
        		    store: Store,
        		    fieldLabel: '告警方案',
        		    labelWidth : 100,
        		    editable:false,
        		    margin: '7 30 3 30',
        		    queryMode: 'local',
        		    name:'riskAlarmPlan',
        		    displayField: 'name',
        		    valueField: 'id',
        		    triggerAction :'all',
        		    //columnWidth:.5,
        		    listeners:{
        		    	select: function(){
        		    		var departmentGrid = me.up('weightingSetMain').departmentGrid
        		    		me.body.mask("保存中...","x-mask-loading");
        		    		FHD.ajax({//查询所有打分部门id
	    						url:__ctxPath + '/sys/assess/saveriskalarmplan.f',
	    						params : {
    								alarmId:me.riskAlarmplan.value,
    								id: departmentGrid.store.data.items[0].data.id
    							},
	    						callback:function(data){
									me.body.unmask();
									FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
	    						}
	    					});
        		    	}
        		    }
        		});
        	
        Ext.applyIf(me, {
            border: false,
            bodyPadding: "5 5 5 5",
            items: [me.riskAlarmplan]
            
        });
        
        me.callParent(arguments);
    }
});