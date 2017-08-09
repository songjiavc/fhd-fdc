/**
 * @author 宋佳
 * @date 2017-4-6
 * @description 评估模板设置中将公式跟模板挂钩
 */
Ext.define('FHD.view.risk.baseConfig.TemplateRelaFormula', {
    extend: 'Ext.panel.Panel',
    id : 'templaterelaformula',
    alias: 'widget.templaterelaformula',
    layout : 'column',
    border : false,
    height : 200,
    //保存风险水平公式  验证
    saveLevelValue: function(){
    	var me = this;
    	//显示
    	var formulartextarea = me.formularSetEditMain.rightPanel.formularSetEditFormPanel.formularTextArea;
    	var formularId = formulartextarea.formularId;
    	me.riskLevelCount.setValue(formulartextarea.getValue());//风险水平公式显示
    	me.editWin.hide();
    },
    //重置按钮
    reEdit: function(){
    	var me = this;
    	var formularTextArea = me.formularSetEditMain.rightPanel.formularSetEditFormPanel.formularTextArea;
    	formularTextArea.setValue("");
    	formularTextArea.formularId = "";
    },
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	me.riskLevelCount = Ext.widget('textfield', {
            fieldLabel: '风险水平计算公式',
            labelWidth : 200,
            readOnly: true,		//文本只读
            margin: '7 0 3 30',
            name: 'riskLevelFormula',
            columnWidth : .7
        });
        me.editButton = Ext.widget('button',{
            margin: '7 7 0 0',
            disabled : true,
            text : '公式编辑',
            //text: '公式编辑',
            columnWidth : .1,
            handler: function(){
            	me.formularSetEditMain = Ext.create('FHD.view.risk.assess.formularSet.FormularSetEditMain');
       			me.editWin = Ext.create('FHD.ux.Window', {
					title:'公式编辑',
		   		 	height: 400,
		    		width: 600,
		   			layout: 'fit',
		   			buttonAlign: 'right',
		   			parent : me,
		    		items: [me.formularSetEditMain],
		   			fbar: [
		   					{ xtype: 'button', text: '确定', handler:function(){me.saveLevelValue();}},
		   					{ xtype: 'button', text: '重置', handler:function(){me.reEdit();}},
		   					{ xtype: 'button', text: '关闭', handler:function(){me.editWin.hide();}}
						  ]
				}).show();
            }
        });
        
        me.saveButton = Ext.widget('button',{
            margin: '7 7 0 0',
            //text: '公式编辑',
            text : '保存',
            columnWidth : .1,
            disabled : true,
            handler: function(){
            	var templateId = me.up('template').down('fhdeditorgrid').getSelectionModel().getSelection()[0].data.id;
            	FHD.ajax({
					url : __ctxPath + '/risk/mergeTemplateRelaCaluFormular.f',//判断是否发起工作流
					params : {
						templateId : templateId,
						formularContext :　me.riskLevelCount.getValue()
					},
					callback: function (data) {
			           if(data.success){
			           		FHD.alert('模板对应计算公式保存成功!');
			           		//刷新模板列表
			           		
			           		me.up('template').down('fhdeditorgrid').getStore().reload();
			           }else{
			           		FHD.alert('保存失败!');
			           }
			        }
				});
            }
        });
        
        Ext.applyIf(me,{
        	items : [me.riskLevelCount,me.editButton,me.saveButton]
        });
        
        me.callParent(arguments);
        
    }
});