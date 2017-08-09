Ext.define('FHD.view.myallfolder.SF2deptrisks.DeptRiskCard',{
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.deptriskcard',


    initComponent: function () {
        var me = this;
        //风险排序类表
        me.deptRisksGrid = Ext.create('FHD.view.myallfolder.SF2deptrisks.DeptRiskGrid',{
            authority: 'ROLE_ALL_WORK_FILE_DEP_TENRISK',
        	autoHeight: true,
            border: false,
            pContainer: me,
            face: me,
            autoDestroy: true,
            navHeight: me.navHeight
        });
        Ext.apply(me, {
            border:false,
            activeItem : 0,
            items: [me.deptRisksGrid]
        });

        me.callParent(arguments);
    }

});