/**
 * 
 * 风险上报标准
 * 使用card布局
 * 
 * 下级有两个组件 潜在风险上报标准、历史风险上报标准
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.risk.riskedit.LatentRiskStandardPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.latentriskstandardpanel',
    requires: [
    	'FHD.view.risk.riskedit.Quantitative',
    	'FHD.view.risk.riskedit.Quantification',
    	'FHD.view.risk.riskedit.Confidence'
    ],
//	autoWidth: true,
//	autoHeight: true,
	autoScoller : true,
	kpiItems : [],
	layout: {
		type: 'vbox',
		align: 'stretch'
	},
	initComponent: function() {
		/*定性 qualitative  定量quantification*/
		/*定性 qualitative  定量quantification*/
		var me = this;
		//定性
        me.quantitativeContainer = Ext.widget('quantitative');
        me.addQuantitative = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").addDx()'>增加</a>"
        });
    	me.fieldsetQuantitative = Ext.widget('fieldset', {
            layout : {
                type: 'vbox',
                align: 'stretch'
            },
            title : '定性',
            items : [me.quantitativeContainer,me.addQuantitative]
        });
        me.fieldsetQuantification = Ext.widget('quantification');
        me.addQuantification = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").addDl()'>增加</a>"
        });
        me.quantificationFieldset = Ext.widget('fieldset', {
            layout : {
                type: 'vbox',
                align: 'stretch'
            },
            title : '定量',
            items : [me.fieldsetQuantification,me.addQuantification]
        });
        // 置信度
        me.fieldsetConfidence = Ext.widget('confidence');
        me.addConfidence = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").addZxd()'>增加</a>"
        });
        me.confidenceFieldset = Ext.widget('fieldset', {
            layout : {
                type: 'vbox',
                algin: 'center'
            },
            title : '置信度',
            items : [me.fieldsetConfidence,me.addConfidence]
        });
        Ext.apply(me, {
            items: [
               me.fieldsetQuantitative,
               me.quantificationFieldset,
               me.confidenceFieldset
            ]
        });
        me.callParent(arguments);
    },
	addDx : function(){
    	var latentriskstandardpanel = this;
    	var fieldsetQuantitative = latentriskstandardpanel.fieldsetQuantitative;
    	fieldsetQuantitative.insert(fieldsetQuantitative.items.length-1,Ext.widget('quantitative'));
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
    addZxd : function(){
    	var me = this;
    	me.kpiItems = [];
    	var quantificationFieldset = me.quantificationFieldset;   /*定量的fieldset*/
    	var insertobj = {};
    	me.kpiselectwindow =  Ext.create('FHD.ux.kpi.opt.KpiSelectorWindow',{
			    multiSelect:true,
			    onSubmit:function(store){
			    	var kpis = store.data.items;
					Ext.Array.each(kpis,function(kpi){
						var item =kpi.data;
						insertobj = {
							id : item.id,
							text : item.name
							};
						Ext.Array.push(me.kpiItems,insertobj);
						confidenceFieldset.insert(confidenceFieldset.items.length-1,Ext.widget('confidence',{kpiId : item.id}));
						})
				    }
		});
		me.kpiselectwindow.addComponent();
		me.kpiselectwindow.show();
    },
    save : function(){
		var me = this;
		var form = me.getForm();
		var saveUrl = '/chf/reportingstandards/savehistorystandards.f';
		FHD.submit({
			form : form,
			params : {
				type : 'latent'
			},
			url : __ctxPath + saveUrl,
			callback: function (data) {
				
			}
		});
	}
});