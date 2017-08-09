Ext.define('FHD.view.bpm.processinstance.untread.UntreadActinstForm', {
    extend: 'Ext.form.Panel',
	alias: 'widget.untreadactinstform',
	border:false,
	layout : {
		type : 'column'
	},
	jbpmHistActinstId:'',
	processInstanceId:'',
	a_submitData:function(){
		
	},
	reloadData:function(){
		var me=this;
	},
	submitData:function(){
		var me=this;
		//提交from表单
        var form = me.getForm();
        if(form.isValid()){
	        FHD.submit({
		        form: form,
		        url: __ctxPath + '/jbpm/processInstance/UntreadToActinst.f',
		        callback: function (flag) {
		        	me.a_submitData();
		        }
	        });
        }
	},
    
    initComponent: function() {
		var me = this;
		me.idTextfield = Ext.create("Ext.form.field.Text",{
    		labelWidth : 95,
			disabled : false,
			name : 'id',
			hidden : true,
			value:me.jbpmHistActinstId
		});
		me.executionIdTextfield = Ext.create("Ext.form.field.Text",{
    		labelWidth : 95,
			disabled : false,
			name : 'executionId',
			hidden : true,
			value:me.executionId
		});
		me.processInstanceIdTextfield = Ext.create("Ext.form.field.Text",{
    		labelWidth : 95,
			disabled : false,
			name : 'processInstanceId',
			hidden : true,
			value:me.processInstanceId
		});
		me.untreadToActinst = Ext.create('Ext.form.ComboBox',{
		    fieldLabel: '退回到' + '<font color=red>*</font>',
			store:Ext.create('Ext.data.Store', {
				fields: [
					{name: 'id', type: 'string'},
					{name: 'name',  type: 'string'}
				],
				proxy : {
					type : 'ajax',
					url : __ctxPath + '/jbpm/processInstance/findUntreadToActinsts.f',
					extraParams:{jbpmHistActinstId:me.jbpmHistActinstId},
					timeout: 1000000,
					reader : {
						type:'json',
						root:'datas'
					}
				},    
				autoLoad : true
			}),
			emptyText:'请选择',
			allowBlank : false,
			valueField : 'id',
			name:'untreadToActinst',
			displayField : 'name',
			editable : false
		});
		Ext.applyIf(me, {
			defaults: {
				labelWidth : 80,
				lblAlign : 'right',
				maxLength : 200,
				columnWidth: 1,
				margin:'5 50 5 50'
			},
		    items: [
		    	me.idTextfield,
		    	me.executionIdTextfield,
		    	me.processInstanceIdTextfield,
				me.untreadToActinst
		    ]
		});
		me.callParent(arguments);
		me.reloadData();
    }

});