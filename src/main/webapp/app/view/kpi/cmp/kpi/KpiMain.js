Ext.define('FHD.view.kpi.cmp.kpi.KpiMain', {
    extend: 'Ext.container.Container',
    layout: 'fit',
	flex:1,
    paramObj: {},

    requires: [],

    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },

    initComponent: function () {
        var me = this;
        if (me.kpicardpanel == null) {
            me.kpicardpanel = Ext.create(
                'FHD.view.kpi.cmp.kpi.KpiCard', {
                    flex: 1,
                    pcontainer: me,
                    paramObj: me.paramObj,
                    undo: me.undo
                });
        }
        me.navigationBar = Ext.create('FHD.ux.NavigationBars');
        if (me.treeId) {
            me.childCom = [{
                    xtype: 'box',
                    height: 40,
                    style: 'border-left: 1px  #99bce8 solid;',
                    html: '<div id="' + me.id + 'DIV" class="navigation"></div>',
                    listeners: {
                        afterrender: function () {
                            me.reLoadNav();
                        }
                    }
                },
                me.kpicardpanel
            ]
        } else {
            me.childCom = [me.kpicardpanel]
        }

        Ext.apply(me, {
            layout: {
                align: 'stretch',
                type: 'vbox'
            },
            items: me.childCom
        });

        me.callParent(arguments);

    },

    reLoadNav: function () {
        var me = this;
        if (!me.paramObj.kpiname) {
            me.paramObj.kpiname = "";
        }
        me.navigationBar.renderHtml(me.id + 'DIV', me.paramObj.navId,
            me.paramObj.kpiname, me.paramObj.backType, me.treeId);
    }

});