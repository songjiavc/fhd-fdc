Ext.define('FHD.view.icm.assess.AssessTemplateCardPanel',{
	extend: 'FHD.ux.CardPanel',
	alias: 'widget.assesstemplatecardpanel',
	activeItem: 0,
	border:false,
    requires: [
       'FHD.view.icm.assess.AssessTemplateExportList',
       'FHD.view.icm.assess.AssessTemplateImportForm'
    ],
    // 初始化方法
	initComponent: function() {
		var me = this;
		//评价底稿导出列表
        me.assessTemplateExportList = Ext.widget('assesstemplateexportlist');
        //评价底稿导入表单
        me.assessTemplateImportForm = Ext.widget('assesstemplateimportform');
		Ext.apply(me, {
            items: [
				me.assessTemplateExportList,
                me.assessTemplateImportForm
            ]
        });
        me.callParent(arguments);
	},
	//cardpanel切换
    navBtnHandler: function (index) {
    	var me = this;
        me.setActiveItem(index);
        if(0 == index){
        	//重新加载评价底稿导出列表
        	me.assessTemplateExportList.reloadData();
        }else if(1 == index){
        	//重新加载评价底稿导入表单
         	me.assessTemplateImportForm.reloadData();
        }
    },
    reloadData:function(){
    	var me=this;
    	//重新加载评价底稿导出列表
    	me.assessTemplateExportList.reloadData();
    	//重新加载评价底稿导入表单
     	me.assessTemplateImportForm.reloadData();
    }
})