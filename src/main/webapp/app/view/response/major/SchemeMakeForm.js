
Ext.define('FHD.view.response.major.SchemeMakeForm',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.schememakeform',
 	border:false,
 	layout:{
		align: 'stretch',
		type: 'form'
	},
 	requires: [
	],
	
    initComponent: function () {
    	var me = this;
		
		me.btnSubmit = Ext.create('Ext.button.Button',{
            text: '提交',//提交按钮
            disabled: true,
            iconCls: 'icon-operator-submit',
            handler: function () {
            
            }
        });
		
		me.states = Ext.create('Ext.data.Store', {
		    fields: ['abbr', 'name'],
		    data : [
		        {"abbr":"风险规避", "name":"风险规避"},
		        {"abbr":"风险承担", "name":"风险承担"},
		        {"abbr":"风险转移", "name":"风险转移"}
		    ]
		});
		
		me.dateContainer = {
				xtype:'container',
				layout:'hbox',
				defaults: {
			        labelWidth: 95,
			        margin: '0 30 5 0'
			    },
				items:[
					{xtype:'datefield', fieldLabel : '实施时间', name : 'contactName',allowBlank: true  ,format: 'Y-m-d'},
					{xtype:'datefield', fieldLabel : '完成时间', name : 'contactName',allowBlank: true,format: 'Y-m-d'}
				]
		};
		
		
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [
				{
					xtype:"fieldset",
					title:"重大风险信息",
					collapsible: true,
					collapsed: true,
					layout: 'column',
					defaults: {
	                    columnWidth : 1 / 3,
	                    margin: '7 30 3 30',
	                    labelWidth: 95
	                },
			        items :[{xtype:'displayfield', fieldLabel : '重大风险', name:'planName',allowBlank: false},
							{xtype:'displayfield', fieldLabel : '部门', name : 'beginendDateStr',allowBlank: true},
							{xtype:'displayfield', fieldLabel : '责任类型', name : 'contactName',allowBlank: true}]
				},
				{
					xtype:"fieldset",
					title:"方案信息",
					collapsible: true,
		            collapsed : false,//初始化收缩
		            defaults: {
		                    columnWidth : 1 / 2,
		                    margin: '7 30 3 30',
		                    labelWidth: 95
		                },
		            layout:'vbox',
			        items :[{xtype:'textareafield', fieldLabel : '风险简述', name:'planName',allowBlank: false,rows:1,cols:50},
							{xtype:'textareafield', fieldLabel : '风险分析', name : 'beginendDateStr',allowBlank: true,rows:1,cols:50},
							{xtype:'textareafield', fieldLabel : '管理目标', name : 'contactName',allowBlank: true,rows:1,cols:50},
							{xtype:'combobox',fieldLabel:'应对策略',name:'celve',store:me.states,queryMode: 'local',displayField: 'name',valueField: 'abbr',},
							me.dateContainer
							]
				}
				
				],
				buttons: [
			        { xtype: "button", text: "保存" },
			        { xtype: "button", text: "返回" }
			    ]
		});
		
    	me.callParent(arguments);
    	
    }

});