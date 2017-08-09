/**
 *    @description 展示列表
 *    @author 宋佳
 *    @since 2013-3-10
 */
Ext.define('FHD.view.icm.icsystem.FlowRiskList', {
	extend : 'FHD.ux.GridPanel',
	alias : 'widget.flowrisklist',
	
	requires: [
    	'FHD.view.icm.icsystem.form.RiskEditForm',
    	'FHD.view.icm.icsystem.form.RiskEditFormForView'
    ],
    
	cols : [],
	tbar : [],
	url: __ctxPath + '/processrisk/findProcessRiskListByPage.f',
	storeAutoLoad:false,
	tbarItems : [],
	idSeq : '',
	upName : '',
	paramObj:[],
	selectId : '',
	initComponent : function() {
		var me = this;
		me.on('selectionchange',me.onchange);//选择记录发生改变时改变按钮可用状态
		me.tbarItems = [{
			iconCls : 'icon-add',
			text: '添加',tooltip: '添加风险信息',
			handler : me.addNote,
			scope : this
		}, '-', {
			iconCls : 'icon-edit',
			text: '修改',tooltip: '修改风险信息',
			handler : me.editNote,
			scope : this
			//disabled : true
		}, '-', {
			iconCls : 'icon-del',
			text: '删除',tooltip: '删除风险信息',
			handler : me.delNote,
			scope : this
		}, '-', {
			iconCls : 'icon-delete-icon',
			text: '删除关联',tooltip: '删除流程和风险关联关系',
			handler : me.delProRelaRisk,
			scope : this
		}];
		me.cols = [{
			dataIndex : 'riskId',
			hidden : true
		},{
			header : '编号',
			dataIndex : 'code',
			sortable : true,
			flex : .5
		},{
			dataIndex : 'riskId',
			hidden : true
		},{
			header : '名称',
			dataIndex : 'name',
			sortable : true,
			flex : 1,
			renderer:function(value,metaData,record,colIndex,store,view) { 
				metaData.tdAttr = 'data-qtip="'+value+'"';
				return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showRiskEditView('" + record.data.riskId + "')\" >" + value + "</a>"; 
			}
		},
		/*
		{
			header : '风险描述',
			dataIndex : 'desc',
			sortable : true,
			flex : 2,
			renderer:function(value,metaData,record,colIndex,store,view) { 
				metaData.tdAttr = 'data-qtip="'+value+'"';
				return value;
			}
		},
		*/
		{
			header : '流程类型',
			dataIndex : 'type',
			sortable : true,
			flex : .5,
			renderer:function(value,metaData,record,colIndex,store,view) { 
	    		if(value == 'I'){
	    			return '影响流程';
	    		}else{
	    			return '控制流程';
	    		}
			}
		},{
			header : '控制措施数量',
			dataIndex : 'measureNum',
			sortable : true,
			flex : .5
		}];
        Ext.apply(me,{ 
			extraParams: {
        		editflag: undefined
    		}
        });
		me.callParent(arguments);
		me.onchange();
	},
	// 
	addNote : function(){
	    var me = this;
	    me.selectId = '';
	    var upPanel = me.up('riskmeasuremainpanel');
	    upPanel.remove(upPanel.riskeditform,true);
	    upPanel.riskeditform = Ext.widget('riskeditform',{processId:me.paramObj.processId});
	    upPanel.riskeditform.measureeditform = [];
	    upPanel.add(upPanel.riskeditform);
	    upPanel.getLayout().setActiveItem(1);
	    upPanel.riskeditform.initParam({
	    	processId : me.paramObj.processId,
			processRiskId : me.selectId
	    });
	    upPanel.riskeditform.initMeasureForm();
	    //初始化流程值
	    upPanel.riskeditform.initRiskForm(me.paramObj.processId);
	},
	// 编辑
	editNote : function() {
		var me = this;
		var selection = me.getSelectionModel().getSelection();// 得到选中的记录
		if(selection == '' || null == selection){
	    	Ext.Msg.alert(FHD.locale.get('fhd.common.message'), '对叶子节点无法进行编辑！');
	    	return ;
		}
		me.selectId  = selection[0].get('riskId');
	    var upPanel = me.up('riskmeasuremainpanel');
	    upPanel.remove(upPanel.riskeditform);
	    upPanel.riskeditform = Ext.widget('riskeditform');
		if(me.up('planprocessedittabpanel')){
			me.up('planprocessedittabpanel').autoHeight=true;
		}
	    upPanel.add(upPanel.riskeditform);
	    upPanel.getLayout().setActiveItem(1);
	    upPanel.riskeditform.initParam({
	    	processId : me.paramObj.processId,
			processRiskId : me.selectId
	    });
	    upPanel.riskeditform.initMeasureForm();
	    upPanel.riskeditform.getInitData();
		upPanel.riskeditform.reloadData();
	},
	// 删除节点
	delNote : function() {
		var me = this;
		var selection = me.getSelectionModel().getSelection();// 得到选中的记录
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : FHD.locale.get('fhd.common.makeSureDelete'),
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {// 确认删除
					var ids = [];
					for (var i = 0; i < selection.length; i++) {
						ids.push(selection[i].get('riskId'));
					}
					FHD.ajax({// ajax调用
						url : __ctxPath
								+ '/processrisk/removeprocessrisk.f',
						params : {
							riskId : ids
						},callback: function (data) {
                            if (data.data) {
                            	me.reloadData();
                                FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
                            }else{
                            	FHD.notification(FHD.locale.get('fhd.common.operateFailure'),FHD.locale.get('fhd.common.prompt'));
                            }
                        }
					});
				}
			}
		});
	},
	// 删除节点
	delProRelaRisk : function() {
		var me = this;
		var selection = me.getSelectionModel().getSelection();// 得到选中的记录
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : FHD.locale.get('fhd.common.makeSureDelete'),
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {// 确认删除
					var ids = [];
					for (var i = 0; i < selection.length; i++) {
						ids.push(selection[i].get('id'));
					}
					FHD.ajax({// ajax调用
						url : __ctxPath
								+ '/processrisk/removeprocessrelariskbyid.f',
						params : {
							riskId : ids
						},callback: function (data) {
                            if (data.data) {
                            	me.reloadData();
                                FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
                            }else{
                            	FHD.notification(FHD.locale.get('fhd.common.operateFailure'),FHD.locale.get('fhd.common.prompt'));
                            }
                        }
					});
				}
			}
		});
	},
	reloadData :function(){
		var me = this;
        me.store.proxy.extraParams.processId = me.paramObj.processId;
		me.store.load();
	},
	onchange :function(){//设置你按钮可用状态
		var me = this;   // iconCls : 'icon-del',
		me.down('[iconCls=icon-edit]').setDisabled(me.getSelectionModel().getSelection().length === 0);
		me.down('[iconCls=icon-del]').setDisabled(me.getSelectionModel().getSelection().length === 0);
		me.down('[iconCls=icon-delete-icon]').setDisabled(me.getSelectionModel().getSelection().length === 0);
	},
	showRiskEditView:function(id){
    	var me=this;
    	me.riskeditformforview=Ext.widget('riskeditformforview',{processRiskId:id});
		me.riskeditformforview.initParam({
			processId : me.paramObj.processId,
			processRiskId : id
		});
		me.riskeditformforview.reloadData();
		me.riskeditformforview.getInitData();
		var win = Ext.create('FHD.ux.Window',{
			title:'风险详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(me.riskeditformforview);
    }
});