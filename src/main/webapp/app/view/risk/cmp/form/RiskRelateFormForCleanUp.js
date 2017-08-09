/**
 * add by songjia
 * 提供给辨识整理环节的风险录入和展示页面
 */
Ext.define('FHD.view.risk.cmp.form.RiskRelateFormForCleanUp', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskrelateformforcleanup',
	autoHeight : true,
	autoWidth : true,
	width : '99%',
	defaults : {
		margin : '5 30 5 30',
		height : 24,
		labelWidth : 100
	},
	layout : {
		type : 'column'
	},
	/**
	 * 常量
	 */
	
	mergeUrl:'/risk/relate/mergeRiskInfoForCleanUp.f',
	findUrl: '/cmp/risk/findRiskEditInfoByScoreObjectIdForCleanUp.f',

	merge : function(scoreObjectId,callback){
		var me = this;
		
		var form = me.getForm();
		//责任部门
		var respDeptName = me.respDeptName.getValue();
		//相关部门
		var relaDeptName = me.relaDeptName.getValue();

		if(form.isValid() && me.customValidate()){
			FHD.submit({
				form : form,
				url : __ctxPath + me.mergeUrl,
				params : {
					schm:me.schm
				},callback : function(data) {
					if(callback){
						callback(data);
					}
				}
			});
		}else{
			return false;
		}
	},
	resetData: function (type,id,empid) {	//id为树节点id
        var me = this;
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
		
		//3.根据不同的类型，设置不同的初始值
		if(id=='root'){	//根节点不做赋值处理
			return;
		}
    	FHD.ajax({
   			async:false,
   			params: {
                riskId: id
            },
            url: __ctxPath + '/risk/findRiskEditInfoById.f',
            callback: function (ret) {
             	//上级风险
            	var parentId = [];
            	var obj = {};
            	obj["id"] = id;
            	parentId.push(obj);
        		me.parentId.setHiddenValue(parentId);
        		me.parentId.initValue();
        		//必须延迟一会，否则得到的store为空
        		setTimeout(function() {
                	me.setRiskCode();//风险编号联动
                },500);
            }
        });
    },
    
    reloadData: function (scoreObjectId) {	//id是风险事件id
    	var me = this;
    	me.isEdit = true;
    	
    	//将变灰的数据项恢复过来
    	me.parentId.grid.setDisabled(false);
		me.parentId.button.setDisabled(false);
		
    	FHD.ajax({
   			async:false,
   			params: {
                scoreObjectId : scoreObjectId
            },
            url: __ctxPath + me.findUrl,
            callback: function (json) {
            	//赋值
            	me.form.setValues({
        			scoreObjectId : json.scoreObjectId,
        			parentId : json.parentId,
        			code : json.code,
        			name : json.name,
        			desc : json.desc,
        			respDeptName : json.respDeptName,
        			relaDeptName : json.relaDeptName,
        			responseText : json.responseText
        		});

        		//上级风险
        		me.parentId.initValue();
        		//责任部门，可能没有sethidden值
        		
        		if(json.respDeptName){
        			var value = Ext.JSON.decode(json.respDeptName);
	        		me.respDeptName.setValues(value);
	        		
        		}
        		//相关部门
        		if(json.relaDeptName){
        			var value = Ext.JSON.decode(json.relaDeptName);
	        		me.relaDeptName.setValues(value);
        		}
            }
        });
		
    },
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
	initComponent : function() {
		// 基本信息
		var me = this;
		
		var scoreObjectId = {
	        xtype: 'hidden',
	        margin : '0 0 0 0',
			height : 0,
			labelWidth : 0,
	        name: 'scoreObjectId'
	    };
		
		//上级风险
		me.parentId = Ext.create('FHD.view.risk.cmp.RiskSelector', {
			onlyLeaf: true,
			allowBlanks:false,
			title : '请您选择风险',
			fieldLabel : '上级风险' + '<font color=red>*</font>',
			name : 'parentId',
			multiSelect: false,
			//2017年4月20日15:12:05吉志强添加         风险辨识流程到达风险辨识环节添加风险时加上分库标示
			schm: me.schm,
			columnWidth : .5,
			afterEnter:function(){
				me.setRiskCode();
			}
		});
		
		//编码
		var code = Ext.widget('textfield', {
			xtype : 'textfield',
			fieldLabel : '风险编号' + '<font color=red>*</font>',
			name : 'code',
			maxLength : 255,
			columnWidth : .5,
			allowBlank:false
		});

		//风险名称
		var name = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 2,
			fieldLabel : '风险名称' + '<font color=red>*</font>',
			name : 'name',
			allowBlank : false,
			height : 40,
			columnWidth : .5
		});
		
		//风险描述
		var desc = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 2,
			fieldLabel : '风险描述',
			name : 'desc',
			allowBlank : true,
			height : 40,
			columnWidth : .5
		});
		
		var responseText = Ext.widget('textareafield',{
			rows : 2,
			fieldLabel : '应对措施',
			name : 'responseText',
			allowBlank : true,
			height : 40,
			columnWidth : 1
		});
	
		me.respDeptName = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect:true,
			type: 'dept_emp',
			fieldLabel: '责任部门/人' + '<font color=red>*</font>', // 所属部门人员
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
			value:''
		});

		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [scoreObjectId,me.parentId,code,name,desc,responseText,me.respDeptName,me.relaDeptName]//,relafieldSet
		});
		me.callParent(arguments);
	}
});