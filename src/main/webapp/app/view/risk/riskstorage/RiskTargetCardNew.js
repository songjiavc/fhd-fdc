/*
 * 风险-目标 右侧页面
 * 
 * @time 2017年3月22日15:16:16
 */
Ext.define('FHD.view.risk.riskstorage.RiskTargetCardNew',{
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.risktargetcardnew',
    
    //展示列表
    showTargetGrid : function(){
        var me = this;
        me.getLayout().setActiveItem(me.targetGrid);
    },
    //展示表单
    showRiskRelateForm : function(){
        var me = this;
        me.getLayout().setActiveItem(me.riskRelateForm);
    },
    //初始化方法
    initComponent: function () {
        var me = this;
        //风险-目标列表
        me.targetGrid = Ext.create('FHD.view.risk.riskstorage.RiskTargetEventGridNew',{
            face: me,
            autoDestroy: true,
            navHeight: me.navHeight,
            schm : me.schm      //添加风险分库标识
        });
        //风险-目标编辑表单
        me.riskRelateForm = Ext.create('FHD.view.risk.riskstorage.RiskTargetRelateFormNew',{
            showbar:false,
            type: 're',
            border: false,
            schm : me.schm,
            autoDestroy: true,
            title: '基本信息'
        });
        Ext.apply(me, {
            border:false,
            activeItem : 0,
            items: [me.targetGrid, me.riskRelateForm]
        });
        me.callParent(arguments);
    }

});