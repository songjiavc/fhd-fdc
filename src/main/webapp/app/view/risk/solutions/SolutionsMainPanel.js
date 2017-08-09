/**
 * 风险应急方案（历史事件）/风险预防方案（潜在风险）主页面
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.solutions.SolutionsMainPanel', {
	extend: 'Ext.form.Panel',
	alias: 'widget.solutionsmainpanel',
    requires: [
    	'FHD.view.risk.solutions.SolutionsGridPanel'
       ],
    preplanId: '',
    iseffective : '',
    type: '',
    frame: false,
    border : false,
	autoHeight : true,
	autoWidth : true,
	autoScroll:true,
	defaults : {
    	margin: '7 30 3 30'
    },
   // 初始化方法
    initComponent: function() {
        var me = this;
        me.saveUrl = 'chf/solutions/savepreplan.f';
        me.name = Ext.widget('textfield',{
        	name : 'name',
        	fieldLabel: '名称',
        	width : 500,
        	value : '',
        	columnWidth : 1,
        	labelAlign : 'right',
        	allowBlank:false
        });
        me.desc = Ext.widget('textareafield',{
        	name : 'desc',
        	fieldLabel: '描述',
        	labelAlign : 'right',
        	row : 5,
        	width : 500,
        	height: 100,
        	colunmWidth : .5,
        	value : ''
        });
       //附件
        me.file = Ext.widget('FileUpload', {
			labelAlign : 'right',
			labelText : '上传附件',
			labelWidth : 100,
			name : 'fileIds',
			height: 100,
			width : 500,
			colunmWidth : .5,
			showModel : 'base'
		});
		me.tbarItems = ['->',{
			iconCls : 'icon-arrow-undo',
			text: '返回',tooltip: '返回',
			handler : function(){me.backPage();},
			scope : this
		}, '-', {
			iconCls : 'icon-save',
			text: '保存',tooltip: '保存',
			handler : function(){me.savePage();},
			scope : this
		}, '-', {
			iconCls : 'icon-operator-submit',
			text: '提交',tooltip: '提交',
			handler : function(){me.subPage();},
			scope : this
		}];
		
		me.fieldSet = Ext.widget('fieldset',{
                title: '案件基本信息',
                collapsible: true,
                margin: '5 5 0 5',
                layout: {
         	        type: 'vbox'
         	    },
         	    items : [me.name,
		         	    {
		         	    	layout: { type:'column' },
		         	    	border : false,
		         	    	items : [me.desc,me.file]
		         	    }]
            });
		
		me.solutionsgridpanel = Ext.widget('solutionsgridpanel',{
			height : FHD.getCenterPanelHeight()/2,
			hidden : true,
			type : me.type,
			preplanId : me.preplanId
		});
		
		me.fieldSet2 = Ext.widget('fieldset',{
			title: '应对措施维护',
            collapsible: true,
            margin: '5 5 0 5',
            layout: {
     	        type: 'form'
     	    },
     	    hidden : true,
     	    items : [me.solutionsgridpanel]
    	});
		
		Ext.applyIf(me,{
			tbar : me.tbarItems,
			items : [me.fieldSet,me.fieldSet2]
			})
  		me.callParent(arguments);
    },
    
    backPage : function(){
    	var me = this;
    	me.cleanValue();
    	me.fieldSet2.setVisible(false);
    	me.solutionsgridpanel.setVisible(false);
    	me.updateLayout();
    	if(me.type!=null && me.type == 'preplan'){
			var preplanmainpanel = me.up('preplanmainpanel');
			preplanmainpanel.solutionsgrid.store.load();
			preplanmainpanel.showSolutionsGrid();
			
		}else{
			var responseplanmainpanel = me.up('responseplanmainpanel');
			responseplanmainpanel.solutionsgrid.store.load();
			responseplanmainpanel.showSolutionsGrid();
		}
    },
    
    savePage : function(){
    	var me = this;
    	var form = me.getForm();
    	if (form.isValid()){
    		FHD.submit({//ajax调用
    			form : form,
    			url : me.saveUrl,
    			params : {
    				id:me.preplanId,
    				type:me.type,
    				iseffective : me.iseffective
    			},
    			callback : function(data){
    				if(data){
    					me.preplanId = data.id;
						me.iseffective = data.iseffective;
    					me.solutionsgridpanel.preplanId = data.id;
    					me.solutionsgridpanel.store.proxy.extraParams.preplanId = data.id;
    					me.solutionsgridpanel.store.load();
				    	me.fieldSet2.setVisible(true);
				    	me.solutionsgridpanel.setVisible(true);
				    	me.updateLayout();
    				}
    			}
    		});
    	}
	},
	
	subPage : function(){
		var me = this;
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : "提交后会保存当前数据且不可进行修改，是否继续",
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {// 确认删除
					me.savePage();
					FHD.ajax({//ajax调用
						url : 'chf/solutions/subpreplan',
						params : {
							id:me.preplanId
						},
						callback : function(data){
							me.backPage();
						}
					});
				}
			}
		});
		
	},
    
    loadForm : function (id,name,desc,iseffective){
    	var me = this;
    	me.preplanId = id;
    	me.iseffective = iseffective;
    	me.name.setValue(name);
    	me.desc.setValue(desc);
    	me.solutionsgridpanel.preplanId = id;
    	me.fieldSet2.setVisible(true);
    	me.solutionsgridpanel.setVisible(true);
    	if(iseffective != '1'){
	    	me.loadUpFile(id);
    	}else{
    		me.loadUpFile();
    	}
    	me.updateLayout();
    },
    
    cleanValue : function(){
    	var me = this;
    	me.preplanId = "";
    	me.iseffective = "";
    	me.name.setValue("");
    	me.name.clearInvalid();
    	me.desc.setValue("");
    	me.solutionsgridpanel.store.removeAll();
    	me.fieldSet2.setVisible(false);
    	me.solutionsgridpanel.setVisible(false);
    	me.loadUpFile();
    	me.updateLayout();
    },
    
    loadUpFile : function(id){
    	var me = this;
    	if(id == null || id == ''){
    		me.file.setValue('');
    	}else{
			me.form.load({
		        url:'chf/solutions/loadupfile',
		        params:{
		        	id:me.preplanId
		        		},
		        failure:function(form,action) {
		            alert("err 155");
		        },
		        success:function(form,action){
		        	var formValue = form.getValues();
		        }
	    	});
    	}
    }
});