/**
 * 
 * 风险上报标准
 * 使用card布局
 * 
 * 下级有两个组件 潜在风险上报标准、历史风险上报标准
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.risk.measureedit.ClassBaseFirstLevel', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.classbasefirstlevel',
    requires: [
    	'FHD.view.risk.measureedit.ClassBaseSecondLevel'
    ],
    layout : {
    	type : 'vbox',
    	align : 'stretch'
    },
    btnName : '',
    firsttextfiled : '',
    firsthidfield : '0',
    type : '',
	initComponent: function() {
		//定量
		var me = this;
		//定量fieldset
        me.firstTextField = Ext.widget('textfield',{
        	name : 'firsttextfield',
        	value : me.firsttextfiled,
        	colspan : 5
        });
        
        if(me.firsthidfield == '0'){
	        me.firstlabel = Ext.widget('label',{
	        	html : '<font color=red>(未绑定方案)</font>',
	        	name : 'firstlabel'
	        })
        }else{
        	me.firstlabel = Ext.widget('label',{
	        	html : '<font color=green>(已绑定方案)</font>',
	        	name : 'firstlabel'
	        })
        }
        me.firsthidfield = Ext.widget('hiddenfield',{
        	value : me.firsthidfield,
        	name : 'firsthidfield'
        });
        
        me.addSetPlanBtn = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").setPlan()'>设定"+me.btnName+"/</a>"
        });
		me.addLookPlanBtn = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").lookPlan()'>查看"+me.btnName+"/</a>"
        });
        me.delPlanBtn = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").delSelf()'>删除标准</a>"
        });
   		me.fieldContainer = Ext.widget('fieldcontainer',{
   			layout : {
   				type : 'hbox'
   			},
   			items : [me.firstTextField,me.firstlabel,me.firsthidfield,
            		{
                   		xtype:'tbspacer',
                   		flex : 1
                	},
                	me.addSetPlanBtn,
                	me.addLookPlanBtn,
                	me.delPlanBtn
            		]
   		});
        Ext.apply(me, {
            items: [
            	me.fieldContainer
            ]
        });
        me.callParent(arguments);
    },
    
	setPlan : function(){
    	var me = this;
    	me.window = Ext.widget('levelsetplanwindow',{
    				type : me.type,
					onSubmit:function(win){
						var id = win.plangrid.getSelectionModel().getSelection()[0].data.id;
						if(id != ''){
							me.changeHidField(id);
						}
					}
		}).show();
    },
    
    changeHidField : function(id){
    	var me = this;
    	me.firstlabel.update('<font color=green>(已绑定方案)</font>');
    	me.firsthidfield.setValue(id);
    },
    
    lookPlan : function(){
    	var me = this;
    	if(me.secondhidfield.getValue() != '0'){
    		//已设定方案
    	
    	}
    },
    
    delSelf : function(){
    	var me = this;
//    	me.firstTextField.setValue('');
    	var upPanel = me.up('fieldset');
    	upPanel.remove(me,true);
    }
});