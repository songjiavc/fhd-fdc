Ext.define('FHD.view.risk.processStorage.ProcessStorageCardNew',{
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.processstoragecardnew',

    showRiskGrid : function(){
        var me = this;
        me.getLayout().setActiveItem(me.riskGrid);
    },

    showRiskRelateForm : function(){
        var me = this;
        var processForm=me.processRelateForm;
        var orgObj=me.up('processstoragemainpanelnew').processTree.getCurrentTreeNode();
        var id = orgObj.data.id;
        processForm.influProcessureName.initValue(id);
        me.getLayout().setActiveItem(processForm);     
    },

    initComponent: function () {
        var me = this;
        //风险列表
        me.processGrid = Ext.create('FHD.view.risk.processStorage.ProcessEventGridNew',{
            face: me,
            autoDestroy: true,
            navHeight: me.navHeight,
            schm : me.schm      //添加风险分库标识
        });
        //风险编辑表单
        me.processRelateForm = Ext.create('FHD.view.risk.processStorage.ProcessRelateFormNew',{
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
            items: [me.processGrid, me.processRelateForm]
        });

        me.callParent(arguments);
    }

});