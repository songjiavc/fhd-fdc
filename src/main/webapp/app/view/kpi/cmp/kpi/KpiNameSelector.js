Ext.define('FHD.view.kpi.cmp.kpi.KpiNameSelector', {
    extend: 'Ext.form.FieldContainer',
    alias : 'widget.kpinameselector',

    labelWidth: 100,
    
    labelAlign:'left',
    
    defaultValue:'0yn_y',
    
    height:75,

    /**
     * field字段名称
     */
    fieldLabel: '',

    /**
     * 名称控件name属性
     */
    textfieldname: '',
    /**
     * 是否使用默认名称数据字典name属性
     */
    is_default: '',
    
    onDestroy:function(){
    	if(this.isDefaultDictRadio){
    		this.isDefaultDictRadio.destroy();
    	}
    	this.callParent(arguments);
    },

    initComponent: function () {
        var me = this;
        
        me.textfield = Ext.create('Ext.form.field.TextArea',{
        	rows: 3,
            labelAlign: me.labelAlign,
            name: me.textfieldname,
            maxLength: 255,
            allowBlank: false
            ,disabled: false
        });
        
        me.isDefaultDictRadio = Ext.create('FHD.ux.dict.DictRadio',{
        	labelWidth: 105,
            name: me.is_default,
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel: '',
            defaultValue: me.defaultValue,
            labelAlign: me.labelAlign
        	
        });
        
        Ext.applyIf(me, {

            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            labelWidth: me.labelWidth,
            fieldLabel: me.fieldLabel,
            height:me.height,
            items: [me.textfield]
        });
        me.callParent(arguments);
    }


});