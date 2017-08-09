Ext.define('FHD.view.comm.analysis.ThemeAnalysisMainPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.themeanalysismainpanel',
    
    activeItem: 0,
    border:false,
    paramObj:{
    	editflag:false,
    	businessId:''
    },
    requires: [
       'FHD.view.comm.analysis.ThemeAnalysisList',
       'FHD.view.comm.analysis.ThemeAnalysisPanel'
    ],
    
    // 初始化方法
    initComponent: function() {
        var me = this;

        //主题分析列表
        me.themeanalysislist = Ext.widget('themeanalysislist');
        //主题分析container
        me.themeanalysispanel = Ext.widget('themeanalysispanel',{
        });
        
        Ext.apply(me, {
            items: [
                me.themeanalysislist,
                me.themeanalysispanel
            ]
        });

        me.callParent(arguments);
    },
    //cardpanel切换
    navBtnHandler: function (index) {
    	var me = this;
        me.setActiveItem(index);
        if(0 == index){
        	//重新加载主题分析列表
        	me.themeanalysislist.reloadData();
        }else if(1 == index){
        	//重新加载mainpanel
         	me.themeanalysispanel.reloadData();
        }
    },
    reloadData:function(){
    	var me=this;
    	
    	//重新加载报告列表
    	me.themeanalysislist.reloadData();
    	//重新加载mainpanel
     	me.themeanalysispanel.loadData();
    }
});