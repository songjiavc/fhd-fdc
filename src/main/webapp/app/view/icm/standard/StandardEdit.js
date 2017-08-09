/**
 *    @description 录入内控标准FORM 
 *    
 *    @author 宋佳
 *    @since 2013-3-5
 */
Ext.define('FHD.view.icm.standard.StandardEdit', {
	extend : 'Ext.form.Panel',
	alias : 'widget.standardedit',
	
	title:'基本信息',
	layout : {
		type : 'column'
	},
	defaults : {
		columnWidth : 1/1
	},
	collapsed : false,
	bodyPadding:'0 3 3 3',
	paramObj:{
		nodeId:'',
		addType:'',	//addNow(添加同级),addNext(添加下级),0(添加要求)
		idSeq:'',
		standardId:''
	},
	requires: [
       'FHD.view.process.ProcessMainPanel'
    ],
	initComponent :function() {
		var me = this;
		var parentIdFile={
			xtype : 'textfield',
			disabled : false,
			name : 'parent.id',
			hidden:true
		};
		var levelField={
			xtype : 'textfield',
			disabled : false,
			name : 'level',
			hidden:true
	    };
		var idSqlField={
			xtype : 'textfield',
			disabled : false,
			name : 'idSeqp',
			hidden:true
		};
		var idStroFile={
			xtype : 'textfield',
			disabled : false,
			name : 'id',
			hidden:true
		};
		var standardUpStep={
			id : 'standardUpStepId',
			labelWidth : 80,
			xtype : 'textfield',
			name:'upName',
			//readOnly:true,
			disabled:true,
			lblAlign:'right',
			fieldLabel : '分　　类' + '<font color=red>*</font>',
			margin: '7 10 10 30'
		};
		me.standardCode = {
			id : 'standardCodeId',
			labelWidth :80,
			xtype : 'textfield',
			disabled : false,
			lblAlign:'rigth',
			fieldLabel : '编　　号' + '<font color=red>*</font>',
			value : '',
			name : 'code',
			margin: '7 10 10 30',
			maxLength : 200,
			columnWidth:.5,
			allowBlank : false
		};
//		me.standardCreateCodeButton = {
//            xtype: 'button',
//            margin: '7 10 0 0',
//            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'),
//            handler: function(){
//       			FHD.ajax({
//	            	url:__ctxPath+'/standard/standardTree/createStandardCode.f',
//	            	params: {
//	                	nodeId: me.nodeId
//                 	},
//	                callback: function (data) {
//	                 	me.getForm().setValues({'code':data.code});//给code表单赋值
//	                }
//                });
//            },
//            columnWidth: .1
//        };
		//控制层级
		me.standardControlLevel=Ext.create('FHD.ux.dict.DictSelect',{
			name:'controlLevelId',
		    dictTypeId:'ic_control_level',
		    margin: '7 10 10 30',
		    labelWidth : 80,
		    labelAlign : 'left',
		    editable:false,
		    value:'',
		    multiSelect:false,
		    fieldLabel : '控制层级'+ '<font color=red>*</font>'
	    });
		//名称
		var standardControlRequirement={
	        xtype: 'textareafield',
            margin: '7 10 10 30',
            labelWidth : 80,
            name:'name',
            rows: 3,
            maxLength : 2000,
            fieldLabel:'名　　称'+ '<font color=red>*</font>',
			allowBlank : false
		};
		//内控要素
		me.standardControlPoint=Ext.create('FHD.ux.dict.DictSelect',{
		    name:'controlPoint',
		    dictTypeId:'ic_control_point',
		    margin: '7 10 10 30',
		    labelWidth : 80,
		    labelAlign : 'left',
		    editable:false,
		    multiSelect:false,
		    fieldLabel : '内控要素'+ '<font color=red>*</font>',
	    	onChange :function(nValue,oValue){
		     	if('ic_control_point_c' == nValue){//控制活动ic_control_point_c
		     		me.standardRelaProcess.label.setText('选择流程' + '<font color=red>*</font>:', false);
		     	}else{
		     		me.standardRelaProcess.label.setText('选择流程 :', false);
		     	}
         	 }
	    });
		//责任部门
		me.standardDepart=Ext.create('Ext.ux.form.OrgEmpSelect',{
         	fieldLabel : '责任部门'+ '<font color=red>*</font>',
         	name:'deptId',
         	margin: '7 10 0 30',
         	type : 'dept',
         	labelWidth : 80,
            multiSelect : false,
            growMin: 75,
			growMax: 120,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true
        });
		//处理状态-第4步显示
	    me.standardStatus = Ext.widget('combo', {
	        editable: false,
	        labelWidth: 80,
	        multiSelect: false,
	        margin: '7 10 10 30',
	        columnWidth:.5,
	        name: 'statusId',
	        fieldLabel:'处理状态' + '<font color=red>*</font>', 
	        labelAlign: 'left',
	        store : [['U', ' 待更新'],['O', '已纳入内控手册运转'],['F','已完成']]
	    });
		//附件
		me.fileUpLoad=Ext.widget('fieldset',{
			title : '相关附件',
			//margin: '10 8 0 10',
			defaults : {
				columnWidth : 1/1
			},
			layout : {
				type : 'column'
			},
			items:[Ext.create('FHD.ux.fileupload.FileUpload',{
				hideLabel : true,
				fieldLabel : '附　　件 ',
				labelWidth : 80,
				margin: '7 10 0 0',
				height:120,
				name:'fileId',
				showModel:'base'
			})]
        });
		
		
		/*对应流程  */
		me.standardRelaProcess = Ext.create('FHD.ux.process.ProcessSelector',{
		    name:'processId',
		    labelWidth : 80,
		    parent : false,
		    value:'',
		    columnWidth:.4,
         	height : 23,
			//labelText : '',
		    fieldLabel:'选择流程 ',
		    margin: '7 10 10 30',
         	//hidden : false,
            multiSelect : false,
		    extraParam : {
		    	smIconType:'display',
		    	canChecked:true,
		    	leafCheck : true
		    }
        });
         
        //编辑流程按钮
		me.editProcessureButton = Ext.widget('button',{
	        name : 'editProcessureButton',
	        text:'流程编辑',
	        columnWidth:.1,
	        margin: '7 10 0 0',
	        height: 23,
	        handler:function(){
	        	me.processMainPanel = Ext.widget('processmainpanel', {});
				me.win = Ext.create('FHD.ux.Window', {
					title : '流程编辑',
					closable : true,
					maximizable: true,
					items : [me.processMainPanel]  //ITEMS里面是弹出窗体所包含的PANEL
				}).show();
				//me.win.setVisible(true); //设置可见
	        }
		});
		
		me.items= [{
			xtype : 'fieldset',
			defaults : {
				columnWidth : 1/2
			},
			layout : {
				type : 'column'
			},
			collapsed : false,
			//margin: '8 10 0 10',
			collapsible : false,
			title : '基本信息',
			items:[standardUpStep, me.standardCode, 
			    standardControlRequirement, me.standardControlLevel,
			    me.standardControlPoint,me.standardStatus,
			    me.standardDepart,me.standardRelaProcess,me.editProcessureButton,
			    idStroFile,parentIdFile,levelField,idSqlField
			]
        }, me.fileUpLoad];
		me.bbar={
			style: 'background-image:url() !important;background-color:rgb(250,250,250);',
			items: [{
				xtype: 'tbtext'
				// text: '<font color="#3980F4"><b>' + FHD.locale.get('fhd.strategymap.strategymapmgr.form.basicinfo') + '</b>&nbsp;&rArr;&rArr;&nbsp;</font><font color="#cccccc"><b>' + FHD.locale.get('fhd.strategymap.strategymapmgr.form.kpiset') + '</b>&nbsp;&rArr;&rArr;&nbsp;</font><font color="#cccccc"><b>' + FHD.locale.get('fhd.strategymap.strategymapmgr.form.alarmset') + '</b></font>'
			}, '->',{
				name: 'icm_defect_undo_btn' ,
				text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
				iconCls: 'icon-operator-home',
				handler: function () {
            		var standardtab = me.up('standardtab');
            		standardtab.setActiveTab(standardtab.standardlist);
				}
	        },{
                text: FHD.locale.get("fhd.common.save"),//保存按钮
	            name: 'icm_standard_save_btn' ,
	            iconCls: 'icon-control-stop-blue',
                handler: function () {
                //提交from表单
                var form = me.getForm();
                var vobj = form.getValues();
                var validUrl= __ctxPath + '/standard/standardTree/validateStandard.f';
                if(!form.isValid()){
                     Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),'存在未通过的验证!');
                     return;
                }
                //验证一下名称的长度，300最长
                if(vobj.name.length>300){
                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),'名称不能超过300字!');
                    return;
                }
                if(me.paramObj.controlType != 'addTree'){
                	//process
                	var process = me.standardRelaProcess.getValue();
                	//责任部门
	            	if(vobj.deptId == undefined || vobj.deptId =='' || vobj.deptId =='[]'){
	            		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),'责任部门必选!');
	            		return ;
	            	}
               
	                //控制层级
	                if(me.standardControlLevel.getValue()==undefined || me.standardControlLevel.getValue()=='' || me.standardControlLevel.getValue()=='[]'){
	            		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),'控制层级必选!');
	            		return ;
	                }
	                //内控要素
	            	if(me.standardControlPoint.getValue() == undefined || me.standardControlPoint.getValue() =='' || me.standardControlPoint.getValue() =='[]'){
	            		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),'内控要素必选!');
	            		return ;
	            	}else if('ic_control_point_c' == me.standardControlPoint.getValue()){
						if(process == "" || null == process || process == undefined){
							Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),'内控要素为“控制活动”时，流程为必填项!');
							return;
						}
					}
	                //处理状态
	                if(me.standardStatus.getValue()== undefined || me.standardStatus.getValue() =='' || me.standardStatus.getValue() =='[]'){
	            		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),'处理状态必选!');
	            		return ;
	                }
                }
                
                
                FHD.ajax({
                     url:validUrl,
                     params: {
                     	 id: vobj.id,
	                     code: vobj.code
                     },
                     callback: function (data) {
		                 if(data.flagStr == "codeRepeat") {
		                	 Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '编号重复，请尝试其他编号!');
		                	 return;
		                 }
	                     if(data.flagStr=='notRepeat') {
	                         //提交目标信息
	                      	 var addUrl='';
		                     if(me.controlType=='listEdit'){
		                      	 addUrl=__ctxPath+'/standard/standardTree/editStandard.f';
		          	         }else{
		          	         	 addUrl=__ctxPath + '/standard/standardTree/saveStandard.f?nodeId='+me.paramObj.nodeId+'&addType='+me.paramObj.addType+'&idSeq='+me.paramObj.idSeq+'&standardId='+me.paramObj.standardId;
		          	         }
		                     FHD.submit({
			                     form: form,
			                     url: addUrl,
			                     callback: function (data) {
			                    	 if(data.success){
					                      me.up('standardtab').setActiveTab(1);
					                      var tree=me.up('standardtab').up('standardmainpanel').up('standardmanage').standardTree;
										  var node = tree.getSelectionModel().getLastSelected();
										  
										  if(me.paramObj.addType=="addNext"){//标准分类添加下一级
											  //设置父节点为非叶子节点
								              var newnode = {
								                  id:data.id,
								                  text:data.text,
								                  iconCls:"icon-note",
								                  leaf:true
								              };
								              if(node.isLeaf()){
				                                   node.data.leaf = false;
				                              }
				                              node.appendChild(newnode);
				                              node.expand();
										  }else if(me.paramObj.addType=="addNow"){//标准分类添加同级
											  var newnode = {
									                  id:data.id,
									                  text:data.text,
									                  iconCls:"icon-note",
									                  leaf:true
									              };
				                              node.parentNode.appendChild(newnode);										  
										  }else{
											  if(me.controlType=='treeEdit'){//修改标准分类
												  //更新当前选中的node名称
												  var nodeData = node.data;
					                              nodeData.text = data.text;//取得后台返回的修改后的名称
					                        	  node.updateInfo(true, nodeData);
											  }
										  }
										  
					                     me.up('standardtab').standardlist.reloadData();
				                     }
			                     }
		                    });
	                     }
                     }
                 });
               }
           }]
		};
		Ext.applyIf(me,{
			items:me.items
		});
		me.callParent(arguments);
	},
	initParam:function(paramObj){
		var me = this;
		me.paramObj = paramObj;
		if(me.paramObj.controlType == 'addTree'){
			me.standardControlLevel.hide();
			me.standardControlPoint.hide();
			me.standardStatus.hide();
			me.standardDepart.hide();
			me.fileUpLoad.hide();
			me.standardRelaProcess.hide();
			me.editProcessureButton.hide();
		}else{
			me.standardControlLevel.show();
			me.standardControlPoint.show();
			me.standardStatus.show();
			me.standardDepart.show();
			me.fileUpLoad.show();
			me.standardRelaProcess.show();
			me.editProcessureButton.show();
		}
	},
	generateCode: function(){
		var me = this;
		FHD.ajax({
        	url:__ctxPath+'/standard/standardTree/createStandardCode.f',
        	params: {
            	nodeId: ''
         	},
            callback: function (data) {
             	me.getForm().setValues({'code':data.code});//给code表单赋值
            }
        });
    }
});