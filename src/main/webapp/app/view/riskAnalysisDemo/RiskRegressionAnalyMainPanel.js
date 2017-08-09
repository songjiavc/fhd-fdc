/**
 * 
 * 回归分析主面板
 */

Ext.define('FHD.view.riskAnalysisDemo.RiskRegressionAnalyMainPanel',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.riskRegressionAnalyMainPanel',
 	border:false,
 	requires: [
	],
	
    initComponent: function () {
    	var me = this;
		me.p1 = Ext.create('FHD.view.riskAnalysisDemo.AnalysisParamSetEditPanel',{
	 		businessId:me.businessId,
		 	navigatorTitle:'参数设置',
		 	border: false,
		 	last:function(){//计划制定保存按钮事件
		 		/*var gridPanel = Ext.getCmp('riskAnalysisGridPanelId');
		 		gridPanel.preWin.close();*/
		 	}
		});
		me.p2 = Ext.create('FHD.view.riskAnalysisDemo.AnalysisResultMainPanel',{
		 	navigatorTitle:'结果输出',
		 	border: true,
		 	last:function(){
		 	},
		 	back:function(){//上一步方法，传参
		 	}
		});
		me.basicPanel = Ext.create('FHD.ux.layout.StepNavigator',{
		    hiddenTop:false,	//是否隐藏头部
		    hiddenBottom:false, //是否隐藏底部
		    hiddenUndo:true,	//是否有返回按钮
		 	items:[me.p1,me.p2],
		 	undo : function(){
		 		
		 	}
		});
    	me.callParent(arguments);
    	me.add(me.basicPanel);
    	
    	me.on('resize',function(p){
    		me.basicPanel.setHeight(568);
    	});
    }
    	
});