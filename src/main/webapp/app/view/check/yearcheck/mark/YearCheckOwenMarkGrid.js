/**
 * 年度考核自我评价打分
 * Grid AUTHOR:Perry Guo 
 * Date:2017-08-01
 */
Ext.define('FHD.view.check.yearcheck.mark.YearCheckOwenMarkGrid', {
	extend : 'FHD.ux.EditorGridPanel',
	alias : 'widget.yearCheckOwenMarkGrid',
	
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
					flex : 1,
					editor : new Ext.form.NumberField({
								allowBlank : false
							})
				}];

		Ext.apply(me, {
			region : 'center',
			cols : cols,
			checked: false,
		    pagable : false,
		    searchable : true,
		    autoScroll:true,
			url : __ctxPath + "/check/yearcheck/findCheckRuleByEmpMap.f",
			clicksToEdit : 2
			
		})
		me.callParent(arguments);

	}
})