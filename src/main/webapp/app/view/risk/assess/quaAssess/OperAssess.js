/**
 * 
 * 风险整理表单
 */

Ext.define('FHD.view.risk.assess.quaAssess.OperAssess', {
	extend : 'Ext.form.Panel',
	alias : 'widget.operAssess',

	requires : [ 'FHD.view.risk.assess.quaAssess.AssessScore' ],
	
	getFieldSetAssess : function(result, quaAssessOpe, onId, rangObjectDeptEmpId, editConent, oid){
		var me = this;
		me.ooid = oid;
		var html = '';
		for(var i = 0; i < result.length; i++){
			for(var g = 0; g < result[i].length; g++){
				html += result[i][g].html;
			}
		}
		html += "<div class='starp'>" + 
				"<span></span>" + 
				"<p id='ppp'>aaa</p>" + 
			"</div><br/><br/><br/><br/><br/>";
		
		
		if(onId.indexOf('notDim') != -1){
			var message = '<font color=\'red\'>此模板没有维度,请重新配置风险评价准则.</font>';
			me.fieldSetAssess = {
					id : oid,
					xtype : 'fieldset',
					title : '定性评估',
					padding : '10 5 5 5',
					//margin : '40 40 40 40',
					collapsible : true,
					html : message
			};
		}else{
			me.fieldSetAssess = {
					id : oid,
					xtype : 'fieldset',
					title : '定性评估',
					padding : '10 5 5 5',
					//margin : '40 40 40 40',
					collapsible : true,
					items : [
					          Ext.create('Ext.panel.Panel',
					          {
					        	  border : false,
					        	  layout: {
			        					 type: 'vbox'
			        			  },
					              items : [//height : 295
					                       		{html : html, 
					                       			border : false, margin : '10 0 0 0', width : 1400},
					                    		{xtype : 'button', text : '保存并继续',columnWidth : 1, margin : '10 0 0 265',
					                       			handler:function(){
					                       				quaAssessOpe.count++;
			                			            	quaAssessOpe.assessAppLoad(quaAssessOpe.count, me);
			                			            	quaAssessOpe.evaluateLoad(rangObjectDeptEmpId, onId);
			                			            	if(quaAssessOpe.totalCount == quaAssessOpe.count){
			                			            		Ext.MessageBox.show({
			        								    		title : '提示',
			        								    		width : 260,
			        								    		msg : '已保存到最后一条,是否预览提交？',
			        								    		buttons : Ext.MessageBox.YESNO,
			        								    		icon : Ext.MessageBox.QUESTION,
			        								    		fn : function(btn) {
			        								    			if (btn == 'yes') {
			        													Ext.getCmp('quaAssessPanelId').quaAssessCard.quaAssessShow();
			        								    			}
			        								    		}
			        								    	});
			                			            	}
								    				}
					                       		}
					                       ]
					      		}
					        	)
					 ]
			};
		}
		return me.fieldSetAssess;
	},
	
	assessRender : function(result, quAassessOpe){
		var me = this;
		var intStart = 0;
		var score = '';
		var msg = '';
		var iScore = 0;
		
		for(var i = 0; i < result.length; i++){
			for(var g = 0; g < result[i].length; g++){
				if(result[i][g].html.indexOf('true') != -1){
					intStart = result[i][g].html.indexOf('true');
					iScore = result[i][g].html.substring(0, intStart + 6).split('--')
								[result[i][g].html.substring(0, intStart + 6).split('--').length - 1].split(',')[1];
					msg = result[i][g].html.substring(0, intStart + 6).split('--')[result[i][g].html.substring(0, intStart + 6).split('--').length - 1 - 6]
					score = result[i][g].html.substring(0, intStart + 6).split('--')[result[i][g].html.substring(0, intStart + 6).split('--').length - 1 - 3]
					
					me.assessScore.assessApp(result[i][g].divId, quAassessOpe, me.ooid);
					me.assessScore.assessInitApp(result[i][g].divId, iScore, score, msg, quAassessOpe);
				}else{
					me.assessScore.assessApp(result[i][g].divId, quAassessOpe, me.ooid);
				}
			}
		}
	},

	// 初始化方法
	initComponent : function() {
		var me = this;
		me.assessScore = Ext.widget('assessScore');
		me.assessScore.meMap = me.meMap;
		
		Ext.apply(me, {
			border : false,
			region : 'center',
			layout : {
				align : 'stretch',
				type : 'vbox'
			}
		});

		me.callParent(arguments);
	}
});