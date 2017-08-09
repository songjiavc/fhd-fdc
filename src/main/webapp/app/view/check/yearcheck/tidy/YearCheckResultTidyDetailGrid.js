/**
 * 汇总详细页
 * Grid AUTHOR:Perry Guo 
 * Date:2017-08-01
 */
Ext.define('FHD.view.check.yearcheck.tidy.YearCheckResultTidyDetailGrid', {
	extend : 'FHD.ux.EditorGridPanel',
	alias : 'widget.yearCheckResultTidyDetailGrid',
	
	initComponent : function() {
		var me = this;
		var cols = [{
					dataIndex : 'id',
					sortable : false,
					hidden : true
				}, {
					header : '考评项目',
					dataIndex : 'checkProjectName',
					sortable : false,
					flex : 2
				}, {
					header : '总分',
					dataIndex : 'checkProjectScore',
					sortable : false,
					flex : 2
				},{
					header : '考评内容',
					dataIndex : 'checkCommenttName',
					sortable : false,
					flex : 2
				},{
					header : '评分项目',
					dataIndex : 'checkDetailName',
					sortable : false,
					flex : 2
				},{
					header : '评分标准',
					dataIndex : 'checkDetailDescribe',
					sortable : false,
					flex : 2
				},{
					header : '标准分',
					dataIndex : 'checkDetailScore',
					sortable : false,
					flex : 2
				},{
					header : '自评评分',
					dataIndex : 'owenScore',
					sortable : false,
					flex : 1
				},{
					dataIndex : 'maxScore',
					sortable : false,
					hidden : true
				}, {
					header : '审计处评分',
					dataIndex : 'auditScore',
					sortable : false,
					flex : 1
				}, {
					header : '专项检查评分',
					dataIndex : 'riskScore',
					sortable : false,
					flex : 1,
					editor : new Ext.form.NumberField({
								allowBlank : false,
						listeners:{focus:function(editor){
							var sm = me.getSelectionModel();
							var obj=sm.getSelection()
							editor.minValue = 0;
                  			editor.maxValue = obj[0].data.maxScore;
                }}
							})
				}];

		Ext.apply(me, {
			region : 'center',
			cols : cols,
			checked: false,
		    pagable : false,
		    searchable : true,
		    autoScroll:true,
			clicksToEdit : 2
			
		})
		me.callParent(arguments);

	}
})