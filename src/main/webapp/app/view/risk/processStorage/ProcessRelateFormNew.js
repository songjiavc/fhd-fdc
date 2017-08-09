/**
 * 风险事件基本信息页面（精简）
 *
 * @author
 */
Ext.define('FHD.view.risk.processStorage.ProcessRelateFormNew', {
    extend: 'Ext.form.Panel',
    alias: 'widget.processrelateformnew',
    requires:[],

    //保存方法
    save: function() {
        var me = this;
        if(me.isEdit){      //修改
            return me.merge(me.riskId);
        }
        var form = me.getForm();
        //责任部门
        var respDeptName = me.respDeptName.getValue();
        //相关部门
        var relaDeptName = me.relaDeptName.getValue();
        // 影响指标
        var influKpiName = me.influKpiName.getFieldValue();
        // 影响流程
        var influProcessureName = me.influProcessureName.getValue();

        if (form.isValid() && me.customValidate()) {
            FHD.submit({
                form : form,
                url : __ctxPath + '/risk/relate/saveRiskInfo',
                params : {
                    isRiskClass : me.type, // 风险还是风险事件
                    state:me.state,
                    archiveStatus:me.archiveStatus,

                    schm: me.schm
                },
                callback : function(data) {
                    if(data.success){
                        me.up('processstoragecardnew').riskGrid.reloadData(me.riskId,'org');
                        me.up('processstoragecardnew').showRiskGrid();
                    }
                }
            });
        }else{
            me.body.unmask();.36
            return false;
        }
    },
    //修改保存
    merge : function(id) {
        var me = this;
        var form = me.getForm();
        //责任部门
        var respDeptName = me.respDeptName.getValue();
        //相关部门
        var relaDeptName = me.relaDeptName.getValue();
        // 影响指标
        var influKpiName = me.influKpiName.getFieldValue();
        // 影响流程
        var influProcessureName = me.influProcessureName.getValue();

        if (form.isValid() && me.customValidate()) {
            FHD.submit({
                form : form,
                url : __ctxPath + '/risk/relate/mergeRiskInfo',
                params : {
                    id:id,
                    isRiskClass : me.type, // 风险还是风险事件
                    state:me.state
                },
                callback : function(data) {
                    if(data.success){
                    	var orgObj=me.up('processstoragemainpanelnew').orgTree.getCurrentTreeNode();
                        me.up('processstoragecardnew').riskGrid.reloadData(id,'org');
                        me.up('processstoragecardnew').showRiskGrid();
                    }
                }
            });
        }else{
            return false;
        }
    },
    //返回
    backGrid: function(){
        var me = this;
        me.up('processstoragecardnew').showRiskGrid();
    },
    //清空表单缓存
    resetData: function (type,id,empid) {	//id为树节点id
        var me = this;
        me.isEdit = false;

        //1.清空组件值
        me.getForm().reset();
        // 上級风险
        me.parentId.clearValues();
        if(id == 'root'){	//上级节点是根元素，文本框和按钮变灰
            me.parentId.grid.setDisabled(true);
            me.parentId.button.setDisabled(true);
        }else{
            me.parentId.grid.setDisabled(false);
            me.parentId.button.setDisabled(false);
        }
        // 责任部门
        me.respDeptName.clearValues();
        // 相关部门
        me.relaDeptName.clearValues();
        // 影响指标
        me.influKpiName.initGridStore(null);
        // 影响流程
        me.influProcessureName.initValue(null);
    },
    //加载表单数据
    reloadData: function (id) {	//id是风险事件id
        var me = this;
        me.isEdit = true;
        me.riskId = id;
        FHD.ajax({
            async: false,
            params: {
                riskId: id
            },
            url: __ctxPath + '/cmp/risk/findRiskEditInfoById',
            callback: function (json) {
                //赋值
                    me.form.setValues({
                    parentId: json.parentId,
                    code: json.code,
                    name: json.name,
                    desc: json.desc,
                    influProcessureName: json.influProcessureName,
                    templateId: json.templateId
                });

                //上级风险
                me.parentId.initValue();
                //责任部门，可能没有sethidden值
                if (json.respDeptName) {
                    var value = Ext.JSON.decode(json.respDeptName);
                    me.respDeptName.setValues(value);
                }
                //相关部门
                if (json.relaDeptName) {
                    var value = Ext.JSON.decode(json.relaDeptName);
                    me.relaDeptName.setValues(value);
                }
                //影响指标
                me.influKpiName.initGridStore(json.influKpiName);
                //影响流程
                me.influProcessureName.initValue(json.influProcessureName);
            }
        });
    },
    /**
     * 根据上级编码生成下级编码
     */
    setRiskCode : function(){
        var me = this;
        var values = [];
        var store = me.parentId.getGridStore();
        store.each(function(r){
            values.push(r.data.id);
        });
        var id = values[0];
        var code = me.getRiskCode(id);//编号自动生成

        me.getForm().setValues({
            code : code
        });
    },
    getRiskCode : function(parentRiskId) {
        var code = "";
        FHD.ajax({
            async:false,
            params: {
                parentId: parentRiskId
            },
            url: __ctxPath + '/cmp/risk/getRiskCode.f',
            callback: function (ret) {
                code = ret.code;
            }
        });
        return code;
    },

    /**
     * 组件内部验证,调用用户自己的验证
     */
    customValidate : function(){
        var me = this;

        //调用用户自定义验证
        if(me.userValidate){
            return me.userValidate();//调用用户验证
        }

        /**
         * 组件内部验证
         */
            //上级风险验证
        var parentRiskValid = true;
        var value=me.parentId.getValue();
        if(!me.parentId.allowBlanks){
            if(value && value !='[]'){
                me.parentId.grid.setBodyStyle('background','#FFFFFF');
            }else{
                me.parentId.grid.setBodyStyle('background:#FFEDE9;border-color:red');
                parentRiskValid = false;
            }
        }

        //责任部门和相关部门重名验证
        var orgValid = true;
        var emparr = [];
        var deptarr = [];
        var v1 = [];
        var vv1 = me.respDeptName.getValue();
        if(vv1!=""&&vv1!=undefined){
            v1 = Ext.JSON.decode(vv1);
        }
        for(var i=0;i<v1.length;i++){
            var empid = v1[i].empid;
            var deptid = v1[i].deptid;
            deptarr.push(deptid);
            if(empid!=null && empid!=''){
                emparr.push(empid);
            }
        }

        var v2 = [];
        var vv2 = me.relaDeptName.getValue();
        if(vv2!=""&&vv2!=undefined){
            v2 = Ext.JSON.decode(vv2);
        }
        for(var i=0;i<v2.length;i++){
            var empid = v2[i].empid;
            var deptid = v2[i].deptid;
            if(empid!=null && empid!=''){
                //判断人员是否重复
                var repeat = false;
                for(var j=0;j<emparr.length;j++){
                    if(emparr[j]==empid){
                        repeat = true;
                        break;
                    }
                }
                if(repeat){
                    FHD.notification('人员重复','操作提示');
                    orgValid = false;
                }
            }else{
                //判断部门是否重复
                var repeat = false;
                for(var j=0;j<deptarr.length;j++){
                    if(deptarr[j]==deptid){
                        repeat = true;
                        break;
                    }
                }
                if(repeat){
                    FHD.notification('部门重复','操作提示');
                    orgValid = false;
                }
            }
        }

        return parentRiskValid && orgValid;
    },

    // 初始化方法
    initComponent: function() {
        var me = this;

        //上级风险
        me.parentId = Ext.create('FHD.view.risk.cmp.RiskSelector', {
            onlyLeaf: true,
            allowBlanks:false,
            title : '请您选择风险',
            fieldLabel : '上级风险' + '<font color=red>*</font>',
            name : 'parentId',
            multiSelect: false,
            columnWidth : .5,
            margin: '30 30 3 30',
            schm: me.schm,
            afterEnter:function(){
                me.setRiskCode();
            }
        });

        //编码
        me.code = Ext.widget('textfield', {
            xtype : 'textfield',
            fieldLabel : '风险编号' + '<font color=red>*</font>',
            margin : '30 30 3 30',
            name : 'code',
            maxLength : 255,
            columnWidth : .5,
            allowBlank:false
        });

        me.name = Ext.widget('textareafield', {
            xtype : 'textareafield',
            rows : 2,
            fieldLabel : '风险名称' + '<font color=red>*</font>',
            margin : '7 30 3 30',
            name : 'name',
            allowBlank : false,
            height : 40,
            margin: '7 30 3 30',
            columnWidth : .5
        });

        me.desc = Ext.widget('textareafield', {
            xtype : 'textareafield',
            rows : 2,
            fieldLabel : '风险描述',
            margin : '7 30 3 30',
            name : 'desc',
            allowBlank : true,
            height : 40,
            columnWidth : .5
        });

        me.respDeptName = Ext.create('Ext.ux.form.OrgEmpSelect',{
            multiSelect:true,
            type: 'dept_emp',
            fieldLabel: '责任部门/人', // 所属部门人员
            labelAlign: 'left',
            labelWidth: 100,
            columnWidth: .5,
            height:85,
            store: [],
            queryMode: 'local',
            forceSelection: false,
            createNewOnEnter: true,
            createNewOnBlur: true,
            filterPickList: true,
            name : 'respDeptName',
            margin: '7 30 3 30',
            value:''
        });

        me.relaDeptName = Ext.create('Ext.ux.form.OrgEmpSelect',{
            multiSelect:true,
            type: 'dept_emp',
            fieldLabel: '相关部门/人', // 所属部门人员
            labelAlign: 'left',
            labelWidth: 100,
            columnWidth: .5,
            height:85,
            store: [],
            queryMode: 'local',
            forceSelection: false,
            createNewOnEnter: true,
            createNewOnBlur: true,
            filterPickList: true,
            name: 'relaDeptName',
            margin: '7 30 3 30',
            value:''
        });

        // 影响指标
        me.influKpiName = Ext.create('FHD.ux.kpi.opt.KpiSelector', {
            labelWidth : 100,
            gridHeight : 40,
            btnHeight : 25,
            btnWidth : 22,
            multiSelect : true,
            labelAlign : 'left',
            labelText : '影响指标',// + '<font color=red>*</font>',
            margin : '7 30 3 30',
            name : 'influKpiName',
            allowBlank : true,
            height : 40,
            columnWidth : .5
        });

        // 影响流程
        me.influProcessureName = Ext.create('FHD.ux.process.ProcessSelector', {
            labelWidth : 95,
            gridHeight : 25,
            btnHeight : 25,
            btnWidth : 22,
            single : false,
            fieldLabel : '影响流程',// + '<font color=red>*</font>',
            margin : '7 30 3 30',
            name : 'influProcessureName',
            allowBlank : true,
            multiSelect : true,
            height : 40,
            margin: '7 30 3 30',
            columnWidth : .5
        });

        Ext.applyIf(me, {
            autoScroll: true,
            border : false,
            items:[{
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[me.parentId, me.code]
            },{
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[me.name,me.desc]
            },{
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[me.respDeptName,me.relaDeptName]
            },{
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[me.influKpiName,me.influProcessureName]
            }

            ],
            bbar: {items: [ '->',{
                text: '返回', //保存按钮
                iconCls: 'icon-control-repeat-blue',
                handler: function () {
                    me.backGrid();
                }
            },{text: '保存', //保存按钮
                iconCls: 'icon-control-stop-blue',
                handler: function () {
                    me.save();
                }
            }
            ]
            }
        });

        me.callParent(arguments);
    }

});