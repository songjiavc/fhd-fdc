/*
 * add by 宋佳
 * 
 * 登录菜单树展示
 */

Ext.define('FHD.view.firstpage.FirstAccordionPanel',{
	extend : 'Ext.panel.Panel',
	alias: 'widget.firstaccordionpanel',
    defaults: {
        // applied to each contained panel
        bodyStyle: 'padding:15px'
    },
    layout: {
        // layout-specific configs go here
        type: 'accordion',
        titleCollapse: true,
        animate: false,
        titleCollapse : false,
        activeOnTop : false,
        hideCollapseTool : true
    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        Ext.applyIf(me, {
            items: me.getItems()
        });
        me.callParent(arguments);
    },
    getItems : function(){
    	var items = [];
    	jQuery.ajax({
    		type: "POST",
    		url : __ctxPath + '/sys/menu/getAuthorityTreeLoader.f',
    		async:false,
    		success: function(data){
    			Ext.each(data,function(item){
    				var firstTreepanel = Ext.create('FHD.view.firstpage.FirstTreePanel',{
        				title : item.text,
        				header : {
        					iconCls : item.iconCls
        				},
        				extraParams : {params : item.id}
        			});
    				Ext.Array.push(items,firstTreepanel);
    			});
    		},
    		error: function(){
    			FHD.notification("<font color=red>首页读取失败，请确认该用户数据！</font>",FHD.locale.get('fhd.common.error'));
    		}
    	});
    	return items;
    }
});

