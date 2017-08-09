Ext.define('FHD.view.risk.riskstorage.RiskStorageCardNew',{
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.riskstoragecardnew',

    showRiskGrid : function(){
        var me = this;
        me.getLayout().setActiveItem(me.riskGrid);
    },

    showRiskRelateForm : function(){
        var me = this;
        var currentNode = me.up('riskstoragemainpanelnew').riskTree.getCurrentNode();
        var initVal = [];
        Ext.Array.push(initVal,{id : currentNode.data.id});
        me.getLayout().setActiveItem(me.riskRelateForm);
        me.riskRelateForm.parentId.initValue(Ext.encode(initVal));
        me.riskRelateForm.parentId.setHiddenValue(initVal);
        me.riskRelateForm.setRiskCode();
    },

    initComponent: function () {
        var me = this;
        //风险列表
        me.riskGrid = Ext.create('FHD.view.risk.riskstorage.RiskEventGridNew',{
            face: me,
            autoDestroy: true,
            navHeight: me.navHeight,
            schm : me.schm      //添加风险分库标识
        });
        //风险编辑表单
         /*根据SCHM判断创建页面
         * change by 郭鹏
         * 20170425
         * */
        if (me.schm=="security") {
        me.riskRelateForm = Ext.create('FHD.view.risk.riskStorageSpecial.RiskStorageFormSpecial',{
            showbar:false,
            type: 're',
            border: false,
            schm : me.schm,
            autoDestroy: true
        });
        }else
        	{
        	 me.riskRelateForm = Ext.create('FHD.view.risk.riskstorage.RiskRelateFormNew',{
            showbar:false,
            type: 're',
            border: false,
            schm : me.schm,
            autoDestroy: true
         });
         }
        Ext.apply(me, {
            border:false,
            activeItem : 0,
            items: [me.riskGrid, me.riskRelateForm]
        });

        me.callParent(arguments);
    }

});