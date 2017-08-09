/**
 * 机构管理TAB面板
 * 
 * @author 金鹏祥
 */
Ext.define('FHD.view.sys.organization.TabPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.tabPanel',
    
    requires: [
    ],
    listeners: {
	  	tabchange:function(tabPanel, newCard, oldCard, eOpts){
	  		var me = tabPanel;
	  		var treepanel = Ext.getCmp('treePanel');
	  		var cardid = newCard.id;
	  		if('tabpanelorgGridPanel'==cardid){	
	  			me.orgGridPanel.store.proxy.url = me.orgGridPanel.queryUrl;//动态赋给机构列表url
	  			me.orgGridPanel.store.proxy.extraParams.orgIds = treepanel.currentNode.data.id;
	  			me.orgGridPanel.store.load();
  			}else if('tabpanelempGridPanel'==cardid){
  				if('gw'!=treepanel.currentNode.data.type){//选中节点是岗位节点会报错
  					me.empGridPanel.store.proxy.url = me.empGridPanel.queryUrl;
	  				me.empGridPanel.store.proxy.extraParams.orgIds = treepanel.currentNode.data.id;
	  				me.empGridPanel.store.proxy.extraParams.positionIds = null;
	  				me.empGridPanel.store.load();
  				}
	  		}else if('orgEditPanel'==cardid){
	  			if("addOrg"!=me.orgEditPanel.newFlag){//修改
	  				if(!me.orgEditPanel.orgtreeId){
	  					me.orgEditPanel.orgtreeId = treepanel.currentNode.data.id;
	  				}
		  			me.orgEditPanel.load();
		  			me.orgEditPanel.isAdd=false;
	  			}else{//新增
	  				me.orgEditPanel.orgtreeId = treepanel.currentNode.data.id;//将节点id传给edit页面
	  				me.orgEditPanel.parentOrgLoad();//右键添加机构时，显示‘上级机构'
	  			}
	  		}else if('positionGridPanel'==cardid){
	  			me.positionGridPanel.store.proxy.url = me.positionGridPanel.queryUrl;
	  			me.positionGridPanel.store.proxy.extraParams.orgId = treepanel.currentNode.data.id;
	  			me.positionGridPanel.store.load();
	  		}
	  	}
    },
    
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	
    	me.id = 'tabPanel';
    	variable = 'tabpanel';
    	//机构GRID
    	me.orgGridPanel = Ext.create('FHD.view.sys.organization.org.OrgGridPanel');
    	//人员GRID
    	me.empGridPanel = Ext.create('FHD.view.sys.organization.emp.EmpGridPanel');
//    	//机构基本信息
    	me.orgEditPanel = Ext.create('FHD.view.sys.organization.org.OrgEditPanel');
    	//岗位GRID
    	me.positionGridPanel = Ext.create('FHD.view.sys.organization.positiion.PositionGridPanel');
    	
    	Ext.apply(me, {
            deferredRender: false,
            activeTab: 0,     // first tab initially active
            items: [me.orgGridPanel, me.positionGridPanel, me.empGridPanel, me.orgEditPanel],//me.orgGridPanel, me.empGridPanel],
            plain: true
        });
        
    	me.orgGridPanel.on('resize',function(p){
    		me.orgGridPanel.setHeight(Ext.getCmp('center-panel').getHeight()-77);
    	});
    	
    	me.positionGridPanel.on('resize',function(p){
    		me.positionGridPanel.setHeight(Ext.getCmp('center-panel').getHeight()-77);
    	});
    	
    	me.empGridPanel.on('resize',function(p){
    		me.empGridPanel.setHeight(Ext.getCmp('center-panel').getHeight()-77);
    	});
    	
    	me.orgEditPanel.on('resize',function(p){
    		me.orgEditPanel.setHeight(Ext.getCmp('center-panel').getHeight()-77);
    	});
    	
        me.callParent(arguments);
    }
});