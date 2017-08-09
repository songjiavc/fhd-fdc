Ext.define('FHD.demo.layout.TreeTabFaceDemo', {
    extend: 'FHD.ux.layout.treeTabFace.TreeTabFace',
    alias: 'widget.treetabfacedemo',
    requires: [
               'FHD.view.risk.risk.RiskTreePanel',
               'FHD.view.risk.risk.RiskBasicFormView',
               'FHD.view.risk.risk.RiskEventGrid',
               'FHD.view.risk.risk.RiskHistoryGrid'
    ],
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;

        //树
//        var tree = Ext.widget('risktreepanel',{
//        	region:'west',
//        	rbs:true        	
//        }); 
        var queryUrl = __ctxPath + '/pages/demo/layout/tree.json';
        var tree1 = Ext.create('FHD.ux.TreePanel', {
        	region:'west',
        	treeTitle:'树1',
        	treeIconCls : 'icon-orgsub',
        	width:200,
			url : queryUrl
		});
        //基本信息
        var riskBasicFormView =  Ext.widget('riskBasicFormView');
        //风险事件列表
        var riskEventGrid =  Ext.widget('riskeventgrid',{
        	title:'风险事件列表',
        	border:false,
        	height:FHD.getCenterPanelHeight()-47
        });
        //历史信息
        var riskHistoryGrid =  Ext.widget('riskhistorygrid',{
        	title:'历史信息',
        	border:false,
        	height:FHD.getCenterPanelHeight()-47
        });

        Ext.apply(me,{
        	tree:tree1,
        	tabs:[riskBasicFormView,riskEventGrid,riskHistoryGrid]
        });
        
        me.callParent(arguments);
    }
});