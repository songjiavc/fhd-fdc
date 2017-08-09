Ext.define('FHD.view.kpi.cmp.sc.scWarningForm', {
    extend: 'FHD.ux.GridPanel',
    initComponent: function() {
    	var me = this;
        var gridColums = [{
            header: FHD.locale.get("fhd.alarmplan.form.level"),
            dataIndex: 'level',
            sortable:false,
            menuDisabled: true,
            width: 60,
            flex: 1,
            renderer: function (v) {
                var color = "";
                var text = "";
                var icon = "";
                switch (v) {
                    case '0alarm_startus_h':
                        /* 对应数据字典中的主键 */
                        color = 'symbol_high_sm';
                        break;
                    case '0alarm_startus_m':
                        color = 'symbol_mid_sm';
                        break;
                    case '0alarm_startus_l':
                        color = 'symbol_low_sm';
                        break;
                    case '0alarm_startus_safe':
                    	color = 'symbol_jrj_g_sm';
                    	break;
                    		
                }
                return color != "" ? '<img src="' + __ctxPath + '/images/icons/' + color + '.gif">' : "";
            }

        }, {
            header: FHD.locale.get("fhd.alarmplan.form.range"),
            dataIndex: 'range',
            width: 60,
            flex: 1,
            menuDisabled: true,
            sortable:false,
            renderer: function (v) {
                var rangestr = v;
                if (v.indexOf("<") != -1) {
                    rangestr = v.replace("<", "&lt;");
                }
                return rangestr;
            }
        }];
        Ext.apply(me, {
        	url: me.url,
            searchable: false,
            cols: gridColums,
            height: 180,
            pagable: false,
            checked: false,
            extraParams: me.extraParams
        });
        
        me.callParent(arguments);
    }
})