/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderParamPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.orgrelaleaderparampanel',
    requires: [
    	'FHD.ux.TreeCombox'
    ],
	layout : {
		type : 'hbox',
		align : 'stretch'
	},
	defaults : {
		margin : '10 10 10 10'
	},
	/**
	 * @author jia.song
	 * @desc    初始化查询参数
	 */
	initParam : function(orgId,roleId){
		var me = this;
		me.deptCombo.setValue(orgId);
		me.roleCombo.setValue(roleId);
	},
	
	/**
	 * @author jia.song
	 * @desc    查询方法
	 */
	queryFun : function () {
	   var me;
	   if(this.xtype == 'orgrelaleaderparampanel'){
	   		me = this;
	   }else{
	   	   me = this.up('orgrelaleaderparampanel');
	   }
       var bussinessId = me.bussinessCombo.getValue();
       var managedOrgId = me.managedCombo.getValue();
       var managePeopleId = me.managePeopleCombo.getValue();
       var extraParams = {
       		bussinessId : bussinessId,
       		managedId : managedOrgId,
       		managePeopleId : managePeopleId
       };
       me.up('orgrelaleadermainpanel').gridPanel.reloadData(extraParams);
    },
    /**
     *   清空查询条件
     */
    eraserFun : function () {
	   var me;
	   if(this.xtype == 'orgrelaleaderparampanel'){
	   		me = this;
	   }else{
	   	   me = this.up('orgrelaleaderparampanel');
	   }
       me.bussinessCombo.setValue('');
       me.managedCombo.setValue('');
       me.managePeopleCombo.setValue('');
    },
    
    
	/**
	 * 
	 */
	initComponent : function(){
		
		var me = this;

		var bussinessStore = Ext.create('Ext.data.Store', {
      		fields: ['id', 'name'],
      		proxy: {
    	    	url: __ctxPath + '/check/findBussinessByAll.f',
    	        type: 'ajax',
    	        reader: {
    	            type: 'json'
    	        }
      		},
      		autoload : false
        });
		
		me.bussinessCombo = Ext.widget('combobox',{
			fieldLabel : '所属业务',
			flex : .2,
			valueField : 'id',
			displayField: 'name',
			store : bussinessStore
		});
		
		var deptComboxStore = Ext.create('Ext.data.TreeStore', {// treeCombox的store
			fields : ['text', 'id', 'dbid','code','leaf','iconCls','cls'],
			root : {
				text: __user.companyName,
    	        id: '',
    	        expanded: true
			},
			proxy: {
    	    	url: __ctxPath + '/sys/org/cmp/depttreeloader.f',
    	        type: 'ajax',
    	        reader: {
    	            type: 'json'
    	        },
    	        extraParams: {
    	        	checkable: false,
    	        	subCompany: false,
    	        	companyOnly : false
    	        }
    	    },
			autoload : false
		});
		
		var managedLabel = Ext.widget('label',{
			margin : '10 0 0 10',
			text : '被管理部门:',
			flex : .06
		});
		
	    me.managedCombo = Ext.widget('treecombox',{
	    	valueField : 'dbid',
			displayField : 'text',
			flex : .14,
			vtype : 'treeNode',
			rootVisible : false,
			labelWidth : 0,
			maxPickerHeight : 300,
			maxPickerWidth : 200,
			store : deptComboxStore
	    });
	    
	    var empLeaderRelaOrgStore = Ext.create('Ext.data.Store', {// treeCombox的store
			fields : ['id','empName'],
			proxy: {
    	    	url: __ctxPath + '/check/findEmpFromLeaderRelaOrgByAll.f',
    	        type: 'ajax',
    	        reader: {
    	            type: 'json'
    	        }
    	    },
			autoload : false
		});
		
		
		var deptComboxStore = Ext.create('Ext.data.TreeStore', {// treeCombox的store
			fields : ['text', 'id', 'dbid','code','leaf','iconCls','cls'],
			root : {
				text: __user.companyName,
    	        id: '',
    	        expanded: true
			},
			proxy: {
    	    	url: __ctxPath + '/sys/org/cmp/depttreeloader.f',
    	        type: 'ajax',
    	        reader: {
    	            type: 'json'
    	        },
    	        extraParams: {
    	        	checkable: false,
    	        	subCompany: false,
    	        	companyOnly : false
    	        }
    	    },
			autoload : false
		});
		
		var managePeopleLabel = Ext.widget('label',{
			margin : '10 0 0 10',
			text : '管理人员:',
			flex : .06
		});
		
	    me.managePeopleCombo = Ext.widget('combobox',{
	    	valueField : 'id',
			displayField : 'empName',
			flex : .14,
			vtype : 'treeNode',
			rootVisible : false,
			labelWidth : 0,
			maxPickerHeight : 300,
			maxPickerWidth : 200,
			store : empLeaderRelaOrgStore
	    });
	    
	    var queryButton = Ext.widget('button',{
	    	text: '查询',
	    	flex : .05,
	    	handler: me.queryFun
	    });
	    
	     var eraserButton = Ext.widget('button',{
	    	text: '清空',
	    	flex : .05,
	    	handler: me.eraserFun
	    });
	    
	    Ext.apply(me, {
        	items : [me.bussinessCombo,managedLabel,me.managedCombo,managePeopleLabel,me.managePeopleCombo,queryButton,eraserButton]
        });
        me.callParent(arguments);
	}
});