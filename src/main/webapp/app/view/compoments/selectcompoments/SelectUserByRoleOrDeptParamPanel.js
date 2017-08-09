/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.selectcompoments.SelectUserByRoleOrDeptParamPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.selectuserbyroleordeptparampanel',
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
        if(this.xtype == 'selectuserbyroleordeptparampanel'){
        	me = this;
        }else{
        	me = this.up('selectuserbyroleordeptparampanel');
        }
		//grid 根据新参数进行重新load
		//获取grid
		var extraParams = {
			orgId : me.deptCombo.getValue(),
			roleId : me.roleCombo.getValue()
		};
		me.up('selectuserbyroleordept').gridPanel.reloadData(extraParams);
    },
	/**
	 * 
	 */
	initComponent : function(){
		var me = this;
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
		
		var deptLabel = Ext.widget('label',{
			margin : '10 0 0 10',
			text : '部门:',
			flex : .03
		});
		
	    me.deptCombo = Ext.widget('treecombox',{
	    	valueField : 'dbid',
			displayField : 'text',
			flex : .32,
			vtype : 'treeNode',
			rootVisible : false,
			labelWidth : 0,
			maxPickerHeight : 300,
			maxPickerWidth : 200,
			store : deptComboxStore
	    });
	    
	    var roleComboxStore = Ext.create('Ext.data.TreeStore', {// treeCombox的store
			fields : ['text', 'id', 'dbid','code','leaf','iconCls','cls'],
			root : {
				text: __user.companyName,
    	        id : '',
    	        expanded: true
			},
			proxy: {
    	    	url: __ctxPath + '/sys/auth/role/treeloader',
    	        type: 'ajax',
    	        reader: {
    	            type: 'json'
    	        },
    	        extraParams: {
    	        	checkable: false,
    	        	subCompany: false,
    	        	chooseId:''
    	        }
    	    },
			autoload : false
		});
		var roleLabel = Ext.widget('label',{
			margin : '10 0 0 10',
			text : '角色:',
			flex : .03
		});
     	me.roleCombo = Ext.widget('treecombox',{
	    	valueField : 'id',
			displayField : 'text',
			flex : .32,
			vtype : 'treeNode',
			rootVisible : false,
			labelWidth : 0,
			maxPickerHeight : 300,
			maxPickerWidth : 200,
			store : roleComboxStore
	    });
	    
	    var queryButton = Ext.widget('button',{
	    	text: '查询',
	    	flex : .1,
	    	handler: me.queryFun
	    });
	    
	     var eraserButton = Ext.widget('button',{
	    	text: '清空',
	    	flex : .1,
	    	handler: function() {
		        //grid 根据新参数进行重新load
	    		//获取grid
	    		me.deptCombo.setValue('');
	    		me.roleCombo.setValue('');
	    		var extraParams = {
	    			orgId : me.deptCombo.getValue(),
	    			roleId : me.roleCombo.getValue()
	    		};
	    		me.up('selectuserbyroleordept').down('selectuserbyroleordeptgridpanel').reloadData(extraParams);
		    }
	    });
	    
	     var confirmButton = Ext.widget('button',{
	    	text: '确定',
	    	flex : .1,
	    	handler: function() {
		       	var me = this;
		       	var selecteds = me.up('selectuserbyroleordept').selGridPanel.getStore();     //获取grid 选中的人员
	    		var ids = [];
	    		var names = '';
	    		var temp = []; //清空数组
	    		selecteds.each(function(record){
	    			temp.push({
	    				id : record.data.id,
	    				name : record.data.empname
	    			});
	    		});
	    		me.up('selectuserbyroleordept').parentField.setValue(temp);
	    		me.up('window').close();
		    }
	    });
	    
	    Ext.apply(me, {
        	items : [deptLabel,me.deptCombo,roleLabel,me.roleCombo,queryButton,eraserButton,confirmButton]
        });
        me.callParent(arguments);
	}
});