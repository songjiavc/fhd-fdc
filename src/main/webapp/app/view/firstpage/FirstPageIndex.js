/**
 * 
 * add by jia.song@pcitc.com
 * firstpage
 */
Ext.define('FHD.view.firstpage.FirstPageIndex',{
	extend : 'Ext.container.Container',
	alias: 'widget.firstpageindex',
	layout : {
		type : 'vbox',
		align : 'stretch' 
	},
	defaults : {
		padding : '10 10 10 10'
	},
	initComponent: function() {
		var me = this;
		//init todolist
		me.todoList1 = Ext.create('FHD.view.myallfolder.SF2mytodo.MyTodoMain',{
			flex : .5
		});
		
		me.riskGridForSecurity = Ext.create('FHD.view.risk.riskstorage.RiskEventGridNew',{
            storeAutoLoad :false,
            flex : .5,
            extraParams : {
            	id : 'root',
            	type : 'root',
            	test : 'songjia',
            	schm : 'security'
            },
            title : '保密风险列表',
            reloadData : function(){
            	var me = this;
            	me.store.proxy.url = __ctxPath + '/cmp/risk/findEventById';
                me.store.load();
            }
        });
		
		var northPanel = Ext.widget('panel',{
			flex : .5,
			border : false,
			defaults : {
				padding : '10 10 10 10'
			},
			layout : {
				type : 'hbox',
				align : 'stretch'
			},
			items : [me.todoList1,me.riskGridForSecurity]
		});
		
		me.riskGridForCompany = Ext.create('FHD.view.risk.riskstorage.RiskEventGridNew',{
            storeAutoLoad :false,
            extraParams : {
            	id : 'root',
            	type : 'root',
            	schm : 'company'
            },
            flex : .5,
            title : '公司风险列表',
            reloadData : function(){
            	var me = this;
            	me.store.proxy.url = __ctxPath + '/cmp/risk/findEventById';
                me.store.load();
            }
        });
		
		me.riskGridForDept = Ext.create('FHD.view.risk.riskstorage.RiskEventGridNew',{
            storeAutoLoad :false,
            flex : .5,
            extraParams : {
            	id : 'root',
            	type : 'root',
            	schm : 'dept'
            },
            title : '部门风险列表',
            reloadData : function(){
            	var me = this;
            	me.store.proxy.url = __ctxPath + '/cmp/risk/findEventById';
                me.store.load();
            }
        });
		
		var southPanel = Ext.widget('panel',{
			border : false,
			flex : .5,
			defaults : {
				padding : '10 10 10 10'
			},
			layout : {
				type : 'hbox',
				align : 'stretch'
			},
			items : [me.riskGridForCompany,me.riskGridForDept]
		});
		
		Ext.applyIf(me,{
			items : [northPanel,southPanel]
		});
		me.riskGridForSecurity.reloadData();
		me.riskGridForCompany.reloadData();
		me.riskGridForDept.reloadData();
		me.callParent(arguments);
	}
});

