Ext.define('FHD.view.response.PreplanListPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.preplanlistpanel',
    
    activeItem: 0,
    paramObj:{
    	editflag:false,
    	businessId:''
    },
    requires: [
       'FHD.view.response.PreplanForRiskList',
       'FHD.view.response.PreplanForRiskForm',
       'FHD.view.response.PreplanForTargetList'
    ],
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        //评价计划列表
        me.preplanforrisklist = Ext.widget('preplanforrisklist');
        //评价计划第一步container
        me.preplanforriskform = Ext.widget('preplanforriskform');
        
        Ext.apply(me, {
            items: [
                me.preplanforrisklist,
                me.preplanforriskform
            ]
        });

        me.callParent(arguments);
    },
    
    reloadData:function(){
    	var me=this;
    }
});