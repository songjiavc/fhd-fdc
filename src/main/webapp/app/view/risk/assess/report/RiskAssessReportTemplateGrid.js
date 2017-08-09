/**
 * @author : 邓广义
 *  风险评价报告模板管理树面板
 */
Ext.define('FHD.view.risk.assess.report.RiskAssessReportTemplateGrid',{
    extend: 'FHD.ux.GridPanel',
 	alias : 'widget.riskassessreporttemplategrid',
    requires: [
//               'FHD.view.risk.assess.report.RiskAssessReportTemplateForm'
           ],
    initComponent: function () {
    	var me = this;
    	me.queryUrl = __ctxPath + '/sys/report/findReportTemplateList.f';
    	me.cols = [
	             	{header: 'id' ,dataIndex: 'id',sortable: true,flex : 1,hidden : true},
	             	{header: '模板类型' ,dataIndex: 'templateType',sortable: true,flex : 1,hidden : false},
	             	{header: '模板描述' ,dataIndex: 'templateName',sortable: true,flex : 2,hidden : false},
	             	{header: '模板编号' ,dataIndex: 'templateCode',sortable: true,flex : 1,hidden : false},
	             	{header: '所属公司' ,dataIndex: 'companyName',sortable: true,flex : 1,hidden : false},
	             	{header: '是否默认模板' ,dataIndex: 'isDefault',sortable: true,flex : .5,hidden : false}
    	          ];
		me.tbar =[//按钮
		           {text : "添加",iconCls: 'icon-add', handler:me.addTem, scope : this,id:'report_template_add',disabled:true},'-',
    	           {text : "修改",iconCls: 'icon-edit', handler:me.editTem, scope : this,id:'report_template_edit',disabled:true},'-',
    	           {text : "删除",iconCls: 'icon-del', handler:me.delTem, scope : this,id:'report_template_del',disabled:true},'-',
    	           {text : "设为默认",iconCls: 'icon-add', handler:me.setDefault, scope : this,id:'report_template_setDefault',disabled:true}];
		
    	Ext.apply(me, {
    		border:false,
    	   flex:1,
    	   pagable:false,
    	   searchable:false,
 	       multiSelect: false,
 	       rowLines:true,//显示横向表格线
 	       checked: true, //复选框
 	       autoScroll:false,
 	       tbarItems:me.tbar
         });
         me.callParent(arguments);
     	
     	me.on('selectionchange',function(){me.setStatus(me)});
//     	me.store.on('load',function(){
//     		me.setStatus(me);
//     	});
		
		
    },
    addTem:function(){
    	var me = this;
    	var rightPanel = me.up('riskassessreporttemplateright');
    	rightPanel.remove(me);
    	rightPanel.add(Ext.create('FHD.view.risk.assess.report.RiskAssessReportTemplateForm'));
    	
    },
    editTem:function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
    	if(!selection.length)return
    	var id = selection[0].get('id');
    	var me = this;
    	var rightPanel = me.up('riskassessreporttemplateright');
    	rightPanel.remove(me);
    	var formPanel = Ext.create('FHD.view.risk.assess.report.RiskAssessReportTemplateForm',{templateId:id});
    	formPanel.reloadData(id);
    	rightPanel.add(formPanel);
    },
    delTem:function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
    	if(!selection.length)return
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : '你确定删除此模板吗？',
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {
					var ids = [];
					for(var i=0;i<selection.length;i++){
						var delId = selection[i].get('id');
						if(delId){
								ids.push(delId)
						}
					}
					FHD.ajax({
						url : __ctxPath + '/sys/report/delTemplate.f',//设置默认
						params : {
							id : ids.join(',')
						},callback: function (data) {
							me.store.load();
						}
					});
				}
			} 
		});
    },
    setDefault:function(){

    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
    	if(!selection.length)return
		Ext.MessageBox.show({
			title : '提示',
			width : 260,
			msg : '你确定设置此模板为默认模板吗？',
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {
					var ids = [];
					for(var i=0;i<selection.length;i++){
						var delId = selection[i].get('id');
						if(delId){
								ids.push(delId)
						}
					}
					FHD.ajax({
						url : __ctxPath + '/sys/report/setDefaultTemplate.f',//设置默认
						params : {
							id : ids.join(',')
						},callback: function (data) {
							me.store.load();
						}
					});
				}
			} 
		});
    
    },
    setStatus:function(me){
    	var me = this;
        var length = me.getSelectionModel().getSelection().length;
        var rows = me.store.getModifiedRecords();
        me.down('#report_template_del').setDisabled(length === 0);
        me.down('#report_template_edit').setDisabled(length !== 1);
        var item = me.getSelectionModel().getSelection()[0];
        if(item){
	       length === 1 && me.down('#report_template_setDefault').setDisabled(item.data.isDefault!='否')||me.down('#report_template_setDefault').setDisabled(true);
        }
    
    },
    reloadData:function(param){
    	var me = this;
    	me.store.proxy.extraParams.templateType = param;
    	me.store.proxy.url = me.queryUrl;
    	me.store.load();
    }

});