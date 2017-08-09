Ext.define('FHD.view.icm.assess.AssessPlanMainPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.assessplanmainpanel',
    
    activeItem: 0,
    paramObj:{
    	editflag:false,
    	businessId:'',
    	assessMeasureId:'',
    	assessTypeId:''
    },
    border:false,
    
    // 初始化方法
    initComponent: function() {
        var me = this;

        //评价计划列表
        me.assessplanlist = Ext.create('FHD.view.icm.assess.AssessPlanList');
        //评价计划第一步container
        me.assessplanpanel = Ext.create('FHD.view.icm.assess.AssessPlanPanel',{
        	businessId:me.paramObj.businessId,
        	editflag:me.paramObj.editflag
        });
        
        Ext.apply(me, {
            items: [
                me.assessplanlist,
                me.assessplanpanel
            ]
        });

        me.callParent(arguments);
    },
    //cardpanel切换
    navBtnHandler: function (index) {
    	var me = this;
    	
        me.setActiveItem(index);
        if(0 == index){
        	//重新加载报告列表
        	me.assessplanlist.reloadData();
        }else if(1 == index){
        	//重新加载mainpanel
         	me.assessplanpanel.loadData();
        }
    },
    reloadData:function(){
    	var me=this;
    	
    	//重新加载报告列表
    	me.assessplanlist.reloadData();
    	//重新加载mainpanel
     	me.assessplanpanel.loadData();
    }
});