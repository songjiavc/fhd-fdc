/**
 * 流程基本信息编辑页面
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.icm.icsystem.constructplan.form.DefectClearUpRiskLabel', {
	extend: 'Ext.form.FieldContainer',
	alias: 'widget.defectclearuprisklabel',
	layout : {
		type : 'hbox',
		align : 'stretch'
	},
	initParam:function(paramObj){
		var me = this;
		me.paramObj = paramObj;
	},
	// 初始化方法
	initComponent: function() {
		var me = this;
		me.riskId =Ext.widget('hiddenfield',{
			name : 'riskId',
			value : me.riskId
		});
		me.riskName = Ext.widget('displayfield',{
			fieldLabel : '风险名称',
			name : 'riskName',
			value : me.riskName
		});
		me.delBtn = Ext.widget('button',{
			text : '删除',
			maxWidth : 100,
			handler : function(){
				me.delSelf();
			} 
		});
		Ext.applyIf(me,{
			items : [
					me.riskId,
					me.riskName,
					{
                   		xtype:'tbspacer',
                   		flex:1
                	},me.delBtn]
		});
		me.callParent(arguments);
       },
	delSelf : function(){
    	var self = this;
    	var delUrl = '/risk/risk/removeRiskById.f';
    	FHD.ajax({
			url : __ctxPath + delUrl,
			params : {
				ids : self.riskId.getValue()
			},
			callback : function(data) {
				if (data) {//删除成功！
					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
				}
			}
		});
    	self.up('defectclearupform').addBtn.setDisabled(false);
    	self.removeAll(true);
    	self.up('fieldset').remove(self,true);
    }
});