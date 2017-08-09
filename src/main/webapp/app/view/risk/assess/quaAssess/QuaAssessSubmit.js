/**
 * 
 * 风险整理上下面板
 */

Ext.define('FHD.view.risk.assess.quaAssess.QuaAssessSubmit', {
    extend: 'Ext.form.Panel',
    alias: 'widget.quaAssessSubmit',
    
    requires: [
               'FHD.view.risk.assess.quaAssess.QuaAssessShowGrid'
              ],
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.quaAssessShowGrid = Ext.widget('quaAssessShowGrid',{url:__ctxPath + '/assess/quaAssess/findAssessShowGrid.f'});
        me.store.proxy.extraParams.assessPlanId = me.businessId;
		me.quaAssessShowGrid.store.load();
        
        Ext.apply(me, {
        	border:false,
        	region:'center',
        	layout:{
                align: 'stretch',
                type: 'vbox'
    		},
            items: [me.quaAssessShowGrid, me.submitPanel],
            buttons: [{
    			text: '提交',
    				handler:function(){
    					me.body.mask("提交中...","x-mask-loading");
    					FHD.ajax({
    			            url: __ctxPath + '/assess/quaassess/submitAssess.f',
    			            params: {
    			            	params : Ext.JSON.encode(me.riskDatas),
    			            	executionId : me.executionId,
    			            	assessPlanId : me.businessId
    			            },
    			            callback: function (data) {
    			            		me.body.unmask();
    			                	if(Ext.getCmp('QuaAssessManId').winId != null){
    			                		me.formwindow.close();
    			                		Ext.getCmp(Ext.getCmp('QuaAssessManId').winId).close();
    			                	}else{
    			                		window.location.reload();
    			                	}
    			            }
    			        });
    				}
    			},{
    				text: '取消',
    				handler:function(){
    					me.formwindow.close();
    				}
    		}]
        });

        me.callParent(arguments);
        
        me.on('resize',function(p){
        	me.quaAssessShowGrid.setHeight(me.getHeight() - 30);
    	});
    }

});