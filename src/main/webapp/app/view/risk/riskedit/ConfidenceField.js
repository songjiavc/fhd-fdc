/**
 * 
 * 风险上报标准
 * 使用card布局
 * 
 * 下级有两个组件 潜在风险上报标准、历史风险上报标准
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.risk.riskedit.ConfidenceField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.confidencefield',
    requires: [
    ],
	autoHeight: true,
	layout: {
        type: 'hbox',
        align : 'stretch'
    },
    border : false,
    kpiname : '',
	initComponent: function() {
		//定量
		var me = this;
        me.delConfidence = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").delSelf()'>删除</a>"
        });
        /*隐藏kpiid*/
		me.conKpiIdField = Ext.widget('textfield',{
        	name : 'conKpiIdHid',
        	value : me.conKpiId,
        	hidden : true
        });
        /*隐藏预警区间*/
		me.conColorField = Ext.widget('textfield',{
        	name : 'conAlarmHid',
        	value : me.conColor,
        	hidden : true
        });
		me.max = Ext.widget('textfield',{
        	fieldLabel : '最大值',
        	name : 'max',
        	flex : .3
        	
        });
		me.min = Ext.widget('textfield',{
        	fieldLabel : '最小值',
        	name : 'min',
        	flex : .3
        });
        me.prob = Ext.widget('numberfield',{
        	fieldLabel : '概率',
        	name : 'prob',
        	flex : .3
        });
		Ext.apply(me, {
			items: [
				me.conKpiIdField,
				me.conColorField,
				me.max,
				me.min,
				me.prob,
               	{
                   	xtype: 'label',
                   	text: '%',
        			flex : .1
               	}
            ]
		});
        me.callParent(arguments);
    },
    delSelf : function(){
    	var self = this;
    	upPanel = self.up('fieldset');
    	upPanel.remove(self);
    }
});