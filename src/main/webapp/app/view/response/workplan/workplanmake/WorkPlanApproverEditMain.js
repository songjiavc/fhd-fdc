Ext.define('FHD.view.response.workplan.workplanmake.WorkPlanApproverEditMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.workPlanApproverEditMain',
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	
    	//审批人表单
    	me.approver = Ext.create('FHD.ux.org.CommonSelector',{
        	fieldLabel: '审批人',
        	name : 'approver',
        	//id : 'approverId',
            type:'emp',
            multiSelect:false,
            margin: '40 10 30 10'
        });
    	
    	me.approvePanel = Ext.create('Ext.panel.Panel', {
    		//region: 'center',
    		border: false,
    		items: [me.approver]
    	});

    	
    	Ext.apply(me, {
            border:false,
     		layout: {
     			align: 'stretch',
     	        type: 'fit'
     	    },
     	    items:[me.approvePanel]
        });
    	
        me.callParent(arguments);
        
    }
});