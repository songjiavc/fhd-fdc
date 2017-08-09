/**
 * 流程基本信息编辑页面
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.icm.icsystem.constructplan.form.DefectClearUpRiskDefineForm', {
	extend: 'Ext.form.Panel',
	alias: 'widget.defectclearupriskdefineform',
	requires: [
		'FHD.view.risk.cmp.form.RiskShortForm',
		'FHD.view.icm.icsystem.constructplan.form.DefectClearUpRiskLabel'
	],
	layout : 'fit',
	initParam:function(paramObj){
		var me = this;
		me.paramObj = paramObj;
	},
	// 初始化方法
	initComponent: function() {
		var me = this;
		me.riskDefineForm = Ext.widget('riskshortform',{type : 're',border : false});
		me.bbar={
			items: [
				'->',{
					text: '保存',
    				iconCls: 'icon-control-stop-blue',
    				handler: function () {
					    me.save();
    				}
					}]
		};
		Ext.applyIf(me,{
			items : [me.riskDefineForm]
		});
		me.callParent(arguments);
	},
	save : function() {
		var me = this;
		var form = me.riskDefineForm.getForm();
		if (form.isValid()) {
			FHD.submit({
				form : form,
				url : __ctxPath + me.riskDefineForm.saveUrl,
				params : {
					isRiskClass : me.riskDefineForm.type, // 风险还是风险事件
					state:me.riskDefineForm.state
				},
				callback : function(data) {
					if(data.success){
						var labelForm = Ext.widget('defectclearuprisklabel',{
							riskId : data.id,
							riskName : data.name
						});
						var riskIdentFieldSet = me.up('window').upPanel.riskIdentFieldSet;
						riskIdentFieldSet.insert(riskIdentFieldSet.items.length-1,labelForm);
						me.up('window').upPanel.addBtn.setDisabled(true);
						me.up('window').close();
					}
				}
			});
		}else{
			return false;
		}
	}
});