/*评价底稿导出列表*/
Ext.define('FHD.view.icm.assess.AssessTemplateExportList',{
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.assesstemplateexportlist',
    layout: 'fit',
    border: false,
    searchable:false,
    pagable:false,//如果分页存在数量不对应的问题
    url:__ctxPath + '/icm/assess/findAssessPlanProcessRelaOrgEmpPageBySome.f', //调用后台url
    extraParams:{
    	companyId:__user.companyId,
    	empId:__user.empId
    },
    cols: [ 
        {dataIndex : 'id',invisible:true},
        {dataIndex : 'planId',invisible:true},
        {dataIndex : 'processId',invisible:true},
        {header : '计划名称',dataIndex : 'planName',sortable: false, minWidth : 150, flex:1,
        	renderer:function(value,metaData,record,colIndex,store,view) { 
				metaData.tdAttr = 'data-qtip="'+value+'"'; 
				return value; 
			}
        }, 
        {header : '流程名称',dataIndex : 'processName',sortable: false, minWidth : 150, flex:1,
        	renderer:function(value,metaData,record,colIndex,store,view) { 
				metaData.tdAttr = 'data-qtip="'+value+'"'; 
				return value;  
			}
		}, 
		{header : '评价方式',dataIndex : 'assessMeasureName',sortable: false, width : 150},
        {header : '评价人',dataIndex : 'empName',sortable: false, width : 150}
    ],
	initComponent:function(){
		var me=this;
		
		Ext.apply(me,{
			tbarItems:[
			    {iconCls : 'icon-database-save',text: '导出', name: 'exportAssessTemplate', tooltip: '导出评价底稿',handler: me.exportAssessTemplate,scope: this},
			    {iconCls : 'icon-database-save',text: '导入', name: 'importAssessTemplate', tooltip: '导入评价底稿',handler: me.importAssessTemplate,scope: this}
			]
		});
		me.callParent(arguments);
		me.store.on('load', function () {
            me.setstatus();
        });
        me.on('selectionchange', function () {
            me.setstatus();
        });
	},
    setstatus: function(){
    	var me = this;
        var selection = me.getSelectionModel().getSelection();
        if(me.down('[name=exportAssessTemplate]')){
			me.down('[name=exportAssessTemplate]').setDisabled(selection.length === 0);
		}
		var planId = null;
        for ( var i = 0; i < selection.length; i++) {
			if(planId){
				if(planId != selection[i].get('planId')){
					me.down('[name=exportAssessTemplate]').setDisabled(true);
					continue;
				};
			}else{
				planId = selection[i].get('planId');
			}
		}
    },
    //导入评价底稿
    importAssessTemplate:function(){
    	var me=this;
    	var assesstemplatecardpanel = me.up('assesstemplatecardpanel');
    	if(assesstemplatecardpanel){
    		//激活新增面板
    		assesstemplatecardpanel.navBtnHandler(1);
    	}
    },
	//导出评价底稿
	exportAssessTemplate:function(){
		var me = this;
		var selection = me.getSelectionModel().getSelection();//得到选中的记录
		var planId = null;
		for ( var i = 0; i < selection.length; i++) {
			if(!planId){
				planId = selection[i].get('planId');
				continue;
			}
		}
		var processIds = [];
		for ( var i = 0; i < selection.length; i++) {
			processIds.push(selection[i].get('processId'));
		}
		window.location.href = __ctxPath+ '/icm/assess/exportAssessTemplateBySome.f?planId='+planId+'&processIds='+Ext.encode(processIds);
	},
	reloadData:function(){
		var me=this;
		
	}
});