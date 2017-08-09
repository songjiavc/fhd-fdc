/**
 * 
 * 审批人
 */

Ext.define('FHD.view.risk.assess.formulatePlan.FormulateApproverEdit', {
    extend: 'Ext.form.Panel',
    alias: 'widget.formulateapproveredit',
    
    // 初始化方法
    initComponent: function() {
        var me = this;
      //审批人
      /*me.approver = Ext.create('FHD.ux.org.CommonSelector',{
            	fieldLabel: '审批人',
            	name : 'approver',
                type:'emp',
                multiSelect:false,
                margin: '40 10 30 10'
                //columnWidth: .5
            });*/
            
     me.approver = Ext.create('Ext.ux.form.OrgEmpSelect',{
			type: 'emp',
			fieldLabel : '审批人',
			multiSelect: false,
            margin: '40 10 30 30',
			labelAlign: 'left',
			labelWidth: 80,
			width:500,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'approver',
			value:''
		});
		
        Ext.apply(me, {
        	autoScroll:false,
        	border:false,
        	//region:'center',
            items : [me.approver]
        });

       me.callParent(arguments);
    }

});