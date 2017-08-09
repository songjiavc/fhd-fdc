/**
 * 风险事件基本信息页面（精简）
 *
 * @author
 */
 
 /**
  *    modify by songjia@pcitc.com
  *    由于风险组件有重复加载问题，同时代码逻辑混乱，必须重写～！
  */
Ext.define('FHD.view.risk.riskstorage.RiskRelateFormNew', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskrelateformnew',
    requires:[
    	'FHD.view.compoments.commonselect.CommonSelectInput'
    ],
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
                        me.up('riskstoragecardnew').riskGrid.reloadData(null,'risk');
                        me.up('riskstoragecardnew').showRiskGrid();
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
                        me.up('riskstoragecardnew').riskGrid.reloadData(null,'risk');
                        me.up('riskstoragecardnew').showRiskGrid();
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
        me.up('riskstoragecardnew').showRiskGrid();
    },
    //清空表单缓存
    resetData: function (type,id,empid) {	//id为树节点id
        var me = this;
        me.isEdit = false;

        //1.清空组件值
        me.getForm().reset();
        // 上級风险
        // me.parentId.clearValues();
        if(id == 'root'){	//上级节点是根元素，文本框和按钮变灰
            // me.parentId.grid.setDisabled(true);
            // me.parentId.button.setDisabled(true);
        }else{
            // me.parentId.grid.setDisabled(false);
            // me.parentId.button.setDisabled(false);
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
                    templateId: json.templateId,
                    responseText : json.responseText
                });

                //上级风险
                // me.parentId.initValue();
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
        var store = // me.parentId.getGridStore();
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
    },

    userValidate : function(){
    	var me = this;
    	return me.orgIsNull() && me.orgOrEmpRepeat();
    },
    
    orgIsNull : function(){
    	var flag = true;
    	var me = this;
    	var respDeptValue = me.respDeptName.getValue();
    	if(Ext.isEmpty(respDeptValue)){
    		flag = false;
    		FHD.notification('主责部门不能为空！','操作提示');
    	}else{
    		flag = true;
    	}
    	return flag;
    },
    
    orgOrEmpRepeat : function(){
    	var me = this;
    	var orgValid = true;
        var emparr = [];
        var deptarr = [];
        var v1 = [];
        var vv1 = me.respDeptName.getValue();
        if(Ext.isEmpty(vv1)){
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
        return orgValid;
    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        /* 宋佳重写了风险选择组件*/
        me.parentTest = Ext.widget('commonselectinput',{
        	columnWidth : .5,
            margin: '30 30 3 30',
            labelWidth : 105,
            schm : me.schm,
            fieldLabel : '上级风险' + '<font color=red>*</font>:'
        });
        
        //上级风险
        /*
        // me.parentId = Ext.create('FHD.view.risk.cmp.RiskSelector', {
            onlyLeaf: true,
            allowBlanks:true,
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
		*/
        //编码
        me.code = Ext.widget('textfield', {
            fieldLabel : '风险编号' + '<font color=red>*</font>',
            margin : '30 30 3 30',
            name : 'code',
            maxLength : 255,
            columnWidth : .5,
            allowBlank:false
        });

        me.name = Ext.widget('textareafield', {
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
            rows : 2,
            fieldLabel : '风险描述',
            margin : '7 30 3 30',
            name : 'desc',
            allowBlank : true,
            height : 40,
            columnWidth : .5
        });
        
        me.responseContent = Ext.widget('textareafield',{
        	fieldLabel : '应对措施',
        	margin : '7 30 3 30',
        	height : 100,
        	name : 'responseText',
        	columnWidth: 1
        });

        me.respDeptName = Ext.create('Ext.ux.form.OrgEmpSelect',{
            multiSelect:false,
            type: 'dept_emp',
            fieldLabel: '责任部门/人'+"<font color='red' >*</font>", // 所属部门人员
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
                items:[me.parentTest, me.code]
            },{
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[me.name,me.desc]
            },{
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[me.responseContent]
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
            dockedItems: [{
				xtype: 'toolbar',
	            dock: 'bottom',
	            ui: 'footer',
	            items: ['->', {
	                text: '返回',
	                iconCls: 'icon-control-repeat',
	                height : 40,
	                handler: function () {
	                    me.backGrid();
	                }
	            },{
	                text: '保存',
	                iconCls: 'icon-database-save',
	                height : 40,
	                handler: function () {
	                     me.save();
	            }}]
            }]
            /*
            bbar: {
            	height : 50,
            	
            	items: [ '->',{
	                text: '<font size="4" color = "white">返回</font>', //保存按钮
	                iconCls: 'icon-control-repeat-blue',
	                height : 40,
	                style : {
	                	background : '#157fcc'
	                },
	                width : 80,
	                handler: function () {
	                    me.backGrid();
	                }
		            },{
		            
		            	xtype : 'label',
		            	width : 20
		            },{
		                text: '<font size="4" color = "white">保存</font>', //保存按钮
			            height : 40,
			            width : 80,
			            style : {
	                		background : '#157fcc'
	                	},
		                iconCls: 'icon-control-stop-blue',
		                handler: function () {
		                    me.save();
		                }
		            },{
		            
		            	xtype : 'label',
		            	width : 20
		            }
	            ]
            }
            */
        });

        me.callParent(arguments);
    }

});