Ext.define('FHD.view.comm.analysis.ThemeDataSourceSet', {
    extend: 'Ext.form.Panel',
    alias: 'widget.themedatasourceset',

    border:false,
    
    // 初始化方法
    initComponent: function () {
        var me = this;
        
        me.datasourceFieldSet = Ext.widget('fieldset', {
    		flex:1,
    		xtype: 'fieldset',
    		collapsible: true,
    		//autoHeight: true,
    		//autoWidth: true,
    		defaults: {
               //margin: '7 10 3 30',
               labelWidth: 95
    		},
    		autoScroll:true,
    		layout:{
        	   type:'vbox',
        	   align:'stretch'
    		},
    		title: '数据信息'
       	});
        
        me.radioContainer = Ext.create('Ext.form.RadioGroup',{
        	fieldLabel: '对象类型：',
        	labelWidth: 80,
            items: [
                {
                    boxLabel  : '风险',
                    name      : 'objectType',
                    inputValue: 'risk'
                }, {
                    boxLabel  : '指标',
                    name      : 'objectType',
                    inputValue: 'kpi'
                }, {
                    boxLabel  : '记分卡',
                    name      : 'objectType',
                    inputValue: 'category'
                }, {
                    boxLabel  : '战略目标',
                    name      : 'objectType',
                    inputValue: 'strategy'
                }
            ],
            listeners:{
            	change:function(t, newValue, oldValue, eOpts){
            		me.radioValue = newValue.objectType;
            //		alert("me.radioValue="+me.radioValue);
            		me.operateCoulum();
            		/*
            		if(!oldValue.attribute && newValue.attribute!=oldValue.attribute){
            			alert('change11111');
            			me.isChangeColumn = false;
            		}else{
            			alert('change222222222');
            		}
            		*/
            	}
            }
        });
        
        me.store = Ext.create('Ext.data.Store', {
            fields: ['id', 'name'],
            data : [
                {"id":"AL", "name":"Alabama"},
                {"id":"AK", "name":"Alaska"},
                {"id":"AZ", "name":"Arizona"}
            ]
        });
        me.timePeriod = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '时间区间：',
            store: me.store,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'id'
        });
        
        me.paramsFieldSet = Ext.widget('fieldset', {
        	flex:1,
            xtype: 'fieldset',
            collapsible: true,
            //autoHeight: true,
            //autoWidth: true,
            defaults: {
                //margin: '7 30 3 30',
                columnWidth:1,
                labelWidth: 95
            },
            layout: {
                type: 'column'
            },
            title: '参数选择'
        });
        //对象类型
        me.paramsFieldSet.add(me.radioContainer);
        //时间区间维度
        me.paramsFieldSet.add(me.timePeriod);
        //值类型
        if(me.radioValue){
        	me.operateCoulum();
        }
       
        Ext.applyIf(me, {
        	layout: {
				type: 'vbox',
	        	align:'stretch'
	        },
	        items:[me.paramsFieldSet,me.datasourceFieldSet]
        });
        
        me.callParent(arguments);
    },
    operateCoulum:function(){
    	var me=this;
    	
    	if('risk'==me.radioValue){
    		me.paramsFieldSet.remove(me.valueType,true);
    		me.valueType = Ext.create('Ext.form.CheckboxGroup',{
                fieldLabel: '值类型：',
                columns: 5,
                vertical: true,
                items: [
                    {
                        boxLabel  : '发生可能性',
                        name      : 'valueType',
                        inputValue: 'probability'
                    }, {
                        boxLabel  : '影响程度',
                        name      : 'valueType',
                        inputValue: 'impacts'
                    }
                ]
            });
    		me.paramsFieldSet.add(me.valueType);
    		/**********************风险数据源**********************/
    		me.datasourceFieldSet.remove(me.datasourceGrid,true);
	          
	        me.datasourceGrid = Ext.create('FHD.ux.GridPanel', {
	  	        border: true,
	  	        url: __ctxPath + '/icm/assess/findAssessPlanListByParams.f',
	  	        extraParams:{
	  	        	companyId: __user.companyId,
	  	        	dealStatus: 'N'
	  	        },
	  	        cols: [
  		   			{
		  	            cls: 'grid-icon-column-header grid-statushead-column-header',
		  	            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
		  	            dataIndex: 'assessmentStatus',
		  	            sortable: true,
		  	            width:40,
		  	            renderer: function (v) {
		  	                var color = "";
		  	                var display = "";
		  	                if (v == "icon-ibm-symbol-4-sm") {
		  	                    color = "symbol_4_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.hight");
		  	                } else if (v == "icon-ibm-symbol-6-sm") {
		  	                    color = "symbol_6_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.low");
		  	                } else if (v == "icon-ibm-symbol-5-sm") {
		  	                    color = "symbol_5_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.min");
		  	                } else if(v=="icon-ibm-symbol-safe-sm"){
		  	                	 display = "安全";
		  	                } 
		  	                else {
		  	                    v = "icon-ibm-underconstruction-small";
		  	                    display = "无";
		  	                }
		  	                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
		  	                    "background-position: center top;' data-qtitle='' " +
		  	                    "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
		  	            }
		  	        }, {
		  	            cls: 'grid-icon-column-header grid-trendhead-column-header',
		  	            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
		  	            dataIndex: 'directionstr',
		  	            sortable: true,
		  	            width:40,
		  	            renderer: function (v) {
		  	                var color = "";
		  	                var display = "";
		  	                if (v == "icon-ibm-icon-trend-rising-positive") {
		  	                    color = "icon_trend_rising_positive";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.positiv");
		  	                } else if (v == "icon-ibm-icon-trend-neutral-null") {
		  	                    color = "icon_trend_neutral_null";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.flat");
		  	                } else if (v == "icon-ibm-icon-trend-falling-negative") {
		  	                    color = "icon_trend_falling_negative";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.negative");
		  	                }
		  	                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
		  	                    "background-position: center top;' data-qtitle='' " +
		  	                    "class='" + v + "'  data-qtip='" + display + "'></div>";
		  	            }
		  	        },{
  		   				header:'风险名称', sortable: false,dataIndex: 'name',flex:3,
  		  				renderer:function(value,metaData,record,colIndex,store,view) { 
  		  					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showPlanViewList('" + record.data.id + "','" + record.data.dealStatus +"')\" >" + value + "</a>"; 
  		  				}
  		  			},
  		  			{header : '发生可能性',dataIndex : 'finishVallue',sortable : true, flex:1}, 
		   			{header : '影响程度',dataIndex :'targetValue',sortable : true,flex:1}, 
  		   			{dataIndex: 'id',hidden:true}
  		  		],
	            tbarItems: [
	  				{iconCls : 'icon-add',id:'theme_select_risk',text:'选择风险',tooltip: '选择风险',handler :me.selectRisk,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_kpi',text:'选择指标',tooltip: '选择指标',handler :me.selectKpi,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_category',text:'选择记分卡',tooltip: '选择记分卡',handler :me.selectCategory,disabled: true,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_strategy',text:'选择战略目标',tooltip: '选择战略目标',handler :me.selectStrategy,disabled: true,scope : this}
	  			]
	  		});
    		me.datasourceFieldSet.add(me.datasourceGrid);
    	}else if('kpi'==me.radioValue){
    		me.paramsFieldSet.remove(me.valueType,true);
    		me.valueType = Ext.create('Ext.form.CheckboxGroup',{
                fieldLabel: '值类型：',
                columns: 5,
                vertical: true,
                items: [
                    {
                        boxLabel  : '目标值',
                        name      : 'valueType',
                        inputValue: 'targetValue'
                    }, {
                        boxLabel  : '实际值',
                        name      : 'valueType',
                        inputValue: 'finishValue'
                    }, {
                        boxLabel  : '评估值',
                        name      : 'valueType',
                        inputValue: 'assessValue'
                    }
                ],
                listeners:{
                	change:function(t, newValue, oldValue, eOpts){
                		me.checkValues = newValue.valueType;
                		//alert('me.checkValues='+me.checkValues);
                	}
                }
            });
    		me.paramsFieldSet.add(me.valueType);
    		/**********************指标数据源**********************/
    		me.datasourceFieldSet.remove(me.datasourceGrid,true);
	          
	        me.datasourceGrid = Ext.create('FHD.ux.GridPanel', {
	  	        border: true,
	  	        url: __ctxPath + '/icm/assess/findAssessPlanListByParams.f',
	  	        extraParams:{
	  	        	companyId: __user.companyId,
	  	        	dealStatus: 'N'
	  	        },
	  	        cols: [
  		   			{
		  	            cls: 'grid-icon-column-header grid-statushead-column-header',
		  	            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
		  	            dataIndex: 'assessmentStatus',
		  	            sortable: true,
		  	            width:40,
		  	            renderer: function (v) {
		  	                var color = "";
		  	                var display = "";
		  	                if (v == "icon-ibm-symbol-4-sm") {
		  	                    color = "symbol_4_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.hight");
		  	                } else if (v == "icon-ibm-symbol-6-sm") {
		  	                    color = "symbol_6_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.low");
		  	                } else if (v == "icon-ibm-symbol-5-sm") {
		  	                    color = "symbol_5_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.min");
		  	                } else if(v=="icon-ibm-symbol-safe-sm"){
		  	                	 display = "安全";
		  	                } 
		  	                else {
		  	                    v = "icon-ibm-underconstruction-small";
		  	                    display = "无";
		  	                }
		  	                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
		  	                    "background-position: center top;' data-qtitle='' " +
		  	                    "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
		  	            }
		  	        }, {
		  	            cls: 'grid-icon-column-header grid-trendhead-column-header',
		  	            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
		  	            dataIndex: 'directionstr',
		  	            sortable: true,
		  	            width:40,
		  	            renderer: function (v) {
		  	                var color = "";
		  	                var display = "";
		  	                if (v == "icon-ibm-icon-trend-rising-positive") {
		  	                    color = "icon_trend_rising_positive";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.positiv");
		  	                } else if (v == "icon-ibm-icon-trend-neutral-null") {
		  	                    color = "icon_trend_neutral_null";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.flat");
		  	                } else if (v == "icon-ibm-icon-trend-falling-negative") {
		  	                    color = "icon_trend_falling_negative";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.negative");
		  	                }
		  	                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
		  	                    "background-position: center top;' data-qtitle='' " +
		  	                    "class='" + v + "'  data-qtip='" + display + "'></div>";
		  	            }
		  	        },{
  		   				header:'指标名称', sortable: false,dataIndex: 'name',flex:3,
  		  				renderer:function(value,metaData,record,colIndex,store,view) { 
  		  					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showPlanViewList('" + record.data.id + "','" + record.data.dealStatus +"')\" >" + value + "</a>"; 
  		  				}
  		  			},
  		  			{header : '目标值',dataIndex :'targetValue',sortable : true,flex:1}, 
		   			{header : '实际值',dataIndex : 'finishVallue',sortable : true, flex:1}, 
		   			{header : '评估值',dataIndex : 'assessValue',sortable : true, flex : 1},
  		   			{dataIndex: 'id',hidden:true}
  		  		],
	            tbarItems: [
	  				{iconCls : 'icon-add',id:'theme_select_risk',text:'选择风险',tooltip: '选择风险',handler :me.selectRisk,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_kpi',text:'选择指标',tooltip: '选择指标',handler :me.selectKpi,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_category',text:'选择记分卡',tooltip: '选择记分卡',handler :me.selectCategory,disabled: true,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_strategy',text:'选择战略目标',tooltip: '选择战略目标',handler :me.selectStrategy,disabled: true,scope : this}
	  			]
	  		});
    		me.datasourceFieldSet.add(me.datasourceGrid);
    	}else if('category'==me.radioValue){
    		me.paramsFieldSet.remove(me.valueType,true);
    		me.valueType = Ext.create('Ext.form.CheckboxGroup',{
                fieldLabel: '值类型：',
                columns: 5,
                vertical: true,
                items: [
                    {
                        boxLabel  : '评估值',
                        name      : 'valueType',
                        inputValue: 'assessValue'
                    }
                ]
            });
    		me.paramsFieldSet.add(me.valueType);
    		/**********************记分卡数据源**********************/
    		me.datasourceFieldSet.remove(me.datasourceGrid,true);
	          
	        me.datasourceGrid = Ext.create('FHD.ux.GridPanel', {
	  	        border: true,
	  	        url: __ctxPath + '/icm/assess/findAssessPlanListByParams.f',
	  	        extraParams:{
	  	        	companyId: __user.companyId,
	  	        	dealStatus: 'N'
	  	        },
	  	        cols: [
  		   			{
		  	            cls: 'grid-icon-column-header grid-statushead-column-header',
		  	            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
		  	            dataIndex: 'assessmentStatus',
		  	            sortable: true,
		  	            width:40,
		  	            renderer: function (v) {
		  	                var color = "";
		  	                var display = "";
		  	                if (v == "icon-ibm-symbol-4-sm") {
		  	                    color = "symbol_4_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.hight");
		  	                } else if (v == "icon-ibm-symbol-6-sm") {
		  	                    color = "symbol_6_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.low");
		  	                } else if (v == "icon-ibm-symbol-5-sm") {
		  	                    color = "symbol_5_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.min");
		  	                } else if(v=="icon-ibm-symbol-safe-sm"){
		  	                	 display = "安全";
		  	                } 
		  	                else {
		  	                    v = "icon-ibm-underconstruction-small";
		  	                    display = "无";
		  	                }
		  	                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
		  	                    "background-position: center top;' data-qtitle='' " +
		  	                    "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
		  	            }
		  	        }, {
		  	            cls: 'grid-icon-column-header grid-trendhead-column-header',
		  	            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
		  	            dataIndex: 'directionstr',
		  	            sortable: true,
		  	            width:40,
		  	            renderer: function (v) {
		  	                var color = "";
		  	                var display = "";
		  	                if (v == "icon-ibm-icon-trend-rising-positive") {
		  	                    color = "icon_trend_rising_positive";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.positiv");
		  	                } else if (v == "icon-ibm-icon-trend-neutral-null") {
		  	                    color = "icon_trend_neutral_null";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.flat");
		  	                } else if (v == "icon-ibm-icon-trend-falling-negative") {
		  	                    color = "icon_trend_falling_negative";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.negative");
		  	                }
		  	                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
		  	                    "background-position: center top;' data-qtitle='' " +
		  	                    "class='" + v + "'  data-qtip='" + display + "'></div>";
		  	            }
		  	        },{
  		   				header:'记分卡名称', sortable: false,dataIndex: 'name',flex:3,
  		  				renderer:function(value,metaData,record,colIndex,store,view) { 
  		  					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showPlanViewList('" + record.data.id + "','" + record.data.dealStatus +"')\" >" + value + "</a>"; 
  		  				}
  		  			},
		   			{header : '评估值',dataIndex : 'assessValue',sortable : true, flex : 1},
  		   			{dataIndex: 'id',hidden:true}
  		  		],
	            tbarItems: [
	  				{iconCls : 'icon-add',id:'theme_select_risk',text:'选择风险',tooltip: '选择风险',handler :me.selectRisk,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_kpi',text:'选择指标',tooltip: '选择指标',handler :me.selectKpi,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_category',text:'选择记分卡',tooltip: '选择记分卡',handler :me.selectCategory,disabled: true,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_strategy',text:'选择战略目标',tooltip: '选择战略目标',handler :me.selectStrategy,disabled: true,scope : this}
	  			]
	  		});
    		me.datasourceFieldSet.add(me.datasourceGrid);
    	}else if('strategy'==me.radioValue){
    		me.paramsFieldSet.remove(me.valueType,true);
    		me.valueType = Ext.create('Ext.form.CheckboxGroup',{
                fieldLabel: '值类型：',
                columns: 5,
                vertical: true,
                items: [
                    {
                        boxLabel  : '评估值',
                        name      : 'valueType',
                        inputValue: 'assessValue'
                    }
                ]
            });
    		me.paramsFieldSet.add(me.valueType);
    		/**********************战略目标数据源**********************/
    		me.datasourceFieldSet.remove(me.datasourceGrid,true);
	          
	        me.datasourceGrid = Ext.create('FHD.ux.GridPanel', {
	  	        border: true,
	  	        url: __ctxPath + '/icm/assess/findAssessPlanListByParams.f',
	  	        extraParams:{
	  	        	companyId: __user.companyId,
	  	        	dealStatus:'N'
	  	        },
	  	        cols: [
	  	            {
		  	            cls: 'grid-icon-column-header grid-statushead-column-header',
		  	            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
		  	            dataIndex: 'assessmentStatus',
		  	            sortable: true,
		  	            width:40,
		  	            renderer: function (v) {
		  	                var color = "";
		  	                var display = "";
		  	                if (v == "icon-ibm-symbol-4-sm") {
		  	                    color = "symbol_4_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.hight");
		  	                } else if (v == "icon-ibm-symbol-6-sm") {
		  	                    color = "symbol_6_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.low");
		  	                } else if (v == "icon-ibm-symbol-5-sm") {
		  	                    color = "symbol_5_sm";
		  	                    display = FHD.locale.get("fhd.alarmplan.form.min");
		  	                } else if(v=="icon-ibm-symbol-safe-sm"){
		  	                	 display = "安全";
		  	                } 
		  	                else {
		  	                    v = "icon-ibm-underconstruction-small";
		  	                    display = "无";
		  	                }
		  	                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
		  	                    "background-position: center top;' data-qtitle='' " +
		  	                    "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
		  	            }
		  	        }, {
		  	            cls: 'grid-icon-column-header grid-trendhead-column-header',
		  	            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
		  	            dataIndex: 'directionstr',
		  	            sortable: true,
		  	            width:40,
		  	            renderer: function (v) {
		  	                var color = "";
		  	                var display = "";
		  	                if (v == "icon-ibm-icon-trend-rising-positive") {
		  	                    color = "icon_trend_rising_positive";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.positiv");
		  	                } else if (v == "icon-ibm-icon-trend-neutral-null") {
		  	                    color = "icon_trend_neutral_null";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.flat");
		  	                } else if (v == "icon-ibm-icon-trend-falling-negative") {
		  	                    color = "icon_trend_falling_negative";
		  	                    display = FHD.locale.get("fhd.kpi.kpi.prompt.negative");
		  	                }
		  	                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
		  	                    "background-position: center top;' data-qtitle='' " +
		  	                    "class='" + v + "'  data-qtip='" + display + "'></div>";
		  	            }
		  	        }, {
		   				header:'战略目标名称', sortable: false,dataIndex: 'name',flex:3,
		  				renderer:function(value,metaData,record,colIndex,store,view) { 
		  					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showPlanViewList('" + record.data.id + "','" + record.data.dealStatus +"')\" >" + value + "</a>"; 
		  				}
		  			},
		   			{header : '评估值',dataIndex : 'assessValue',sortable : true, flex : 1},
		   			{dataIndex: 'id',hidden:true}
  		  		],
	            tbarItems: [
	  				{iconCls : 'icon-add',id:'theme_select_risk',text:'选择风险',tooltip: '选择风险',handler :me.selectRisk,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_kpi',text:'选择指标',tooltip: '选择指标',handler :me.selectKpi,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_category',text:'选择记分卡',tooltip: '选择记分卡',handler :me.selectCategory,disabled: true,scope : this},
	  				{iconCls : 'icon-add',id:'theme_select_strategy',text:'选择战略目标',tooltip: '选择战略目标',handler :me.selectStrategy,disabled: true,scope : this}
	  			]
	  		});
    		me.datasourceFieldSet.add(me.datasourceGrid);
    	}
    },
    saveData:function(){
    	var me=this;
    	
    	alert("保存'对象类型  时间区间  值类型参数'save data ......");
    	return true;
    },
   	reloadData:function(){
   		var me=this;
   		
   		var themeanalysismainpanel = me.up('themeanalysismainpanel');
		if(themeanalysismainpanel){
			me.businessId = themeanalysismainpanel.paramObj.businessId;
		}
		
        FHD.ajax({
        	url: __ctxPath + '/themeAnalysis/findThemeAnalysisById.f',
            params: {
            	themeAnalysisId: me.businessId
            },
            callback: function (response) {
                if (response.success) {
                	
                	//me.layoutType = form.getValues().layoutType;
   	    		 	//me.layoutTypeHiddenValue.setValue(me.layoutType);
   	    		 	//var index = me.dataview.getStore().find('name', me.layoutType);
   	    		 	//var record = me.dataview.getStore().getAt(index);
   	    		 	//me.dataview.getSelectionModel().select(record);
                	
                	if('layout0'==response.data.layoutType){
                		//布局0
                		me.panel = Ext.create('Ext.panel.Panel',{
                			title:'A'
                		});
                		me.add(me.panel);
                	}else if('layout1'==response.data.layoutType){
                		//布局1
                		me.aPanel = Ext.create('Ext.panel.Panel',{
                			title:'A',
                			flex:1
                		});
                		me.add(me.aPanel);
                		me.bPanel = Ext.create('Ext.panel.Panel',{
                			title:'B',
                			flex:1
                		});
                		me.cPanel = Ext.create('Ext.panel.Panel',{
                			title:'C',
                			flex:1
                		});
                		me.add(Ext.create('Ext.container.Container',{
                			layout: {
                				type: 'hbox',
                	        	align:'stretch'
                	        },
                	        flex:1,
                			items:[me.bPanel,me.cPanel]
                		}));
                	}else if('layout2'==response.data.layoutType){
                		//布局2
                		me.aPanel = Ext.create('Ext.panel.Panel',{
                			title:'A',
                			flex:1,
                			tools: [
                			    {
	        				        itemId: 'gear',
	        				        type: 'gear',
	        				        handler: function(){
	        				        	me.showWindow();
	        				        }
	        				    }
		        			]
                		});
                		me.bPanel = Ext.create('Ext.panel.Panel',{
                			title:'B',
                			flex:1,
                			tools: [
                			    {
	        				        itemId: 'gear',
	        				        type: 'gear',
	        				        handler: function(){
	        				        	me.showWindow();
	        				        }
	        				    }
		        			]
                		});
                		me.add(Ext.create('Ext.container.Container',{
                			layout: {
                				type: 'hbox',
                	        	align:'stretch'
                	        },
                	        flex:1,
                			items:[me.aPanel,me.bPanel]
                		}));
                		me.cPanel = Ext.create('Ext.panel.Panel',{
                			title:'C',
                			flex:1,
                			tools: [
                			    {
	        				        itemId: 'gear',
	        				        type: 'gear',
	        				        handler: function(){
	        				        	me.showWindow();
	        				        }
	        				    }
		        			]
                		});
                		me.dPanel = Ext.create('Ext.panel.Panel',{
                			title:'D',
                			flex:1,
                			tools: [
                			    {
	        				        itemId: 'gear',
	        				        type: 'gear',
	        				        handler: function(){
	        				        	me.showWindow();
	        				        }
	        				    }
		        			]
                		});
                		me.add(Ext.create('Ext.container.Container',{
                			layout: {
                				type: 'hbox',
                	        	align:'stretch'
                	        },
                	        flex:1,
                			items:[me.cPanel,me.dPanel]
                		}));
                	}
                }
            }
    	});
   	}
});