Ext.define('FHD.view.icm.icsystem.constructplan.ConstructPlanEditPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.constructplaneditpanel',
    
    activeItem: 0,
    paramObj:{
    	editflag:false,
    	businessId:''
    },
    
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        //评价计划列表
        me.constructplanlist = Ext.create('FHD.view.icm.icsystem.constructplan.ConstructPlanList');
        //评价计划第一步container
        me.constructplanpanel = Ext.create('FHD.view.icm.icsystem.constructplan.ConstructPlanPanel');
        
        Ext.apply(me, {
            items: [
                me.constructplanlist,
                me.constructplanpanel
            ]
        });

        me.callParent(arguments);
    },
    //cardpanel切换
    navBtnHandler: function (index) {
    	var me = this;
        me.setActiveItem(index);
    },
    reloadData:function(){
    	var me=this;
    	//重新加载报告列表
    	me.constructplanlist.reloadData();
    	//重新加载mainpanel
     	me.constructplanpanel.initParam(me.paramObj);
     	me.constructplanpanel.reloadData();
    }
});