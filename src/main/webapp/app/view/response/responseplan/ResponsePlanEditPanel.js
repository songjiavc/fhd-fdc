Ext.define('FHD.view.response.responseplan.ResponsePlanEditPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.responseplaneditpanel',
    
    activeItem: 0,
    
    
    requires: [
       'FHD.view.response.responseplan.ResponsePlanList',
       'FHD.view.response.responseplan.ResponsePlanPanel'
    ],
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        //评价计划列表
        me.responseplanlist = Ext.widget('responseplanlist');
        //评价计划第一步container
        me.responseplanpanel = Ext.widget('responseplanpanel');
        
        Ext.apply(me, {
            items: [
                me.responseplanlist,
                me.responseplanpanel
            ]
        });

        me.callParent(arguments);
    },
    
    reloadData:function(){
    	var me=this;
    	
    	me.responseplanlist.reloadData();
    }
});