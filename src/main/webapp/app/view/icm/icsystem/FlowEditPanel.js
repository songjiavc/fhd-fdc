/**
 * 流程基本信息编辑页面
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.icm.icsystem.FlowEditPanel', {
	extend: 'Ext.form.Panel',
	alias: 'widget.floweditpanel',
	
	frame: false,
	border : false,
	//url: __ctxPath + '/process/process/saveProcess.f',
	paramObj:[],
	
	addComponent: function () {
		var me = this;
    	//基本信息fieldset
		me.basicinfofieldset = Ext.widget('fieldset', {
			title: '基本信息',
			collapsible: false,
			autoHeight: true,
			autoWidth: true,
			defaults: {
			    columnWidth : .5,
			    margin: '7 30 3 30',
			    labelWidth: 105
			},
			layout: {
			    type: 'column'
			}
		});
	    me.add(me.basicinfofieldset);
        // 详细信息
		me.attachmentfieldset = Ext.widget('fieldset', {
			title: '更多',
			collapsible: true,
			autoHeight: true,
			autoWidth: true,
			defaults: {
			   	columnWidth : .5,
			   	margin: '7 30 3 30',
			    labelWidth: 95
			},
			layout: {
			    type: 'column'
			}
	    });
	    me.add(me.attachmentfieldset);
	    //当前流程id
		me.processId=Ext.widget('textfield', {
            value: '',
            name: 'id',
            hidden : true
		});
		me.basicinfofieldset.add(me.processId);
		//父级流程id
		me.parentprocessId=Ext.widget('textfield', {
            value: '',
            name: 'parentid',
            hidden : true
		});
		me.basicinfofieldset.add(me.parentprocessId);
        //上级流程
        me.parentprocess = Ext.widget('textfield', {
            name: 'parentprocess',
            fieldLabel: '流程分类', //上级流程
            readOnly : true
        });
        me.basicinfofieldset.add(me.parentprocess);
        //流程编号
        me.code = Ext.widget('textfield', {
            name : 'code',
            fieldLabel : '流程编号' + '<font color=red>*</font>',
            value: '',
            allowBlank: false
        });
        me.basicinfofieldset.add(me.code);
        //名称
        me.processname = Ext.widget('textfield', {
			fieldLabel : '流程名称' + '<font color=red>*</font>',
			allowBlank : false,
			value : '',
			name : 'name'
        });
        me.basicinfofieldset.add(me.processname);
        /*处理状态 store */
        me.dealStatusStore=Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			data : [
				{'id':'N', 'name':'未开始'},
		        {'id':'H', 'name':'处理中'},
		        {'id':'F', 'name':'可调用'}
			]
		});
        /*处理状态*/
		me.dealStatus = Ext.widget('combobox',{
		    fieldLabel: '处理状态' + '<font color=red>*</font>',
			store :me.dealStatusStore,
			emptyText:'请选择',
			allowBlank : false,
			valueField : 'id',
			name:'dealStatus',
			displayField : 'name',
			editable : false
		});
		
		me.basicinfofieldset.add(me.dealStatus);
        /*责任部门  */
		me.processDepart = Ext.create('Ext.ux.form.OrgEmpSelect', {
			fieldLabel : '责任部门' + '<font color=red>*</font>',
			name:'orgId',
			type : 'dept',
			allowBlank : false,
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
        me.basicinfofieldset.add(me.processDepart);
        /*员工单选 */
		me.processradio = Ext.create('Ext.ux.form.OrgEmpSelect', {
			fieldLabel : '责&nbsp;&nbsp;任&nbsp;人'+ '<font color=red>*</font>',
			name:'empId',
			type : 'emp',
			allowBlank : false,
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
        me.basicinfofieldset.add(me.processradio);
		/*流程发生频率*/
		me.frequency = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'frequency',
			dictTypeId : 'ic_control_frequency',
			multiSelect : false,
			allowBlank : false,
			labelAlign : 'left',
			fieldLabel : '发生频率' + '<font color=red>*</font>'
		});
		me.basicinfofieldset.add(me.frequency);
        //描述
        me.desc = Ext.widget('textareafield', {
			height:60,
			rows : 3,
			fieldLabel : '流程描述',
			allowBlank : true,
			value : '',
			name : 'desc'
        });
        me.attachmentfieldset.add(me.desc);
        //控制目标
        me.controlTarget = Ext.widget('textareafield', {
			height:60,
			rows : 3,
			fieldLabel : '控制目标',
			allowBlank : true,
			value : '',
			name : 'controlTarget'
        });
        me.attachmentfieldset.add(me.controlTarget);
        /*相关部门  */
		me.relaProcessDepart = Ext.create('Ext.ux.form.OrgEmpSelect', {
			fieldLabel : '相关部门',
			name:'relaOrgId',
			type : 'dept',
			multiSelect : true,
			growMin: 75,
			growMax: 120,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true
		});
        me.attachmentfieldset.add(me.relaProcessDepart);
        //制度选择
//		me.ruleselector = Ext.create('FHD.ux.rule.RuleSelector', {
//			extraParams : {
//				smIconType : 'display',
//				canChecked : true
//			},
//			name : 'ruleId',
//			value:'',
//			multiSelect:true,
//			fieldLabel : $locale('fhd.icm.rule.ruleSelectorLabelText')
//		});
//		me.attachmentfieldset.add(me.ruleselector);
        /*重要性*/
		me.importance = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'importance',
			dictTypeId : 'ic_processure_importance',
			multiSelect : false,
			labelAlign : 'left',
			fieldLabel : '重&nbsp;&nbsp;要&nbsp;&nbsp;性'
		});
		me.attachmentfieldset.add(me.importance);
		/* 影响财报科目 */
		me.relaSubject = Ext.create('FHD.ux.dict.DictSelectForEditGrid', {
			name : 'relaSubject',
			dictTypeId : 'ic_rela_subject',
			fieldLabel : '影响财报科目',
			labelAlign : 'left',
			columnWidth: .5,
			multiSelect : true
		});
		me.attachmentfieldset.add(me.relaSubject);   
		//排序
		me.sort = Ext.widget('numberfield', {
			fieldLabel : '排&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;序',
			name : 'sort'
		});
		me.attachmentfieldset.add(me.sort);
		//附件
        me.attachment = Ext.create('FHD.ux.fileupload.FileUpload', {
			labelAlign : 'left',
			labelText : '附件',
			labelWidth : 100,
			columnWidth: 1,
			name : 'fileId',
			height: 50,
			showModel : 'base'
		});    
        me.attachmentfieldset.add(me.attachment);
    },
    reLoadData: function(store, record, parentNode) {
    	var me = this;
		if (record.parentNode == null) {
			 return;//如果是根节点直接返回
		}
    	var id = record.data.id;
        var name = record.data.text;
        var parentId = parentNode.data.id;
        var parentName = parentNode.data.text;
        
        me.parentprocessId.setValue(parentId);
        me.parentprocess.setValue(parentName);
        me.processname.setValue(name);
        me.load({
            url: __ctxPath + '/process/process/editProcess.f',
            params: {
                processEditID: id
            },
            success: function (form, action) {
            	//手动设置控件的值 	
            	if(action.result.data.orgId){
            		me.processDepart.setValues(Ext.JSON.decode(action.result.data.orgId));
            	}
	        	if(action.result.data.empId){
	        		me.processradio.setValues(Ext.JSON.decode(action.result.data.empId));
	        	}
	        	if(action.result.data.relaOrgId){
	        		me.relaProcessDepart.setValues(Ext.JSON.decode(action.result.data.relaOrgId));
	        	}
                return true;
            }
        });
    },
    reloadData: function() {
    	var me = this;
        me.load({
            url: __ctxPath + '/process/process/constructplaneditProcess.f',
            params: {
                processEditID: me.paramObj.processId
            },
            success: function (form, action) {
            	//手动设置控件的值 	
            	if(action.result.data.orgId){
            		me.processDepart.setValues(Ext.JSON.decode(action.result.data.orgId));
            	}
	        	if(action.result.data.empId){
	        		me.processradio.setValues(Ext.JSON.decode(action.result.data.empId));
	        	}
	        	if(action.result.data.relaOrgId){
	        		me.relaProcessDepart.setValues(Ext.JSON.decode(action.result.data.relaOrgId));
	        	}
                return true;
            }
        });
    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            autoScroll: true,
            border: me.border,
            layout: 'column',
            width: FHD.getCenterPanelWidth() - 258,
            bodyPadding: "0 3 3 3"
		 });
        me.callParent(arguments);
       //向form表单中添加控件
	    me.addComponent();
	},
	bbar: {
		items: [ '->',{
		    text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.save"), //保存按钮
		    iconCls: 'icon-control-stop-blue',
		    handler: function () {
		       var me = this.up('panel');
		       me.save();
		       }
			}
		]
	},
	save: function() {
	    var me = this;
	    var processForm = me.getForm();
	    //me.processDepart.renderBlankColor(me.processDepart);
        //me.processradio.renderBlankColor(me.processradio);
	    
	    //判断树中是否有选中的元素
		var selectId = me.up('flowmainmanage').flowtree.selectId;   
		if(selectId == ''){
			Ext.Msg.alert("注意","请选择一个且唯一一个流程进行流程的节点和风险维护!");
			return false;
		}
		
    	if(processForm.isValid()) {
    		FHD.submit({
				form : processForm,
				params : {
					parentId: me.parentprocessId.getValue()
				},
				url : __ctxPath + '/process/process/saveProcess.f',
				callback: function (data) {
					if(!data.success){
						if(data.info){
							Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),data.info);
						}else if(data.pointInfo){
							Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),data.pointInfo);
						}
						return true;
					}
				}
			});
		}
	},
	clearFormData:function(){
		var me = this;
		me.getForm().reset();
		me.relaProcessDepart.clearValues();
		me.processDepart.clearValues();
		me.processradio.clearValues();
	}
});