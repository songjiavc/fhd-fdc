Ext.define('FHD.view.icm.rectify.bpm.RectifyImproveBpmThree', {
    extend: 'FHD.view.icm.rectify.RectifyLeadApprove',
    alias: 'widget.rectifyimprovebpmthree',
    initComponent: function() {
        var me = this;
        Ext.apply(me,{
			isWindow: true
		});
        me.callParent(arguments);
        me.loadData(me.businessId,me.executionId);
    }
});