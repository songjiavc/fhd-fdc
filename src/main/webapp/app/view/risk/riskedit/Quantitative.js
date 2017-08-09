/**
 * 
 * 风险上报标准
 * 使用card布局
 * 
 * 下级有两个组件 潜在风险上报标准、历史风险上报标准
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.risk.riskedit.Quantitative', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.quantitative',
    requires: [
    ],
    border : 0,
	autoWidth: true,
	autoHeight: true,
	autoScroll : true,
	initParam : {},
	layout: {
		type: 'hbox'
	},
	initComponent: function() {
		/*定性 qualitative  定量quantification*/
		var me = this;
		//id 隐藏域
		me.pk = Ext.widget('hiddenfield', {
			name : 'pk', 
			value: me.initParam.id
		});
		me.name = Ext.widget('textfield', {
			fieldLabel : '名称',
			name : 'name', 
			value: me.initParam.name,
			width:300
		});
		//定性
		me.desc = Ext.widget('textfield', {
			fieldLabel : '描述',
			name : 'desc', 
			value: me.initParam.desc,
			width:300
		});
		me.delQuantitative = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").delSelf()'>删除</a>"
        });
        Ext.apply(me, {
            items : [
            	me.pk,
            	me.name,
            	me.desc,
            	{
                   xtype:'tbspacer',
                   flex:1
                },
                me.delQuantitative
            	]
        });
        me.callParent(arguments);
    },
    delSelf : function(){
    	var me = this;
    	me.removeAll(true);
    	upPanel = me.up('fieldset');
    	upPanel.remove(me,true);
    }
    
});