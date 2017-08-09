Ext.define('FHD.view.kpi.cmp.selector.StrategyMapSelector', {
	extend : 'Ext.container.Container',
	alias : 'widget.kpistrategymapselector',
	requires:['FHD.view.kpi.cmp.selector.StrategyMapSelectorWindow'],
	layout: {
        type: 'column'
    },
	
    /*属性*/
    /**
     * 是否单选
     * @type Boolean
     */
	single:true,
	/**
	 * 高度
	 * @type Number
	 */
	height: 22,
	/**
	 * 当前值
	 * @type String
	 */
	value:'',
	/**
	 * 标示名称
	 * @type String
	 */
	labelText: $locale('kpistrategymapselector.labeltext'),
	/**
	 * 标示对齐方式
	 * @type String
	 */
	labelAlign:'left',
	/**
	 * 标示宽度
	 * @type Number
	 */
	labelWidth:50,
	
	/*成员*/
	grid:null,
	field:null,
	/**
	 * 弹出窗口：默认未开启
	 * @type 
	 */
	selectorWindow:null,
    /**
     * 按钮的高度
     */
    btnHeight: 30,
    
    /**
     * 按钮的宽度
     */
    btnWidth:25,
	
	//是否显示组织目标树
	OrgSmTreeVisible : false,
	//是否显示目标树
	smTreeVisible : true,
	//是否显示我的目标树
	mineSmTreeVisible : false,
	//设置机构目标树图标
	orgSmTreeIcon : 'icon-org',
	//设置目标树图标
	smTreeIcon : 'icon-flag-red',
	//设置我的目标树图标
	mineSmTreeIcon : 'icon-orgsub',
	extraParams : {},

	
	/*方法*/
	initValue:function(value,invalidValue){
		var me=this;
		var ids;
		if(value) {
			ids=value.split(",");
			FHD.ajax({
	        	url: __ctxPath + '/kpi/KpiStrategyMap/findSmById',
	        	params:{ids:ids},
	        	callback:function(kpiStrategyMaps){
	 				var ids=new Array();
	        		me.grid.store.removeAll();
	        		Ext.Array.each(kpiStrategyMaps,function(kpiStrategyMap){
	        			var kpiStrategyMapTemp= new KpiStrategyMap({
			    			id:kpiStrategyMap.id,
			    			dbid:kpiStrategyMap.dbid,
			    			text:kpiStrategyMap.text,
			    			type:kpiStrategyMap.type
			    		});
						ids.push(kpiStrategyMapTemp.data.id);
		        		me.grid.store.insert(me.grid.store.count(),kpiStrategyMapTemp);
	        		})
					var value=ids.join(",");
	        		me.value = value;
	        		me.field.setValue(value);
	        	}
	        });
		} else {
			var kpiStrategyMapTemp= new KpiStrategyMap({
    			id:'sm_root',
    			dbid:'sm_root',
    			text:FHD.locale.get('fhd.sm.strategymaps'),
    			type: 'sm'
    		});
    		me.grid.store.removeAll();
			me.grid.store.insert(me.grid.store.count(),kpiStrategyMapTemp);
			me.value  = 'sm_root';
			me.field.setValue('sm_root')
		}
		
		if(invalidValue) {
			me.extraParams.invalidSmId = invalidValue;
		}

	},
    /**
     * 设定当前值
     * @param {} value设定值
     */
    setValue:function(value){
    	var me = this;
    	me.value = value;
		me.field.setValue(value);
    },
    
    setValues:function(values){
    	var me = this;
    	var ids=new Array();
		me.grid.store.removeAll();
    	values.each(function(value){
    		ids.push(value.data.id);
    		me.grid.store.insert(me.grid.store.count(),value);
    	});
    	var value=ids.join(",");
    	me.value = value;
		me.field.setValue(value);
    },
    /**
     * 获得当前值
     * @return {当前值}
     */
    getValue:function(){
    	var me = this;
		me.value = me.field.getValue();
    	return me.value;
    },
	getValues:function(){
    	var me = this;
		return me.grid.store;
    },
	/**
	 * 初始化方法
	 */
    initComponent: function() {
		Ext.define('KpiStrategyMap', {
		    extend: 'Ext.data.Model',
		    fields:['id', 'code', 'text','dbid','type']
		});
    	
        var me = this;
		
		me.field=Ext.widget('textfield',{
			hidden:true,
	        name:me.name,
	        value:me.value
        });
		
		me.grid=Ext.widget('grid',{
        	hideHeaders:true,
            height: me.height,
            columnWidth: 1,
        	columns:[{
        		xtype: 'gridcolumn',
                dataIndex: 'text',
                flex:2,
                renderer:function(value, metaData, record, rowIndex, colIndex, store){
					return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";					
			    }
        	},{
        		xtype:'templatecolumn',
                tpl:'<font class="icon-close" style="cursor:pointer;">&nbsp&nbsp&nbsp&nbsp</font>',
            	width:35,
            	align:'center',
            	listeners:{
            		click:{
            			fn:function(t,d,i){
							me.grid.store.removeAt(i);
							var ids=new Array();
                        	me.grid.store.each(function(r){
                        		ids.push(r.data.id);
                        	});
                        	var value = ids.join(",");
                        	me.value = value;
                    		me.field.setValue(value);
            			}
            		}
            	}
        	}],
        	store:Ext.create('Ext.data.Store',{
        		idProperty: 'id',
        		proxy : {
					type : 'ajax',
					url : __ctxPath + '/kpi/Kpi/listMap',
					reader : {
						type : 'json',
						root : 'users'
					}
				},
			    fields:['id', 'code', 'text','dbid','type']
        	})
        });

        Ext.applyIf(me, {
            items: [
            	{
            		xtype:'label',
            		width:me.labelWidth,
            		text:me.labelText + ':',
            		height: me.height,
            		style:{
            			marginTop: '3px',
            			marginRight: '5px',
            			textAlign: me.labelAlign
            		}
            	},
            	me.grid,
            	{
                    xtype: 'button',
                    width:me.btnWidth,
                    height: me.btnHeight,
                   // iconCls:'icon-kpistrategymap-add',
                    iconCls:'icon-magnifier',
                   // columnWidth: 0.1,
                    handler:function(){
						me.selectorWindow= Ext.create('FHD.view.kpi.cmp.selector.StrategyMapSelectorWindow',{
							extraParams:me.extraParams,
							single:me.single,
							values:me.getValues(),
							smTreeVisible : me.smTreeVisible,
							//设置我的目标树图标
							mineSmTreeIcon : me.mineSmTreeIcon,
							onSubmit:function(values){
								me.setValues(values);
							}
						}).show();
					}
                },
                me.field
            ]
        });
		me.callParent(arguments);
		me.initValue(me.value);
    }
})