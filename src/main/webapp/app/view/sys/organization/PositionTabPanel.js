/**
 * 岗位管理TAB面板
 * 
 * @author 
 */
Ext.define('FHD.view.sys.organization.PositionTabPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.positionTabPanel',
    
    requires: [
           'FHD.view.sys.organization.org.OrgGridPanel',
           'FHD.view.sys.organization.emp.EmpGridPanel',
           'FHD.view.sys.organization.positiion.PositionEditPanel'
    ],
    
    listeners: {
	  	tabchange:function(tabPanel, newCard, oldCard, eOpts){
	  		var me = tabPanel;
	  		var treepanel = Ext.getCmp('treePanel');
	  		var cardid = newCard.id;
	  		if('positionempGridPanel'==cardid){	
	  			me.empGridPanel.store.proxy.url = me.empGridPanel.queryUrl;
  				me.empGridPanel.store.proxy.extraParams.positionIds = treepanel.currentNode.data.id;
  				me.empGridPanel.store.load();
  			}else if('positionEditPanel'==cardid){
  				if("jg"!=treepanel.currentNode.data.type){
  					me.positionEditPanel.orgtreeId = treepanel.currentNode.data.id;
  	  				me.positionEditPanel.load();
  	  				me.positionEditPanel.isAdd=false;
  				}
	  		}
	  	}
    },
    
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	variable = 'position';
    	
    	me.id = 'positionTabPanel';
    	//机构GRID
    	me.orgGridPanel = Ext.widget('orgGridPanel');
    	//岗位GRID
    	//me.positionGridPanel = Ext.widget('positionGridPanel');
    	//人员GRID
    	me.empGridPanel = Ext.widget('empGridPanel');
    	//岗位基本信息
    	me.positionEditPanel = Ext.widget('positionEditPanel');
    	//设置员工列表菜单文本
    	var empGridTbar = Ext.getCmp('empGridTbar'+variable);
    	empGridTbar.items.items[4].setText("取消关联");
    	empGridTbar.items.items[4].iconCls='icon-plugin-delete';//设置关联图标
    	
    	Ext.apply(me, {
            deferredRender: false,
            activeTab: 0,    
            items: [me.empGridPanel, me.positionEditPanel],
            plain: true
        });
    	
        me.callParent(arguments);
    }
});