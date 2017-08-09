Ext.define('FHD.view.risk.analyse.AssessRiskEventDetail', {
    extend: 'FHD.view.risk.cmp.RiskDetailForm',
    alias: 'widget.assessriskeventdetail',
    autoScroll: true,
    border: false,
    /**
     * 返回的风险事件列表
     */
    riskEventGrid:null,
    
    initComponent: function () {
        var me = this;
        
        var returnBtn = Ext.create('Ext.button.Button',{
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
            iconCls: 'icon-arrow-undo',
            handler: function () {
            	me.face.cardpanel.setActiveItem(me.riskEventGrid);
            	//设置导航
            	me.face.changeNavigation(me.face.nodeType,me.linkcmpurl);
            	//刷新数据
            	me.loadConainer.reloadData(me.face.nodeType,me.face.nodeId);
            }
        });
        Ext.apply(me, {
        	type:'re',
        	hiddenSaveBtn:true,
			border:false,
            tbar:['->',returnBtn]
        });
        me.callParent(arguments);
        
    }
});