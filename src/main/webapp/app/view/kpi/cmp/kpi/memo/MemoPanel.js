Ext.define('FHD.view.kpi.cmp.kpi.memo.MemoPanel', {
    extend: 'Ext.form.Panel',
    layout: 'vbox',
    border: true,
    autoScroll: false,

    saveFun: function () {
        var me = this;
        if(!me.kgrid){
        	Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请先点击列表中的备注图标，再添加备注信息');
        	return ;
        }
        if(!me.theme.getValue()) {
        	Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请填写主题！');
        	return;
        }
        var form = me.getForm();
        var memorecordgrid = me.memomainpanel.memorecordgrid;
        FHD.submit({
            form: form,
            url: __ctxPath + '/kpi/kpimemo/savekpimemo.f',
            params: {
                id: me.memoId,
                kgrid: me.kgrid
            },
            callback: function (data) {
                memorecordgrid.store.load();
                me.memoId = null;
                me.clearFormData();
            }
        });
    },

    clearFormData: function () {
        var me = this;
        me.getForm().reset();
    },

    formLoad: function (id) {
        var me = this;
        me.clearFormData();
        var vobj = me.getForm().getValues();
        var id = id;
        me.form.load({
            url: __ctxPath + '/kpi/kpimemo/findkpimemobyid.f',
            params: {
                id: id
            },
            /**
             * form加载数据成功后回调函数
             */
            success: function (form, action) {
                return true;
            }
        });
    },

    initComponent: function () {
        var me = this;
        me.tbar = ['->', {
            iconCls: 'icon-control-stop-blue',
            text: '保存',
            handler: function () {
                me.saveFun();
            }
        }]

        me.theme = Ext.create('Ext.form.field.Text', {
            xtype: 'textfield',
            name: 'theme',
            margin: '7 10 5 20',
            fieldLabel: "主题"+ '<font color=red>*</font>',
            maxLength: 255,
            width: 400,
            allowBlank: false
        });
        me.important = Ext.create('Ext.form.RadioGroup', {
            fieldLabel: "重要性",
            width: 300,
            margin: '7 10 5 20',
            vertical: true,
            items: [{
                boxLabel: "高",
                name: 'important',
                inputValue: '0alarm_startus_h',
                checked: true
            }, {
                boxLabel: "低",
                name: 'important',
                inputValue: '0alarm_startus_l'
            }, {
                boxLabel: "正常",
                name: 'important',
                inputValue: '0alarm_startus_n'
            }]
        });

        me.memo = Ext.create('Ext.form.field.TextArea', {
            value: '',
            margin: '7 10 7 20',
            name: 'memo',
            fieldLabel: "注释",
            maxLength: 255,
            height: 60,
            width: 400
        });

        Ext.apply(me, {
            items: [me.theme, me.important, me.memo]
        }, me.tbar)

        me.callParent(arguments);

    }
});