/**
 * 预案执行查询面板
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.execute.preplan.ExePreplanGrid', {
	extend : 'FHD.ux.GridPanel',
	alias : 'widget.exepreplangrid',
	cols : [],
	initComponent : function() {
		var me = this;
		me.on('selectionchange',function(){me.onchange(me)});//选择记录发生改变时改变按钮可用状态
		me.btnAdd = Ext.create('Ext.Button', {
		    tooltip: '执行情况',
		    text: '执行情况',
		    disabled : true,
            iconCls: 'icon-add',
		    handler: function() {
		        me.exePreplan();
		    }
		});		
		
		me.tbarItems = [me.btnAdd];
		me.cols = [
		           {header: 'id',dataIndex: 'id',sortable: true,flex : 1,hidden : true},
		           {header: '预案名称',dataIndex: 'preplanname',sortable: true,flex : 1,hidden : false},
		           {header: '风险名称',dataIndex: 'riskname',sortable: true,flex : 1,hidden : false},
		           {header: '实际开始时间',dataIndex: 'preplanStartDate',sortable: true,flex : 1,hidden : false},
		           {header: '实际结束时间',dataIndex: 'preplanEndDate',sortable: true,flex : 1,hidden : false},
		           {header: '执行情况说明',dataIndex: 'executedesc',sortable: true,flex : 1,hidden : false},
		           {header: '执行状态',dataIndex: 'status',sortable: true,flex : 1,hidden : false,
		           		renderer : function(data){
		           			if(data == "0"){
		           				return "<font color='red'>"+"待处理"+"</font>";
		           			}else if(data == "S"){
		           				return "已保存";
		           			}else if(data == "P"){
		           				return "<font color='blue'>"+"已提交"+"</font>";
		           			}else if(data == "D"){
		           				return "<font color='green'>"+"审核通过"+"</font>";
		           			}
		           		}
		           }
	           ];
		Ext.apply(me, {
    		multiSelect: true,
            border:true,
            rowLines:true,//显示横向表格线
            checked: true, //复选框
            autoScroll:true,
    		cols:me.cols,//cols:为需要显示的列
    		tbarItems:me.tbarItems,
    		url:'chf/execute/preplan/queryexepage.f'
    		//,extraParams : ''
        });
		me.callParent(arguments);
	},
	
	onchange : function(me){
		var selections = me.getSelectionModel().getSelection();
		me.btnAdd.setDisabled(selections.length != 1);
		Ext.each(selections,function(selected){
			if(selected.data.status == "P"){
				me.btnAdd.setDisabled(true);
				return;
			}
		});
	},
	//
	exePreplan : function(){
		var me = this;
		var selections = me.getSelectionModel().getSelection();
		var mainpanel = me.up('exepreplanmainpanel');
		if(selections[0].data.status == "S"){
			mainpanel.exepreplaneditpanel.editLoadForm(selections[0].data.id);
			mainpanel.showexepreplaneditpanel();
		}else{
			mainpanel.exepreplaneditpanel.cleanValue(selections[0].data.id);
			mainpanel.showexepreplaneditpanel();
		}
	},
	reloadData : function(){
		var me = this;
		me.store.load();
	}
});