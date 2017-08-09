/**
 * 
 * 风险整理表单
 */

Ext.define('FHD.view.risk.assess.quaAssess.commAssess.RiskOperAssess', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskOperAssess',

	requires : [ 'FHD.view.risk.assess.quaAssess.commAssess.RiskAssessScore' ],
	
	getFieldSetAssess : function(result, quaAssessOpe, onId, rangObjectDeptEmpId, editConent, oid){
		var me = this;
		var html = '';
		for(var i = 0; i < result.length; i++){
			for(var g = 0; g < result[i].length; g++){
				html += result[i][g].html;
			}
		}
		
		html += "<div class='starp'>" + 
					"<span></span>" + 
					"<p id='ppp'>aaa</p>" + 
				"</div>";
		
		me.fieldSetAssess = {
				id : oid,
				xtype : 'fieldset',
				title : '评估信息',
				//padding : '10 5 5 5',
				//margin : '40 40 40 40',
				collapsible : true,
				items : [ 
				          
				          Ext.create('Ext.panel.Panel',
				          {
				        	  border : false,
				        	  layout: {
		        					 type: 'vbox'
		        			  },
				              items : [
				                       		{html : html, border : false, height : 300, margin : '10 0 0 0', width : 1400}
				                       ]
				      		}
				        	)
				 ]
		};
		
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
					
					me.assessScore.assessApp(result[i][g].divId, quAassessOpe);
					me.assessScore.assessInitApp(result[i][g].divId, iScore, score, msg, quAassessOpe);
				}else{
					me.assessScore.assessApp(result[i][g].divId, quAassessOpe);
				}
				
			}
		}
	},

	// 初始化方法
	initComponent : function() {
		var me = this;
		me.assessScore = Ext.widget('riskAssessScore');
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