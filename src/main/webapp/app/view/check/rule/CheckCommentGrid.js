/**
 * 考评项目
 * Grid AUTHOR:Perry Guo 
 * DATE:2017-07-14
 */
 Ext.define('FHD.view.check.rule.CheckCommentGrid', {
	extend : 'FHD.ux.EditorGridPanel',
	alias : 'widget.checkCommentGrid',
    requires: [
    	'FHD.ux.TreeCombox',
    	'FHD.view.risk.assess.utils.GridCells'
    ],
	initComponent : function() {
		var me = this;
		var projectStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : [ 'id', 'name' ],
			remoteSort : true,
			proxy : {
				type : 'ajax',
				url : __ctxPath + "/check/checkproject/findCheckProjects.f",//'/access/formulateplan/findtemplates.f',
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
				m.set("project.id",newValue);
				m.set("projectId",newValue);
				})
				}
			}

		});
		var cols = [{
					dataIndex : 'id',
					sortable : false,
					hidden : true
				}, {
					dataIndex : 'project.id',
					sortable : false,
					hidden : true
				},, {
					dataIndex : 'projectId',
					sortable : false,
					hidden : true
				}, {
					header : '考评项目',
					dataIndex : 'project.name',
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
					header : '考评内容',
					dataIndex : 'name',
					sortable : false,
					flex : 2,
					editor : new Ext.form.field.Text({
					allowBlank : false
					})
				},{
					header : '排序',
					dataIndex : 'commentOrder',
					sortable : true,
					flex : 1,
					editor : new Ext.form.NumberField({
								allowBlank : false
							})
				}];

		Ext.apply(me, {
			region : 'center',
			cols : cols,
			url : __ctxPath + "/check/checkcomment/findCheckComment.f",
			clicksToEdit : 2,
			tbarItems : [{
						btype : 'add',
						handler : function() {
							var initValue = {
								id : "",
								projectId:"",
								name : "",
								projectName : "",
								commentOrder : ""
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
													var commentOrder=m.data.commentOrder;
													var projectId=m.data.projectId;
												var	str="{'commentOrder':'"+commentOrder+"','id':'"+id+"','name':'"+name+"','project':"+"{'id':'"+projectId+"'"
													+",'name':'1','totalScore':'1','projectOrder':'1','isUserd':'1'"+"}}";
													jsonStr=jsonStr+str+",";
													//str=JSON.stringify(json);
													//jsonArray.push(str.replace(/'/g, '"'));
														})
												jsonStr = jsonStr.substr(0, jsonStr.length - 1)
												jsonStr=jsonStr+"]";	
												json=JSON.parse(jsonStr.replace(/'/g, '"'));	
												var data = str=JSON.stringify(json);
												Ext.Ajax.request({
													url : __ctxPath
															+ "/check/checkcomment/savaCheckComment.s",
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
					}

			]
		})
		me.callParent(arguments);
  		me.on('afterlayout', function () {
            Ext.widget('gridCells').mergeCells(me, [2]);
        });
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