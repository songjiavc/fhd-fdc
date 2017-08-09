/**
 * 系统菜单主面板
 * 
 * @author 邓广义
 */
Ext.define('FHD.view.icm.statics.IcmMyDatasTreePanel', {
    extend: 'FHD.ux.org.DeptTree',
    alias: 'widget.icmmydatastreepanel',
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	
    	

    	Ext.apply(me, {
    		checkable:false,
    		subCompany:true,
    		rootVisible: true,
    		width:260,
    		split: true,
           	collapsible : true,
           	border:true,
           	region: 'west',
           	multiSelect: true,
           	rowLines:false,
          	singleExpand: false,
           	checked: false,
           	listeners : {
	   			'itemclick' : function(view,re){
	   				if(re.data&&re.data.id){
	   					if(me.up('icmmydatas')){
	   						me.up('icmmydatas').orgId=re.data.id;
	   						me.up('icmmydatas').orgType=re.data.type;
	   						me.up('icmmydatas').reloadData();
	   					}
	   					if(me.up('icmmysearch')){
	   						me.up('icmmysearch').orgId=re.data.id;
	   						me.up('icmmysearch').orgType=re.data.type;
	   						me.up('icmmysearch').reloadData();
	   					}
	   				}
	   			}
           	}
        });
    	
        me.callParent(arguments);
    }
});