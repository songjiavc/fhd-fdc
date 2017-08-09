Ext.define('FHD.view.kpi.homepage.home', {

    extend: 'Ext.panel.Panel',

    title: '',


    border: false,

    layout: {
        align: 'stretch',
        type: 'vbox'
    },

    focusAdd: function () {
        var me = this;
        me.objectfocuswindow = Ext.create('FHD.ux.kpi.ObjectFocusSelectorWindow', {
            submit: function () {
                $("#nav_myfocuse").click();
            }
        });
        me.objectfocuswindow.show();
        me.objectfocuswindow.addComponent();
    },
    setComponentBtnVisable: function () {
        var me = this;
        me.setBtnVisable(me.myFocusKpiPanel);
        me.setBtnVisable(me.myFocusScPanel);
        me.setBtnVisable(me.myFocusStrPanel);
    },
    setBtnVisable: function (c) {
        var me = this;
        var length = c.centerpanel.items.items.length;
        if (0 == length) {
            c.preBtn.setVisible(false);
            c.nextBtn.setVisible(false);
        } else {
            c.preBtn.setVisible(true);
            c.nextBtn.setVisible(true);
        }

    },

    explore: function (me) {
        if ($("#statusDiv").css('height') == '50px') {
            $("#statusDiv").animate({
                height: "0px",
                opacity: "1"
            }, 200);
            $("#explore").remove();
            $("#infolabel").css("display", "block");
        } else {
            $("#infolabel").css("display", "none");

            var exporehtml = '<div id="explore" style="width:600px;margin-left:380px;">';
            exporehtml += '<input type="text"  name="country" id="autocomplete" style="float: left;margin:5px; 0 0 0;width:450px;font-size: 20px;font-family:Microsoft YaHei,微软雅黑; padding: 5px; font-weight: 600; display: block;"/>';
            exporehtml += '<div id="selection">';
            exporehtml += '<img style="cursor: pointer;" id="exploreImgsubmit" title="搜索" src="' + __ctxPath + '/images/icons/explore.png' + ' "/>';
            exporehtml += '</div>';
            exporehtml += '</div>';

            $("#statusDiv").animate({
                height: "50px",
                opacity: "0.8"
            }, 200);

            $(exporehtml).insertAfter($("#imgfocusDiv"));

            $("#exploreImgsubmit").click(function () {
                me.exploreSubmit(me);

            });

        }
    },

    exploreSubmit: function (me) {
        var queryname = $("#autocomplete").val();
        me.myFocusKpiPanel.query(queryname);
        me.myFocusKpiPanel.setBtnStatus(me.myFocusKpiPanel.centerpanel.hiddenStart, me.myFocusKpiPanel.centerpanel.isNext);
        me.myFocusScPanel.query(queryname);
        me.myFocusScPanel.setBtnStatus(me.myFocusScPanel.centerpanel.hiddenStart, me.myFocusScPanel.centerpanel.isNext);
        me.myFocusStrPanel.query(queryname);
        me.myFocusStrPanel.setBtnStatus(me.myFocusStrPanel.centerpanel.hiddenStart, me.myFocusStrPanel.centerpanel.isNext);
        me.setComponentBtnVisable();
    },

    initScript: function () {
        var me = this;

        $("#exploreImg").click(function () {
            me.explore(me);
        });

        $("#focusImg").click(function () {
            me.focusAdd(me);
        });

    },

    reload: function (me) {
        me.myFocusStrPanel.reload();
        me.myFocusScPanel.reload();
        me.myFocusKpiPanel.reload();
    },

    // 初始化方法
    initComponent: function () {
        var me = this;

        var statusHtml = '<div id="statusDiv" style="margin:0 0 0 0;position:relative;height:0px; filter:alpha(Opacity=80);-moz-opacity:0.5;opacity: 0.5; background-color:#C6E2FF; ">';
        statusHtml += '<div id="imgDiv" style="width: 20px; float: right;margin:15px 5px 0 0 ;">';
        statusHtml += '<img style="cursor: pointer;" id="exploreImg" title="搜索" src="' + __ctxPath + '/images/icons/icon_search.gif' + ' "/>';
        statusHtml += '</div>';
        statusHtml += '<div id="imgfocusDiv" style="width: 20px; float: right;margin:15px 10px 0 0 ;">';
        statusHtml += '<img style="cursor: pointer;" id="focusImg" title="关注" src="' + __ctxPath + '/images/icons/kpi_heart_add.png' + ' "/>';
        statusHtml += '</div>';
        statusHtml += '</div>';


        me.myFocusScfieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset',
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            title: '我关注的记分卡',
            flex: 1,
            layout: 'fit',
            style: 'margin:5px 0 0 0'
        });

        me.myFocusStrfieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset',
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            title: '我关注的目标',
            flex: 1,
            layout: 'fit',
            style: 'margin:48px 0 0 0'
        });

        me.myFocusKpifieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset',
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            title: '我关注的指标',
            flex: 1,
            layout: 'fit',
            style: 'margin:5px 0 0 0'
        });

        me.myFocusStrPanel = Ext.create('FHD.view.kpi.homepage.myFocus', {
            objectType: 'str',
            chartType: 'AngularGauge',
            queryUrl: __ctxPath + '/kpi/createchartList.f',
            searchContent: '输入目标名称',
            chartHeight: 140,
            chartWidth: 200,
            pcontainer: me
        });

        me.myFocusStrfieldSet.add(me.myFocusStrPanel);

        me.myFocusScPanel = Ext.create('FHD.view.kpi.homepage.myFocus', {
            objectType: 'sc',
            chartType: 'HLinearGauge2',
            chartHeight: 90,
            chartWidth: 200,
            searchContent: '输入记分卡名称',
            queryUrl: __ctxPath + '/kpi/createchartList.f',
            pcontainer: me
        });

        me.myFocusScfieldSet.add(me.myFocusScPanel);

        me.myFocusKpiPanel = Ext.create('FHD.view.kpi.homepage.myFocus', {
            objectType: 'kpi',
            chartType: 'AngularGauge',
            queryUrl: __ctxPath + '/kpi/createkpichartList.f',
            searchContent: '输入指标名称',
            chartHeight: 130,
            chartWidth: 190,
            pcontainer: me
        });

        me.myFocusKpifieldSet.add(me.myFocusKpiPanel);

        Ext.applyIf(me, {
            html: statusHtml,
            items: [
                me.myFocusStrfieldSet, me.myFocusScfieldSet, me.myFocusKpifieldSet
            ],
            autoScroll: true,
            listeners: {
                afterrender: function (c, opts) {
                    me.initScript();
                }
            }
        });

        me.callParent(arguments);

    }

});