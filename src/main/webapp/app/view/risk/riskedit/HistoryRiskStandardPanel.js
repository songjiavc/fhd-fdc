/**
 * 
 * 风险上报标准
 * 使用card布局
 * 
 * 下级有两个组件 潜在风险上报标准、历史风险上报标准
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.risk.riskedit.HistoryRiskStandardPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.historyriskstandardpanel',
    requires: [
    	'FHD.view.risk.riskedit.Quantitative',
    	'FHD.view.risk.riskedit.Quantification'
    ],
	width: 800,
	autoHeight: true,
	kpiItems : [],
	layout: {
		type: 'vbox',
		align: 'center'
	},
	initParam:function(paramObj){
        var me = this;
    	me.paramObj = paramObj;
	},
	initComponent: function() {
		/*定性 qualitative  定量quantification*/
		var me = this;
		//定性
        me.addQuantitative = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").addDx()'>增加</a>"
        });
    	me.fieldsetQuantitative = Ext.widget('fieldset', {
            layout : {
                type: 'vbox',
                align: 'stretch'
            },
            title : '定性',
            width : 700,
            items : [me.addQuantitative]
        });
        me.addQuantification = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").addDl()'>增加</a>"
        });
        me.quantificationFieldset = Ext.widget('fieldset', {
            layout : {
                type: 'vbox',
                align: 'stretch'
            },
            title : '定量',
            width : 700,
            items : [me.addQuantification]
        });
        Ext.apply(me, {
            items: [
               me.fieldsetQuantitative,
               me.quantificationFieldset
            ]
        });
        me.callParent(arguments);
        me.getInitData();
    },
    addDx : function(){
    	var fieldsetQuantitative = this.fieldsetQuantitative;
    	fieldsetQuantitative.insert(fieldsetQuantitative.items.length-1,Ext.widget('quantitative',{initParam:{id:'1',quantitative : 'good'}}));
    },
    addDl : function(){
    	var me = this;
    	me.kpiItems = [];
    	var quantificationFieldset = me.quantificationFieldset;   /*定量的fieldset*/
    	me.kpiselectwindow =  Ext.create('FHD.ux.kpi.opt.KpiSelectorWindow',{
			    multiSelect:true,
			    onSubmit:function(store){
			    	var kpis = store.data.items;
					Ext.Array.each(kpis,function(kpi){
						Ext.Array.push(me.kpiItems,kpi.data.id);
					});
					FHD.ajax({
						url: __ctxPath+'/chf/reportingstandardcontrol/addquantificationinitvalue.f',
						params: {
							kpis : me.kpiItems
						},
				     	callback: function (data) {
				     		var quantificationFieldset = me.quantificationFieldset;   /*定量的fieldset*/
				     		Ext.Array.each(data.quantificationArray,function(quantificationItem){
				     			var quantification = Ext.widget('quantification',
				     				{
				     					key : '1',
				     					fieldValue : quantificationItem.kpiId,
				     					alarmRegions : quantificationItem.alarmRegions
				     				});
				     				quantification.initValues();
									quantificationFieldset.insert(quantificationFieldset.items.length-1,quantification);
				     		});
				     		
				     	}
			         });
			    }
		});
		me.kpiselectwindow.show();
		me.kpiselectwindow.addComponent();
    },
    /**
     * 在数据库中动态取出所有的历史类上报标准，并在页面中展示出来
     */
    getInitData : function(){
   	    var me = this;
   		FHD.ajax({
			url: __ctxPath+'/chf/reportingstandardcontrol/getreportstandards.f',
			params: {
				riskId : '1',       /** 风险分类id  */
				type : 'history'   /** 风险类型:(历史：潜在)  */
			},
	     	callback: function (data) {
	     		var quantificationFieldset = me.quantificationFieldset;   /*定量的fieldset*/
     			var fieldsetQuantitative = me.fieldsetQuantitative;
	     		Ext.Array.each(data.qualitativeArray,function(qualitative){
    				fieldsetQuantitative.insert(fieldsetQuantitative.items.length-1,Ext.widget('quantitative',
    					{initParam:
    						{id : qualitative.id,name : qualitative.name,desc : qualitative.desc}
    					}));
	     		});
	     		Ext.Array.each(data.quantificationArray,function(quantificationItem){
	     			var quantification = Ext.widget('quantification',
	     				{
	     					key : quantificationItem.id,
	     					fieldValue : quantificationItem.kpiId,
	     					alarmRegions : quantificationItem.alarmRegions,
	     					selAlarmRegions : quantificationItem.selAlarmRegions
	     				});
	     				quantification.initValues();
						quantificationFieldset.insert(quantificationFieldset.items.length-1,quantification);
	     		});
	     		
	     	}
         });
	},
	save : function(){
		var me = this;
		var form = me.getForm();
		var saveUrl = '/chf/reportingstandards/savehistorystandards.f';
		FHD.submit({
			form : form,
			params : {
				type : 'history'
			},
			url : __ctxPath + saveUrl,
			callback: function (data) {
				
			}
		});
	},
	undo : function(){

	}
});