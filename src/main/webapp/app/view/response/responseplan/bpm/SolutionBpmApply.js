Ext.define('FHD.view.response.responseplan.bpm.SolutionBpmApply', {
    extend: 'FHD.view.response.responseplan.bpm.SolutionApplyForm',
    alias: 'widget.solutionbpmapply',
    initComponent: function() {
        var me = this;
        Ext.apply(me,{
			isWindow: true
		});
        me.callParent(arguments);
        me.loadData(me.businessId, me.executionId);
    }
});