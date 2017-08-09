Ext.define('FHD.view.icm.icsystem.FlowTabMainPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.flowtabmainpanel',
    
    requires: [
        'FHD.view.icm.icsystem.FlowTabPanel',
        'FHD.ux.NavigationBars'
    ],
    
    style: 'background-color:White',
    
    initComponent: function() {
        var me = this;
        
        me.navigationBar = Ext.create('FHD.ux.NavigationBars',{
    	   type: 'process',
    	   inss : 'flowtree',
    	   style: 'background-color:White'
        });
        
        me.flowtabpanel = Ext.widget('flowtabpanel',{
        	flex:1
        });
        
        Ext.apply(me, {
        	layout:{
                align: 'stretch',
                type: 'vbox'
    		},
    		items:[
    			{
	    			xtype:'box',
	    			height:28,
	    			//style : 'border-left: 1px  #99bce8 solid;',
	    			html:'<div id="'+me.id+'_div" class="navigation"></div>'
    			},
    			me.flowtabpanel
    		]
        });
        
        me.callParent(arguments);
        //me.getTabBar().insert(0,{xtype:'tbfill'});
    },
    reLoadNav:function(id){
    	var me = this;
    	//renderhtml时把tree的id传入进去，到时候点击时可以调用约定的reloadNavigator方法
    	me.navigationBar.renderHtml(me.id+"_div",id, '', 'process',me.up('flowmainmanage').flowtree.id);
    },
    reloadData:function(){
    	var me=this;
    	
    }
});