/**
 * 
 * 
 */

Ext.define('FHD.view.risk.assess.formularSet.FormularSetEditFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.formularSetEditFormPanel',
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        //公式区
        me.formularTextArea = Ext.widget('textareafield', {
            xtype: 'textareafield',
            //value: '',
            //disabled: true,
            readOnly: true,		//文本只读
            border: 0,
            margin: '1 1 1 1',
            name: 'formulartextarea',
            formularId: ''
        });
        
        Ext.apply(me, {
        	border:false,
        	defaults : {columnWidth : 1/1,margin : '2 2 2 2'},//每行显示一列，可设置多列
			layout : {
				type : 'vbox',
	        	align:'stretch'
	        },
			flex:1,
			title : FHD.locale.get('fhd.formula.formulaArea'),
            items : [me.formularTextArea]
        });
		
       me.callParent(arguments);
        //公式区赋初始值
       var templaterelaformula = Ext.getCmp('templaterelaformula');
   	   me.formularTextArea.setValue(templaterelaformula.riskLevelCount.getValue());
       me.formularTextArea.on('resize',function(p){
    		me.formularTextArea.setHeight(me.getHeight()-30);
    	});
    }

});