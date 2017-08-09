/**
 *    @description 展示列表
 *    @author 宋佳
 *    @since 2013-3-10
 */
Ext.define('FHD.view.icm.icsystem.FlowNoteList', {
	extend : 'FHD.ux.GridPanel',
	alias : 'widget.flownotelist',
	
	requires: [
    	'FHD.view.icm.icsystem.form.NoteEditForm',
    	'FHD.view.icm.icsystem.form.NoteEditFormForView',
    	'FHD.ux.Window'
    ],
	cols : [],
	tbar : [],
	url: '',
	tbarItems : [],
	idSeq : '',
	upName : '',
	paramObj:[],
	selectId : '',
	url: __ctxPath + '/process/findprocesspointlistbypage.f',
	storeAutoLoad:false,
	initComponent : function() {
		var me = this;
		me.on('selectionchange',me.onchange);//选择记录发生改变时改变按钮可用状态
		me.tbarItems = [{
			iconCls : 'icon-add',
			text: '添加',tooltip: '添加流程节点',
			handler : me.addNote,
			scope : this
		}, '-', {
			iconCls : 'icon-edit',
			text: '修改',tooltip: '修改流程节点',
			handler : me.editNote,
			scope : this
			//disabled : true
		}, '-', {
			iconCls : 'icon-del',
			text: '删除',tooltip: '删除流程节点',
			handler : me.delNote,
			scope : this
		}];
		me.cols = [{
			header : '编号',
			dataIndex : 'code',
			sortable : false,
			flex : 1
		}, {
			header : '名称',
			dataIndex : 'name',
			sortable : false,
			flex : 2,
			renderer:function(value,metaData,record,colIndex,store,view) { 
				return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showNoteEditView('" + record.data.id + "')\" >" + value + "</a>"; 
			}
		}, {
			header : '责任部门',
			dataIndex : 'orgName',
			sortable : false,
			flex : 1
		}, {
			header : '责任人',
			dataIndex : 'responsilePersionId',
			sortable : false,
			flex : 1
		}];
        Ext.apply(me,{ 
        	border:false,
			extraParams: {
        		editflag: undefined
    		}
        });
		me.callParent(arguments);
		me.onchange();
	},
	// 添加
	addNote : function(){
	    var me = this;
	    me.selectId = '';
	    var upPanel = me.up('flownotemainpanel');
	    //如果card组件没有创建表单，则创建表单，添加到card中
	    if(!upPanel.noteeditform){
		    upPanel.noteeditform = Ext.widget('noteeditform');
		    upPanel.add(upPanel.noteeditform);
	    }
	    upPanel.noteeditform.clearFormData();
	    upPanel.getLayout().setActiveItem(1);
	    upPanel.noteeditform.initParam({
	    	processId : me.paramObj.processId,
			processPointId : me.selectId
	    });
	    upPanel.noteeditform.reloadData();
	},
	// 编辑
	editNote : function() {
		var me = this;
		var selection = me.getSelectionModel().getSelection();// 得到选中的记录
		if(selection == '' || null == selection){
	    	Ext.Msg.alert(FHD.locale.get('fhd.common.message'), '对叶子节点无法进行编辑！');
	    	return ;
		}
		me.selectId  = selection[0].get('id');
	    var upPanel = me.up('flownotemainpanel');
	    //如果card组件没有创建表单，则创建表单，添加到card中
	    if(!upPanel.noteeditform){
		    upPanel.noteeditform = Ext.widget('noteeditform');
		    upPanel.add(upPanel.noteeditform);
	    }	    
	    upPanel.noteeditform.clearFormData();
	    upPanel.getLayout().setActiveItem(1);
	    upPanel.noteeditform.initParam({
	    	processId : me.paramObj.processId,
			processPointId : me.selectId
	    });
		upPanel.noteeditform.reloadData();
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
						ids.push(selection[i].get('id'));
					}
					FHD.ajax({// ajax调用
						url : __ctxPath + '/ProcessPoint/ProcessPoint/removeProcessPoint.f',
						params : {
							processPointID: ids
						},
						callback: function (data) {
                            if (data) {
                            	me.reloadData();
                                FHD.notification('操作成功!','提示');
                            }else{
                            	Ext.MessageBox.alert('提示', '操作失败!');
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
	},
	showNoteEditView:function(id){
    	var me=this;
    	me.noteeditformforview=Ext.widget('noteeditformforview',{processPointId:id});
		me.noteeditformforview.initParam({
			processPointId : id
		});
		me.noteeditformforview.reloadData();
		var win = Ext.widget('fhdwindow',{
			title:'流程节点详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(me.noteeditformforview);
    }
});