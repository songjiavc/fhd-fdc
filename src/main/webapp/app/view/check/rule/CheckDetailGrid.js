/**
 * 考评细则
 * Grid AUTHOR:Perry Guo 
 * DATE:2017-07-14
 */
 Ext.define('FHD.view.check.rule.CheckDetailGrid', {
	extend : 'FHD.ux.EditorGridPanel',
	alias : 'widget.checkDetailGrid',
    requires: [
    	'FHD.ux.TreeCombox'
    ],
	initComponent : function() {
		var me = this;
		/*
		 * 考评项目Store
		 * */
		var projectStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : [ 'id', 'name' ],
			remoteSort : true,
			proxy : {
				type : 'ajax',
				url : __ctxPath + "/check/checkproject/findCheckProjects.f",
				reader : {
					type : 'json',
					root : 'datas',
					totalProperty : 'totalCount'
				}
			}
		});

		me.projectComboBox = Ext.create('Ext.form.ComboBox', {
			store : projectStore,
			displayField : 'name',
			valueField : 'id',
			labelAlign : 'left',
			multiSelect : false,
			triggerAction : 'all',
			flex : 9,
			allowBlank : false,
			listeners : {
				change : function(com,newValue,newName,eOpts){
				var sm = me.getSelectionModel();
				var obj=sm.getSelection()
				Ext.each(obj,function(m){
				m.set("projectId",newValue);
				m.set("commentName","");
				})
				//当考评项目改变时重新加载考评内容
				commnetStore.proxy.url=__ctxPath + "/check/checkcomment/findCheckComments.f?projectId=" + newValue;
				commnetStore.reload();
				}
			}

		});
		/*
		 * 考评内容Store
		 * */
		var commnetStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : [ 'id', 'name' ],
			remoteSort : true,
			proxy : {
				type : 'ajax',
				url : __ctxPath + "/check/checkcomment/findCheckComments.f",
				reader : {
					type : 'json',
					root : 'datas',
					totalProperty : 'totalCount'
				}
			}
		});

		me.commentComboBox = Ext.create('Ext.form.ComboBox', {
			store : commnetStore,
			displayField : 'name',
			valueField : 'id',
			labelAlign : 'left',
			multiSelect : false,
			triggerAction : 'all',
			flex : 9,
			allowBlank : false,
			listeners : {
				change : function(com,newValue,newName,eOpts){
				var sm = me.getSelectionModel();
				var obj=sm.getSelection()
				Ext.each(obj,function(m){
				m.set("commentId",newValue);
				})
				}
			}

		});
		//tbar 启用状态
		me.statesText = Ext.create('Ext.form.field.Display', {
			fieldLabel : '启用状态' 
		});
		var cols = [{
					dataIndex : 'id',
					sortable : false,
					hidden : true
				}, {
					dataIndex : 'projectId',
					sortable : false,
					hidden : true
				}, {
					dataIndex : 'commentId',
					sortable : false,
					hidden : true
				},
				{
					dataIndex : 'detailStutes',
					sortable : false,
					hidden : true,
					renderer: function(value,metadata,record){  
						 newValue=Ext.util.Format.stripTags(value);
                            return newValue;  
                        }  
				},{
					header : '考评项目',
					dataIndex : 'projectName',
					sortable : false,
					flex : 2,
					editor : me.projectComboBox,
					renderer: function(value,metadata,record){  
						 
                            var index = projectStore.find('id',value);  
                            if(index!=-1){  
                                return projectStore.getAt(index).data.name;  
                            }  
                            return value;  
                        }  
				},{
					header : '考核内容',
					dataIndex : 'commentName',
					sortable : false,
					flex : 2,
					editor : me.commentComboBox,
					renderer: function(value,metadata,record){  
                            var index = commnetStore.find('id',value);  
                            if(index!=-1){  
                                return commnetStore.getAt(index).data.name;  
                            }  
                            return value;  
                        }  
				},{
					header : '评分项',
					dataIndex : 'name',
					sortable : false,
					flex : 2,
					editor : new Ext.form.field.Text({
					allowBlank : false
					})
				},{
					header : '评分标准',
					dataIndex : 'detailStandard',
					sortable : false,
					flex : 2,
					editor : new Ext.form.field.Text({
					allowBlank : false
					})
				},{
					header : '分值',
					dataIndex : 'detailScore',
					sortable : true,
					flex : 1,
					editor : new Ext.form.NumberField({
								allowBlank : false
							})
				}];

		Ext.apply(me, {
			region : 'center',
			cols : cols,
			url : __ctxPath + "/check/checkdetail/finCheckDetailAllPage.f",
			clicksToEdit : 2,
			tbarItems : [
						{
						btype : 'add',
						handler : function() {
							var initValue = {
								id : "",
								projectId:"",
								commentId:"",
								name : "",
								projectName : "",
								commentName:"",
								detailStandard:"",
								detailScore : ""
							};
							me.getStore().add(initValue);
						}

					}, {
						btype : 'delete',
						handler : function() {
							var sm = me.getSelectionModel();
							var array=[];
							if (sm.hasSelection()) {
								Ext.Msg.confirm("提示", "真的要删除选中的行吗？", function(
										btn) {
									if (btn == "yes") {
									var delData=sm.getSelection();
								    Ext.each(delData,function(d){
								     	if(d.data.id!=null)
									{
										array.push(d.data.id);
									}
								    })
								    if (array.length!=0) {
								    Ext.Ajax.request({
										url : __ctxPath+ "/check/checkproject/deleteCheckProject.d",
									    method : 'post',
										success : function() {
										me.store.load();
										},
										params : {
										data : array
												}
											})
										}
									me.getStore().remove(sm.getSelection());
									}
								});
							} else {
								Ext.Msg.alert("错误", "请先选择删除的行，谢谢");
							}
						}
					}, {
						btype : 'save',
						handler : function() {
							var store = me.getStore();
							var modified = store.getModifiedRecords();
							if (modified.length != 0) {
								Ext.Msg.confirm("提示", "是否需要保存数据",
										function(btn) {
											if (btn == "yes") {
												var t=me.validaData(modified)
												if(!t){
												return;
												}else{
												var jsonArray = [];
												var jsonStr="["
												var json="";
												Ext.each(modified, function(m) {
													var id=m.data.id;
													var name=m.data.name;
													var detailStandard=m.data.detailStandard;
													var detailScore=m.data.detailScore;
													var projectId=m.data.projectId;
													var commentId=m.data.commentId
												var	str="{'id':'"+id+"','name':'"+name+"','detailStandard':'"+detailStandard+"','detailScore':'"+detailScore+"','checkComment':"+"{'id':'"+commentId+"'"
													+",'project':"+"{'id':'"+projectId+"'}}}";
													jsonStr=jsonStr+str+",";
														})
												jsonStr = jsonStr.substr(0, jsonStr.length - 1)
												jsonStr=jsonStr+"]";	
												json=JSON.parse(jsonStr.replace(/'/g, '"'));	
												var data = str=JSON.stringify(json);
												Ext.Ajax.request({
													url : __ctxPath
															+ "/check/checkdetail/savaCheckDetail.s",
													method : 'post',
													success : function() {
														me.store.load();
													},
													params : {
														data : data
													}
												})
											}
											}
										});
							}
						}
					},me.statesText
					
			]
		})
		me.callParent(arguments);
		//store 加载后为状态赋值
		me.store.on("load", function() {
			var statues = me.store.getAt(0).get("detailStutes");
			if (statues) {
				if (statues !=1) {
					me.statesText
							.setValue("<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
									+ "margin-left:-44px;margin-top:3px' data-qtitle='' "
									+ "class='icon-ibm-symbol-4-sm'>&nbsp</div>");
					Ext.QuickTips.init();
					Ext.QuickTips.register({
								target : me.statesText.id,
								text : statues,
								autoHide:false
							});
			
				} else {
					me.statesText
							.setValue("<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
									+ "margin-left:-44px;margin-top:3px' data-qtitle='' "
									+ "class='icon-ibm-symbol-6-sm'>&nbsp</div>");
		
				 
				}

			}

		})
	},
	/*
	 * 新增数据的验证
	 * 考评项目名称    不能为空
	 * 考评总分              不能为空且只能为整数
	 * 排序                        不能为空且只能为整数
	 * */
	validaData:function (modified)
		{
		 var me=this;
		 var result = true;  
		 var r=false;
         Ext.each(modified,function(d){
         	if(d.data.name!=null&&d.data.commentOrder!='')
         		{
         		r=true;
         		}
         })
                if(!r){  
                    Ext.MessageBox.alert("验证","对不起，您输入的数据非法");  
                    result = false;  
                    return result;  
                }  
        return result;  
 		
		}

})