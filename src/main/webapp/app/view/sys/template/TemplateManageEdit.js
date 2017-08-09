/**
 * 基本信息编辑页面
 * 
 * @author 
 */
Ext.define('FHD.view.sys.template.TemplateManageEdit', {
	extend: 'Ext.form.Panel',
	alias: 'widget.templateManageEdit',
	requires:[],
	
	//保存方法
	save: function() {
	   var me = this;
	   var form = me.getForm();
	   var requestObj = form.getValues();
	   requestObj.contents = me.editor.html();
	   FHD.ajax({
		   url: __ctxPath + '/sys/templatemanage/savetemplatemanagebyentryid.f',
	       params: {
	       		contentEdit: Ext.JSON.encode(requestObj)
	       },
	       callback: function (data) {
	       		FHD.notification('操作成功！',FHD.locale.get('fhd.common.prompt'));
   				var templateManageCard = me.up('templateManageCard');
   				var nodeId = me.dictEntryId.getValue();
   				templateManageCard.templateManageGrid.reloadData(nodeId);
   				templateManageCard.showTemplateManageGrid();
	       }
	   });
	},
	//返回
	backGrid: function(){
		var me = this;
		me.up('templateManageCard').showTemplateManageGrid();
	},
	
	clearFormData:function(){
		var me = this;
		me.getForm().reset();
		if(me.editor){
			me.editor.html('');
		}
	},
	//加载表单数据
	reLoadData: function(id) {
		var me = this;	
		me.load({
            url: __ctxPath + '/sys/templatemanage/findtemplatemanageformbyid.f',
            params: {
                id: id
            },
            failure:function(form,action) {
    	        alert("加载失败!");
    	    },
            success: function (form, action) {
               	var formValue = form.getValues();
               	if(formValue.content != ''){
		        	me.editor.html(formValue.content);
			    }else{
			    	me.editor.html('');
				}
            }
        });
	},
       
    // 初始化方法
	initComponent: function() {
        var me = this;
        
        //id
        me.idfiled = Ext.widget('hiddenfield',{
            name:"id",
            value:''
	    });
        //数据字典id，隐藏域
        me.dictEntryId = Ext.widget('hiddenfield',{
            name:"dictEntryId",
            value:''
	    });
		//模板名称
		me.templateName = Ext.widget('textfield', {
		    fieldLabel:"模板名称"+'<font color=red>*</font>',
		    allowBlank:false,
		    name:'name'
		});
		
		me.templateContent = Ext.widget('textarea', {
		    fieldLabel:"内容"+'<font color=red>*</font>',
		    //allowBlank:false,
		    name:'content'
		});
		me.templateContent.setHeight(FHD.getCenterPanelHeight()-200);
		
		me.paramGrid = Ext.create('FHD.view.sys.template.TemplateManageParamGrid');
		
		//基本信息fieldset
		me.basicinfofieldset = Ext.widget('fieldset', {
		    flex:2,
		    collapsible: true,
		    autoHeight: true,
		    autoWidth: true,
		    defaults: {
		        columnWidth : 1 / 1,
		        margin: '7 30 3 30',
		        labelWidth: 50
		    },
		    layout: {
		        type: 'column'
		    },
		    title: '基本信息',
		    items:[me.templateName, me.templateContent, me.dictEntryId, me.idfiled]
		});
		
		//参数列表
		me.paramfieldset = Ext.widget('fieldset', {
		    flex:1,
		    collapsible: true,
		    collapsed: true,
		    autoHeight: true,
		    autoWidth: true,
		    layout: {
		        type: 'fit'
		    },
		    title: '参数列表',
		    items:[me.paramGrid]
		});
           
	   	Ext.applyIf(me, {
	       	autoScroll: true,
	       	border : false,
	       	layout: 'column',
		  	bodyPadding: "0 3 3 3",
		  	items:[me.basicinfofieldset, me.paramfieldset],
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
	   		},
	   		listeners:{
            	render:function(){
    		        setTimeout(function(){
    		        	me.editor = KindEditor.create('#' + (me.templateContent.getEl().query('textarea')[0]).id);
    		        	me.editor.resizeType = 1;
    		        });
    	        }  
    		}
	   	});

   		me.callParent(arguments);
   		
	}
	   
});