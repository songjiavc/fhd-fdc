/**
 * 考评细则
 * Grid AUTHOR:Perry Guo 
 * DATE:2017-07-14
 */
 Ext.define('FHD.view.check.YearCheckDimesionGrid', {
	extend : 'FHD.ux.EditorGridPanel',
	alias : 'widget.yearCheckDimesionGrid',
    requires: [
    	'FHD.ux.TreeCombox'
    ],
	initComponent : function() {
		var me = this;
		/*
		 * 计划类型Store
		 * */
		var bussinessStore = Ext.create('Ext.data.Store', {
      		fields: ['id', 'name'],
      		proxy: {
    	    	url: __ctxPath + '/check/findPlanTypeByAll.f',
    	        type: 'ajax',
    	        reader: {
    	            type: 'json'
    	        }
      		},
      		autoload : false
        });
		
		me.planTypeCombobox = Ext.widget('combobox',{
			flex : .2,
			valueField : 'id',
			displayField: 'name',
			store : bussinessStore,
			listeners : {
				change : function(com,newValue,newName,eOpts){
				var sm = me.getSelectionModel();
				var obj=sm.getSelection()
				Ext.each(obj,function(m){
				m.set("planTypeID",newValue);
				})
				}
			}
		});
		/*
		 * 考评内容Store
		 * */
		var detailStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : [ 'id', 'name' ],
			remoteSort : true,
			proxy : {
				type : 'ajax',
				url : __ctxPath + "/check/checkdetail/findCheckDetail.s",
				reader : {
					type : 'json',
					root : 'datas',
					totalProperty : 'totalCount'
				}
			}
		});

		me.detailComboBox = Ext.create('Ext.form.ComboBox', {
			store : detailStore,
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
				m.set("detailId",newValue);
				})
				}
			}

		});

		var cols = [{
					dataIndex : 'id',
					sortable : false,
					hidden : true
				}, {
					dataIndex : 'detailId',
					sortable : false,
					hidden : true
				}, {
					dataIndex : 'planTypeID',
					sortable : false,
					hidden : true
				},{
					header : '考评细则名称',
					dataIndex : 'detailName',
					sortable : false,
					flex : 0.2,
					editor : me.detailComboBox,
					renderer: function(value,metadata,record){  
                            var index = detailStore.find('id',value);  
                            if(index!=-1){  
                                return detailStore.getAt(index).data.name;  
                            }  
                            return value;  
                        }  
				},{
					header : '计划类型',
					dataIndex : 'planTypeName',
					sortable : false,
					flex : 0.2,
					editor : me.planTypeCombobox,
					renderer: function(value,metadata,record){  
                            var index = bussinessStore.find('id',value);  
                            if(index!=-1){  
                                return bussinessStore.getAt(index).data.name;  
                            }  
                            return value;  
                        }  
				},{
					header : '推迟天数',
					dataIndex : 'dalayDate',
					sortable : false,
					flex : 0.2,
					editor : new Ext.form.NumberField({
								allowBlank : false
							})
				},{
					header : '扣分',
					dataIndex : 'subScore',
					sortable : false,
					flex : 0.2,
					editor : new Ext.form.NumberField({
								allowBlank : false
							})
				}
				];

		Ext.apply(me, {
			region : 'center',
			cols : cols,
			url : __ctxPath + "/check/finCheckDimesionAllPage.f",
			clicksToEdit : 2,
			border: false,
		    checked : true,
		    pagable : true,
			tbarItems : [
						{
						btype : 'add',
						handler : function() {
							var initValue = {
								id : "",
								detailId:"",
								planTypeID:"",
								detailName : "",
								planTypeName : "",
								dalayDate:"",
								subScore:""
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
												var jsonArray = [];
												var jsonStr="["
												var json="";
												Ext.each(modified, function(m) {
													var id=m.data.id;
													var dalayDate=m.data.dalayDate;
													var subScore=m.data.subScore;
													var detailId=m.data.detailId;
													var planTypeID=m.data.planTypeID;
												var	str="{'id':'"+id+"','dalayDate':'"+dalayDate+"','subScore':'"+subScore+"','checkDetail':"+"{'id':'"+detailId+"'"
													+"},'playType':{'id':'"+planTypeID+"'}}";
													jsonStr=jsonStr+str+",";
														})
												jsonStr = jsonStr.substr(0, jsonStr.length - 1)
												jsonStr=jsonStr+"]";	
												json=JSON.parse(jsonStr.replace(/'/g, '"'));	
												var data = str=JSON.stringify(json);
												Ext.Ajax.request({
													url : __ctxPath
															+ "/check/savaCheckDimesion.s",
													method : 'post',
													success : function() {
														me.store.load();
													},
													params : {
														data : data
													}
												})
											}
										});
							}
						}
					}
					
			]
		})
		me.callParent(arguments);
	}
})