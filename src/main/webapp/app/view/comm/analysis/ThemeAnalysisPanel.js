Ext.define('FHD.view.comm.analysis.ThemeAnalysisPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.themeanalysispanel',
    
    requires: [
       'FHD.view.comm.analysis.ThemeAnalysisCardPanel',
       'FHD.ux.icm.common.FlowTaskBar'
    ],
	autoScroll:true,
    
    initComponent: function() {
        var me = this;
        
        me.cardpanel = Ext.widget('themeanalysiscardpanel',{
        	flex : 1,
        	businessId:me.businessId,
        	editflag:me.editflag
        });

        Ext.applyIf(me, {
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
    		items:[Ext.widget('panel',{border:false,items:Ext.widget('flowtaskbar',{
        		jsonArray:[
  		    		{index: 1, context:'1.选择布局',status:'current'},
  		    		{index: 2, context:'2.选择数据源',status:'undo'},
  		    		{index: 3, context:'3.选择图表',status:'undo'}
  		    	]
  	    	})}),me.cardpanel]
        });
    
        me.callParent(arguments);
    },
    reloadData:function(){
    	var me=this;
    	
    	var themeanalysismainpanel = me.up('themeanalysismainpanel');
    	if(themeanalysismainpanel){
    		me.businessId = themeanalysismainpanel.paramObj.businessId;
    		me.editflag = themeanalysismainpanel.paramObj.editflag;
    		//cardpanel内容刷新
        	me.cardpanel.loadData(me.businessId,me.editflag);
    	}
    }
});