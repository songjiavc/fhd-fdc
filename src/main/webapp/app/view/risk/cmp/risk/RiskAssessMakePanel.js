Ext.define('FHD.view.risk.cmp.risk.RiskAssessMakePanel', {
	extend : 'Ext.form.FieldSet',
	alias : 'widget.riskassessmakepanel',

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
					if(newValue != me.templateid){
						//新增状态
						me.isEdit = false;
					}
					me.reLayout(newValue);
				}
			}
		});
		
		me.templatebutton = Ext.create('Ext.button.Button',{
			text : '评价',
            flex : 1,
            handler:function(){
            	if(me.assesstemplate.getValue() != null && me.assesstemplate.getValue() != ''){
            		var riskAssessPanel = Ext.create('FHD.view.risk.cmp.RiskAssessPanel',{
            			oprType : 'cmp',
            			type : 'risk'
            		});
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
		                    handler: function () {
		                    	me.getAssessType(me.assesstemplate.getValue(),riskAssessPanel.save());
		                    	assesswindow.close();
		                    }
		                }, {
		                    text: '关闭',
		                    handler: function () {
		                        assesswindow.close();
		                    }
		                }]
		            }).show();
		           riskAssessPanel.reloadData(me.riskId,me.assesstemplate.getValue());
		           assesswindow.add(riskAssessPanel);
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
	
	initParams : function(isEdit,templateid){
		var me = this;
		if(isEdit != undefined && isEdit != null && isEdit != ''){
			me.isEdit = isEdit;
		}else{
			me.isEdit = false;
		}
		if(templateid != undefined && templateid != null && templateid != ''){
			me.templateid = templateid;
			me.assesstemplate.setValue(templateid);
			me.assesstemplateStore.load();
		}else{
			me.templateid = '';
			me.assesstemplate.setValue("");
		}
		me.riskId = "";
		me.assessValue = "";
		me.fieldContainer.removeAll();
		me.assesstype.setValue("");
	},
	
	reloadData : function(riskId){
		var me = this;
		if(riskId != undefined && riskId != null && riskId != ''){
			me.riskId = riskId;
		}
		FHD.ajax({
            url: __ctxPath + '/cmp/risk/riskassessmaketemplate.f',
            params: {
            	riskId : me.riskId
            },
            callback: function (data) {
            	me.hisId = data.hisId
            	if(me.isEdit){
	            	me.templateid = data.templateid;
					me.assesstemplate.setValue(data.templateid);
					me.assesstemplateStore.load();
            	}
            }
		})
	},
	
	reLayout: function(templateid){
		var me = this;
		me.fieldContainer.removeAll();
		//me.assesstype.setValue("");
		if(templateid == undefined || templateid == null || templateid == ''){
			me.assessValue = '';
		}else{
			FHD.ajax({
	            url: __ctxPath + '/cmp/risk/riskassessmakecmp.f',
	            params: {
	            	templateid : templateid,
	            	isEdit : me.isEdit,
	            	riskId : me.riskId,
	            	hisId : me.hisId
	            },
	            callback: function (data) {
	            	//datas:维度内容，datasDesc维度描述
	            	var datas = data.datas;
	            	var fieldsetContainer = null;
	        		fieldsetContainer = me.fieldContainer;
	            	var num=1;
	            	var saveinfo = '';
	            	for(var i=0;i<datas.length;i++){
	            		var datasinfo = datas[i];
            			//无子维度，当前维度打分
            			fieldsetContainer.insert(num,Ext.create('Ext.form.field.Display',{
	        				name : 'scorenum',
	        				scoreid : datasinfo[1],
	        				margin : me.fieldmargin,
	        				columnWidth : .5,
							fieldLabel : datasinfo[2],
							labelWidth : me.fieldlabelWidth
						}));
						num++;
						saveinfo = saveinfo + datasinfo[1] + '*' + datasinfo[4] + "|";
	            		if(datasinfo[3] != null){
	            			var datasdown = datasinfo[3];
							for(var j=0;j<datasdown.length;j++){
								datadowninfo = datasdown[j];
								saveinfo = saveinfo + datadowninfo[0] + '*' + datadowninfo[2] + "|";
							}
	            		}
						num++;
	            	}
	            	if(i == 0){
	            		fieldsetContainer.insert(num,Ext.create('Ext.form.Label',{
	        				margin : me.fieldmargin,
							labelWidth : me.fieldlabelWidth,
							text : '模板无维度信息，请重新操作',
							width : 300
						}));
	            	}
	            	if(me.isEdit){
		            	me.getAssessType(templateid,saveinfo,'load');
	            	}else{
	            		//新增只选择模板
	            		var value = {};
						value['templateid'] = templateid;
						me.templateid = templateid;
						me.assessValue = value;
	            	}
	            }
			});
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