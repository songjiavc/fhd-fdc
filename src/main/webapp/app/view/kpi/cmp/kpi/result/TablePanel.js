/**
 * HTML表格面板
 *
 * @author 王鑫
 */
Ext.define('FHD.view.kpi.cmp.kpi.result.TablePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.tablePanel',
	region:'center',
    isNumber: function (oNum) {
        if (oNum.indexOf(' ') != -1) return false;
        if (oNum == '') return true;
        var pattern = new RegExp('[^d|^-]+(.d+)?$');
        if (!oNum.match(pattern))
            return false;
        try {
            if (parseFloat(oNum) != oNum) return false;
        } catch (ex) {
            return false;
        }
        return true;
    },
    // 初始化方法
    initComponent: function () {
        var me = this;
        Ext.apply(me, {
            html: me.html,
            border: false,
            autoScroll: true,
            bodyStyle: 'border-bottom: 1px solid #bec0c0 !important;'
        });

        me.callParent(arguments);
    },

    //单点编辑
    oneInput: function (timeId, yearId) {
        var me = this;
        var pcontainer = me.pcontainer;
        pcontainer.resultParam.paraobj.oneEdit = 1;
        pcontainer.resultParam.paraobj.timeId = timeId;
        pcontainer.resultParam.paraobj.yearId = yearId;
        pcontainer.resultParam.paraobj.isNewValue = FHD.data.isNewValue;

        me.body.mask("Loading...", "x-mask-loading");
        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/createtable.f?edit=false',
            params: {
                condItem: Ext.JSON.encode(pcontainer.resultParam.paraobj)
            },
            callback: function (data) {
                if (data && data.success) {
                    me.body.update(data.tableHtml);
                    me.body.unmask();
                }
            }
        });

        FHD.pram.edit = 1;
    },
    focusyear: function (obj) {
        $(obj).css("background-color", "#EEEEEE");
    },
    focusmonth: function (obj, id) {
        var rows = $("#" + id + " tr").each(function () {
            $(this).css('background-color', '#FFFFFF');
            if ($(this).attr("yearrowspan") == "1") {
                $(this).children("td").each(function (index, obj) {
                    if (0 != index && 1 != index) {
                        $(obj).css("background-color", "#FFFFFF");
                    }
                });
            }
            if ($(this).attr("quarterrowspan") == "1") {
                $(this).children("td").each(function (index, obj) {
                    if (0 != index) {
                        $(obj).css("background-color", "#FFFFFF");
                    }
                });
            }
        });
        if ("1" == $(obj).attr("yearrowspan")) {
            $(obj).children("td").each(function (index) {
                if (0 != index && 1 != index) {
                    $(this).css("background-color", "#EEEEEE");
                }
            });
        } else if ("1" == $(obj).attr("quarterrowspan")) {
            $(obj).children("td").each(function (index) {
                if (0 != index) {
                    $(this).css("background-color", "#EEEEEE");
                }
            });
        } else {
            $(obj).css("background-color", "#EEEEEE");
        }
    },
    focusweek: function (obj, id) {
        var rows = $("#" + id + " tr").each(function () {
            $(this).css('background-color', '#FFFFFF');
            if ($(this).attr("yearrowspan") == "1") {
                $(this).children("td").each(function (index, obj) {
                    if (0 != index && 1 != index) {
                        $(obj).css("background-color", "#FFFFFF");
                    }
                });
            }
            if ($(this).attr("quarterrowspan") == "1") {
                $(this).children("td").each(function (index, obj) {
                    if (0 != index) {
                        $(obj).css("background-color", "#FFFFFF");
                    }
                });
            }
        });
        if ("1" == $(obj).attr("yearrowspan")) {
            $(obj).children("td").each(function (index) {
                if (0 != index && 1 != index) {
                    $(this).css("background-color", "#EEEEEE");
                }
            });
        } else if ("1" == $(obj).attr("quarterrowspan")) {
            $(obj).children("td").each(function (index) {
                if (0 != index) {
                    $(this).css("background-color", "#EEEEEE");
                }
            });
        } else {
            $(obj).css("background-color", "#EEEEEE");
        }
    },
    focus: function (obj, id) {
        var me = this;
        var rows = $("#" + me.pcontainer.id + " tr").each(function () {
            $(this).css('background-color', '#FFFFFF');
            if ($(this).attr("rowspaneflag") == "1") {
                $(this).children("td").each(function (index, obj) {
                    if (0 != index) {
                        $(obj).css("background-color", "#FFFFFF");
                    }
                });
            }
        });
        if ("1" == $(obj).attr("rowspaneflag")) {
            $(obj).children("td").each(function (index) {
                if (0 != index) {
                    $(this).css("background-color", "#EEEEEE");
                }
            });
        } else {
            $(obj).css("background-color", "#EEEEEE");
        }

    },
    //单点保存
    oneSave: function (yearId, kpiId, timeId, realityValue, targetValue, assessValue) {
        var me = this;
        if (!me.isNumber(realityValue)) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
            return;
        }
        if (!me.isNumber(targetValue)) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
            return;
        }
        if (!me.isNumber(assessValue)) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
            return;
        }
        var pcontainer = me.pcontainer;
        var paraarr = [];
        var value = {};

        value[timeId] = realityValue + ',' + targetValue + ',' + assessValue;
        paraarr.push(value);

        me.body.mask("Loading...", "x-mask-loading");
        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/savekpigatherresultquarter.f?kpiid=' + kpiId,
            params: {
                params: Ext.JSON.encode(paraarr)
            },
            callback: function (data) {
                if (data && data.success) {
                    pcontainer.resultParam.paraobj.timeId = '';
                    pcontainer.resultParam.paraobj.oneEdit = 1;
                    FHD.ajax({
                        url: __ctxPath + '/kpi/kpi/createtable.f?edit=false',
                        params: {
                            condItem: Ext.JSON.encode(pcontainer.resultParam.paraobj)
                        },
                        callback: function (data) {
                            if (data && data.success) {
                                me.body.update(data.tableHtml);
                                me.body.unmask();
                            }
                        }
                    });
                }
            }
        });
        FHD.pram.save = 1;
    },

    //全部修改保存
    save: function () {
        var me = this;
        var pcontainer = me.pcontainer;
        if (pcontainer.resultParam.frequenceTemp == "0frequecy_month") {
            this.saveMonth();
        } else if (pcontainer.resultParam.frequenceTemp == "0frequecy_quarter") {
            this.saveQuarter();
        } else if (pcontainer.resultParam.frequenceTemp == "0frequecy_year") {
            this.saveYear();
        } else if (pcontainer.resultParam.frequenceTemp == "0frequecy_week") {
            this.saveWeek();
        } else if (pcontainer.resultParam.frequenceTemp == "0frequecy_halfyear") {
            this.saveHalfYear();
        }
    },

    saveYear: function () {
        var me = this;
        var pcontainer = me.pcontainer;
        var paraarr = [];
        var realityYear = null;
        var targetYear = null;
        var assessYear = null;
        var me = this;

        //年
        realityYear = document.getElementById(pcontainer.id + 'realityYearId' + FHD.data.yearId + 'reality');
        targetYear = document.getElementById(pcontainer.id + 'targetYearId' + FHD.data.yearId + 'target');
        assessYear = document.getElementById(pcontainer.id + 'assessYearId' + FHD.data.yearId + 'assess');
        if (realityYear != null && targetYear != null && assessYear != null) {
            if (!me.isNumber(realityYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
                return;
            }
            if (!me.isNumber(targetYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
                return;
            }
            if (!me.isNumber(assessYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
                return;
            }

            var value = {};
            value[FHD.data.yearId + ''] = realityYear.value + ',' + targetYear.value + ',' + assessYear.value;
            paraarr.push(value);
        }


        pcontainer.body.mask("Loading...", "x-mask-loading");
        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/savekpigatherresultquarter.f?kpiid=' + pcontainer.resultParam.kpiid,
            params: {
                params: Ext.JSON.encode(paraarr)
            },
            callback: function (data) {
                if (data && data.success) {
                    me.refresh();
                }
            }
        });
    },

    getNumber: function (str) {
        if (parseInt(str) < 10) {
            str = '0' + str;
        }

        return str;
    },

    saveWeek: function () {
        var me = this;
        var pcontainer = me.pcontainer;
        var paraarr = [];

        var week = 0;
        var month = 0;
        var realityWeek = null;
        var targetWeek = null;
        var assessWeek = null;

        var realityMonth = null;
        var targetMonth = null;
        var assessMonth = null;

        var realityYear = null;
        var targetYear = null;
        var assessYear = null;
        var me = this;

        //周
        for (var j = 1; j < 55 + 1; j++) {
            week = j;
            realityWeek = document.getElementById(pcontainer.id + FHD.data.yearId + 'w' + week + 'reality');
            targetWeek = document.getElementById(pcontainer.id + FHD.data.yearId + 'w' + week + 'target');
            assessWeek = document.getElementById(pcontainer.id + FHD.data.yearId + 'w' + week + 'assess');
            if (realityWeek != null && targetWeek != null && assessWeek != null) {
                if (!me.isNumber(realityWeek.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
                    return;
                }
                if (!me.isNumber(targetWeek.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
                    return;
                }
                if (!me.isNumber(assessWeek.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
                    return;
                }
                var value = {};
                value[FHD.data.yearId + 'w' + week] = realityWeek.value + ',' + targetWeek.value + ',' + assessWeek.value;
                paraarr.push(value);
            }
        }

        //月
        for (var j = 1; j < 12 + 1; j++) {
            month = j;
            realityMonth = document.getElementById(pcontainer.id + 'realityMonthId' + month + 'reality');
            targetMonth = document.getElementById(pcontainer.id + 'targetMonthId' + month + 'target');
            assessMonth = document.getElementById(pcontainer.id + 'assessMonthId' + month + 'assess');
            if (realityMonth != null && targetMonth != null && assessMonth != null) {
                if (!me.isNumber(realityMonth.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
                    return;
                }
                if (!me.isNumber(targetMonth.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
                    return;
                }
                if (!me.isNumber(assessMonth.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
                    return;
                }
                var value = {};
                value[FHD.data.yearId + 'mm' + this.getNumber(j)] = realityMonth.value + ',' + targetMonth.value + ',' + assessMonth.value;
                paraarr.push(value);
            }
        }

        //年
        realityYear = document.getElementById(pcontainer.id + 'realityYearId' + FHD.data.yearId + 'reality');
        targetYear = document.getElementById(pcontainer.id + 'targetYearId' + FHD.data.yearId + 'target');
        assessYear = document.getElementById(pcontainer.id + 'assessYearId' + FHD.data.yearId + 'assess');
        if (realityYear != null && targetYear != null && assessYear != null) {
            if (!me.isNumber(realityYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
                return;
            }
            if (!me.isNumber(targetYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
                return;
            }
            if (!me.isNumber(assessYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
                return;
            }
            var value = {};
            value[FHD.data.yearId + ''] = realityYear.value + ',' + targetYear.value + ',' + assessYear.value;
            paraarr.push(value);
        }

        pcontainer.body.mask("Loading...", "x-mask-loading");
        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/savekpigatherresultquarter.f?kpiid=' + pcontainer.resultParam.kpiid,
            params: {
                params: Ext.JSON.encode(paraarr)
            },
            callback: function (data) {
                if (data && data.success) {
                    me.refresh();
                }
            }
        });
    },

    saveMonth: function () {
        var me = this;
        var pcontainer = me.pcontainer;
        var paraarr = [];
        var quarter = 0;
        var month = 0;
        var realityQuarter = null;
        var targetQuarter = null;
        var assessQuarter = null;

        var realityMonth = null;
        var targetMonth = null;
        var assessMonth = null;

        var realityYear = null;
        var targetYear = null;
        var assessYear = null;
        var me = this;

        //季度
        for (var j = 1; j < 4 + 1; j++) {
            quarter = j;
            realityQuarter = document.getElementById(me.pcontainer.id + FHD.data.yearId + 'Q' + quarter + 'reality');
            targetQuarter = document.getElementById(me.pcontainer.id + FHD.data.yearId + 'Q' + quarter + 'target');
            assessQuarter = document.getElementById(me.pcontainer.id + FHD.data.yearId + 'Q' + quarter + 'assess');
            if (realityQuarter != null && targetQuarter != null && assessQuarter != null) {
                if (!me.isNumber(realityQuarter.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
                    return;
                }
                if (!me.isNumber(targetQuarter.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
                    return;
                }
                if (!me.isNumber(assessQuarter.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
                    return;
                }
                var value = {};
                value[FHD.data.yearId + 'Q' + quarter] = realityQuarter.value + ',' + targetQuarter.value + ',' + assessQuarter.value;
                paraarr.push(value);
            }
        }

        //月
        for (var j = 1; j < 12 + 1; j++) {
            month = j;
            realityMonth = document.getElementById(me.pcontainer.id + FHD.data.yearId + 'mm' + this.getNumber(month) + 'reality');
            targetMonth = document.getElementById(me.pcontainer.id + FHD.data.yearId + 'mm' + this.getNumber(month) + 'target');
            assessMonth = document.getElementById(me.pcontainer.id + FHD.data.yearId + 'mm' + this.getNumber(month) + 'assess');
            if (realityMonth != null && targetMonth != null && assessMonth != null) {
                if (!me.isNumber(realityMonth.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
                    return;
                }
                if (!me.isNumber(targetMonth.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
                    return;
                }
                if (!me.isNumber(assessMonth.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
                    return;
                }
                var value = {};
                value[FHD.data.yearId + 'mm' + this.getNumber(j)] = realityMonth.value + ',' + targetMonth.value + ',' + assessMonth.value;
                paraarr.push(value);
            }
        }

        //年
        realityYear = document.getElementById(pcontainer.id + 'realityYearId' + me.pcontainer.id + FHD.data.yearId + 'reality');
        targetYear = document.getElementById(pcontainer.id + 'targetYearId' + me.pcontainer.id + FHD.data.yearId + 'target');
        assessYear = document.getElementById(pcontainer.id + 'assessYearId' + me.pcontainer.id + FHD.data.yearId + 'assess');
        if (realityYear != null && targetYear != null && assessYear != null) {
            if (!me.isNumber(realityYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
                return;
            }
            if (!me.isNumber(targetYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
                return;
            }
            if (!me.isNumber(assessYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
                return;
            }
            var value = {};
            value[FHD.data.yearId + ''] = realityYear.value + ',' + targetYear.value + ',' + assessYear.value;
            paraarr.push(value);
        }

        pcontainer.body.mask("Loading...", "x-mask-loading");
        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/savekpigatherresultquarter.f?kpiid=' + pcontainer.resultParam.kpiid,
            params: {
                params: Ext.JSON.encode(paraarr)
            },
            callback: function (data) {
                if (data && data.success) {
                    me.refresh();
                }
            }
        });
    },

    saveQuarter: function () {
        var me = this;
        var pcontainer = me.pcontainer;
        var paraarr = [];
        var quarter = 0;
        var month = 0;
        var realityQuarter = null;
        var targetQuarter = null;
        var assessQuarter = null;

        var realityYear = null;
        var targetYear = null;
        var assessYear = null;
        var me = this;

        //季度
        for (var j = 1; j < 4 + 1; j++) {
            quarter = j;
            realityQuarter = document.getElementById(pcontainer.id + FHD.data.yearId + 'Q' + quarter + 'reality');
            targetQuarter = document.getElementById(pcontainer.id + FHD.data.yearId + 'Q' + quarter + 'target');
            assessQuarter = document.getElementById(pcontainer.id + FHD.data.yearId + 'Q' + quarter + 'assess');
            if (realityQuarter != null && targetQuarter != null && assessQuarter != null) {
                if (!me.isNumber(realityQuarter.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
                    return;
                }
                if (!me.isNumber(targetQuarter.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
                    return;
                }
                if (!me.isNumber(assessQuarter.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
                    return;
                }
                var value = {};
                value[FHD.data.yearId + 'Q' + quarter] = realityQuarter.value + ',' + targetQuarter.value + ',' + assessQuarter.value;
                paraarr.push(value);
            }
        }

        //年
        realityYear = document.getElementById(pcontainer.id + 'realityYearId' + FHD.data.yearId + 'reality');
        targetYear = document.getElementById(pcontainer.id + 'targetYearId' + FHD.data.yearId + 'target');
        assessYear = document.getElementById(pcontainer.id + 'assessYearId' + FHD.data.yearId + 'assess');
        if (realityYear != null && targetYear != null && assessYear != null) {
            if (!me.isNumber(realityYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
                return;
            }
            if (!me.isNumber(targetYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
                return;
            }
            if (!me.isNumber(assessYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
                return;
            }
            var value = {};
            value[FHD.data.yearId + ''] = realityYear.value + ',' + targetYear.value + ',' + assessYear.value;
            paraarr.push(value);
        }

        pcontainer.body.mask("Loading...", "x-mask-loading");
        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/savekpigatherresultquarter.f?kpiid=' + pcontainer.resultParam.kpiid,
            params: {
                params: Ext.JSON.encode(paraarr)
            },
            callback: function (data) {
                if (data && data.success) {
                    me.refresh();
                }
            }
        });
    },

    saveHalfYear: function () {
        var me = this;
        var pcontainer = this.pcontainer;
        var paraarr = [];
        var halfYear = 0;
        var month = 0;
        var realityHalfYear = null;
        var targetHalfYear = null;
        var assessHalfYear = null;

        var realityYear = null;
        var targetYear = null;
        var assessYear = null;
        var me = this;

        //半年
        for (var j = 0; j < 2; j++) {
            halfYear = j;
            realityHalfYear = document.getElementById(FHD.data.yearId + 'hf' + halfYear + 'reality');
            targetHalfYear = document.getElementById(FHD.data.yearId + 'hf' + halfYear + 'target');
            assessHalfYear = document.getElementById(FHD.data.yearId + 'hf' + halfYear + 'assess');
            if (realityHalfYear != null && targetHalfYear != null && assessHalfYear != null) {
                if (!me.isNumber(realityHalfYear.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
                    return;
                }
                if (!me.isNumber(targetHalfYear.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
                    return;
                }
                if (!me.isNumber(assessHalfYear.value)) {
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
                    return;
                }
                var value = {};
                value[FHD.data.yearId + 'hf' + halfYear] = realityHalfYear.value + ',' + targetHalfYear.value + ',' + assessHalfYear.value;
                paraarr.push(value);
            }
        }

        //年
        realityYear = document.getElementById('realityYearId' + FHD.data.yearId + 'reality');
        targetYear = document.getElementById('targetYearId' + FHD.data.yearId + 'target');
        assessYear = document.getElementById('assessYearId' + FHD.data.yearId + 'assess');
        if (realityYear != null && targetYear != null && assessYear != null) {
            if (!me.isNumber(realityYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "实际值请输入数字.");
                return;
            }
            if (!me.isNumber(targetYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "目标值请输入数字.");
                return;
            }
            if (!me.isNumber(assessYear.value)) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "评估值请输入数字.");
                return;
            }
            var value = {};
            value[FHD.data.yearId + ''] = realityYear.value + ',' + targetYear.value + ',' + assessYear.value;
            paraarr.push(value);
        }

        pcontainer.body.mask("Loading...", "x-mask-loading");
        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/savekpigatherresultquarter.f?kpiid=' + pcontainer.resultParam.kpiid,
            params: {
                params: Ext.JSON.encode(paraarr)
            },
            callback: function (data) {
                if (data && data.success) {
                    me.refresh();
                }
            }
        });
    },

    refresh: function () {
        var me = this;
        var pcontainer = this.pcontainer;
        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/createtable.f?edit=false',
            params: {
                condItem: Ext.JSON.encode(pcontainer.resultParam.paraobj)
            },
            callback: function (data) {
                if (data && data.success) {
                    pcontainer.items.items[2].body.update(data.tableHtml);
                    pcontainer.down("[name='gatherresulttableinputsave']").hide();
                    pcontainer.body.unmask();
                }
            }
        });
    },

    //add by haojing  添加备注信息
    getData: function (kgrid, memoStr) {
        var me = this;
        me.queryMemoUrl = __ctxPath + "/kpi/kpimemo/kpimemolistloader.f"; //树查询url
        var memorecordgrid = me.pcontainer.memomainpanel.memorecordgrid;
        memorecordgrid.store.proxy.url = me.queryMemoUrl; //动态赋给机构列表url
        memorecordgrid.store.proxy.extraParams.kgrid = kgrid;
        memorecordgrid.reloadData();
        var con = me.pcontainer.memomainpanel;
        con.items.items[1].kgrid = kgrid;
        con.setTitle(memoStr);
    }
});