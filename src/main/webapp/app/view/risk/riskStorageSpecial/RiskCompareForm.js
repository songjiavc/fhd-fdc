/**
 * @author 郭鹏 2017-04-25
 * 
 */
Ext.define('FHD.view.risk.riskStorageSpecial.RiskCompareForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskCompareForm',

	/**
	 * 接口属性
	 */
	type : 're',   		//风险还是风险事件 rbs:风险 re：风险事件
	setLoginDept:false,	//true 设置责任部门为当前登录人的部门，false 不进行设置
    isEdit:false,	//是否处于编辑状态
	riskId:null,	//编辑的id
	state:1,		//默认是1，但是风险评估模块添加的状态是2
	
	/**
	 * 变量
	 */
	archiveStatus:'archived',	//归档状态
	
	/**
	 * 常量
	 */
	saveUrl: '/cmp/risk/saveRiskStorage.f',
	mergeUrl:'/cmp/risk/mergeRiskStorage.f',
	findUrl: '/cmp/risk/findRiskEditInfoByRiskIdCompare.f',
	findTemplateUrl: '/access/formulateplan/findTemplatesrisk.f',
	isInherit:'0yn_y',	//是否继承
	
	
	/**
	 * 构建基本信息
	 */
	addBasicComponent : function() {
		var me = this;

		var itemArr = [];	//显示的数据项
		//上级风险
		
			itemArr.push(me.parentId);
			me.parentId = Ext.widget('displayfield', {
                xtype: 'displayfield',
                fieldLabel: '上级风险',
                margin : '7 30 3 30',
           		 columnWidth: .5,
                name: 'parentId'
           
            });
    		itemArr.push(me.parentId);

		//编码
		var code = Ext.widget('displayfield', {
			xtype : 'textfield',
			fieldLabel : '风险编号' ,
			margin : '7 30 3 30',
            columnWidth: .5,
			name : 'code',
			
			allowBlank:false
		});
		itemArr.push(code);

		//风险名称
		var name = Ext.widget('displayfield', {
			xtype : 'displayfield',
			rows : 2,
			fieldLabel : '风险名称' ,
		 	 margin: '7 30 3 30', 
			name : 'name',
			allowBlank : false,
			height : 40,
			columnWidth : 1
		});
		itemArr.push(name);
		
		//风险描述
		var desc = Ext.widget('displayfield', {
			xtype : 'displayfield',
			rows : 2,
			fieldLabel : '风险描述',
			margin: '7 30 3 30', 
			name : 'desc',
			columnWidth : 1
		});
		itemArr.push(desc);
			
		me.respDeptName = Ext.widget('displayfield', {
			xtype : 'displayfield',
			rows : 2,
			fieldLabel : '责任部门/人',
			margin : '7 30 3 30',
			name : 'respDeptName',
			columnWidth : .5
		});
		itemArr.push(me.respDeptName);

		me.relaDeptName = Ext.widget('displayfield', {
			xtype : 'displayfield',
			rows : 2,
			fieldLabel: '相关部门/人',
			margin : '7 30 3 30',
			name: 'relaDeptName',
			value:'',
			columnWidth : .5
		});
		itemArr.push(me.relaDeptName);

		// 影响指标
		me.influKpiName =Ext.widget('displayfield', {
			xtype : 'displayfield',
			margin : '7 30 3 30',
			fieldLabel : '影响指标',// + '<font color=red>*</font>',
			name : 'influKpiName',
			columnWidth : .5
		});
		itemArr.push(me.influKpiName);
		
		// 影响流程
		me.influProcessureName = Ext.widget('displayfield', {
		xtype : 'displayfield',
			fieldLabel : '影响流程',// + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'influProcessureName',
			columnWidth : .5
			
		});
		itemArr.push(me.influProcessureName);
		
		var basicfieldSet = Ext.widget('fieldset', {
			title : '风险信息',
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '99%',
			collapsible : true,
			margin : '10 10 10 10',
			defaults : {
				margin : '3 30 3 30',
				height : 24,
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			items : itemArr
		});
		
		return basicfieldSet;
	},
	
	addAssessComponent : function() {
		var me = this;

		me.assessfieldSet = Ext.create("FHD.view.risk.cmp.risk.RiskAssessCompare",{
			title:'评估信息'
		});	
		return me.assessfieldSet;
	},
	addExtendComponent : function() {
	var me = this;
	var editIdea = Ext.widget('textareafield', {
		xtype : 'textareafield',
		rows : 4,
		name : 'respondComments',
		margin : '5 0 3 20',
		columnWidth : 1
		 });

		var respondfieldSet = Ext.widget('fieldset', {
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '100%',
			readonly:true,
			collapsible : true,
			collapsed:true,
			defaults : {
				margin : '3 30 3 30',
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			title : '应对意见',
			items : [editIdea]
		});
		return respondfieldSet;
	},
    reloadData: function (id) {	//id是风险事件id
    	var me = this;
    	me.isEdit = true;
    	me.riskId = id;
    		
		//初始化评价信息
		me.assessfieldSet.initParams(true);
		me.assessfieldSet.reloadData(id);
		
		//初始化扩展信息
    	FHD.ajax({
   			async:false,
   			params: {
                riskId: id
            },
            cache: false,
            url: __ctxPath + me.findUrl,
            callback: function (json) {
            	debugger;
            	//赋值
            	me.form.setValues({
        			parentId : json.parentId,
        			code : json.code,
        			name : json.name,
        			desc : json.desc,
        			respDeptName : json.respDeptName,
        			relaDeptName : json.relaDeptName,
        			influKpiName : json.influKpiName,
        			influProcessureName : json.influProcessureName,
        			templateId:json.templateId
        		});




            }
        });
    },
	
	initComponent : function() {
		var me = this;
		// 基本信息
		var basicfieldSet = me.addBasicComponent();

		
		//评价信息
		var assessfieldSet = me.addAssessComponent();
		
		// 扩展信息
		me.extendfieldSet = me.addExtendComponent();
           
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			dockedItems: [{
			xtype: 'toolbar',
	        dock: 'bottom',
	        ui: 'footer'
            }],
			items : [basicfieldSet,assessfieldSet,me.extendfieldSet]
		});

		me.callParent(arguments);

	}
});