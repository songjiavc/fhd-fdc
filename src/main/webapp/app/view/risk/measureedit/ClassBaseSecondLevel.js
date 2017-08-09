/**
 * 
 * 风险上报标准
 * 使用card布局
 * 
 * 下级有两个组件 潜在风险上报标准、历史风险上报标准
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.risk.measureedit.ClassBaseSecondLevel', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.classbasesecondlevel',
    requires: [
    	'FHD.view.risk.measureedit.LevelSetPlanWindow'
    ],
    secondnamefield : '',
    secondhidfield : '0',
    secondtextfield : '',
	autoHeight: true,
	layout: {
        type: 'hbox'
    },
    type : '',
	initComponent: function() {
		//定量
		var me = this;
		me.addSetPlanBtn = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").setPlan()'>设定"+me.btnName+"/</a>"
        });
		me.addLookPlanBtn = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").lookPlan()'>查看"+me.btnName+"</a>"
        });
		//定量fieldset
        me.secondTextField = Ext.widget('textfield',{
        	value : me.secondnamefield,
        	readOnly : true,
        	name : 'secondnamefield'
        });
		//定量fieldset
        me.desc = Ext.widget('textfield',{
        	value : me.secondtextfield,
        	name : 'secondtextfield'
        });
        if(me.secondhidfield == '0'){
	        me.secondlabel = Ext.widget('label',{
	        	html : '<font color=red>(未绑定方案)</font>',
	        	name : 'secondlabel'
	        })
        }else{
        	me.secondlabel = Ext.widget('label',{
	        	html : '<font color=green>(已绑定方案)</font>',
	        	name : 'secondlabel'
	        })
        }
        me.secondhidfield = Ext.widget('hiddenfield',{
        	value : me.secondhidfield,
        	name : 'secondhidfield'
        });
        Ext.apply(me, {
            items: [
               {
               		xtype : 'image',
               		src : __ctxPath+'/images/makegrid.png'
               },
               me.secondTextField,
               me.desc,
               me.secondlabel,
               me.secondhidfield,
               {
                   xtype:'tbspacer',
                   flex : 1
                },
               me.addSetPlanBtn,
               me.addLookPlanBtn
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
    
    lookPlan : function(){
    	var me = this;
    	if(me.secondhidfield.getValue() != '0'){
    		//已设定方案
    	
    	}
    },
    
    changeHidField : function(id){
    	var me = this;
    	me.secondlabel.update('<font color=green>(已绑定方案)</font>');
    	me.secondhidfield.setValue(id);
    }
    
    
});