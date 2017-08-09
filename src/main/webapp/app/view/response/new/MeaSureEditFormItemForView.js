/**
 * 控制措施基本信息只读
 * @author 宋佳
 */
Ext.define('FHD.view.response.new.MeaSureEditFormItemForView', {
    extend: 'Ext.form.Panel',
    alias: 'widget.measureeditformitemforview',
    requires: [
    	'FHD.view.risk.cmp.form.RiskRelateFormDetail',
    	'FHD.view.icm.icsystem.component.AssessPointEditGridForView'
   	],
   	frame: false,
   	border : false,
   	autoScroll : true,
   	initParam:function(paramObj){
    	var me = this;
	 	me.paramObj = paramObj;
	},
    addComponent: function () {
		var me = this;
		//控制信息
		me.riskdetailform = Ext.widget('riskrelateformdetail');
	    me.riskFieldSet = Ext.widget('fieldset',{
	    	flex : 1,
			collapsible : true,
			collapsed : true,
			autoHeight : true,
			autoWidth : true,
			defaults : {
			    columnWidth : 1
			},
			layout: {
			    type: 'column'
			},
			items : me.riskdetailform,
			title: '风险信息'
	    });
		me.mainfieldset = Ext.widget('fieldset', {
			flex:1,
			collapsible: false,
			autoHeight: true,
			autoWidth: true,
			defaults: {
			    columnWidth : 1
			},
			layout: {
			    type: 'column'
			},
			title: '控制措施信息'
		});
	    //基本信息fieldset
		me.basicinfofieldset = Ext.widget('fieldset', {
		    flex:1,
		    collapsible: false,
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
		    title: '基本信息'
		});
    	//基本信息fieldset
        me.moreinfofieldset = Ext.widget('fieldset', {
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
            title: '更多信息'
        });
//	        me.add(me.basicinfofieldset);
        me.code = Ext.widget('displayfield',{
            name : 'measurecode',
            fieldLabel : '控制措施编号',
            value: '',
            allowBlank: false
        });
        me.basicinfofieldset.add(me.code);
        //控制措施内容
        me.desc = {
			xtype : 'displayfield',
			fieldLabel : '控制措施内容',
			name : 'meaSureDesc',
			columnWidth: 1
        };
        me.basicinfofieldset.add(me.desc);
        /*责任部门  */
		me.noteDepart = Ext.widget('displayfield',{
			fieldLabel : '责任部门' ,
			name:'orgName',
			type : 'dept',
			multiSelect : false,
			allowBlank: false
		});
        me.basicinfofieldset.add(me.noteDepart);
       	/*员工单选 */
		me.noteradio = Ext.widget('displayfield',{
			fieldLabel : '责&nbsp;&nbsp;任&nbsp;&nbsp;人',
			name:'empName'
		});
        me.basicinfofieldset.add(me.noteradio);
		me.pointNote = Ext.widget('displayfield',{
			fieldLabel : '所属流程/节点',
			name : 'processPoint'
		});
    	me.basicinfofieldset.add(me.pointNote);
        /*节点类型*/
		/*me.notestyle = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'pointType',
			dictTypeId : 'ca_point_type',
			multiSelect : false,
			fieldLabel : '节点类型'
		});
		me.basicinfofieldset.add(me.notestyle);*/
		/* 是否关键节点 */
		me.isKeyPoint = Ext.widget('displayfield',{
		 	margin : '7 5 5 30',
			name:'isKeyControlPoint',
			fieldLabel : '是否关键控制点'
		});
		me.basicinfofieldset.add(me.isKeyPoint);
		me.controlTarget = Ext.widget('displayfield', {
            name : 'controlTarget',
            fieldLabel : '控制目标'
        });
        me.moreinfofieldset.add(me.controlTarget);
		//实施证据
		me.measureControl = Ext.widget('displayfield', {
            name : 'implementProof',
            fieldLabel : '实施证据',
            columnWidth: .5
        });
        me.moreinfofieldset.add(me.measureControl);
        //控制点 
		me.controlPoint = Ext.widget('displayfield', {
            name : 'controlPoint',
            fieldLabel : '控制点'
        });
        me.moreinfofieldset.add(me.controlPoint);
        /*控制频率  dict */
		me.controlFrequency = Ext.widget('displayfield', {
			name:'controlFrequency',
			fieldLabel : '控制频率'
		});
		me.moreinfofieldset.add(me.controlFrequency);
		/* 控制方式 */
		me.controlMeasure = Ext.widget('displayfield', {
			name : 'controlMeasure',
			fieldLabel : '控制方式'
		});
		 me.assesspointfieldset = Ext.widget('fieldset', {
            flex:1,
            autoHeight: true,
            collapsible : false,
            collapsed : false,
            autoWidth: true,
            defaults: {
                columnWidth : 1,
                labelWidth: 95
            },
            layout: {
                type: 'column'
            },
            title: '评价点列表'
        });
        me.assesspointeditgrid =  Ext.widget('assesspointeditgridforview',{
	    	processId : me.processId,
			measureId : me.measureId,
			type : 'E'
	    });    //将展示父节点组件创建
        me.assesspointfieldset.add(me.assesspointeditgrid);
        me.mainfieldset.add(me.basicinfofieldset);
        me.mainfieldset.add(me.moreinfofieldset);
        me.mainfieldset.add(me.assesspointfieldset);
        me.add(me.riskFieldSet);
        me.add(me.mainfieldset);
        },
	    reloadData: function() {
	        var me = this;
	        me.load({
	            waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
	            url: __ctxPath + '/response/loadmeasureedititemformdataforview.f',
	            params: {
	                measureId : me.paramObj.measureId
	            },
	            success: function (form, action) {
	            	if(action.result.data.riskId != ''){
	            		me.riskFieldSet.setVisible(true);
		                me.riskdetailform.reloadData(action.result.data.riskId);
	            	}else{
						me.riskFieldSet.setVisible(false);            	
	            	}
	                me.assesspointeditgrid.initParam({
				    	processId : '',
						measureId : me.paramObj.measureId
	    			});
	    			me.assesspointeditgrid.reloadData();
	                return true;
	            }
	         });
	    },
	    select1 : function(tt){
		    var me = this;
		   	if(tt){
		   		me.up('riskeditform').selectArray.push(me.num);
		   	}else{
		   		Ext.Array.remove(me.up('riskeditform').selectArray,me.num);
		   	}
	    },
       // 初始化方法
	initComponent: function() {
		var me = this;
		Ext.applyIf(me,{
    	    bbar: {
                items: ['->',	
                {   
            	    text: '返回',
                    iconCls: 'icon-operator-home',
                    handler: me.cancel
                }
                ]
           		},bodyPadding: "0 3 3 3"
           }
		);
		me.callParent(arguments);
		//向form表单中添加控件
		me.addComponent();
	},
	cancel : function(){
		var me = this;
		me.up('window').close();
    }
});