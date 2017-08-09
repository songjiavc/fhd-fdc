/**
 * 方案制定表单
 * 
 */
Ext.define('FHD.view.response.major.scheme.MajorRiskSchemeAddForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorriskschemeaddform',
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.states = Ext.create('Ext.data.Store', {
		    fields: ['id', 'name'],
		    data : [
		        {"id":"风险规避", "name":"风险规避"},
		        {"id":"风险承担", "name":"风险承担"},
		        {"id":"风险转移", "name":"风险转移"}
		    ]
		});
        var startTime = {xtype:'datefield', fieldLabel : '实施时间', name : 'startTimeStr',allowBlank: true  ,format: 'Y-m-d'};
        var finishTime = {xtype:'datefield', fieldLabel : '完成时间', name : 'finishTimeStr',allowBlank: true,format: 'Y-m-d'}
		me.dateContainer = {
				xtype:'container',
				layout:'hbox',
				defaults: {
			        labelWidth: 100,
			        margin: '0 30 3 0'
			    },
				items:[startTime,finishTime]
		};
        me.description = {xtype:'textareafield', fieldLabel : '风险简述', name:'description',allowBlank: false,rows:1,cols:100};
        me.analysis = {xtype:'textareafield', fieldLabel : '风险分析', name : 'analysis',allowBlank: true,rows:1,cols:100};
        me.target = {xtype:'textareafield', fieldLabel : '管理目标', name : 'target',allowBlank: true,rows:1,cols:100};
        me.strategy = {xtype:'combobox',fieldLabel:'应对策略',name:'strategy',store:me.states,queryMode: 'local',displayField: 'name',valueField: 'id',}
        me.filedId = Ext.widget('hiddenfield',{name:"id",value:''});
        me.schemeType = Ext.widget('hiddenfield',{name:"type",value:''});
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.description,me.analysis,me.target,me.strategy,me.dateContainer,me.filedId,me.schemeType]
        });
        me.callParent(arguments);
        me.form.load({
	        url: __ctxPath + '/majorResponse/loadScheme',
	        params:{
	        	planId: me.businessId,
	        	executionId: me.executionId,
	        	empType :me.empType
	        },
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });
      
    }
});