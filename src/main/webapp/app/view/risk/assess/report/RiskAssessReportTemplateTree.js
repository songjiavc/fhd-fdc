/**
 * @author : 邓广义
 *  风险评价报告模板管理
 */
Ext.define('FHD.view.risk.assess.report.RiskAssessReportTemplateTree',{
 	extend: 'FHD.ux.TreePanel',
 	alias : 'widget.riskassessreporttemplatetree',
    requires : [
                	'FHD.view.risk.assess.report.RiskAssessReportTemplateGrid'
             ],
	reloadData:function(){},
 	
    initComponent: function () {
    		var me = this;
        Ext.apply(me, {
        	border:true,
    		rootVisible: false,
    		width:260,
    		split: true,
           	collapsible : true,
           	region: 'west',
           	multiSelect: true,
           	rowLines:false,
          	singleExpand: false,
           	checked: false,
        	autoScroll:true,
        	url: __ctxPath + '/app/view/risk/assess/report/report.json',
    	    extraParams: {},
    	    root: {},
   			listeners : {
	   			'itemclick' : function(view,re){
	   				if(re.data.leaf){
	   					var me = this;
	   					var mainPanel = me.up('riskassessreporttemplatemain');
	   					var gridPanel = mainPanel.down('riskassessreporttemplategrid');
	   					if(!gridPanel){
	   						mainPanel.down('riskassessreporttemplateright').removeAll(true);
	   						gridPanel = Ext.widget('riskassessreporttemplategrid');
	   						mainPanel.down('riskassessreporttemplateright').add(gridPanel);
	   					}
	   					me.currentNode = re;
	   					gridPanel.reloadData(re.data.id);
	   					gridPanel.down('#report_template_add').setDisabled(false);
	   					
//	   					if(re.data.id == 'dailyReportTemplate'){
//		   					gridPanel.reloadData('dailyReportTemplate');
//	   					}else if(re.data.id == 'yearlyReportTemplate'){
//	   						gridPanel.reloadData('yearlyReportTemplate');
//	   					}
	   				}
	   			},
	   			'itemcontextmenu':function(menutree, record, items, index, e){
   		   			e.preventDefault();
   		           	e.stopEvent();
	   			}
   			}
        });
        me.callParent(arguments);
    }

});