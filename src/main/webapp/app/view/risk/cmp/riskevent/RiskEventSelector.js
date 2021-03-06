/**
 * 左侧事件折叠树，右侧待选列表和已选列表布局
 * @author 郑军祥 2013-07-13
 * */

Ext.define('FHD.view.risk.cmp.riskevent.RiskEventSelector', {
	extend : 'Ext.container.Container',
	alias : 'widget.riskeventselector',
	
	/**
	 * 接口属性
	 */
	winWidth:1000,
	winHeight:500,
	
	layout: {
        type: 'column'
    },
	autoWidth: true,
	multiSelect: true,
	allowBlank: true,
	labelWidth:95,
	disabled:false,
	noteField:'code',	//括号里的字段值
	columns:[{dataIndex: 'code',header: '编号'},{dataIndex: 'name',header: '名称',isPrimaryName:true}],
	initUrl:'/risk/initPotentialRiskEventByIds',
	
	//左侧风险分类树的显示
    riskcatalogtreevisable:false,
    //左侧我的文件夹树显示
    riskmyfoldertreevisable:false,
    //右侧是否显示评估状态灯
    showLight:false,
    
	initComponent : function() {
		var me = this;
				
    	me.gridColumns = [];		//列表项
        me.modelFields = ['id'];	//列表实体定义

        //1. 构建grid的columns
        for(var i = 0; i < me.columns.length; i++) {
        	var obj = {};
        	obj['xtype'] = 'gridcolumn';
        	obj['dataIndex'] = me.columns[i]['dataIndex'];
        	obj['text'] = me.columns[i]['header'];
        	obj['hidden'] = true;
        	if(me.columns[i]['isPrimaryName']){
        		obj['hidden'] = false;
        		obj['flex'] = 2;
        		obj['renderer'] = function(value, metaData, record, rowIndex, colIndex, store){//动态
        			if(me.multiSelect){
	        			return "<div onClick=\"Ext.getCmp('" + me.id + "').showRiskEventDetail('" + record.get('id') + "')\" data-qtitle='' data-qtip='" + value+" ( "+record.get(me.noteField)+" ) " + "'>" + value + " ( "+record.get(me.noteField)+" ) " + "</div>";					
        			}else{
	        			return "<div style='white-space:normal;overflow:hidden;' onClick=\"Ext.getCmp('" + me.id + "').showRiskEventDetail('" + record.get('id') + "')\" data-qtitle='' data-qtip='" + value+" ( "+record.get(me.noteField)+" ) " + "'>" + value + " ( "+record.get(me.noteField)+" ) " + "</div>";					
        			}
			    }
        	}
        	me.gridColumns.push(obj);
        	
        	//构建modelField
        	me.modelFields.push(me.columns[i]['dataIndex']);
        }
        me.gridColumns.push({
    		xtype:'templatecolumn',
        	tpl:'<font class="icon-close" style="cursor:pointer;">&nbsp&nbsp&nbsp&nbsp</font>',
        	width:35,
        	align:'center',
        	listeners:{
        		click:{
        			fn:function(grid,d,i){
        				var select=grid.store.getAt(i);
        				me.removeFromStore(select);
        			}
        		}
        	}
    	});
        
		if(me.multiSelect){
			if(me.height){
				me.height = me.height;
			}else{
				me.height = 80;				
			}
		}else{
			if(me.height){
				me.height = me.height;
			}else{
				me.height = 36;				
			}
		}
    	me.field=Ext.widget('textfield',{
			hidden:true,
	        name:me.name,
	        value: me.value,
	        allowBlank:me.allowBlank,
	        listeners:{
				change:function (field,newValue,oldValue,eOpts ){
					//me.initValue(newValue);
				}
		    }
        });
        me.label=Ext.widget('label',{
    		width: me.labelWidth,
    		html: '<span style="float:'+me.labelAlign+'">'+me.fieldLabel + ':</span>',
    		height: 22,
    		style: {
    			marginRight: '10px'
    		}
    	});
    	
        /**grid数据源*/
	    me.valueStore = Ext.create('Ext.data.Store',{
    		idProperty: 'id',
    		queryMode: 'local',
		    fields:me.modelFields
    	})
      
		me.grid=Ext.widget('grid',{
        	hideHeaders: true,
        	autoScroll: true,
            height: me.height,
            columnWidth: 1,
        	columns:me.gridColumns,
        	store:me.valueStore
        });
        me.button=Ext.widget('button',{
            iconCls:'icon-magnifier',
            height: 22,
            width: 22,
            disabled: me.disabled,
            handler:function(){
            	var selects = new Array();
            	me.getGridStore().each(function(r){
            		selects.push(r);
            	});
				me.window = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow',{
					title:me.title,
					width:me.winWidth,
					height:me.winHeight,
					multiSelect:me.multiSelect,
					typeId:me.typeId,
					riskcatalogtreevisable:me.riskcatalogtreevisable,
					riskmyfoldertreevisable:me.riskmyfoldertreevisable,
					showLight:me.showLight,
					onSubmit:function(win){
						me.grid.store.removeAll();
						//var recs = win.selectedgrid.store.getRange(0,win.selectedgrid.store.getTotalCount()-1);
						me.setValueFromStore(win.selectedgrid.store);
					}
				}).show();
				me.window.setValue(selects);
		    }
    	});

        Ext.applyIf(me, {
            items: [
            	me.label,
            	me.grid,
            	me.button,
                me.field
            ]
        });
        me.callParent(arguments);
        me.initValue(me.value);
	},
	initValue: function(value){
		var me=this;
		if(value==null||value==""){
			value=me.field.getValue();
		}
		var ids=value.split(",");
		if(!me.allowBlank){
			if(value && value !='[]'){
				me.grid.bodyStyle = 'background:#FFFFFF';
			}else{
				me.grid.bodyStyle = 'background:#FFEDE9';
			}
		}
		if(value && value !='[]'){
			FHD.ajax({//ajax调用
				url : __ctxPath + me.initUrl,
				params : {
					ids:value
				},
				callback : function(data){

					var records = data.data;
	    			Ext.Array.each(records,function(r,i){
		        		me.grid.store.removeAt(i);
		        		me.grid.store.insert(i, r);
		        	});
				}
			});
		}else{
			/*如果传入的是null,则清空parentId*/
			me.grid.store.removeAll();
		}
		
	},
	showRiskEventDetail: function(id){
		var me = this;
            // 风险事件基本信息
        var riskEventDetailForm = Ext.create('FHD.view.risk.cmp.form.RiskFullFormDetail', {
            border: false,
            showbar: false
        });
        var window = Ext.create('FHD.ux.Window', {
    		layout: 'fit',
            title: '风险基本信息',
            maximizable: true,
            modal: true,
            width:800,
    		height:400,
            collapsible: true,
            autoScroll: true
        }).show();
        riskEventDetailForm.reloadData(id);
        window.add(riskEventDetailForm);
	},
	
	/**
	 * 为隐藏域赋值
	 *
	 */
	setHiddenValue:function(valueArray){
    	var me = this;
    	if(valueArray.length>0){
    		var value = Ext.JSON.encode(valueArray);
    		me.value = value;
			me.field.setValue(value);
    	}else{
    		me.value = null;
			me.field.setValue(null);
    	}
    },
    /**
	 * 为隐藏域和显示的grid赋值
	 *
	 */
    setValueFromStore:function(values){
    	var me = this;
		var hiddenValue=new Array();
//    	values.each(function(r){
//    		me.grid.store.insert(0,r);
//    	});
		var recs = values.getRange(0,values.getCount()-1);
		me.grid.store.insert(0,recs);
    	var hiddenValue = me.getHiddenValue();
		me.setHiddenValue(hiddenValue);
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
    /**
     * 获得当前要设置的值的数组
     * @return 隐藏域的值的数组
     */
    getHiddenValue: function(){
    	var me = this;
    	var values=me.getGridStore();
    	var hiddenValue=new Array();
    	values.each(function(value){
    		var ids = {};
    		ids['id'] = value.data.id;
    		hiddenValue.push(ids);
    	});
    	return hiddenValue;
    },
    /**
     * 获得当前grid的store
     * @return 当前grid的store
     */
	getGridStore:function(){
    	var me = this;
		return me.grid.store;
    },
    /**
     * 从当前grid的store中删除
     */
	removeFromStore:function(value){
    	var me = this;
    	me.grid.store.remove(value);
    	var hiddenValue = me.getHiddenValue();
    	me.setHiddenValue(hiddenValue);
    },
    /**
     * 清空值
     */
    clearValues : function(){
        var me = this;
        me.field.setValue(null);
        me.grid.store.removeAll();
    }
});