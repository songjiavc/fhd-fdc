/**
 * 风险版本基本信息编辑页面
 * 
 * @author 
 */
Ext.define('FHD.view.risk.riskversion.RiskVersionForm', {
	extend: 'Ext.form.Panel',
	alias: 'widget.riskversionform',
	requires:[],
	
	//保存方法
	save: function() {
	   var me = this;
	   var form = me.getForm();
	   if(form.isValid()){
		   FHD.submit({
			   form: form,
			   url: __ctxPath + '/risk/riskhistory/saveriskversion.f',
		       params: {
		       		verId: me.verId,
		       		schm: me.typeId
		       },
		       callback: function (data) {
	       			if(data.success){//提示保存成功
	       				me.up('riskversioncardmain').versionGrid.store.load();
	       				me.backGrid();
	       			}
		       }
		   });
	   }
	   
	},
	//返回
	backGrid: function(){
		var me = this;
		me.up('riskversioncardmain').showVersionGrid();
	},
	
	clearFormData:function(){
		var me = this;
		me.getForm().reset();
	},
	//加载表单数据
	reLoadData: function(verId) {
		var me = this;	
		me.load({
            url: __ctxPath + '/risk/riskhistory/findriskversionbyid.f',
            params: {
            	verId: me.verId
            },
            failure:function(form,action) {
    	        alert("加载失败!");
    	    },
            success: function (form, action) {
                var formValue = form.getValues();
                me.versionName.setValue(formValue.versionName);
                me.versionDesc.setValue(formValue.desc);
            }
        });
	},
       
    // 初始化方法
	initComponent: function() {
        var me = this;
		
		//版本名称
		me.versionName = Ext.widget('textfield', {
		    fieldLabel:"版本名称"+'<font color=red>*</font>',
		    allowBlank:false,
		    name:'versionName'
		});
		
		//描述
		me.versionDesc=Ext.widget('textarea', {
	            fieldLabel: "描述",
	            name:"desc",
	            labelSepartor: "：",
	            value:""
	    });
	  
           
	   	Ext.applyIf(me, {
	       	autoScroll: true,
	       	border : false,
	       	autoDestroy: true,
	       	layout: 'anchor',
	        defaults: {
	            anchor: '100%'
	        },
	        bodyPadding: "10 5 5 10",
		  	items:[me.versionName, me.versionDesc],
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