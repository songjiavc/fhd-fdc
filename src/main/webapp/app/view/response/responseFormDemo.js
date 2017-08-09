Ext.define('FHD.view.response.responseFormDemo', {
	extend : 'Ext.form.Panel',
	alias : 'widget.formEditPanel',
	requires: ['FHD.view.response.new.SolutionEditPanel'],
	 initComponent: function() {
		 var me = this;
		 var responsePanel = Ext.widget('responseDemo');
			Ext.applyIf(me, {
				layout : 'column',
				bodyPadding : "0 3 3 3",
				autoScroll : true,
				border : false,
				items : [{
							xtype : 'fieldset',// 基本信息fieldset
							collapsible : false,
							// width : '100%',
							defaults : {
								margin : '3 30 3 30',
								labelWidth : 100
							},
							layout : {
								type : 'column'
							},
							title : "营业收入",
							items : [responsePanel
									]
						}]

			});
	 }
}