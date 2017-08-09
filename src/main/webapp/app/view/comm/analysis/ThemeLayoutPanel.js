Ext.define('FHD.view.comm.analysis.ThemeLayoutPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.themelayoutpanel',

    autoScroll:true,
    border:false,
    //列是否改变标志列
	//isChangeColumn:false,
    
    // 初始化方法
    initComponent: function () {
        var me = this;

        /***************************基本属性设置***************************/
        //布局类型值隐藏域
        me.idValue = Ext.widget('textareafield', {
            xtype: 'textfield',
            name: 'id',
            hidden:true
        });
        //名称
        me.name = Ext.widget('textareafield', {
            xtype: 'textareafield',
            name: 'name',
            rows: 3,
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.name') + '<font color=red>*</font>', //名称
            value: '',
            maxLength: 255,
            columnWidth: .5,
            allowBlank: false
            /*validator :function(){
            	alert("************");
            },
  			invalidText:"名称已经存在"*/
        });
        //说明
        me.desc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            rows: 3,
            labelAlign: 'left',
            name: 'desc',
            fieldLabel: FHD.locale.get('fhd.sys.dic.desc'), //说明
            maxLength: 4000,
            columnWidth: .5
        });
        
        me.basicFieldSet = Ext.widget('fieldset', {
        	flex:1,
            xtype: 'fieldset',
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            defaults: {
                margin: '7 30 3 30',
                labelWidth: 95
            },
            layout: {
                type: 'column'
            },
            title: FHD.locale.get('fhd.common.baseInfo'),
            items: [
                me.idValue,
				me.name,
				me.desc
			]
        });
        /***************************布局类型设置***************************/
        me.data = [
           [1,  "layout0", "32 x 32 pixels"],
           [2,  "layout1", "32 x 32 pixels"],
           [3,  "layout2", "32 x 32 pixels"]
        ];

        Ext.define('Layout', {
        	extend: 'Ext.data.Model',
           	fields: [
               {name: 'id', type: 'int'},
               {name: 'name', type: 'String'},
               {name: 'desc', type: 'String'}
            ]
        });

        me.store = Ext.create('Ext.data.ArrayStore', {
        	model: 'Layout',
           	sortInfo: {
               field    : 'name',
               direction: 'ASC'
           	},
           	data: me.data
        });

        me.dataview = Ext.create('Ext.view.View', {
        	deferInitialRefresh: false,
           	store: me.store,
           	tpl  : Ext.create('Ext.XTemplate',
               '<tpl for=".">',
                   '<div class="phone">',
                       (!Ext.isIE6? '<img width="64" height="64" src="images/icons/{[values.name.replace(/ /g, "-")]}.png" />' :
                        '<div style="width:74px;height:74px;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\'images/icons/{[values.name.replace(/ /g, "-")]}.png\',sizingMethod=\'scale\')"></div>'),
                       '<br/><strong>{name}</strong><br/>',
                       '<span>{desc}</span>',
                   '</div>',
               '</tpl>'
           	),
           	cls: 'phones',
           	plugins : [
               Ext.create('Ext.ux.DataView.Animated', {
                   duration  : 550,
                   idProperty: 'id'
               })
            ],
           	itemSelector: 'div.phone',
           	overItemCls : 'phone-hover',
           	multiSelect : false,
           	autoScroll  : true,
           	listeners:{
           		select:function(t, record, eOpts ){
           			//me.layoutType = record.data.id;
           			/*
           			if(!record.data.name && !me.layoutType && record.data.name!=me.layoutType){
           				alert('select11111');
           				me.isChangeColumn = false;
            		}else{
            			alert('select222222222');
            		}
            		*/
           			me.layoutTypeHiddenValue.setValue(record.data.name);
           			me.layoutType = record.data.name;
           			me.radioAttribute = me.getForm().getValues()["attribute"];
           			if(!me.radioAttribute){
           				//布局属性未选择，不操作
		  			}else if(me.radioAttribute=="relative"){
		  				//布局属性选择相对,设置布局属性
		  				me.operateLayout();
		  			}else if(me.radioAttribute=="absolute"){
		  				//而已属性选择绝对，设置布局属性
		  				me.operateLayout();
		  			}
           		}
           	}
        });
        //布局类型值隐藏域
        me.layoutTypeHiddenValue = Ext.widget('textareafield', {
            xtype: 'textfield',
            name: 'layoutType',
            hidden:true
        });
        /*
       	me.widthSpace = {
        	xtype:'tbspacer',
           	width:3
       	};
       
       	me.heightSpace = {
	   		xtype:'tbspacer',
	   		height:10
       	};
        */
       
        me.layoutTypeFieldSet = Ext.widget('fieldset', {
        	flex:1,
           xtype: 'fieldset',
           collapsible: true,
           //autoHeight: true,
           //autoWidth: true,
           defaults: {
               margin: '7 10 3 30',
               labelWidth: 95
           },
           height:300,
           autoScroll:true,
           layout:{
        	   type:'vbox',
        	   align:'stretch'
           },
           title: '布局类型',
           items:[me.dataview,me.layoutTypeHiddenValue]
       	});
        /***************************布局属性设置***************************/
        me.radioContainer = Ext.create('Ext.form.RadioGroup',{
        	fieldLabel: '属性设置',
        	labelWidth: 80,
            items: [
                {
                    boxLabel  : '相对属性',
                    name      : 'attribute',
                    inputValue: 'relative'
                }, {
                    boxLabel  : '绝对属性',
                    name      : 'attribute',
                    inputValue: 'absolute'
                }
            ],
            listeners:{
            	change:function(t, newValue, oldValue, eOpts){
            		me.radioAttribute = newValue.attribute;
            		/*
            		if(!oldValue.attribute && newValue.attribute!=oldValue.attribute){
            			alert('change11111');
            			me.isChangeColumn = false;
            		}else{
            			alert('change222222222');
            		}
            		*/
            		if(me.layoutType){
            			//设置布局属性
		  				me.operateLayout();
            		}
            	}
            }
        });
        
        me.layoutAttributeFieldSet = Ext.widget('fieldset', {
        	flex:1,
            xtype: 'fieldset',
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            defaults: {
                margin: '7 30 3 30',
                columnWidth:1,
                labelWidth: 95
            },
            layout: {
                type: 'column'
            },
            title: '布局属性',
            items: [
                me.radioContainer
			]
        });
        
        Ext.applyIf(me, {
	        layout: {
				type: 'vbox',
	        	align:'stretch'
	        },
	    	items:[
	    	    me.basicFieldSet,
	    		Ext.create('Ext.container.Container',{
	        		layout: {
						type: 'hbox',
			        	align:'stretch'
			        },
			        flex:1,
			        items:[
			        	me.layoutTypeFieldSet,
			        	me.layoutAttributeFieldSet
			        ]
	    		})
	    	]
        });
        me.callParent(arguments);
    },
    //布局属性动态生成列
    operateLayout:function(){
    	var me=this;
    	
    	//布局值
    	//me.layoutType;
    	//radio值
		//me.radioAttribute;
    	//alert("operateLayout="+me.layoutType+"\t"+me.radioAttribute+"\t"+me.isChangeColumn);
    	if(me.radioAttribute=="relative"){
			if(!me.layoutType){
				Ext.Msg.alert('请选择布局类型!');
			}else{
  				//删除绝对属性设置
    			me.layoutAttributeFieldSet.remove(me.showItem, true);
  				//重置相对属性设置
    			if('layout0' == me.layoutType){
    				//布局0
    				me.widthRatio = Ext.create('Ext.form.field.Text',{
    					name: 'widthRatio',
    			        fieldLabel: '宽度比:',
    			        value:'1:1',
    			        allowBlank: false,
    			        disabled:true
    				});
    				me.heightRatio = Ext.create('Ext.form.field.Text',{
    					name: 'heightRatio',
    			        fieldLabel: '高度比:',
    			        value:'1:1',
    			        allowBlank: false,
    			        disabled:true
    				});
    				
    				me.showItem = Ext.create('Ext.container.Container');
    				me.showItem.add(me.widthRatio);
    				me.showItem.add(me.heightRatio);
    				
    				me.layoutAttributeFieldSet.add(me.showItem);
    				//列是否改变标志列
        			//me.isChangeColumn = true;
    			}else if('layout1' == me.layoutType){
    				//布局1
    				me.oneWidthRatio = Ext.create('Ext.form.field.Text',{
    					name: 'oneWidthRatio',
    			        fieldLabel: '第一行宽度比:',
    			        value:'1:1',
    			        allowBlank: false,
    			        //readonly:true,
    			        disable:true
    				});
    				me.twoWidthRatio = Ext.create('Ext.form.field.Text',{
    					name: 'twoWidthRatio',
    			        fieldLabel: '第二行宽度比:',
    			        value:'1:1',
    			        allowBlank: false
    				});
    				me.heightRatio = Ext.create('Ext.form.field.Text',{
    					name: 'heightRatio',
    			        fieldLabel: '高度比:',
    			        value:'1:1',
    			        allowBlank: false
    				});
    				
    				me.showItem = Ext.create('Ext.container.Container');
    				//me.showItem.add(me.oneWidthRatio);
    				me.showItem.add(me.twoWidthRatio);
    				me.showItem.add(me.heightRatio);
    				
    				me.layoutAttributeFieldSet.add(me.showItem);
    				//列是否改变标志列
        			//me.isChangeColumn = true;
    			}else if('layout2' == me.layoutType){
    				//布局2
    				me.oneWidthRatio = Ext.create('Ext.form.field.Text',{
    					name: 'oneWidthRatio',
    			        fieldLabel: '第一行宽度比:',
    			        value:'1:1',
    			        allowBlank: false
    				});
    				me.twoWidthRatio = Ext.create('Ext.form.field.Text',{
    					name: 'twoWidthRatio',
    			        fieldLabel: '第二行宽度比:',
    			        value:'1:1',
    			        allowBlank: false
    				});
    				me.heightRatio = Ext.create('Ext.form.field.Text',{
    					name: 'heightRatio',
    			        fieldLabel: '高度比:',
    			        value:'1:1',
    			        allowBlank: false
    				});
    				
    				me.showItem = Ext.create('Ext.container.Container');
    				me.showItem.add(me.oneWidthRatio);
    				me.showItem.add(me.twoWidthRatio);
    				me.showItem.add(me.heightRatio);
    				
    				me.layoutAttributeFieldSet.add(me.showItem);
    				//列是否改变标志列
        			//me.isChangeColumn = true;
    			}
			}
		}else if(me.radioAttribute=="absolute"){
			if(!me.layoutType){
				Ext.Msg.alert('请选择布局类型!');
			}else{
				//删除相对属性设置
				me.layoutAttributeFieldSet.remove(me.showItem, true);
				//重置绝对属性设置
				if('layout0' == me.layoutType){
					//布局A
					me.width = Ext.create('Ext.form.field.Text',{
						name: 'oneWidth',
				        fieldLabel: '宽度:',
				        allowBlank: false
					});
					me.height = Ext.create('Ext.form.field.Text',{
						name: 'oneHeight',
				        fieldLabel: '高度:',
				        allowBlank: false
					});
					
					me.showItem = Ext.create('Ext.container.Container');
					me.showItem.add(me.width);
					me.showItem.add(me.height);
					
					me.layoutAttributeFieldSet.add(me.showItem);
					//列是否改变标志列
	    			//me.isChangeColumn = true;
    			}else if('layout1' == me.layoutType){
					//布局B
					me.oneWidth = Ext.create('Ext.form.field.Text',{
						name: 'oneWidth',
				        fieldLabel: '第一个宽度:',
				        allowBlank: false
					});
					me.oneHeight = Ext.create('Ext.form.field.Text',{
						name: 'oneHeight',
				        fieldLabel: '第一个高度:',
				        allowBlank: false
					});
					me.twoWidth = Ext.create('Ext.form.field.Text',{
						name: 'twoWidth',
				        fieldLabel: '第二个宽度:',
				        allowBlank: false
					});
					me.twoHeight = Ext.create('Ext.form.field.Text',{
						name: 'twoHeight',
				        fieldLabel: '第二个高度:',
				        allowBlank: false
					});
					me.threeWidth = Ext.create('Ext.form.field.Text',{
						name: 'threeWidth',
				        fieldLabel: '第三个宽度:',
				        allowBlank: false
					});
					me.threeHeight = Ext.create('Ext.form.field.Text',{
						name: 'threeHeight',
				        fieldLabel: '第三个高度:',
				        allowBlank: false
					});
					
					me.showItem = Ext.create('Ext.container.Container');
					me.showItem.add(me.oneWidth);
					me.showItem.add(me.oneHeight);
					me.showItem.add(me.twoWidth);
					me.showItem.add(me.twoHeight);
					me.showItem.add(me.threeWidth);
					me.showItem.add(me.threeHeight);
					
					me.layoutAttributeFieldSet.add(me.showItem);
					//列是否改变标志列
	    			//me.isChangeColumn = true;
				}else if('layout2' == me.layoutType){
					//布局C
					me.oneWidth = Ext.create('Ext.form.field.Text',{
						name: 'oneWidth',
				        fieldLabel: '第一个宽度:',
				        allowBlank: false
					});
					me.oneHeight = Ext.create('Ext.form.field.Text',{
						name: 'oneHeight',
				        fieldLabel: '第一个高度:',
				        allowBlank: false
					});
					me.twoWidth = Ext.create('Ext.form.field.Text',{
						name: 'twoWidth',
				        fieldLabel: '第二个宽度:',
				        allowBlank: false
					});
					me.twoHeight = Ext.create('Ext.form.field.Text',{
						name: 'twoHeight',
				        fieldLabel: '第二个高度:',
				        allowBlank: false
					});
					me.threeWidth = Ext.create('Ext.form.field.Text',{
						name: 'threeWidth',
				        fieldLabel: '第三个宽度:',
				        allowBlank: false
					});
					me.threeHeight = Ext.create('Ext.form.field.Text',{
						name: 'threeHeight',
				        fieldLabel: '第三个高度:',
				        allowBlank: false
					});
					me.fourWidth = Ext.create('Ext.form.field.Text',{
						name: 'fourWidth',
				        fieldLabel: '第四个宽度:',
				        allowBlank: false
					});
					me.fourHeight = Ext.create('Ext.form.field.Text',{
						name: 'fourHeight',
				        fieldLabel: '第四个高度:',
				        allowBlank: false
					});
					
					me.showItem = Ext.create('Ext.container.Container');
					me.showItem.add(me.oneWidth);
					me.showItem.add(me.oneHeight);
					me.showItem.add(me.twoWidth);
					me.showItem.add(me.twoHeight);
					me.showItem.add(me.threeWidth);
					me.showItem.add(me.threeHeight);
					me.showItem.add(me.fourWidth);
					me.showItem.add(me.fourHeight);
					
					me.layoutAttributeFieldSet.add(me.showItem);
					//列是否改变标志列
	    			//me.isChangeColumn = true;
				}
			}
		}
    	//alert("加载中..."+me.isChangeColumn+"\t"+me.businessId);
    	if(me.businessId){
    		//alert("me.businessId="+me.businessId);
    		//加载数据
    		me.getForm().load({
		    	 url: __ctxPath + '/themeAnalysis/findThemeAnalysisById.f',
		    	 params: {
		    		 themeAnalysisId: me.businessId
		    	 },
		    	 success: function (form, action) {
		    		 return true;
		    	 },
		    	 failure: function (form, action) {
		    		 return false;
		    	 }
	        });
    	}
    },
    saveData:function(){
    	var me=this;
    	
    	//Ext.Msg.alert("layout save data ......");
    	if(!me.getForm().isDirty()){
    		//未修改任何数据，直接显示上一步
    		return true;
    	}else{
    		//已修改，保存返回成功后，再跳转
            var vobj = me.getForm().getValues();
            if(!me.getForm().isValid()){
            	Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'存在未通过的验证!');
    	        return false;
            }
            //名称和说明需要填写
            if(!vobj.name){
            	Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'名称不能为空!');
             	return false;
            }
            //布局类型需要选择
            if(!me.layoutType){
            	Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'请选择布局类型!');
             	return false;
            }
            //布局属性需要选择
            if(!me.radioAttribute){
            	Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'请选择布局属性并设置属性值!');
             	return false;
            }
            
            var flag = true;
            //验证form
    		FHD.ajax({
    	        url: __ctxPath + '/themeAnalysis/validateThemeAnalysisForm.f',
    	        async:false,
    	        params: {
    		        themeAnalysisId: vobj.id,
    		        name: vobj.name
    	        },
    	        callback: function (data) {
    	        	if (data.success) {
    	        		FHD.notification('主题分析名称重复!',FHD.locale.get('fhd.common.prompt'));
    	        		flag = false;
    	        		return false;
    	        	}else{
    	        		//保存数据
    			        FHD.submit({
    				        form: me.getForm(),
    				        url: __ctxPath + '/themeAnalysis/mergeThemeAnalysisByForm.f',
    				        callback: function (data) {
    				        	if(data.success){
    				        		//保存参数
    				        		var themeanalysismainpanel = me.up('themeanalysismainpanel');
    				        		if(themeanalysismainpanel){
    				        			themeanalysismainpanel.paramObj.businessId = data.themeAnalysisId;
    				        			themeanalysismainpanel.paramObj.editflag = true;
    				        			
    				        			me.businessId = data.themeAnalysisId;
    				        			me.editflag=true;
    				        			me.loadData(data.themeAnalysisId, true);
    				        		}
				        		}
    				        }
    			        });
    	        	}
    	        }
            });
    		if(flag){
    			return true;
    		}else{
    			return false;
    		}
    	}
    },
    loadData:function(businessId,editflag){
    	var me=this;
    	
    	me.businessId = businessId;
    	me.editflag = editflag;
    	//alert(me.businessId+'\t'+me.editflag);
    	if(editflag){
			//修改：加载form数据
			me.getForm().load({
		    	 url: __ctxPath + '/themeAnalysis/findThemeAnalysisById.f',
		    	 params: {
		    		 themeAnalysisId: me.businessId
		    	 },
		    	 success: function (form, action) {
		    		 me.layoutType = form.getValues().layoutType;
		    		 me.layoutTypeHiddenValue.setValue(me.layoutType);
		    		 var index = me.dataview.getStore().find('name', me.layoutType);
					 var record = me.dataview.getStore().getAt(index);
		    		 me.dataview.getSelectionModel().select(record);
		    		 return true;
		    	 },
		    	 failure: function (form, action) {
		    		 return false;
		    	 }
	        });
		}else{
			//新增：重置表单
			me.getForm().reset();
		}
    },
   	reloadData:function(){
   		var me=this;
   		
   	}
});