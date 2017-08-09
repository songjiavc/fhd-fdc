Ext.define('FHD.view.response.major.scheme.MajorRiskItemsAddForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorriskitemsaddform',
    defaults: {
        columnWidth : 1,
        margin: '7 30 3 30',
        labelWidth: 95
    },
    layout: {
	        type: 'column'
	    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.description = {xtype:'textareafield', fieldLabel : '风险事项简述', name:'description',allowBlank: false,rows:1,cols:100};
        me.flow = {xtype:'textareafield', fieldLabel : '相关流程', name : 'flow',allowBlank: true,rows:1,cols:100};
        me.reason = {xtype:'textareafield', fieldLabel : '产生动因', name : 'reason',allowBlank: true,rows:1,cols:100};
        me.filedId = Ext.widget('hiddenfield',{name:"id",value:''});
        me.itemType = Ext.widget('hiddenfield',{name:"type",value:''});
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.description,me.flow,me.reason,me.filedId,me.itemType]
        });
        me.callParent(arguments);
        /*me.form.load({
	        url: __ctxPath + '/majorResponse/getMajorRiskTaskSelectEmp',
	        params:{businessId: me.businessId},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });*/
      
    }
});