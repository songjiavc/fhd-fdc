Ext.define('FHD.view.risk.cmp.RiskAssessPanel', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskassesspanel',
	
	isEdit : false,
	
	/**
	 * 历史记录id，用于修改
	 */
	hisid : '',
	
	/**
	 * 选择不同type，左侧主对象的值
	 */
	value : '',
	
	/**
	 * 评估的类型，包括：风险risk;风险事件riskevent;组织org;目标sm;流程process;formula:公式计算
	 */
	type : '',
	/**
	 * 操作状态，包括： opr：默认状态，可以新增修改，view：查看状态，只显示历史评分，切无法调用save方法，cmp：组件状态，返回输入的信息
	 * */
	oprType : 'opr',
	
	callback:Ext.emptyFn(),
	
	initComponent : function() {
		var me = this;
		
		me.fieldContainer = Ext.widget('fieldcontainer',{
			layout : 'vbox'
		});
		
		me.addfieldsetContainer = Ext.widget('fieldset',{
			layout : 'fit',
			title : '评估信息',
			autoHeight : true,
			autoWidth : true,
			collapsible: true,
			items : me.fieldContainer
		});
		
		me.fieldContainerOld = Ext.widget('fieldcontainer',{
			layout : 'vbox'
		});
		me.fieldContainerNew = Ext.widget('fieldcontainer',{
			layout : 'vbox'
		});
		
		me.editfieldsetContainerOld = Ext.widget('fieldset',{
			title : '历史评分',
			autoHeight : true,
			collapsible: true,
			items : me.fieldContainerOld
		});
		
		me.editfieldsetContainerNew = Ext.widget('fieldset',{
			title : '评估信息',
			autoHeight : true,
			collapsible: true,
			items : me.fieldContainerNew
		});
		
		if(me.oprType == 'view'){
			me.editfieldsetContainer = Ext.widget('container',{
				layout : 'fit',
				items : [me.editfieldsetContainerOld]
			})
		}
		else{
			me.editfieldsetContainer = Ext.widget('container',{
				layout : 'fit',
				items : [me.editfieldsetContainerOld,me.editfieldsetContainerNew]
			})
		}
		
		
		Ext.applyIf(me, {
			autoScroll : true,
			border : false
		});

		me.callParent(arguments);
		
	},
	
	/**
	 * templateid 模板id，修改时根据模板构建维度显示，添加时只传递value即可
	 * change by jia.song@pcitc.com
	 */
	reloadData : function(value,templateid,hisid,adjustTypeValue){
		var me = this;
		if(adjustTypeValue == '3'){
			me.type = 'formula';
		}
		me.value = value;
		var tid;
		if(hisid != null){
			me.hisid = hisid;
		}
		if(templateid != null){
			me.templateid = templateid;
		}
		FHD.ajax({
            url: __ctxPath + '/cmp/risk/riskassesscmp.f',
            params: {
            	templateid : templateid,
            	hisid : me.hisid,
            	isEdit : me.isEdit,
            	value : me.value,
            	type : me.type
            },
            callback: function (data) {
            	//datas:维度内容，datasDesc维度描述
            	var datas = data.datas;
            	me.datasDesc = data.datasDesc;
            	var fieldsetContainer = null;
            	var fieldsetContainerOld = null;
            	if(me.isEdit){
            		//修改
            		me.add(me.editfieldsetContainer);
            		fieldsetContainer = me.editfieldsetContainerNew;
            		fieldsetContainerOld = me.editfieldsetContainerOld;
            	}else{
            		me.add(me.addfieldsetContainer);
            		fieldsetContainer = me.fieldContainer;
            	}
            	me.doLayout();
            	var num=1;
            	if(me.type == 'risk' || me.type == 'riskevent'){
            		//风险，要按模板维度进行重新打分
	            	for(var i=0;i<datas.length;i++){
	            		var datasinfo = datas[i];
	            		me.templateid = datasinfo[0];
	            		if(datasinfo[3] == null){
	            			//无子维度，当前维度打分
	            			fieldsetContainer.insert(num,Ext.widget('numberfield',{
	            				name : 'scorenum',
	            				scoreid : datasinfo[1],
	            				scorevalue : datasinfo[1],
	            				margin : '7 30 3 30',
	            				minValue: 0,
								maxValue: 5,
								fieldLabel : datasinfo[2] + '<font color=red>*</font>',
								labelWidth : 200,
								width : 300,
								decimalPrecision: 2,
								allowBlank : false,
								step: 0.1,
								allowDecimals: true,
								listeners : {
									focus : function(component){
										me.showToolTip(component);
									}
								}
							}));
							if(fieldsetContainerOld != null){
								fieldsetContainerOld.insert(num,Ext.widget('displayfield',{
		            				margin : '7 30 3 30',
									fieldLabel : datasinfo[2],
									labelWidth : 200,
									width : 300,
									value : datasinfo[4]
								}));
							}
							num++;
	            		}else{
	            			var datasdown = datasinfo[3];
	            			//有子维度，为子维度打分
	            			fieldsetContainer.insert(num,Ext.widget('label',{
	            				margin : '7 30 3 30',
								text : datasinfo[2],
								width : 300
							}));
							if(fieldsetContainerOld != null){
								fieldsetContainerOld.insert(num,Ext.widget('displayfield',{
		            				margin : '7 30 3 30',
									fieldLabel : datasinfo[2],
									labelWidth : 200,
									width : 300,
									value : datasinfo[4]
								}));
							}
							num++;
							for(var j=0;j<datasdown.length;j++){
								datadowninfo = datasdown[j];
								fieldsetContainer.insert(num,Ext.widget('numberfield',{
									name : 'scorenum',
									scorevalue : datadowninfo[0] + ',' + datasinfo[1],
									scoreid : datadowninfo[0],
									margin : '7 30 3 50',
		            				minValue: 0,
									maxValue: 5,
									fieldLabel : datadowninfo[1] + '<font color=red>*</font>',
									labelWidth : 180,
									width : 280,
									decimalPrecision: 2,
									allowBlank : false,
									step: 0.1,
									allowDecimals: true,
									listeners : {
										focus : function(component){
											me.showToolTip(component);
										}
									}
								}));
								if(fieldsetContainerOld != null){
									fieldsetContainerOld.insert(num,Ext.widget('displayfield',{
			            				margin : '7 30 3 50',
										fieldLabel : datadowninfo[1],
										labelWidth : 180,
										width : 280,
										value : datadowninfo[2]
									}));
								}
								num++;
							}
	            		}
	            	}
	            	if(i == 0){
	            		fieldsetContainer.insert(num,Ext.widget('label',{
            				margin : '7 30 3 30',
							text : '模板无维度信息，请重新操作',
							width : 300
						}));
						if(fieldsetContainerOld != null){
							fieldsetContainerOld.insert(num,Ext.widget('label',{
	            				margin : '7 30 3 30',
								text : '模板无维度信息，请重新操作',
								width : 300
							}));
						}
	            	}
            	}else{
            		if(fieldsetContainerOld != null){
						fieldsetContainerOld.insert(num,Ext.widget('displayfield',{
            				margin : '7 30 3 50',
							fieldLabel : '风险值',
							labelWidth : 200,
							width : 300,
							value : datas[0]
						}));
					}
            		//直接修改风险值
					fieldsetContainer.insert(num,Ext.widget('numberfield',{
						name : 'scorenum',
						margin : '7 30 3 50',
        				minValue: 0,
						fieldLabel : '风险值' + '<font color=red>*</font>',
						labelWidth : 200,
						width : 300,
						decimalPrecision: 2,
						allowBlank : false,
						step: 0.1,
						allowDecimals: true
					}));
            	}
            }
		});
	},
	
	showToolTip : function(component){
		var me = this;
		var scoreid = component.scoreid;
		var htmldesc = '';
		var htmlname = '';
		for(var i=0;i<me.datasDesc.length;i++){
			var datas = me.datasDesc[i].split('--');
			if(scoreid == datas[0]){
				htmlname = datas[4];
				htmldesc = htmldesc +  datas[2].split('.')[0] + '分 : ' + datas[5] + '</br>'
			}
		}
		var toolTipContainer = Ext.widget('tooltip', {
	        target: component.el,
	        name : 'toolTipContainer',
	        title: htmlname,
	        anchor: 'left',
	        autoHide: true,
	        width : 200,
//	        height : 100,
	        dismissDelay: 15000,
	        html : htmldesc
	    }).show();
	},
	
	save : function(){
		var me  = this;
		if(me.oprType != 'view'){
			if(me.type == 'risk' || me.type == 'riskevent'){
				return me.saverisk();
			}else{
				return me.savehistroy();
			}
			return true;
		}else{
			return false;
		}
	},
	
	saverisk : function(){
		var me = this;
		var form = me.getForm();
		if(form.isValid()){
			var scoreobs = Ext.ComponentQuery.query('numberfield[name=scorenum]',me);
			var saveinfo = '';
			if(scoreobs.length > 0){
				for(var i=0;i<scoreobs.length;i++){
					var obs = scoreobs[i];
					saveinfo = saveinfo + obs.scorevalue + '*' + obs.getValue() + '|';
				}
				if(me.oprType == 'cmp'){
					FHD.ajax({
						async:false,
			            url: __ctxPath + '/cmp/risk/riskassesssave.f',
			            params: {
			            	saveinfo : saveinfo,
			            	value : me.value,
			            	templateid : me.templateid,
			            	type : me.type,
			            	isEdit : me.isEdit,
			            	hisid : me.hisid
			            },
			            callback: function (data) {
			            	FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
			            	if(me.callback){
								me.callback(data);
							}	
			            }
			            
					});
					return saveinfo;
				}else{
					return saveinfo;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
	},
	
	savehistroy : function(){
		var me = this;
		var form = me.getForm();
		if(form.isValid()){
			var saveinfo = me.down("[name='scorenum']").getValue();
			FHD.ajax({
	            url: __ctxPath + '/cmp/risk/riskassesssave.f',
	            params: {
	            	saveinfo : saveinfo,
	            	value : me.value,
	            	templateid : me.templateid,
	            	type : me.type,
	            	isEdit : me.isEdit,
	            	hisid : me.hisid
	            },
	            callback: function (data) {
	            	FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
	            	if(me.callback){
						me.callback(data);
					}
	            }
			});
		}else{
			return false;
		}
	}
	
});