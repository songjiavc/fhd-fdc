Ext.define('FHD.view.risk.riskStorageSpecial.RiskAssessMakePanelSpecial', {
	extend : 'Ext.form.FieldSet',
	alias : 'widget.riskassessmakepanelspecial',

	findTemplateUrl: '/access/formulateplan/findTemplatesrisk.f',
	
	fieldmargin : '3 30 3 30',
	fieldlabelWidth : 100,
	assessValue : '',
	templateid : '',
	hisId: '',
	riskId : '',
	isEdit : false,
	
	/**
     * 返回按钮的操作函数,添加完成后自动调用这个方法。
     */
    goback:Ext.emptyFn(),
    
	initComponent : function() {
		var me = this;
		// 评估模板
		me.assesstemplateStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : [ 'type', 'name' ],
			remoteSort : true,
			proxy : {
				type : 'ajax',
				url : __ctxPath + me.findTemplateUrl,//'/access/formulateplan/findtemplates.f',
				reader : {
					type : 'json',
					root : 'datas',
					totalProperty : 'totalCount'
				}
			}
		});

		me.assesstemplate = Ext.create('Ext.form.ComboBox', {
			store : me.assesstemplateStore,
			displayField : 'name',
			valueField : 'type',
			labelAlign : 'left',
			fieldLabel : '评估模板',
			multiSelect : false,
			triggerAction : 'all',
			flex : 9,
			listeners : {
				change : function(com,newValue,oldValue,eOpts){
					me.reLayout(newValue);
				}
			}
		});
		

		me.templatebutton = Ext.create('Ext.button.Button',{
			text : '评价',
            flex : 1,
            handler:function(){
            	if(me.assesstemplate.getValue() != null && me.assesstemplate.getValue() != ''){
            		var riskAssessPanel = Ext.create('FHD.view.risk.cmp.RiskAssessPanelForSecurity');
            		var assesswindow = Ext.create('FHD.ux.Window', {
		                title: '评估打分',
		                maximizable: true,
		                modal: true,
		                width: 600,
		                height: 550,
		                collapsible: true,
		                autoScroll: true,
		                buttons: [{
			                    text: '保存',
			                    handler : function () {
			                    	var rtnArr = riskAssessPanel.save();
			                    	me.fieldContainer.removeAll();
			                    	assesswindow.close();
			                    	me.layoutAssessType(me.fieldContainer,rtnArr);
			                    	me.riskResultScore = rtnArr;
			                    //	me.getAssessType(me.assesstemplate.getValue(),riskAssessPanel.save());
			                    	}
			                	},{
			                    text: '关闭',
			                    handler: function () {
			                        assesswindow.close();
			                    }
		                }]
		            }).show();
		           assesswindow.add(riskAssessPanel);
		           riskAssessPanel.reloadData(me.riskId,me.assessPlanId,me.riskResultScore);
            	}
		    }
    	});
		
    	me.templatacontainer = Ext.create('Ext.form.FieldContainer',{
    		margin : me.fieldmargin,
			labelWidth : me.fieldlabelWidth,
    		columnWidth : .5,
    		layout : {
    			type : 'hbox',
    			align : 'stretch'
    		},
    		items : [
    			me.assesstemplate,me.templatebutton
    		]
    	});
		
		me.assesstype = Ext.widget('displayfield',{
			labelWidth : me.fieldlabelWidth,
			margin : me.fieldmargin,
			fieldLabel : '风险状态',
			columnWidth : .5
		});
		
		me.fieldContainer = Ext.widget('fieldcontainer',{
			layout : 'column',
			columnWidth : 1
		});
		
		Ext.apply(me, {
			autoHeight : true,
			autoWidth : true,
			width : '99%',
			collapsible : true,
			layout : {
				type : 'column'
			},
			items : [me.templatacontainer,me.assesstype,
						me.fieldContainer] 
		});

		me.callParent(arguments);

	},
	
	initParams : function(isEdit){
		var me = this;
		if(isEdit != undefined && isEdit != null && isEdit != ''){
			me.isEdit = isEdit;
		}else{
			me.isEdit = false;
		}
		me.riskId = "";
		me.assessValue = "";
		me.fieldContainer.removeAll();
		me.assesstype.setValue("");
		me.assesstemplate.setValue('');
		me.assesstemplate.setReadOnly(false);
		me.templatebutton.setDisabled(false);
		me.templateid = '';
		me.riskResultScore = [];
	},
	
	reloadData : function(riskId){
		var me = this;
		if(riskId != undefined && riskId != null && riskId != ''){
			me.riskId = riskId;
		}
		FHD.ajax({
            url: __ctxPath + '/cmp/risk/findTemplateAndWeightByRiskIdForSecurity.f',
            params: {
            	riskId : me.riskId
            },
            callback: function (data) {
            	if(me.isEdit){
	            	me.templateid = data.templateId;
	        		if(me.templateid == null){
	        			me.assessValue = '';
	        		}else{
	        			me.riskOldResultScore = data.riskResultScore;
		            	me.riskResultScore = data.riskResultScore;
						me.riskRela ={
							riskScore : data.riskScore,
							riskStatus : data.riskStatus
						};
						me.assesstemplate.setValue(data.templateId);
						me.assesstemplateStore.load();
						// if exists planid set button disabled
						if(data.planId){
							me.assesstemplate.setReadOnly(true);
							me.templatebutton.setDisabled(true);
						}
	        		}
            	}
            }
		})
	},
	/**
	 * update assess layout
	 */
	layoutAssessType : function(container,dataArr){
		var me = this;
		if(dataArr != undefined && dataArr.length > 0){
			Ext.Array.each(dataArr,function(item,index){
				container.insert(index,Ext.create('Ext.form.field.Display',{
					name : 'scorenum',
					scoreid : item.scoreId,
					margin : me.fieldmargin,
					columnWidth : .5,
					value : item.score,
					fieldLabel : item.scoreName,
					labelWidth : me.fieldlabelWidth
				}));
			});
		}
	},
	reLayout: function(templateid){
		if(templateid != '' && templateid != 'null' && templateid != null){
			var me = this;
			me.fieldContainer.removeAll();
			//me.assesstype.setValue("");
			if(me.templateid == templateid){
				var fieldsetContainer =  me.fieldContainer;
				me.layoutAssessType(fieldsetContainer,me.riskOldResultScore);
				// set risk status
				var scoreStr = "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + me.riskRela.riskStatus + "'  data-qtip='" + me.riskRela.riskScore + "'>&nbsp</div>";
				me.assesstype.setValue(scoreStr);
			}else{
				FHD.ajax({
					url: __ctxPath + '/cmp/risk/riskassessmakecmpforsecurity.f',
					params: {
						templateid : templateid
					},
					callback: function (data) {
						//datas:维度内容，datasDesc维度描述
						me.riskResultScore = data.datas;
						var fieldsetContainer = me.fieldContainer;
						me.layoutAssessType(fieldsetContainer,me.riskResultScore);
						if(me.riskResultScore.length == 0){
							fieldsetContainer.insert(0,Ext.create('Ext.form.Label',{
								margin : me.fieldmargin,
								labelWidth : me.fieldlabelWidth,
								text : '模板无维度信息，请重新操作',
								width : 300
							}));
						}
					}
				});
			}
		}
	},
	
	getAssessType : function(templateid,saveinfo,type){
		var me = this;
		FHD.ajax({
            url: __ctxPath + '/cmp/risk/riskassesstype.f',
            params: {
            	templateid : templateid,
            	saveinfo : saveinfo,
            	type : type
            },
            callback : function(data){
            	var scoreStr = '';
            	if(data.riskScoreValue != '-1'){
            		scoreStr = "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + data.riskIcon + "'  data-qtip='" + data.riskScoreValue + "'>&nbsp</div>";
            	}
            	me.assesstype.setValue(scoreStr);
            	var saves = data.scoreValue.split('|');
            	for(var i=Number(0);i<saves.length-1;i++){
            		var savedetail = saves[i].split('*');
            		var scoreobs = Ext.ComponentQuery.query('displayfield[name=scorenum]',me);
            		if(scoreobs.length > 0){
						for(var j=0;j<scoreobs.length;j++){
							var obs = scoreobs[j];
							if(obs.scoreid == savedetail[0]){
								obs.setValue(savedetail[1]);
							}
						}
					}
            		
            	}
            }
        })
        if('load' != type){
	        var value = {};
			value['templateid'] = templateid;
			value['saveinfo'] = saveinfo;
	    	me.templateid = templateid;
			me.assessValue = value;
        }else{
        	var value = {};
			value['templateid'] = templateid;
        	me.assessValue = value;
        }
	},
	
	getValue : function(){
		var me = this;
		if(me.assessValue != ''){
			return Ext.encode(me.assessValue);
		}else{
			return '';
		}
	}
});