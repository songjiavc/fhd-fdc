Ext.define('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
    extend: 'FHD.view.risk.cmp.form.RiskBasicFormDetail',
    alias: 'widget.riskrelateformdetail',
	
    /**
	 * 变量
	 */
	showbar:false,	//是否显示保存，返回工具条
	riskId:undefined,//查看的风险id
	
	autoScrollas : false,
	
    /**
     * 加载数据
     */
    reloadData: function (riskId) {
        var me = this;
        
        me.riskId = riskId;
        
		FHD.ajax({
			params: {
                riskId: riskId,
                objectId: me.objectId
            },
            url: __ctxPath + me.detailUrl,
			callback : function(data){
		        me.form.setValues({
		            parentName: data.parentName,
		            code:data.code,
		            name:data.name,
		            desc:data.desc,
		            respDeptName:data.respDeptName,
		            relaDeptName:data.relaDeptName,
		            riskKpiName:data.riskKpiName,
		            influKpiName:data.influKpiName,
		            controlProcessureName:data.controlProcessureName,
		            influProcessureName:data.influProcessureName
		        });
//		        me.doLayout();
			}
		});
    },

	/**
	 * 保存和返回的回调函数
	 */
	goback:Ext.emptyFn(),

    initComponent: function () {
        var me = this;

        //基本信息
        var basicfieldSet = me.addBasicComponent();
        me.basicfieldSet = basicfieldSet;

        if(me.showbar){
        	var returnBtn = Ext.create('Ext.button.Button',{
                text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
                iconCls: 'icon-operator-home',
                handler: function () {
                	if(me.goback){
                		me.goback();
                	}
                }
            });
        	me.tbar = ['->',returnBtn];
        }
        
        Ext.apply(me, {
        	layout : {
        		type : 'vbox',
        		align : 'stretch'
        	},
            autoScroll: me.autoScrollas,
            border: false,
            items:[basicfieldSet]
        });
        me.callParent(arguments);
        
        //默认不进行初始化
        if(me.riskId != null){
        	me.reloadData(me.riskId);
        }
    }    
});