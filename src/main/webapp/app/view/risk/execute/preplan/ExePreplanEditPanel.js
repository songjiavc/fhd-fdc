/**
 * 控制措施编辑面板
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.execute.preplan.ExePreplanEditPanel',{
    extend : 'Ext.form.Panel',
    alias: 'widget.exepreplaneditpanel',
    requires: [
        'FHD.view.risk.execute.preplan.ExePreplanRelaSolutions'
    ],
    autoScroll:true,
    exeId : '',//执行预案ID
    layout: {
                type : 'column'
            },
    defaults : {
        columnWidth : 1 / 1
    },
    bodyPadding:'0 60 3 60',
    border:false,
    initComponent : function() {
        var me = this;
        me.exepreplanrelasolutions = Ext.widget('exepreplanrelasolutions');
        me.fieldSet = Ext.widget('fieldset',{
                title: '应对措施执行信息',
                collapsible: true,
                margin: '5 5 0 5',
                layout: {
         	        type: 'form'
         	    },
         	    items : [me.exepreplanrelasolutions]
        });
        
        me.bbar=['->',{
        		iconCls : 'icon-arrow-undo',
        		text:'返回',
        		handler : function(){
	        		me.backPage();
        		}
        	},'-',{
        		iconCls : 'icon-save',
        		text:'保存',
        		handler : function(){
        			me.savePage();
        		}
        	},'-',{
        		iconCls : 'icon-operator-submit',
        		text:'提交',
        		handler : function(){
        			me.subPage();
        		}
        }]
        me.preplanEndDate = Ext.widget('datefield', {
            format: 'Y-m-d',
            name: 'preplanEndDate',
            margin: '7 10 10 20',
            fieldLabel: '实际完成时间' + '<font color=red>*</font>', 
            width:400,
            value : new Date(),
            allowBlank: false
        });
        
		me.executestatus = Ext.create('Ext.form.RadioGroup', {
			fieldLabel: "执行状态",
	   		vertical: true,
	   		margin: '7 10 10 20',
	   		width: 400,
	   		allowBlank: false,
        	items: [
             	{ boxLabel: "已按照要求执行预案", name: 'executestatus', inputValue: '0',checked:true},
	            { boxLabel: "由于XX原因与预案不符", name: 'executestatus', inputValue: '1'}]
        });
        
        me.executestatusdesc = Ext.widget('textareafield', {
            fieldLabel:'执行状态说明',
            labelAlign : 'left',
        	margin: '7 10 10 20',
            row : 5,
            width: 1000,
            name : 'executestatusdesc',
            labelWidth : 100
        });
        
        me.executedesc = Ext.widget('textareafield', {
            fieldLabel:'执行情况说明' + '<font color=red>*</font>',
            labelAlign : 'left',
        	margin: '7 10 10 20',
            row : 5,
            width: 1000,
            name : 'executedesc',
            allowBlank: false,
            labelWidth : 100
        });
        
        me.summary = Ext.widget('textareafield',{
            fieldLabel:'心得和建议',
            labelAlign : 'left',
            margin: '7 10 10 20',
            row : 5,
            width: 1000,
            name : 'summary',
            labelWidth : 100
        });
        
        me.fieldSet2 = Ext.widget('fieldset',{
                title: '预案执行信息',
                collapsible: false,
                margin: '5 5 0 5',
                layout: {
         	        type: 'vbox'
         	    },
         	    items : [me.preplanEndDate,me.executestatus,me.executestatusdesc,me.executedesc,me.summary]
        });
       
		Ext.applyIf(me, {
            border:false,
            bbar : me.bbar,
            layout: {
                type : 'vbox'
            },
            collapsed : false,
            items:[me.fieldSet,me.fieldSet2 ]
	        });
	        
	    me.callParent(arguments);
		},
    savePage:function(){
    	var me = this;
    	var form = me.getForm();
    	if(form.isValid()){
    		FHD.submit({
    			form : form,
    			url : 'chf/execute/preplan/saveexepreplan.f',
    			params : {
    				exeId : me.exeId
    			},
    			callback : function(date){
    				if(date){
//    					Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.common.operateSuccess'));
    				}
    			}
    		
    		});
    	}
    },
    subPage:function(){
    	var me = this;
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : "提交后会保存当前数据且不可进行修改，是否继续",
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {// 确认删除
					me.savePage();
					FHD.ajax({//ajax调用
						url : 'chf/execute/preplan/subexepreplan.f',
						params : {
							exeId : me.exeId
						},
						callback : function(data){
							me.backPage();
						}
					});
				}
			}
		});
    },
    
    backPage : function(){
    	var me = this;
    	me.up('exepreplanmainpanel').exepreplangrid.reloadData();
    	me.up('exepreplanmainpanel').showexepreplangrid();
    },
    
    cleanValue : function(exeId){
    	var me = this;
    	me.preplanEndDate.setValue(new Date());
    	me.executedesc.setValue("");
    	me.executestatusdesc.setValue("");
    	me.summary.setValue("");
    	me.executestatus.items.items[0].setValue(true);
    	me.exeId = exeId;
	    me.exepreplanrelasolutions.reloadData(exeId);
	    me.getForm().clearInvalid();
    },
    
    editLoadForm : function(exeId){
    	var me = this;
    	me.cleanValue(exeId);
		if(typeof(me.exeId) != 'undefined') {
    		me.form.load({
    	        url:'chf/execute/preplan/editloadform.f',
    	        params:{exeId:me.exeId},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        }
    	    });
    	}
    }
 });