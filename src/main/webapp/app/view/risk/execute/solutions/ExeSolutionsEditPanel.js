/**
 * 控制措施编辑面板
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.execute.solutions.ExeSolutionsEditPanel',{
    extend : 'Ext.form.Panel',
    alias: 'widget.exesolutionseditpanel',
    autoScroll:true,
    exeId : '',//执行应对措施ID
    layout: {
                type : 'column'
            },
    defaults : {
        columnWidth : 1 / 1
    },
    bodyPadding:'0 60 3 60',
    border:false,
    initComponent : function() {
        var me = this;

        me.bbar=['->',{
        		iconCls : 'icon-arrow-undo',
        		text:'返回',
        		handler : function(){
	        		me.backPage();
        		}
        	},'-',{
        		iconCls : 'icon-save',
        		text:'保存',
        		handler : function(){
        			me.savePage();
        		}
        	},'-',{
        		iconCls : 'icon-operator-submit',
        		text:'提交',
        		handler : function(){
        			me.subPage();
        		}
        }]
        me.activeFinishDate = Ext.widget('datefield', {
            xtype: 'datefield',
            format: 'Y-m-d',
            name: 'activeFinishDate',
            margin: '7 10 10 20',
            fieldLabel: '实际完成时间', 
            width:400,
            value : new Date(),
            allowBlank: false
        });
        me.desc = Ext.widget('textareafield', {
            fieldLabel:'工作开展情况描述',
            labelAlign : 'left',
        	margin: '7 10 10 20',
            row : 5,
            width: 1000,
            name : 'desc',
            allowBlank: false,
            labelWidth : 100
        });
		me.actualWork = Ext.create('Ext.form.RadioGroup', {
			fieldLabel: "实际收效评价",
	   		vertical: true,
	   		margin: '7 10 10 20',
	   		width: 400,
	   		allowBlank: false,
        	items: [
            { boxLabel: "很高", name: 'actualWork', inputValue: '0',checked:true,id:'occurposs_0'},
            { boxLabel: "高", name: 'actualWork', inputValue: '1',id:'occurposs_1'},
            { boxLabel: "一般", name: 'actualWork', inputValue: '2',id:'occurposs_2'},
            { boxLabel: "低", name: 'actualWork', inputValue: '3',id:'occurposs_3'},
            { boxLabel: "很低", name: 'actualWork', inputValue: '4',id:'occurposs_4'}]
        });
//        var addQuantification = Ext.widget('label',{
//        	margin: '7 10 0 20',
//			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").addDl'>增加</a>"
//        });
//        var schemeFieldset = Ext.create('Ext.container.Container', {
//           layout:{
//     	    	type:'hbox'  
//     	    },
//           items : [realYields,addQuantification]
//        });
//        
//        var kpifieldarr = Ext.widget('kpifieldarr');
//        me.kpiFieldSet =  Ext.create('Ext.container.Container', {
//           margin: '7 10 0 20',
//           items : [kpifieldarr]
//        });
//		
//        
//        var kpiEtc = Ext.widget('textfield', {
//            xtype: 'textfield',
//            fieldLabel: '其它',
//             margin: '7 10 10 120',
//            value: '',
//            maxLength: 255,
//            width: 500
//        });
//        var realYieldsCon=Ext.create('Ext.container.Container',{
//     	    layout:{
//     	    	type:'vbox'  
//     	    },
//     	    width: 1000,
//     	    items:[schemeFieldset,me.kpiFieldSet,kpiEtc]
//        });
		
        me.actualCost = Ext.create('Ext.form.RadioGroup', {
			fieldLabel: "实际成本",
	   		vertical: true,
	   		margin: '7 10 10 20',
	   		width: 400,
	   		allowBlank: false,
        	items: [
            { boxLabel: "很高", name: 'actualCost', inputValue: '0',checked:true,id:'actualCost_0'},
            { boxLabel: "高", name: 'actualCost', inputValue: '1',id:'actualCost_1'},
            { boxLabel: "一般", name: 'actualCost', inputValue: '2',id:'actualCost_2'},
            { boxLabel: "低", name: 'actualCost', inputValue: '3',id:'actualCost_3'},
            { boxLabel: "很低", name: 'actualCost', inputValue: '4',id:'actualCost_4'}]
        });
        
//        
//		var cost = Ext.widget('textfield', {
//            xtype: 'textfield',
//            fieldLabel: '直接经济成本',
//            margin: '7 10 10 20',
//            maxLength: 255,
//            width: 500
//        });
//        var unit ={
//        	 xtype:'displayfield',
//        	 margin: '7 10 0 20',
//    		 width:40,
//    	     vertical: true,
//    	     value:'万元'
//        };
//        
//        var costCon=Ext.create('Ext.container.Container',{
//        	margin: '20 10 0 0',
//     	    layout:{
//     	    	type:'column'  
//     	    },
//     	    width:1000,
//     	    items:[cost,unit]
//        });
//        var costEtc = Ext.widget('textfield', {
//            xtype: 'textfield',
//            fieldLabel: '其它',
//            margin: '7 10 10 20',
//            value: '',
//            maxLength: 255,
//            width: 500
//        });
//        var costCon=Ext.create('Ext.container.Container',{
//     	    layout:{
//     	    	type:'vbox'  
//     	    },
//     	    width: 1000,
//     	    items:[actualCost,costCon,costEtc]
//        });
//
//          var suggest = {
//            xtype : 'textareafield',
//            fieldLabel:'心得和建议',
//            labelAlign : 'left',
//            margin: '7 10 10 20',
//            row : 5,
//            width: 1000,
//            name : 'requirement',
//            labelWidth : 100
//        };
        
       
        
       
		Ext.applyIf(me, {
            border:false,
            bbar : me.bbar,
            layout: {
                type : 'vbox'
            },
            collapsed : false,
            items:[
	                {
	                    xtype : 'fieldset',
	                    collapsed : false,
	                    collapsible : false,
	                    title : '应对措施执行信息',
	                    items : [
	                         me.activeFinishDate,me.desc,me.actualWork,me.actualCost]
	                }
            	]
	        });
	        
	        me.callParent(arguments);
	        
	        if(me.executionId){
	            me.add(approvalIdeaFieldSet);
	        }
		},
    savePage:function(){
    	var me = this;
    	var form = me.getForm();
    	if(form.isValid()){
    		FHD.submit({
    			form : form,
    			url : 'chf/execute/solutions/saveexesolutions.f',
    			params : {
    				exeId : me.exeId
    			},
    			callback : function(date){
    				if(date){
    				}
    			}
    		
    		});
    	}
    },
    subPage:function(){
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
						url : 'chf/execute/solutions/subexesolutions.f',
						params : {
							exeId : me.exeId
						},
						callback : function(data){
							me.backPage();
						}
					});
				}
			}
		});
    },
    
    backPage : function(){
    	var me = this;
    	me.cleanValue();
    	me.up('exesolutionsmainpanel').exesolutionsgrid.reloadData();
    	me.up('exesolutionsmainpanel').showexesolutionsgrid();
    },
    
    cleanValue : function(){
    	var me = this;
    	me.exeId = '';
    	me.activeFinishDate.setValue(new Date());
    	me.desc.setValue("");
    	me.actualWork.items.items[0].setValue(true);
    	me.actualCost.items.items[0].setValue(true);
    	me.getForm().clearInvalid();
    },
    
    editLoadForm : function(id){
    	var me = this;
    	me.exeId = id;
		if(typeof(me.exeId) != 'undefined') {
    		me.form.load({
    	        url:'chf/execute/solutions/editloadform.f',
    	        params:{exeId:me.exeId},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        }
    	    });
    	}
    	
    	
    	
    }
 });