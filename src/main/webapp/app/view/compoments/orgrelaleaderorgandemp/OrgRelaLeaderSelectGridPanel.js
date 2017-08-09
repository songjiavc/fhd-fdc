/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderSelectGridPanel', {
    extend: 'FHD.ux.GridPanel',
    url: __ctxPath + '/sys/org/cmp/findDeptListByQuery.f',
    alias: 'widget.orgrelaleaderselectgridpanel',
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
	listeners : {
    	select : function(c,r,o){
    		var me = this;
    		//将选中记录copy到已选列表中
    		var selectedGridPanel = me.up('orgrelaleaderconfigpanel').selectedGridPanel;
    		selectedGridPanel.insertRecord(r);
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