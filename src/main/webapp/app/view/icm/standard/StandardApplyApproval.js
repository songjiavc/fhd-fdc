/**
 *    @description 录入内控标准FORM 第二步
 *    
 *    @author 元杰
 *    @since 2013-3-5
 */
Ext.define('FHD.view.icm.standard.StandardApplyApproval', {
	extend : 'Ext.form.Panel',
	alias : 'widget.standardapplyapproval',
    requires: [
    	'FHD.view.comm.bpm.ApprovalIdea',
    	'FHD.view.icm.standard.StandardControlShowOnly'	
    ],
	//title:'申请更新',
	items:[],
	step:'',//五步中的第几步
	isPass : 'yes', // 第二步中可以直接否决标准审批
	layout : {
		type : 'column'
	},
	defaults : {
		columnWidth : 1/1
	},
	bodyPadding:'0 3 3 3',
	collapsed : false,
	autoScroll : true,
	//传递的参数对象
	paramObj:{
		standardControlIds : ''
	},
	initParam:function(paramObj){
		var me = this;
		me.paramObj = paramObj;
	},
    loadData: function(businessId, executionId){
    	var me = this;
    	me.businessId = businessId;
    	me.executionId = executionId;
    	me.reloadData();
    },
    reloadData: function() {
       var me = this;
       me.load({
           waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
           url: __ctxPath + '/icm/standard/findStandardById.f',
           params: {
//               standardId: me.standardId.value,
           	   standardId : me.businessId,
			   executionId : me.executionId,
			   step : me.step
           },
           success: function (form, action){
           		if(null != me.standardControlIds.getValue() && "" != me.standardControlIds.getValue()){
	           		 me.paramObj.standardControlIds = me.standardControlIds.getValue().split(',');
	             	 for(var i = 0;i<me.paramObj.standardControlIds.length;i++){
             	 	 	 me.standardcontrolshowonly = Ext.widget('standardcontrolshowonly',{step:me.step});
	   	    			 me.standardcontrolshowonly.initParam({
	   	    			 	standardControlId : me.paramObj.standardControlIds[i]
	   	    			 });
				 		 me.standardcontrolshowonly.reloadData();
				 		 me.add(me.standardcontrolshowonly);
	             	 }
	             	 me.add(me.isPassField);
	             	 me.add(me.standardAdvice);
	           		 return true;
           	 	}
           }
        });
    },
	save: function() {
		var me = this.up('standardapplyapproval');
		var standardApplyForm = me.getForm();
		if(!standardApplyForm.isValid()){
	        Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'存在未通过的验证!');
	        return ;
        }
        var url = __ctxPath + '/icm/standard/saveStandardAdvice.f';
		FHD.submit({
			form : standardApplyForm,
			url : url,
			params : {
				businessId:me.businessId,
				executionId:me.executionId,
				isPass:me.approvalidea.isPass,
				examineApproveIdea:me.approvalidea.getValue(),
				step:me.step,
				isTerminal : me.isPass//是否直接终止
			},
			callback: function (data) {
					me.closeWin(me.step);
			}
		});
    },
    /**
     * 关闭窗口 
     * 
     */
    closeWin: function(step){
    	var me = this;
    	var standardPanel = null;
    	if('2' == step){
    		standardPanel = me.up('standardbpmtwo');
    	}else if('5' == step){
    		standardPanel = me.up('standardbpmfive');
    	}
    	if(standardPanel){
    		if(standardPanel.winId){
    			Ext.getCmp(standardPanel.winId).close();
    		}
    	}
    },
	initComponent :function() {
	 	var me = this;
	 	//标准ID，隐藏域
	 	me.standardId = {
	 		xtype : 'hidden',
	 		name : 'id',
	 		value : ''
	 	},
	 	// 要求表单 隐藏域
	    me.standardControlFormsStr = Ext.widget('textfield', {
    		name: 'standardControlFormsStr',
    		hidden : true
	    });
	    // 要求ID 隐藏域
	    me.standardControlIds = Ext.widget('textfield', {
    		name: 'standardControlIds',
    		hidden : true
	    });
	  	me.time ={
			xtype : 'displayfield',
			labelWidth : 80,
			name : 'time',
			fieldLabel : '创建日期 ',
			value : '',
			margin: '7 10 10 30'
		};
		me.company ={
			xtype : 'displayfield',
			labelWidth : 80,
			fieldLabel : '提交单位 ',
			value : '',
			name : 'deptName',
			margin: '7 10 10 30'
		};
		//主责部门id
		me.deptId ={
			xtype : 'hidden',
			name : 'deptId'
		};
		/*标准名称 */
		me.standardName={
			 xtype : 'displayfield',
	         margin: '7 10 10 30',
	         labelWidth : 80,
	         name:'name',
	         maxLength : 200,
	         fieldLabel:'标准名称 '
         };
		
		//标准编号
        me.standardCode = {
			labelWidth :80,
			xtype : 'displayfield',
			disabled : false,
			lblAlign:'rigth',
			fieldLabel : '编　　号',
			value : '',
			name : 'code',
			margin: '7 10 10 30',
			maxLength : 200,
			columnWidth:.5,
			allowBlank : false
		};
		   
        /*分类 */
		me.parentStandard={
			 xtype : 'displayfield',
	         margin: '7 10 10 30',
	         labelWidth : 80,
	         name:'parentStandardName',
	         maxLength : 200,
	         fieldLabel:'分　　类 '
         };
		/*适用范围  */
		me.standardDepart={
			xtype : 'displayfield',
         	fieldLabel : '适用范围 ',
         	name:'companyName',
         	margin: '7 10 10 30',
         	labelWidth : 80
         };		             
//		/*来源 */
//		me.standardControlLevel={
//			 xtype : 'displayfield',
//		     name:'controlLevelName',
//		     margin: '7 10 10 30',
//		     labelWidth : 80,
//		     value:'',
//		     fieldLabel : '控制层级 '
//	     };
		/*更新期限  */
		me.updateTime={
			 xtype : 'displayfield',
			 name: 'updateDeadline', 
			 format: 'Y-m-d',
             margin: '7 10 10 30',
             labelWidth : 80,
             fieldLabel:'更新期限 '
        };
        
        //处理状态
		me.standardStatus={
			xtype : 'displayfield',
		    name:'dealStatus',
		    margin: '7 10 10 30',
		    labelWidth : 80,
		    labelAlign:'left',
		    fieldLabel:'状　　态 ',
		    renderer : function(value, metaData, record, colIndex, store, view) { 
				if(value == 'N'){
					return "未开始";
				}else if(value == 'H'){
					return "处理中";
				}else if(value == 'U'){
					return "待更新";
				}else if(value == 'O'){
					return "已纳入内控手册运转";
				}else if(value == 'F'){
					return "已完成";
				}
				return value;
			}
	    };
        
   	    me.standardControlShowOnly = Ext.widget('standardcontrolshowonly',{step:me.step});
		
   	    //内控标准的审批意见fieldset
        me.standardAdvice = Ext.widget('fieldset',{
			defaults : {
				columnWidth : 1/1
			},//每行显示一列，可设置多列
			layout : {
				type : 'column'
			},
			name : 'advicefieldset',
			//id : 'advicefieldset',
			action:'advicefieldset',
			collapsed : false,
//			margin: '8 10 0 10',
			collapsible : true,
			title : '审批'
        });

        //是否通过Radio
		me.isPassRadio = Ext.create('FHD.ux.dict.DictRadio', {
		    margin: '7 10 10 30',
			labelWidth:80,
			labelAlign:'left',
			fieldLabel:'是否通过 ',
			hideLabel:true,
			dictTypeId:'0yn',
			columnWidth:.5,
			defaultValue :'0yn_y',
			name : 'isPassRadio',
			id : 'isPassRadio'
		});
        
        //是否通过fieldset
        me.isPassField = Ext.widget('fieldset',{
			defaults : {
				columnWidth : 1/1
			},//每行显示一列，可设置多列
			layout : {
				type : 'column'
			},
			name : 'isPassField',
			collapsed : false,
			collapsible : false,
			title : '是否采纳',
			items:[me.isPassRadio]
        });
		me.items= [{
				xtype : 'fieldset',
				defaults : {
					columnWidth : 1/2
				},//每行显示一列，可设置多列
				layout : {
					type : 'column'
				},
				collapsed : false,
//				margin: '8 10 0 10',
				collapsible : true,
				title : '基本信息',
				items:[me.standardId, me.time,me.company,me.standardControlIds,me.deptId,
				       me.standardName,me.standardCode,me.parentStandard,/*me.standardControlLevel,*/me.standardControlFormsStr,
					   /*me.standardDepart,*/me.updateTime/*, me.standardStatus*/]
            }, 
            	me.isPassField
            ];
		me.bbar={
           style: 'background-image:url() !important;background-color:rgb(250,250,250);',
           items: [{
               xtype: 'tbtext'
           }, '->',
           	{
	            text: FHD.locale.get("fhd.common.submit"),//提交按钮
	            iconCls: 'icon-operator-submit',
           		handler: me.save
      		 }]
        };
		if('3' == me.step || '4' == me.step){
			me.standardAdvice.hide();
			me.isPassField.hide();
		}else if('2' == me.step){
			me.standardAdvice.show();
			me.isPassField.show();
		}else if('5' == me.step){
			me.standardAdvice.show();
			me.isPassField.hide();
		}
	           
		Ext.applyIf(me,{
			items:me.items
		});
		me.callParent(arguments);
		
   	    if(me.executionId){
			me.approvalidea = Ext.widget('approvalidea',{executionId:me.executionId,bodyPadding: '0 3 3 3'}); 
			me.standardAdvice.add(me.approvalidea);
			me.add(me.standardAdvice);
   	    }
		
		me.isPassRadio.on('change',function(t,newValue, oldValue,op){
			var me = this.up('fieldset');
			if(newValue.isPassRadio=='0yn_n'){//否
				//Ext.getCmp('advicefieldset').hide();
				var fieldset = Ext.ComponentQuery.query('standardapplyapproval fieldset[action=advicefieldset]')[0]; 
				fieldset.hide();
				this.up('panel').isPass = 'no';
			}else if(newValue.isPassRadio=='0yn_y'){//是
				//Ext.getCmp('advicefieldset').show();
				var fieldset = Ext.ComponentQuery.query('standardapplyapproval fieldset[action=advicefieldset]')[0]; 
				fieldset.show();
				this.up('panel').isPass = 'yes';
			}
		});
		}
	});