/**
 * 

 * 风险整理卡片面板
 */

Ext.define('FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.newRiskTidyCard',
    
    requires: [
               		'FHD.view.risk.cmp.chart.RiskHeatMapPanel',
               		'FHD.view.risk.cmp.chart.RiskGroupCountPanel',
               		'FHD.ux.FusionChartPanel'
              ],
    
    showRiskHeatMapPanel : function(){
    	var me = this;
    	me.getLayout().setActiveItem(me.items.items[0]);
    },
    
    showRiskGroupCountPanel : function(){
    	var me = this;
    	me.getLayout().setActiveItem(me.items.items[1]);
    },
              
    initComponent: function () {
        var me = this;
        me.riskHeatMapPanel = Ext.widget('riskheatmappanel',{assessPlanId : me.riskTidyMan.businessId});
        me.riskGroupCountPanel = Ext.widget('riskgroupcountpanel',{assessPlanId : me.riskTidyMan.businessId});
        
        //菜单项
        var tbar =[
                   {
                	text : '风险图谱',
   					iconCls : 'icon-tupu',
       				id : me.id + 'tp',
       				handler:function(){
       					me.showRiskHeatMapPanel();
       					this.toggle(true);
       					Ext.getCmp(me.id + 'bt').toggle(false);
       				}
       			},'-',{
       				text : '分类分析',
					iconCls : 'icon-chart-pie',
   					id : me.id + 'bt',
   					handler : function(){
   						me.showRiskGroupCountPanel();
   						this.toggle(true);
   						Ext.getCmp(me.id + 'tp').toggle(false);
   					}
   				}
        ];
        
        Ext.apply(me, {
        	border:false,
        	region:'center',
        	activeItem : 0,
            items: [me.riskHeatMapPanel, me.riskGroupCountPanel],
            tbar : tbar
        });
        
        me.callParent(arguments);
        
        Ext.getCmp(me.id + 'tp').toggle(true);
    }
});