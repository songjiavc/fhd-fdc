/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderSelectedGridPanel', {
    extend: 'FHD.ux.GridPanel',
    autoLoad : false,
   // url: __ctxPath + '/sys/org/cmp/findDeptListByQuery.f',
    alias: 'widget.orgrelaleaderselectedgridpanel',
	pageSize : 10,
	initParam : function(extraParams){
		var me = this;
		me.extraParams = extraParams;
	},
    
	reloadData : function(extraParams){
		var me=this;
		me.store.proxy.extraParams = extraParams;
    	me.store.load();
	},
	
	insertRecord : function(r){
		var me = this;
		//判断是否存在该元素
		if(!me.isHasItemInGrid(r.get('id'))){
			me.getStore().insert(0,r);
		}
	},
	
	listeners : {
		itemdblclick : function(c,r,o){
			//双击删除选中的记录m
			var me = this;
			me.getStore().remove(r);
			me.up('orgrelaleaderconfigpanel').selectGridPanel.getSelectionModel().deselect(r);
		}
	},
	initComponent : function(){
		var me = this;
		
		var cols = [
			{
				dataIndex:'id',
				hidden:true
			},{
	            header: "部门编号",
	            dataIndex: 'orgCode',
	            flex:1
	        },{
	        	header: "部门名称",
	            dataIndex: 'orgName',
	            flex:1
	        }
        ];
        
        Ext.apply(me,{
        	cols : cols
        });
        
        me.callParent(arguments);
	}
});