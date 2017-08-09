/**
 * 预案/方案显示主面板
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.solutions.SolutionsGrid', {
	extend : 'FHD.ux.GridPanel',
	alias : 'widget.solutionsgrid',
    type : '',
	cols : [],
	initComponent : function() {
		var me = this;
		me.on('selectionchange',function(){me.onchange(me)});//选择记录发生改变时改变按钮可用状态
		
		me.btnAdd = Ext.create('Ext.Button', {
		    tooltip: '新增',
		    text: '新增',
            iconCls: 'icon-add',
		    handler: function() {
		        me.addProplan();
		    }
		});
		me.btnEdit = Ext.create('Ext.Button', {
		    tooltip: '修改',
		    text: '修改',
            iconCls: 'icon-edit',
            disabled: true,
		    handler: function() {
		        me.editProplan();
		    }
		});
		me.btnDel = Ext.create('Ext.Button', {
		    tooltip: '删除',
		    text: '删除',
            iconCls: 'icon-del',
            disabled: true,
		    handler: function() {
		        me.deleteProplan();
		    }
		});
		
		
		me.tbarItems = [me.btnAdd, '-', me.btnEdit, '-', me.btnDel];
		if(me.type != null && me.type == "preplan"){
			me.cols = [
			           {header: 'id',dataIndex: 'id',sortable: true,flex : 1,hidden : true},
			           {header: '启用状态',dataIndex: 'iseffective',sortable: true,flex : 1,hidden : true},
			           {header: '预案方案名称',dataIndex: 'name',sortable: true,flex : 1,hidden : false},
			           {header: '预案方案描述',dataIndex: 'desc',sortable: true,flex : 1,hidden : false},
			           {header: '案件状态',dataIndex: 'status',sortable: true,flex : 1,hidden : false,
			           		renderer : function(data){
			           			if(data == "S"){
			           				return "已保存";
			           			}else if(data == "P"){
			           				return "<font color='blue'>"+"已提交"+"</font>";
			           			}else if(data == "D"){
			           				return "<font color='green'>"+"审核通过"+"</font>";
			           			}else if(data == "A"){
			           				return "<font color='red'>"+"审核不通过"+"</font>";
			           			}
			           		}
			           }
			           ];
			
		}else{
			me.cols = [
			           {header: 'id',dataIndex: 'id',sortable: true,flex : 1,hidden : true},
			           {header: '应急方案名称',dataIndex: 'name',sortable: true,flex : 1,hidden : false},
			           {header: '应急方案描述',dataIndex: 'desc',sortable: true,flex : 1,hidden : false},
			           {header: '案件状态',dataIndex: 'status',sortable: true,flex : 1,hidden : false,
			           		renderer : function(data){
			           			if(data == "S"){
			           				return "<font color='red'>"+"已保存"+"</font>";
			           			}else if(data == "P"){
			           				return "<font color='blue'>"+"已提交"+"</font>";
			           			}else if(data == "D"){
			           				return "<font color='green'>"+"审核通过"+"</font>";
			           			}else if(data == "A"){
			           				return "<font color='red'>"+"审核不通过"+"</font>";
			           			}
			           		}
			           }
			           ];
		}
		Ext.apply(me, {
    		multiSelect: true,
            border:true,
            rowLines:true,//显示横向表格线
            checked: true, //复选框
            autoScroll:true,
    		cols:me.cols,//cols:为需要显示的列
    		tbarItems:me.tbarItems,
    		url:'chf/solutions/querypreplanpage.f',
    		extraParams : {'type':me.type}
        });
		me.callParent(arguments);
	},
	
	onchange : function(me){
		var selections = me.getSelectionModel().getSelection();
		me.btnEdit.setDisabled(selections.length != 1);
		me.btnDel.setDisabled(selections.length === 0);
		Ext.each(selections,function(selected){
			if(selected.data.status == "P"){
				me.btnEdit.setDisabled(true);
				me.btnDel.setDisabled(true);
				return;
			}
		});
	},
	//
	addProplan : function(){
		var me = this;
		var mainpanel;
		if(me.type!=null && me.type == 'preplan'){
			mainpanel = me.up('preplanmainpanel');
		}else{
			mainpanel = me.up('responseplanmainpanel');
		}
		mainpanel.solutionsmainpanel.isAdd = true;
		mainpanel.showSolutionsMainPanel();
	},
	// 编辑
	editProplan : function() {
		var me = this;
		if(me.getSelectionModel().getSelection().length != 1){
			Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.common.updateTip'));//提示
    	    return;
		}
		var mainpanel;
		var id = me.getSelectionModel().getSelection()[0].data.id;
		var name = me.getSelectionModel().getSelection()[0].data.name;
		var desc = me.getSelectionModel().getSelection()[0].data.desc;
		var iseffective = me.getSelectionModel().getSelection()[0].data.iseffective;
		if(me.type!=null && me.type == 'preplan'){
			mainpanel = me.up('preplanmainpanel');
		}else{
			mainpanel = me.up('responseplanmainpanel');
		}
		var gridpanel = mainpanel.solutionsmainpanel.solutionsgridpanel;
		mainpanel.solutionsmainpanel.idAdd = false;
		mainpanel.solutionsmainpanel.loadForm(id,name,desc,iseffective);
		gridpanel.store.proxy.url = gridpanel.queryUrl;
		gridpanel.store.proxy.extraParams.preplanId = id;
		gridpanel.store.load();
		mainpanel.showSolutionsMainPanel();
	},
	// 删除节点
	deleteProplan : function() {
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
						ids.push(selection[i].get('id')+'_'+selection[i].get('iseffective'));
					}
					FHD.ajax({// ajax调用
						url : 'chf/solutions/deletepreplan.f',
						params : {
							ids : ids.join(',')
						},callback: function (data) {
                            if (data) {
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