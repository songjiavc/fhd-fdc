Ext.define('FHD.view.kpi.realtime.RealTimeKpiEdit', {
    extend: 'Ext.form.Panel',
    border: false,
	requires: ['FHD.ux.dict.DictSelectForEditGrid'],

    initComponent: function () {
        var me = this;

        Ext.applyIf(me, {
            autoScroll: true,
            layout: 'column',
            bodyPadding: "0 3 3 3",
            buttons: [{
                text: "保存",
                handler: function () {
                    me.saveKpi(me);
                }
            }, {
                text: "取消",
                handler: function () {
                    me.paramObj.formwindow.close();
                }
            }]
        });

        me.callParent(arguments);

        //向form表单中添加控件
        me.addComponent();

    },

    addComponent: function () {
        var me = this;
        var basicfieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset', //基本信息fieldset
            autoHeight: true,
            autoWidth: true,
            collapsible: true,
            defaults: {
                margin: '3 30 3 30',
                labelWidth: 100
            },
            layout: {
                type: 'column'
            },
            title: FHD.locale.get('fhd.common.baseInfo')
        });
        //名称
        var name = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: "名称" + '<font color=red>*</font>', //名称
            name: 'name',
            maxLength: 255,
            columnWidth: .5,
            allowBlank: false
        });
        basicfieldSet.add(name);

        var code = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: "编号", //编码
            name: 'code',
            maxLength: 255,
            allowBlank: true,
            columnWidth: .5
        });

        basicfieldSet.add(code);
        
        var units = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            multiSelect: false,
            name: 'unit',
            labelAlign: 'left',
            dictTypeId: '0units',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.unit')+ '<font color=red>*</font>', //单位
            columnWidth: .5,
            allowBlank: false
        });
        
        basicfieldSet.add(units);
        //告警方案
        var alarmStore = Ext.create('Ext.data.Store', { //告警方案store
            fields: ['id', 'text'],
            proxy: {
                type: 'ajax',
                url: __ctxPath + '/kpi/real/findalarmcomboxvalue.f',
                reader: {
                    type: 'json',
                    root: 'datas'
                }
            },
            autoLoad: true
        });

        me.alarmComb = Ext.create('Ext.form.field.ComboBox', {
            store: alarmStore,
            name: 'alarmId',
            columnWidth: .5,
            fieldLabel: '告警方案',
            allowBlank: true,
            editable: false,
            queryMode: 'local',
            displayField: 'text',
            valueField: 'id',
            triggerAction: 'all'
        });
        basicfieldSet.add(me.alarmComb);

        //描述
        var desc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            rows: 5,
            columnWidth: 1,
            labelAlign: 'left',
            fieldLabel: "描述",
            name: 'desc',
            allowBlank: true
        });

        
        
        basicfieldSet.add(desc);
        
        var idHidden = Ext.widget('hidden',{
                xtype: 'hidden',
                hidden: true,
                name: 'id'
            });
            
        basicfieldSet.add(idHidden);  

        me.add(basicfieldSet);

    },

    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },

    reloadData: function () {
        var me = this;
        var kpiId = me.paramObj.kpiId;
        me.load({
                waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
                url: __ctxPath + '/kpi/real/findrealtimekpi.f',
                params: {
                    kpiId: kpiId
                },
                success: function (form, action) {
                    return true;
                }
            });
    },
    
    refresh:function(){
    	var me = this;
    },
    
    validate:function(me,vobj){
    	var flag = true;
    	var kpiId = me.paramObj.kpiId;
    	if(!kpiId){
    		kpiId = "";
    	}
    	var code = vobj.code;
    	var name = vobj.name;
    	var items = {
    		kpiId:kpiId,
    		code:code,
    		name:name
    	}
    	//校验编号是否重复
    	FHD.ajax({
            async: false,
            url: __ctxPath + '/kpi/real/findrealtimekpibycode.f',
            params: {
                items: Ext.JSON.encode(items)
            },
            callback: function (data) {
                if (!data) {
                    FHD.notification("编号重复", FHD.locale.get('fhd.common.prompt'));
                    flag =  false;
                }else{
                	flag = true;
                }
            }
        });
        if(flag){
        	//校验名称是否重复
	        FHD.ajax({
	            async: false,
	            url: __ctxPath + '/kpi/real/findrealtimekpibyname.f',
	            params: {
	                items: Ext.JSON.encode(items)
	            },
	            callback: function (data) {
	                if (!data) {
	                    FHD.notification("名称重复", FHD.locale.get('fhd.common.prompt'));
	                    flag =  false;
	                }else{
	                	flag = true;
	                }
	            }
	        });
        }
        
    	return flag;
    },
    
    saveKpi: function (me) {
        var form = me.getForm();
        
        if (!form.isValid()) {
            return false;
        }
        var vobj = form.getValues();
        if(me.validate(me,vobj)){
        	FHD.ajax({
            async: false,
            url: __ctxPath + '/kpi/real/mergerealtimekpi.f',
            params: {
                items: Ext.JSON.encode(vobj)
            },
            callback: function (data) {
                if (data) {
                    FHD.notification(FHD.locale.get('fhd.common.operateSuccess'), FHD.locale.get('fhd.common.prompt'));
                    me.paramObj.formwindow.close();
                    //刷新列表
                    me.refresh();
                }
            }
        });
        }
        
    }
    




});