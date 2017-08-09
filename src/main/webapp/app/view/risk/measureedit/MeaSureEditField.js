Ext.define('FHD.view.risk.measureedit.MeaSureEditField', {
	extend: 'Ext.form.Panel',
	alias: 'widget.measureeditfield',
    layout: {
        type: 'column'
    },
    defaults :{
    	margin: '7 30 3 30',
    	cloumnsWidth : .5
    },
	frame: false,
	autoHeight : true,
	border : false,
	
    initComponent: function() {
        var me = this;
		me.name = Ext.widget('textfield',{
			name : 'name',
			value : '',
			allowBlank: false,
			fieldLabel : '名称' + '<font color=red>*</font>',
			columnWidth : 1
		});
		//流程选择组建
        me.process = Ext.create('FHD.ux.process.ProcessSelector', {
		 	labelWidth: 95,
		 	gridHeight:25,
		 	btnHeight:25,
		 	btnWidth:25,
		 	single : true,
		 	fieldLabel: '选择流程' + '<font color=red>*</font>',
            margin: '7 30 3 30', 
            name: 'process',
            multiSelect:false,
            checked : true,
            columnWidth : .5
        });
        
        me.process.field.on('change',function(field,newValue,oldValue,eOpts ){
			me.process.initValue(newValue);
        	me.processPoint.clearValue();
        	if(me.process.getValue()!=''){
				me.processStore.proxy.extraParams.proId = me.process.getValue();
				me.processStore.load();
			}
        });
        //子流程选择空间store
        me.processStore = Ext.create('Ext.data.Store', {
    	    fields: ['id', 'text'],
    	    proxy: {
    	         type: 'ajax',
    	         url: 'chf/risk/measure/findprocessnote.f',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:false
    	});
        //子流程选择控件
        me.processPoint = Ext.create('Ext.form.field.ComboBox',{
			fieldLabel : '对应的流程节点' + '<font color=red>*</font>',
			name : 'processPoint',
			store :me.processStore,
			valueField : 'id',
			displayField : 'text',
			allowBlank : false,
			multiSelect : false,
			editable : false,
			columnWidth : .5
		});
		
		//控制措施内容
		me.desc = Ext.widget('textareafield', {
			height:60,
			rows : 3,
			fieldLabel : '控制措施内容' + '<font color=red>*</font>',
			value : '',
			allowBlank: false,
			name : 'desc',
			columnWidth : 1
        });
        
        //责任岗位
//		me.sysposi = Ext.widget('textfield',{
//			name : 'sysposi',
//			value : '',
//			fieldLabel : '责任岗位',
////			allowBlank: false,
//			columnWidth : .5
//		});
        
		//频率选择
        me.radiofield1 = Ext.widget('radiofield', {
             boxLabel: '日常持续执行',
             name : 'round',
             checked : true,
             inputValue : '0',
             columnWidth : .85,
             handler : function(radio){
             	if(me.radiofield3.getValue() == true){
             		me.fieldsetQuantification.setVisible(true);
             	}else{
             		me.fieldsetQuantification.setVisible(false);
             	}
             }
        });
        //频率选择
        me.radiofield2 = Ext.widget('radiofield', {
             boxLabel: '定期执行',
             name : 'round',
             inputValue : '1',
             columnWidth : .2,
             handler : function(){
             	if(me.radiofield3.getValue() == true){
             		me.fieldsetQuantification.setVisible(true);
             	}else{
             		me.fieldsetQuantification.setVisible(false);
             	}
             }	
        });
        //频率选择
        me.radiofield3 = Ext.widget('radiofield', {
		 	 fieldLabel: '',
             boxLabel: '按条件启动',
             name : 'round',
             inputValue : '2',
             columnWidth : .2,
             handler : function(radio){
             	if(me.radiofield3.getValue() == true){
             		me.fieldsetQuantification.setVisible(true);
             	}else{
             		me.fieldsetQuantification.setVisible(false);
             	}
             }
        });
        me.regExecuted = Ext.widget('textfield',{
        	name : 'regExecuted',
        	columnWidth : .65
        });
        me.conExecuted = Ext.widget('textfield',{
        	name : 'conExecuted',
        	columnWidth : .65
        });
    	me.quantificationContainer = Ext.widget('quantification',{name: 'quantificationContainer'});
        me.addQuantitative = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").addQt()'>增加</a>"
        });
    	me.fieldsetQuantification = Ext.widget('fieldset', {
    		hidden : true,
            layout : {
                type: 'vbox',
                algin: 'stretch'
            },
            border : false,
            columnWidth : .85,
            items : [me.quantificationContainer,me.addQuantitative]
        });
        Ext.applyIf(me, {
            items: [
                me.name,
                me.process,
                me.processPoint,
               	me.desc,
//               	me.sysposi,
               	{
					xtype : 'hiddenfield',
					columnWidth : 1
               	},
               	{
					xtype : 'label',
					text : '频率:',
					columnWidth : .15
				},me.radiofield1,
               	{
					xtype : 'hiddenfield',
					columnWidth : .15
               	},me.radiofield2,me.regExecuted,
               	{
					xtype : 'hiddenfield',
					columnWidth : .15
				},me.radiofield3,me.conExecuted,
				{
					xtype : 'hiddenfield',
					columnWidth : .15
				},me.fieldsetQuantification
            ]
        });

        me.callParent(arguments);
    },
    
    addQt : function(){
    	var me = this;
    	me.fieldsetQuantification.insert(me.fieldsetQuantification.items.length-1,Ext.widget('quantification'));
    	
    },
    
    save : function(){
    	var me = this;
    	var id = me.up('riskMainPanel').riskTree.face.nodeId;
    	if(id == ''){
    		alert('请先进行风险选择');
    		return false;
    	}
    	var form = me.getForm();
    	if(form.isValid()){
    		FHD.submit({
		        form : form,
		        url : 'chf/risk/measure/savenowmeasure.f',
		        params : {
                	riskid : id,
                	csid : me.csid
                },
		        callback : function(date){
		            if(date){
						Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.common.operateSuccess'));
		            }
		        }           
		    });
		    return true;
    	}else{
    		return false;
    	}
    	
    },
    
	pointQuery : function(){
    	var me = this;
		if(me.process.getValue()!=''){
			me.processStore.proxy.extraParams.proId = me.process.getValue();
			me.processStore.load();
		}
    },
    
    loadForm : function(){
		var me = this;
		var id = me.up('riskMainPanel').riskTree.face.nodeId;
		if(id != '') {
    		me.form.load({
    	        url:'chf/risk/measure/nowmeasureloadform.f',
    	        params:{riskid:id},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        	pointQuery();
    	        }
    	    });
    	}
	}

});