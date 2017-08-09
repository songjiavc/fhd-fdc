/**
 * 应对措施列表 
 *
 * @author 张健
 */
Ext.define('FHD.view.risk.solutions.SolutionsGridPanel', {
	extend : 'FHD.ux.GridPanel',
	alias : 'widget.solutionsgridpanel',
	requires: [
    	'FHD.view.risk.solutions.SolutionsEditPanel'
    ],
    preplanId : '',
	cols : [],
	pagable : false,
	selectId : '',
	height  : 350,
	initComponent : function() {
		var me = this;
		me.queryUrl = 'chf/solutions/querysolutionsbypreplan.f';
		me.on('selectionchange',function(){me.onchange(me)});//选择记录发生改变时改变按钮可用状态
		me.btnAdd = Ext.create('Ext.Button', {
		    tooltip: '新增',
		    text: '新增',
            iconCls: 'icon-add',
		    handler: function() {
		        me.addSolutions();
		    }
		});
		me.btnEdit = Ext.create('Ext.Button', {
		    tooltip: '修改',
		    text: '修改',
            iconCls: 'icon-edit',
            disabled: true,
		    handler: function() {
		        me.editSolutions();
		    }
		});
		me.btnDel = Ext.create('Ext.Button', {
		    tooltip: '删除',
		    text: '删除',
            iconCls: 'icon-del',
            disabled: true,
		    handler: function() {
		        me.deleteSolutions();
		    }
		});
		me.tbarItems = [me.btnAdd, '-', me.btnEdit, '-', me.btnDel];			
	
		me.cols = [
				{header: 'id',dataIndex: 'id',sortable: false,flex : 1,hidden : true},
				{header: '措施名称',dataIndex: 'name',sortable: false,flex : 1,hidden : false},
				{header: '工作内容',dataIndex: 'desc',sortable: false,flex : 1,hidden : false},
				{header: '工作类别',dataIndex: 'type',sortable: false,flex : 1,hidden : false},
				{header: '计划完成时间',dataIndex: 'planFinishDate',sortable: false,flex : 1,hidden : false}
				];
        Ext.apply(me,{
        	multiSelect: true,
            border:true,
            rowLines:true,//显示横向表格线
            checked: true, //复选框
            autoScroll:true,
    		cols:me.cols,//cols:为需要显示的列
    		tbarItems:me.tbarItems
        });
		me.callParent(arguments);
	},
	
	onchange : function(me){
		me.btnEdit.setDisabled(me.getSelectionModel().getSelection().length === 0);
		me.btnDel.setDisabled(me.getSelectionModel().getSelection().length === 0);
	},
	
	// 
	addSolutions : function(){
		var me = this;
		var solutionseditpanel = Ext.widget('solutionseditpanel',{
			isAdd : true,
			preplanId : me.preplanId,
			iseffective : me.up('solutionsmainpanel').iseffective
		});
		var solutionswindow = Ext.create('FHD.ux.Window',{
			title:'添加控制措施',
			maximizable: true,
			modal:true,
			width:800,
			collapsible:true,
			autoScroll : true,
			items : solutionseditpanel,
			listeners:{
				close : function(panel,eOpts){
					me.up('solutionsmainpanel').iseffective = '0';
					me.up('solutionsmainpanel').preplanId = panel.items.items[0].preplanId;
					me.preplanId = panel.items.items[0].preplanId;
					me.store.proxy.extraParams.preplanId = me.preplanId;
					me.store.load();
				}
			}
		}).show();
	},
	// 编辑
	editSolutions : function() {
		var me = this;
		var selection = me.getSelectionModel().getSelection();// 得到选中的记录
		if(selection.length != 1){
			Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.common.updateTip'));//提示
    	    return;
		}
		me.solutionsId  = selection[0].get('id');
		var solutionseditpanel = Ext.widget('solutionseditpanel',{
			isAdd : false,
			solutionsId : me.solutionsId,
			preplanId : me.preplanId,
			iseffective : me.up('solutionsmainpanel').iseffective
		});
		solutionseditpanel.editLoadForm();
		var solutionswindow = Ext.create('FHD.ux.Window',{
			title:'添加控制措施',
			maximizable: true,
			modal:true,
			width:800,
			collapsible:true,
			autoScroll : true,
			items : solutionseditpanel,
			listeners:{
				close : function(panel,eOpts){
					me.up('solutionsmainpanel').iseffective = '0';
					me.up('solutionsmainpanel').preplanId = panel.items.items[0].preplanId;
					me.preplanId = panel.items.items[0].preplanId;
					me.store.proxy.extraParams.preplanId = me.preplanId;
					me.store.load();
				}
			}
		}).show();
	},
	
	deleteSolutions : function() {
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
						url : 'chf/solutions/deletesolutions.f',
						params : {
							ids : ids,
							preplanId : me.preplanId,
							iseffective : me.up('solutionsmainpanel').iseffective
						},
						callback: function (data) {
                            if (data) {
                            	me.preplanId = data.id;
                            	me.up('solutionsmainpanel').iseffective = '0';
								me.up('solutionsmainpanel').preplanId = data.id;
                            	me.store.proxy.extraParams.preplanId = me.preplanId;
                            	me.store.load();
                                Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.common.operateSuccess'));
                            }else{
                            	Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.common.operateFailure'));
                            }
                        }
					});
				}
			}
		});
	}
});