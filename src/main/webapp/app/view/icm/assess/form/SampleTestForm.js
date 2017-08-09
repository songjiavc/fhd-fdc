/**
 * 编辑样本
 */
Ext.define('FHD.view.icm.assess.form.SampleTestForm',{
	extend: 'Ext.form.Panel',
    alias: 'widget.sampletestform',
    
    autoScroll:true,
    
	initComponent : function() {
	    var me = this;
	    
	    me.isQualifiedStore = Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			data : [
			    {'id' : 'Y','name' : '有效'},
			    {'id' : 'N','name' : '无效'},
			    {'id':'NAN','name':'不适用'}
			]
		});
	    
	    me.basicInfo=Ext.create('FHD.view.icm.assess.form.AssessResultPreviewForm',{
			columnWidth:1/1,
			assessResultId:me.assessResultId
		});
	    /*
	    me.assessGuidelinesGrid = Ext.create('FHD.view.icm.assess.baseset.AssessGuidelinesShowGrid',{
			columnWidth:1/1,
			margin: '7 10 0 30',
			assessPlanId:me.businessId
		});
		
		me.assessGuidelinesGridPanel={
			xtype : 'fieldset',
			layout : {
				type : 'column'
			},
			collapsed : true,
			columnWidth:1/1,
			collapsible : true,
			title: '评价标准查看',
			items :[me.assessGuidelinesGrid]
		};
	    */
	    if('sampling' == me.type){
	    	//抽样测试
	    	me.sampletext={
	    		margin: '0 10 0 30',
    			xtype:'textfield',
    	        fieldLabel: '编号前缀',
    	        name: 'sampletext',
            	columnWidth:1/4
        	};
    		me.startNum={
    			margin: '0 10 0 0',
            	xtype: 'numberfield',
            	name: 'startNum',
            	fieldLabel: '编号期间',
            	columnWidth:1/4,
		        minValue: 1,
		        listeners: {
		        	change: function(field, value) {  
		        		value = parseInt(value,10);  
		        		field.setValue(value);  
		        	}
		        }
        	};
    		me.stopNum={
    			margin: '0 10 0 0',
            	xtype: 'numberfield',
            	name: 'stopNum',
            	fieldLabel: '至',
            	columnWidth:1/4,
		        minValue: 1,
		        listeners: {
		        	change: function(field, value) {  
		        		value = parseInt(value,10);  
		        		field.setValue(value);  
		        	}
		        }
        	};
    		me.button={
            	xtype:'button',
            	margin: '0 30 0 0',
            	text: '生成',
            	tooltip: '生成抽样测试样本',
            	columnWidth:1/8,
            	handler:function(){
            		if(me.validateParams()){
            			FHD.ajax({
            				url:__ctxPath +'/icm/assess/saveSample.f',
                            params: {
                            	sampletext:me.getValues().sampletext,
                            	startNum:me.getValues().startNum,
                            	stopNum:me.getValues().stopNum,
                            	assessResultId:me.assessResultId,
                            	assessPlanId:me.businessId,
                            	processId:me.processId
                            },
                            callback: function (data) {
                            	if(data){
                            		me.sampleEditGrid.store.load();
                            	}
                            }
            			});
            		}
            	}
    	    };
    	    me.desc = {
    	    	xtype:'label',
    	    	margin: '10 10 0 30',
    	    	html:'<font color=red>注:</font>&nbsp;&nbsp;[样本选取规则说明]<br/>&nbsp;&nbsp;样本编号是在一个样本总量中随机生成的，编号由“前缀+数字”组成，例如：“NO073000”',
    	    	columnWidth:1/1
    	    }
	    	me.samplingFieldSet={
    			xtype : 'fieldset',
    			margin: '0 10 0 10',
    			layout:'column',
    			collapsed : false,
    			collapsible : false,
    			title: '1.先随机抽取样本',
    			items :[me.sampletext,me.startNum,me.stopNum,me.button,me.desc]
    		};
	    }

	    me.sampleEditGrid=Ext.create('FHD.view.icm.assess.component.SampleTestEditGrid',{
	    	//id:'icm_assess_sampletesteditgrid',
	    	//type:me.type,
    		//businessId:me.businessId,
    		assessResultId:me.assessResultId
    	});

		me.callParent(arguments);
		
		//评价点信息
		me.add(me.basicInfo);
		//评价标准
		//me.add(me.assessGuidelinesGridPanel);
		var inputDesc = {
    	    	xtype:'label',
    	    	html:'<font color=red>注:</font>&nbsp;&nbsp;[填写说明]<br/>' +
    	    		'&nbsp;&nbsp;&nbsp;&nbsp;1.“状态”：“自动”，表示随机抽取；“补充”，表示手动补充，当随机抽取的样本“不适用”时需手动补充样本，以保证样本量；<br/>' +
    	    		'&nbsp;&nbsp;&nbsp;&nbsp;2.“样本编号”：随机抽取时自动生成，不可编辑；手动补充时需要填写，注意不要重复。<br/>' +
    	    		'&nbsp;&nbsp;&nbsp;&nbsp;3.“样本名称”：当“样本编号”不能明确一个样本时需填写以明确是哪个样本。<br/>' +
    	    		'&nbsp;&nbsp;&nbsp;&nbsp;4.“是否有效”：“有效”，表示样本合格；“无效”，表示样本不合格，需说明情况，并上传证据；“不适用”，需补充样本。<br/>' +
    	    		'&nbsp;&nbsp;&nbsp;&nbsp;5.“说明”：样本无效时需要填写。<br/>' +
    	    		'&nbsp;&nbsp;&nbsp;&nbsp;6.“影响程度”：样本无效时，主观判断该样本对本评价点的有效性的影响程度，1~5。<br/>',
    	    	columnWidth:1/1
    	    }
		if('sampling' == me.type){
			//生成样本
			me.add(me.samplingFieldSet);
			
			me.sampleFieldset = {
				xtype : 'fieldset',
				margin: '10 10 0 10',
				collapsed : false,
				layout:'anchor',
				collapsible : false,
				title: '2.修改样本信息及样本的有效状态',
				items :[me.sampleEditGrid,inputDesc]
			};
			//样本列表
			me.add(me.sampleFieldset);
		}else{
			me.sampleFieldset = {
				xtype : 'fieldset',
				margin: '10 10 10 10',
				collapsed : false,
				collapsible : false,
				title: '填写样本信息及样本的有效状态',
				items :[me.sampleEditGrid,inputDesc]
			};
			//样本列表
			me.add(me.sampleFieldset);
		}
	},
	validateParams:function(){
		var me=this;
		
		var sampletext = me.getValues().sampletext;
    	var startNum = me.getValues().startNum;
    	var stopNum = me.getValues().stopNum;
    	
    	var validateFlag=false;
		var message = '';
    	if(sampletext=='' || sampletext==null || sampletext==undefined){
    		message += "'编号前缀'不能为空!<br/>";
			validateFlag=true;
    	}
    	if(startNum=='' || startNum==null || startNum==undefined || stopNum=='' || stopNum==null || stopNum==undefined){
    		message += "'编号期间'不能为空!<br/>";
			validateFlag=true;
    	}
    	if(validateFlag){
 			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), message);
 			return false;
 		}else{
 			return true;
 		}
	},
	loadData:function(assessResultId){
		var me=this;
		
		//评价点load
		me.basicInfo.getForm().load({
            url: __ctxPath+'/icm/assess/findAssessPointViewByAssessResultId.f',
            params:{
            	assessResultId: assessResultId
            },
            success: function (form, action) {
         	   return true;
            },
            failure: function (form, action) {
         	   return false;
            }
		});
		
		if('sampling' == me.type){
	    	//抽样测试,编号前缀，编号期间清空
			me.clearValues();
		}
		
		me.sampleEditGrid.store.proxy.extraParams.assessResultId = assessResultId;
		me.sampleEditGrid.store.load();
	},
	clearValues:function(){
		var me=this;
		me.getForm().reset();
	},
	reloadData:function(){
		var me=this;
		
	}
});