/**
 * 提交主面板
 * 
 * @author	王再冉
 */
Ext.define('FHD.view.risk.assess.formulatePlan.FormulateSubmitMainPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.formulatesubmitmainPanel',
    
    requires: [
               'FHD.view.risk.assess.formulatePlan.FormulateApproverEdit'
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//列表
    	/*me.formulatesubmitgrid = Ext.widget('formulatesubmitgrid',{
    		height:180
    	});*/
    	
    	//审批人表单
    	me.formulateapproveredit = Ext.widget('formulateapproveredit');
    	
    	Ext.apply(me, {
            border:false,
     		layout: {
     	        type: 'fit',
     			align: 'stretch'
     	    },
     	    items:[me.formulateapproveredit]
        });
    	
        me.callParent(arguments);
        
    }
});