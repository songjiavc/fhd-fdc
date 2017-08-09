Ext.define('FHD.view.response.responseplan.bpm.SolutionBpmApprove', {
    extend: 'FHD.view.response.responseplan.bpm.SolutionApproveForm',
    alias: 'widget.solutionbpmapprove',
    initComponent: function() {
        var me = this;
        Ext.apply(me,{
			isWindow: true
		});
        me.callParent(arguments);
        me.loadData(me.businessId, me.executionId);
    }
});