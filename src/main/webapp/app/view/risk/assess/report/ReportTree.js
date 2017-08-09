/**
 * @author : 元杰
 *  报告树
 */
Ext.define('FHD.view.risk.assess.report.ReportTree',{
 	extend: 'FHD.ux.TreePanel',
 	alias : 'widget.reporttree',
    requires : [
        	'FHD.view.risk.assess.report.ReportGrid'
     ],
	reloadData:function(){},
 	
    initComponent: function () {
    	var me = this;
        Ext.apply(me, {
        	border:false,
    		rootVisible: false,
    		width:260,
    		split: true,
           	collapsible : true,
           	region: 'west',
           	multiSelect: true,
           	rowLines:false,
          	singleExpand: true,
           	checked: false,
        	autoScroll:true,
        	url: __ctxPath + '/app/view/risk/assess/report/reporttree.json',
    	    extraParams: {},
    	    root: {},
   			listeners : {
	   			'itemclick' : function(view,re){
	   				if(re.data.leaf){
	   					var me = this;
	   					var reportTypeId = re.data.id;
	   					me.reloadRightData(reportTypeId);	   					
	   				}
	   			},
	   			'itemexpand':function(){	//默认选中第2级首节点
	   				var me = this;
                    var selectedNode = null;
                    var firstNode = me.getRootNode().firstChild;
                    if (null != firstNode) {
                    	firstNode = firstNode.firstChild;
                    	me.getSelectionModel().select(firstNode);
                        selectedNode = firstNode;
                    }
      
                    //刷新右侧列表
                    var reportTypeId = selectedNode.data.id;
   					me.reloadRightData(reportTypeId);
	   			},
	   			'itemcontextmenu':function(menutree, record, items, index, e){
   		   			e.preventDefault();
   		           	e.stopEvent();
	   			}
   			}
        });
        me.callParent(arguments);
    },
    
    /**
     * 选中树节点，刷新右侧数据
     * id:报告类型id	左侧树节点id，日常报告：dailyReport；评估报告：yearlyReport
     */
    reloadRightData:function(id){
    	var me = this;
		var mainPanel = me.up('reportmainpanel');
		var gridPanel = mainPanel.down('reportgrid');
//		if(!gridPanel){
//			mainPanel.down('reportright').removeAll(true);
//			gridPanel = Ext.widget('reportgrid');
//			mainPanel.down('reportright').add(gridPanel);
//		}
//		me.currentNode = re;
		gridPanel.down('#report_generate').setDisabled(false);
		gridPanel.reloadData(id);
    }
});