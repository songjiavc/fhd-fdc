/*
 * 按照公司查询所有风险库
 * zhengjunxiang
 * */

Ext.define('FHD.view.risk.riskstorage.RiskStorageInit', {
    extend: 'Ext.panel.Panel',
	alias: 'widget.riskstorageinit',
	
	/**
	 * 常量
	 */
	companyUrl:__ctxPath + "/risk/findHierarchyCompany.f",
	removeRiskDataUrl:__ctxPath + "/risk/removeRiskData.f",
	removeRiskAssessDataUrl:__ctxPath + "/risk/removeRiskAssessData.f",
	removeKcDataUrl:__ctxPath + "/sm/init/removeKcData.f",
	removeKPIDataUrl:__ctxPath + "/sm/init/removeKpiData.f",
	removeSmDataUrl:__ctxPath + "/sm/init/removeSmData.f",
	removeScDataUrl:__ctxPath + "/sm/init/removeScData.f",
	removeProcessUrl: __ctxPath + '/icm/process/init/removeProcessData.f',
	
	
	labelWidth:300,	//左侧的宽度
	
	/**
	 * 变量
	 */
	companyId:undefined,	//选择初始化的公司id
	
	/**
	 * 
	 * 监控预警模块
	 */
	
	addSmComponent:function(){
		var me = this;
		var kc = {
	                xtype: 'displayfield',
	                flex: 1,
	                fieldLabel: '清空指标类型数据',
	                value: '<button class="x-btn x-form-file-btn x-unselectable x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon" onclick=\'Ext.getCmp("' + me.id + '").removeSmData("KC")\'>执行</button>'
                 };
        var kpi = {
	                xtype: 'displayfield',
	                flex: 1,
	                fieldLabel: '清空指标数据',
	                value: '<button class="x-btn x-form-file-btn x-unselectable x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon" onclick=\'Ext.getCmp("' + me.id + '").removeSmData("KPI")\'>执行</button>'
                 };
        var sm = {
	                xtype: 'displayfield',
	                flex: 1,
	                fieldLabel: '清空目标数据',
	                value: '<button class="x-btn x-form-file-btn x-unselectable x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon" onclick=\'Ext.getCmp("' + me.id + '").removeSmData("SM")\'>执行</button>'
                 };
        var sc = {
	                xtype: 'displayfield',
	                flex: 1,
	                fieldLabel: '清空记分卡数据',
	                value: '<button  class="x-btn x-form-file-btn x-unselectable x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon" onclick=\'Ext.getCmp("' + me.id + '").removeSmData("SC")\'>执行</button>'
                 };
                 
		var smfieldSet = Ext.widget('fieldset', {
			xtype:'fieldset',
			layout: {
                align: 'left',
                pack: 'end',
                type: 'vbox'
            },
            defaults : {
				margin : '0 22 0 22',
				labelAlign: 'left',
				labelWidth : me.labelWidth
			},
			width:'85%',
			collapsible : true,
            title: '监控预警数据初始化和修复',
			items:[kc,kpi,sm,sc]
		});
		
		return smfieldSet;
	},
	
	/**
	 * 风险模块
	 */
	addRiskComponent : function() {
		var me = this;
		
		var riskfieldSet = Ext.widget('fieldset', {
			xtype:'fieldset',
			layout: {
                align: 'left',
                pack: 'end',
                type: 'vbox'
            },
            defaults : {
				margin : '0 22 0 22',
				labelAlign: 'left',
				labelWidth : me.labelWidth
			},
			width:'85%',
			collapsible : true,
            title: '风险数据初始化和修复',
			items:[{
                xtype: 'displayfield',
                flex: 1,
                fieldLabel: '清空风险基础数据',
                value: '<button class="x-btn x-form-file-btn x-unselectable x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon" onclick=\'Ext.getCmp("' + me.id + '").removeRiskData()\'>执行</button>'
            },{
                xtype: 'displayfield',
                flex: 1,
                fieldLabel: '清空风险评估数据',
                value: '<button class="x-btn x-form-file-btn x-unselectable x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon" onclick=\'Ext.getCmp("' + me.id + '").removeRiskAssessData()\'>执行</button>'
            }]
		});
		
		return riskfieldSet;
	},
	
	/**
	 * 流程模块
	 */
	addProcessComponent:function(){
		var me = this;
		
		var process = {
            xtype: 'displayfield',
            flex: 1,
            fieldLabel: '清空流程数据',
            value: '<button class="x-btn x-form-file-btn x-unselectable x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon" onclick=\'Ext.getCmp("' + me.id + '").removeProcessData()\'>执行</button>'
        };
                 
		var processFieldSet = Ext.widget('fieldset', {
			xtype:'fieldset',
			layout: {
                align: 'left',
                pack: 'end',
                type: 'vbox'
            },
            defaults : {
				margin : '0 22 0 22',
				labelAlign: 'left',
				labelWidth : me.labelWidth
			},
			width:'85%',
			collapsible : true,
            title: '流程数据初始化',
			items:[process]
		});
		
		return processFieldSet;
	},
	
	initComponent: function () {
        var me = this;
       
        if(__user.userName == 'admin'){	//管理员可以选择所有的公司进行删除 
        	var companyStoreData = {};
			FHD.ajax({
				async:false,
				url : me.companyUrl,
				callback : function(data) {
					companyStoreData = data;
				}
			});
	        me.companyStore = Ext.create('Ext.data.TreeStore',{
	        	root:companyStoreData
			});
	        me.companyCombo = Ext.create("Ext.ux.TreePicker",{
	        	width:600,
	        	autoScroll:true,
	        	fieldLabel: '公司',
	        	labelAlign: 'left',
	        	labelWidth:me.labelWidth,
	        	displayField : 'text',
	        	forceSelection : true,// 只能选择下拉框里面的内容    
		        editable : false,// 不能编辑    
		    	store : me.companyStore
	        });
	        me.companyCombo.setRawValue(__user.companyName);
        }else{
        	me.companyCombo = Ext.widget("displayfield",{
        		fieldLabel: '公司',
	        	labelAlign: 'left',
	        	labelWidth:me.labelWidth,
	        	value:__user.companyName
        	});
        }

        //风险模块fieldSet
        me.riskfieldSet = me.addRiskComponent();
        
        me.smfieldSet = me.addSmComponent();
        
        //流程模块fieldSet
        me.processFieldSet = me.addProcessComponent();
	        
		Ext.applyIf(me, {
			bodyPadding: 10,
			items:[{
				xtype:'fieldset',
				layout: {
                    align: 'left',
                    pack: 'end',
                    type: 'vbox'
                },
	            defaults : {
					margin : '0 22 0 22',
					labelAlign: 'left',
					labelWidth : me.labelWidth
				},
				width:'85%',
				collapsible : true,
                title: '公司信息',
				items:[me.companyCombo]
			},me.riskfieldSet,me.smfieldSet,me.processFieldSet]
        });
    		
    	me.callParent(arguments);
	},
	
	//清理流程数据
	removeProcessData:function(){
		var me=this;
		
		var companyId = __user.companyId;
		if(__user.userName == 'admin'){
			companyId = me.companyCombo.getValue();
		}
		Ext.MessageBox.show({
            title: '警告',
            width: 260,
            msg: '您确定要初始化数据吗?',
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') {
                    FHD.ajax({
						url : me.removeProcessUrl,
						params:{
							companyId: companyId
						},
						callback: function (response) {
	    	                if(response.success && true==response.success){
								FHD.notification("操作成功!","提示");
							}else{
								FHD.notification("操作失败!","提示");
							}
						}
					});
                }
            }
        });	
	},
	
	removeSmData:function(type){
		var me = this;
		var requestUrl = "";
		if(type=="KC"){
			requestUrl = me.removeKcDataUrl;
		}else if(type=="KPI"){
			requestUrl = me.removeKPIDataUrl;
		}else if(type=="SM"){
			requestUrl = me.removeSmDataUrl;
		}else if(type=="SC"){
			requestUrl = me.removeScDataUrl;
		}
		var companyId = __user.companyId;
		if(__user.userName == 'admin'){
			companyId = me.companyCombo.getValue();
		}
		Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { //确认删除
                    FHD.ajax({
						url : requestUrl,
						params:{
							companyId:companyId
						},
						callback : function(data) {
							if(data){
								FHD.notification("操作成功！","提示");
							}
							else{
								FHD.notification("操作失败！","提示");
							}
						}
					});
                }
            }
        });		
	},
	/**
	 * 删除风险基础数据
	 */
	removeRiskData:function(){
		var me = this;
		var companyId = __user.companyId;
		if(__user.userName == 'admin'){
			companyId = me.companyCombo.getValue();
		}
		Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { //确认删除
                    FHD.ajax({
						url : me.removeRiskDataUrl,
						params:{
							companyId:companyId
						},
						callback : function(data) {
							FHD.notification("风险基础数据清除成功","提示");
						}
					});
                }
            }
        });
	},
	
	/**
	 * 删除风险评估数据
	 */
	removeRiskAssessData:function(){
		var me = this;
		var companyId = __user.companyId;
		if(__user.userName == 'admin'){
			companyId = me.companyCombo.getValue();
		}
		Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { //确认删除
                    FHD.ajax({
						url : me.removeRiskAssessDataUrl,
						params:{
							companyId:companyId
						},
						callback : function(data) {
							FHD.notification("风险评估数据清除成功","提示");
						}
					});
                }
            }
        });
	}
});