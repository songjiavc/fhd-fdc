/**
 * 文档库基本信息编辑页面
 * 
 * @author 
 */
Ext.define('FHD.view.sys.documentlib.DocumentLibEditPanel', {
	extend: 'Ext.form.Panel',
	alias: 'widget.documentLibEditPanel',
	requires:[],
	
	//保存方法
	save: function() {
	   var me = this;
	   var form = me.getForm();
	   if(form.isValid()){
		   FHD.submit({
			   form: form,
			   url: __ctxPath + '/sys/document/savedocument.f',
		       params: {
		       		docId: me.docId
		       },
		       callback: function (data) {
	       			if(data){//提示保存成功
	       				me.up('documentLibCard').documentLibGrid.store.load();
	       				me.backGrid();
	       			}
		       }
		   });
	   }
	   
	},
	//返回
	backGrid: function(){
		var me = this;
		me.up('documentLibCard').showDocumentLibGrid();
	},
	
	clearFormData:function(){
		var me = this;
		me.getForm().reset();
	},
	//加载表单数据
	reLoadData: function(docId) {
		var me = this;	
		me.load({
            url: __ctxPath + '/sys/document/finddocumentlibbyid.f',
            params: {
                docId: docId
            },
            failure:function(form,action) {
    	        alert("加载失败!");
    	    },
            success: function (form, action) {
                var formValue = form.getValues();
                me.parentDocType.setValue(formValue.dictEntryId);
                //手动设置控件的值
                if(action.result.data.orgId){
                    me.deptSelect.setValues(Ext.JSON.decode(action.result.data.orgId));
                }
            }
        });
	},
       
    // 初始化方法
	initComponent: function() {
        var me = this;
//		me.labelBar = Ext.create("FHD.view.risk.responseplan.WorkFlowNorthBar",{labels : ['<font color="red" size="16pt">aa/</font>','bb/','cc/']});
        //所属分类
        me.parentDocType = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'dictEntryId',
			dictTypeId : me.typeId,
			editable: false,
			labelAlign : 'left',
			fieldLabel : "所属分类"+'<font color=red>*</font>',
			multiSelect : false
		});
		
		//编号
		me.docCode = Ext.widget('textfield', {
		    fieldLabel:"文件编号",
		    name:'documentCode'
		});
		
		//文件名称
		me.docName = Ext.widget('textfield', {
		    fieldLabel:"文件名称"+'<font color=red>*</font>',
		    allowBlank:false,
		    name:'documentName'
		});
		
		//描述
		me.docDesc=Ext.widget('textarea', {
	            fieldLabel: "描述",
	            name:"desc",
	            labelSepartor: "：",
	            value:""
	    });
	    //排序
	    me.docSort=Ext.widget('numberfield', {
	            fieldLabel:"排序",
	            minValue:0,  
	            name:"sort",
	            value:""
	    });
	    //责任部门
	    me.deptSelect=new Ext.create('Ext.ux.form.OrgEmpSelect',{
	    		name:'orgId',
	        	fieldLabel : '责任部门',
	        	type : 'dept',
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
		
		//附件
		me.attachment = Ext.create('FHD.ux.fileupload.FileUpload', {
			margin: '7 30 10 30',
			labelAlign : 'left',
			labelText: '附件',
			fieldLabel : '附件',
			labelWidth : 100,
			multiSelect: false,//是否多选
			height: 50,
			name : 'fileIds',
			showModel : 'base'
		});    
		
		   //基本信息fieldset
		me.basicinfofieldset = Ext.widget('fieldset', {
		    flex:1,
		    collapsible: true,
		    autoHeight: true,
		    autoWidth: true,
		    defaults: {
		        columnWidth : 1 / 2,
		        margin: '7 30 3 30',
		        labelWidth: 95
		    },
		    layout: {
		        type: 'column'
		    },
		    title: '基本信息',
		    items:[me.docName, me.docCode, me.parentDocType, me.deptSelect, me.docSort]
		});
		//描述fieldSet
		me.docDescfieldset = Ext.widget('fieldset', {
		    flex:1,
		    collapsible: true,
		    autoHeight: true,
		    autoWidth: true,
		    defaults: {
		        columnWidth : 1 ,
		        margin: '7 30 3 30',
		        labelWidth: 95
		    },
		    layout: {
		        type: 'column'
		    },
		    title: '文档描述',
		    items:[me.docDesc]
		});
		
		//附件信息fieldset
		me.attachmentfieldset = Ext.widget('fieldset', {
		    flex:1,
		    collapsible: true,
		    autoHeight: true,
		    autoWidth: true,
		    defaults: {
		       	columnWidth : 1 / 1,
		       	margin: '7 30 3 30',
		        labelWidth: 95
		    },
		    layout: {
		        type: 'column'
		    },
		    title: '附件信息',
		    items:[me.attachment]
		});
           
	   	Ext.applyIf(me, {
	       	autoScroll: true,
	       	border : false,
	       	layout: 'column',
		  	bodyPadding: "0 3 3 3",
		  	items:[me.basicinfofieldset, me.docDescfieldset, me.attachmentfieldset],
		  	bbar: {items: [ '->',{
				            text: '返回', //保存按钮
				            iconCls: 'icon-control-repeat-blue',
				            handler: function () {
				               me.backGrid();
				            }
				        },{text: '保存', //保存按钮
				            iconCls: 'icon-control-stop-blue',
				            handler: function () {
				              me.save();
				            }
				        }
		  			]
	   		}
	   	});

   		me.callParent(arguments);
	}
	   
});