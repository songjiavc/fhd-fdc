
Ext.define('FHD.view.response.major.test.TaskForm',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.taskform',
 	border:false,
 	layout:{
		align: 'stretch',
		type: 'form'
	},
 	requires: [
	],
	
    initComponent: function () {
    	var me = this;
		var executionId = me.executionId;
		if(executionId!= null && executionId !=""){
			alert(executionId);
		}
		me.btnStartProcess = Ext.create('Ext.button.Button',{
            text: '启动流程',//提交按钮
            disabled: false,
            iconCls: 'icon-operator-submit',
            handler: function () {
            	var form = me.getForm();
            	if(form.isValid()){
            		FHD.submit({
						form:form,
						executionId:executionId,
						url:__ctxPath + "/majorResponse/startProcess",
						callback:function(data){
							alert(JSON.stringify(data))
						}
					});
            		return true;
            	}else{
            		alert("error");
            		return false;
            	}
            	
            }
        });
		me.btnSubmit = Ext.create('Ext.button.Button',{
            text: '提交',//提交按钮
            disabled: false,
            iconCls: 'icon-operator-submit',
            handler: function () {
            	var form = me.getForm();
            	if(form.isValid()){
            		FHD.submit({
						form:form,
						params:{
			            	executionId:me.executionId
			            },
						url:__ctxPath + "/majorResponse/formSubmit",
						callback:function(data){
							alert(JSON.stringify(data))
						}
					});
            		return true;
            	}else{
            		alert("error");
            		return false;
            	}
            	
            }
        });
		me.btnReturn = Ext.create('Ext.button.Button',{
            text: '驳回',//提交按钮
            disabled: false,
            iconCls: '',
            handler: function () {
            
            }
        });
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [
				{
					xtype:"fieldset",
					title:"重大风险信息",
					collapsible: true,
					collapsed: false,
					layout: 'column',
					defaults: {
	                    columnWidth : 1 / 3,
	                    margin: '7 30 3 30',
	                    labelWidth: 95
	                },
			        items :[{xtype:'textfield', fieldLabel : '重大风险', name:'riskName',allowBlank: false},
							{xtype:'textfield', fieldLabel : '上一办理人', name : 'prePeople',allowBlank: true,readOnly:true}]
				},
				{
					xtype:"fieldset",
					title:"下一审批人",
					collapsible: true,
					collapsed: false,
					layout: 'column',
					defaults: {
	                    columnWidth : 1 / 3,
	                    margin: '7 30 3 30',
	                    labelWidth: 95
	                },
			        items :[{xtype:'textfield', fieldLabel : '审批人', name:'people',allowBlank: false}]
				}
				
				],
				buttons: [
					me.btnStartProcess,me.btnSubmit,me.btnReturn
			    ]
		});
		
    	me.callParent(arguments);
    	FHD.ajax({
			url:__ctxPath + "/majorResponse/getMajorRisk",
            params:{
            	executionId:me.executionId
            },
            async: false,
            callback: function (data) {
            	me.getForm().setValues(data[0]);
            }
        });
    	
    }

});