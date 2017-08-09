/**
 *    @description 录入内控标准FORM 
 *    
 *    @author 元杰
 *    @since 2013-3-5
 */
Ext.define('FHD.view.icm.standard.StandardControlEdit', {
	extend : 'Ext.form.Panel',
	alias : 'widget.standardcontroledit',
	items:[],
	frame: false,
    border : false,
	requires: [
    	'FHD.view.process.ProcessMainPanel'	
    ],
	layout : {
		type : 'column'
	},
	defaults : {
		columnWidth : 1/1
	},
	bodyPadding:'0 3 3 3',
	autoScroll : true,
	collapsed : false,
	//传递的参数对象
	paramObj:{
		standardControlId : ''
	},
	initParam:function(paramObj){
		var me = this;
		me.paramObj = paramObj;
	},    
	reloadData: function() {
       var me = this;
       me.load({
           waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
           url: __ctxPath + '/icm/standard/findstandardControlById.f',
           params: {
               standardControlId: me.paramObj.standardControlId
           },
           success: function (form, action) {
           		var values = me.down('[name=cstandardControlPointHidden]').getValue().split(',');
           		me.standardControlPoint.setValue(values[0]);
           		
           	    //手动设置控件的值
           		if(!me.standardDepart.isHidden()){
           			if(action.result.data.cdeptId){
           				me.standardDepart.setValues(Ext.JSON.decode(action.result.data.cdeptId));
           			}
           		}
           		if(!me.standardSubCompany.isHidden()){//只有显示时才初始化，否则报错
           			if(action.result.data.csubCompanyId){
           				me.standardSubCompany.setValues(Ext.JSON.decode(action.result.data.csubCompanyId));
           			}
           		}
           		
               	return true;
           }
        });
    },
    select1 : function(tt){
	    var me = this;
	    var array = me.up('standardapply').selectArray;//standardapply中存放standardcontroledit的数组
	   	if(tt){
	   		array.push(me.id);//me.id 形式  standardcontroledit0
	   	}else{
	   		Ext.Array.remove(array, me.id);
	   	}
    },
	initComponent :function() {
	  var me = this;
	  //ID
	  me.sandardId = {
			xtype : 'hidden',
			name : 'cid'
	  };
	  
	  //内部控制要求
	  me.standardName = Ext.create('Ext.form.field.TextArea', {
			labelWidth :80,
			disabled : false,
			lblAlign:'rigth',
			fieldLabel : '内控要求' + '<font color=red>*</font>',
			name : 'cname',
			margin: '7 10 10 30',
			maxLength : 200,
			columnWidth:1
		});
		/*是-适用范围  */
		me.standardSubCompany= Ext.create('Ext.ux.form.OrgEmpSelect',{
         	fieldLabel : '适用范围' + '<font color=red>*</font>',
         	companyOnly : true,
         	rootVisible : false,
         	multiSelect : true,
         	height : 60,
         	type : 'dept',
         	name:'csubCompanyId',
         	margin: '7 10 10 30',
         	labelWidth : 75,
         	hidden : true,
         	columnWidth:.5,
         	growMin: 75,
            growMax: 120,
            store: [],
            queryMode: 'local',
            forceSelection: false,
            createNewOnEnter: true,
            createNewOnBlur: true,
            filterPickList: true
         });
       
        /*否-责任部门  */
		me.standardDepart=Ext.create('Ext.ux.form.OrgEmpSelect',{
         	fieldLabel : '责任部门' + '<font color=red>*</font>',
         	name:'cdeptId',
         	margin: '7 10 10 30',
         	type : 'dept',
         	labelWidth : 75,
         	hidden : false,
         	columnWidth:.5,
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
             
       	/*否-对应流程  */
		me.standardRelaProcess = Ext.create('FHD.ux.process.ProcessSelector',{
		    name:'cprocessId',
		    labelWidth : 75,
		    parent : false,
		    value:'',
		    columnWidth:.8,
		   	margin: '0 0 0 0',
         	height : 25,
		    fieldLabel:'选择流程 ',
         	hidden : false,
            multiSelect : false,
		    extraParam : {
		    	smIconType:'display',
		    	canChecked:true,
		    	leafCheck : true
		    }
         });
         
        //编辑流程按钮
		me.editProcessureButton = {
	        xtype:'button',
	        width: 30,
	        name : 'editProcessureButton',
	        text:'流程编辑 ',
	        columnWidth:.2,
	        height: 22,
	        handler:function(){
	        	me.processMainPanel = Ext.widget('processmainpanel', {});
				me.win = Ext.create('FHD.ux.Window', {
					title : '流程编辑',
					closable : true,
					maximizable: true,
					items : [me.processMainPanel]  //ITEMS里面是弹出窗体所包含的PANEL
				}).show();
//				me.win.setVisible(true); //设置可见
	        }
		};
		
        /*否-内控要素 */
		me.standardControlPoint=Ext.create('FHD.ux.dict.DictSelect',{
		     name:'cstandardControlPoint',
		     dictTypeId:'ic_control_point',
		     margin: '7 10 10 30',
		     labelWidth : 80,
		     labelAlign:'left',
		     multiSelect:false,
		     editable:false,
         	 hidden : false,
         	 columnWidth:.5,
         	 value:'',
		     fieldLabel : '内控要素' + '<font color=red>*</font>' ,
		     onChange :function(nValue,oValue){
		     	if('ic_control_point_c' == nValue){//控制活动ic_control_point_c
		     		me.standardRelaProcess.label.setText('选择流程' + '<font color=red>*</font>:', false);
		     	}else{
		     		me.standardRelaProcess.label.setText('选择流程 :', false);
		     	}
         	 }
	     });
	     
	     /*控制层级 */
		 me.standardControlLevel=Ext.create('FHD.ux.dict.DictSelect',{
		     name:'ccontrolLevelId',
		     dictTypeId:'ic_control_level',
		     margin: '7 10 10 30',
		     labelWidth : 80,
		     labelAlign : 'left',
		     columnWidth:.5,
		     editable:false,
		     value:'',
		     multiSelect:false,
		     fieldLabel : '控制层级 '
		 });
//	     //处理状态-第4步显示
//		 me.standardStatus=Ext.create('FHD.ux.dict.DictSelectForEditGrid',{
//		     name:'statusId',
//		     dictTypeId:'ic_control_standard_estatus',
//		     margin: '7 10 10 30',
//		     labelWidth : 80,
//		     columnWidth:.5,
//		     labelAlign:'left',
//		     value:'',
//		     varlue:'请选择',
//		     multiSelect:false,
//		     fieldLabel : '处理状态' + '<font color=red>*</font>'
//	     });
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
	            store : [['U', '待更新'],['O', '已纳入内控手册运转']]
	        });
	     /*内控要素 隐藏域*/
	     me.cstandardControlPointHidden={
	     	xtype:'hidden',
	     	name:'cstandardControlPointHidden'
	     }
	     //是否适用于下级单位Radio
		me.inferiorRadio = Ext.create('FHD.ux.dict.DictRadio', {
		    margin: '7 10 0 30',
			labelWidth:80,
			labelAlign:'left',
			fieldLabel:'适用下级单位 ',
			dictTypeId:'0yn',
			columnWidth:1,
			defaultValue :'0yn_n',
			name : 'inferior'
		});
		
		//内部要求反馈意见
	 	me.standardControlAdvice = {
			labelWidth :80,
			xtype : 'displayfield',
			labelAlign:'left',
			fieldLabel : '反馈意见 ',
			name : 'cstandardControlAdvice',
			columnWidth:1
		};
		
   	    //内控标准的审批意见fieldset
        me.standardControlAdviceField = Ext.widget('fieldset',{
			defaults : {
				columnWidth : 1/2
			},//每行显示一列，可设置多列
			layout : {
				type : 'column'
			},
			name : 'advicefieldset',
			border: 0,
			margin: '7 10 10 20',
			items:[me.standardControlAdvice]
            });
		
		me.processFieldset = {
			xtype : 'fieldset',
			columnWidth : 1/2,
			defaults : {
				columnWidth : 1
			},//每行显示一列，可设置多列
			layout : {
				type : 'column'
			},
			//height:30,
			margin: '7 0 0 20',
			border : 0,
			items:[me.standardRelaProcess,me.editProcessureButton]
        };
         
        me.controlBasicInfo = {
			xtype : 'fieldset',
			defaults : {
				columnWidth : 1/1
			},//每行显示一列，可设置多列
			layout : {
				type : 'column'
			},
			collapsed : false,
			margin: '8 -9 0 7',
			collapsible : false,
			title : '<input id="c1c"  type="checkbox" onclick="Ext.getCmp(\''+me.id+'\').select1(this.checked)"/>要求',
			items:[me.sandardId,me.standardName,me.cstandardControlPointHidden,
					me.inferiorRadio,me.standardSubCompany,me.standardDepart,me.processFieldset,me.standardControlPoint,
					me.standardControlLevel, me.standardStatus, me.standardControlAdviceField
			]
            }
        
		me.items= [me.controlBasicInfo];
		
		if('4' == me.step){
			me.controlBasicInfo.title = '要求';
			me.standardControlAdviceField.show();
			me.inferiorRadio.hide();
			me.standardStatus.show();//标准提交前最后一个可编辑步骤，第4步，显示
			me.standardControlLevel.show();
		}else{
			me.standardControlAdviceField.hide();
			me.inferiorRadio.show();
			me.standardStatus.hide();
			me.standardControlLevel.hide();
		}
		
		Ext.applyIf(me,{
			items:me.items
		});
		me.callParent(arguments);
		me.inferiorRadio.on('change',function(t,newValue, oldValue,op){
			var me = this.up('fieldset');
			if(newValue.inferior=='0yn_n'){//否
				me.down('[name=csubCompanyId]').hide();
				me.down('[name=cdeptId]').show();
				me.down('[name=cprocessId]').show();
				me.down('[name=cstandardControlPoint]').show();
				me.down('[name=editProcessureButton]').show();
			}else if(newValue.inferior=='0yn_y'){//是
				me.down('[name=csubCompanyId]').show();
				me.down('[name=cdeptId]').hide();
				me.down('[name=cprocessId]').hide();
				me.down('[name=cstandardControlPoint]').hide();
				me.down('[name=editProcessureButton]').hide();
			}
		});
	}
});