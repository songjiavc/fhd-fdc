Ext.define('FHD.view.icm.standard.StandardPlanLeftPanel', {
    extend: 'FHD.ux.MenuPanel',
    alias: 'widget.standardplanleftpanel',
    
    requires: [
    	'FHD.view.icm.standard.StandardPlanDashboard',
    	'FHD.view.icm.standard.StandardManage',
    	'FHD.view.icm.standard.bpm.StandardBpmList'
    ],
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
        	autoScroll:true
        });
        me.standardplandashboard = {
	        text: '驾驶舱',
	        iconCls:'icon-btn-home',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){
				var standardplancenterpanel = me.up('panel').standardplancenterpanel;
				standardplancenterpanel.removeAll(true);
				standardplancenterpanel.add(Ext.widget('standardplandashboard'));
			}
	    };
	    me.standardbpmlist = {
	        text: '更新计划',
	        iconCls:'icon-btn-assessPlan',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){	
				var standardplancenterpanel = me.up('panel').standardplancenterpanel;
				standardplancenterpanel.removeAll(true);
				standardplancenterpanel.add(Ext.widget('standardbpmlist'));
			}
	    }
        me.callParent(arguments);
	    //驾驶舱
        if($ifAllGranted('ROLE_ALL_ENV_ICSTANDARD_DASHBOARD')){
        	me.add(me.standardplandashboard);
        }
        //更新计划
        if($ifAllGranted('ROLE_ALL_ENV_ICSTANDARD_LIST')){
        	me.add(me.standardbpmlist);
        }
        
    }
});