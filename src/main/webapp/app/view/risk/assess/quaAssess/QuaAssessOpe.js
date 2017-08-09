/**
 * 
 * 定性评估操作面板
 */

Ext.define('FHD.view.risk.assess.quaAssess.QuaAssessOpe', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.quaAssessOpe',
    
    requires: [
				'FHD.view.risk.assess.quaAssess.OperAssess'
              ],
              
    Map : function() {
        /** 存放键的数组(遍历用到) */  
        this.keys = new Array();  
        /** 存放数据 */  
        this.data = new Object();  
          
        /** 
         * 放入一个键值对 
         * @param {String} key 
         * @param {Object} value 
         */  
        this.put = function(key, value) {  
            if(this.data[key] == null){  
                this.keys.push(key);  
            }  
            this.data[key] = value;  
        };  
          
        /** 
         * 获取某键对应的值 
         * @param {String} key 
         * @return {Object} value 
         */  
        this.get = function(key) {  
            return this.data[key];  
        };  
          
        /** 
         * 删除一个键值对 
         * @param {String} key 
         */  
        this.remove = function(key) {  
            this.keys.remove(key);  
            this.data[key] = null;  
        };  
          
        /** 
         * 遍历Map,执行处理函数 
         *  
         * @param {Function} 回调函数 function(key,value,index){..} 
         */  
        this.each = function(fn){  
            if(typeof fn != 'function'){  
                return;  
            }  
            var len = this.keys.length;  
            for(var i=0;i<len;i++){  
                var k = this.keys[i];  
                fn(k,this.data[k],i);  
            }  
        };  
          
        /** 
         * 获取键值数组(类似Java的entrySet())
         * @return 键值对象{key,value}的数组 
         */  
        this.entrys = function() {  
            var len = this.keys.length;  
            var entrys = new Array(len);  
            for (var i = 0; i < len; i++) {  
                entrys[i] = {  
                    key : this.keys[i],  
                    value : this.data[i]  
                };  
            }  
            return entrys;  
        };  
          
        /** 
         * 判断Map是否为空 
         */  
        this.isEmpty = function() {  
            return this.keys.length == 0;  
        };  
          
        /** 
         * 获取键值对数量 
         */  
        this.size = function(){  
            return this.keys.length;  
        };  
          
        /** 
         * 重写toString  
         */  
        this.toString = function(){  
            var s = "{";  
            for(var i=0;i<this.keys.length;i++,s+=','){  
                var k = this.keys[i];  
                s += k+"="+this.data[k];  
            }  
            s+="}";  
            return s;  
        };  
    },	
    
    shows : function(riskId,objectId){
    	var me = this;
    	me.quaAssessEdit = Ext.create('FHD.view.risk.assess.quaAssess.QuaAssessEdit',{isEditIdea : false,objectId:objectId});
    	
    	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			width:800,
			height:500,
			title : '风险详细信息查看',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.quaAssessEdit],
			buttons: [
    			{
    				text: '关闭',
    				handler:function(){
    					me.formwindow.close();
    				}
    			}
    	    ]
		});
		me.formwindow.show();
		me.quaAssessEdit.load(riskId, "");
    },
    
    load : function(riskDatas) {
		var me = this;
		
		me.riskDatas = riskDatas;
		
		if(me.items.length !=0){
			me.removeAll();
		}
		
//		me.body.mask("读取中...","x-mask-loading");
		me.onId = '';
		if(me.htmlPanel != undefined){
			if(me.bba != undefined){
				me.htmlPanel.removeAll();
				me.bba.removeAll();
			}
		}
		
		
//    	Ext.getCmp('upbzId').enable();
//    	Ext.getCmp('nextId2').enable();
    	
		FHD.ajax({
            url: __ctxPath + '/assess/quaAssess/assessFindRiskInfoByIds.f',
            params: {
            	params : Ext.JSON.encode(riskDatas),
            	assessPlanId : Ext.getCmp('QuaAssessManId').businessId,
            	executionId : Ext.getCmp('QuaAssessManId').executionId
            },
            callback: function (data) {
                if (data && data.success) {
                	me.infoNav = Ext.widget('infoNav',{height : 10, executionId : Ext.getCmp('QuaAssessManId').executionId, 
                										businessId : Ext.getCmp('QuaAssessManId').businessId});
                	me.infoNav.load(riskDatas);
                	
                	var htmls = '';
                	var otherHtml = '';
                	var evaluate = '';
                	var riskId = '';
                	var riskName = '';
                	var rangObjectDeptEmpId = '';
                	me.totalCount = data.totalCount;
                	var startHtml = "<table border='1'>";
                	var endHtml = '</table>';
                	var aHtml = '';
                	var editConent = '';
                	me.evaluateCount = 0;
                	me.dimDescAllMap = data.dimDescAllMap;
                	me.dicDescAllMap = data.dicDescAllMap;
                	var riskNameShow = '';
                	var objectId = "";
                	
                	
                	for(var i = 0; i < data.totalCount; i++){
                		if(data.data[i] != null){
                			
                			riskId = data.data[i].split('--')[1];
                			riskName = data.data[i].split('--')[0];
                			rangObjectDeptEmpId = data.data[i].split('--')[2];
                			evaluate = data.data[i].split('--')[3];
                			objectId = data.data[i].split('--')[4];
                			
                			if(evaluate == 'true'){
                				me.evaluateCount++;
                				//已评价
                				otherHtml = "<img style='height:13px; width:13px;background-position: center center;'  id=" + 
                				riskId + "panelImg src='" + __ctxPath + "/images/icons/state_ok.gif'></img>　" +
                						"<a id=" + riskId + "panelSpan href=javascript:void(0) " +
											" onclick=Ext.getCmp('" + me.id + "').assessAppLoad('" + i + "')>评价<a/>";
                			}else{
                				//未评价
                				otherHtml = "<img style='height:13px; width:13px;background-position: center center;' id=" + 
                				riskId + "panelImg src='" + __ctxPath + "/images/icons/state_error.gif'></img>　" +
                					"<a id=" + riskId + "panelSpan href=javascript:void(0) " +
                						" onclick=Ext.getCmp('" + me.id + "').assessAppLoad('" + i + "')>评价<a/>";
                				
                			    if(me.onId == ''){
	                				me.count = i;
	                				me.onId = riskId + 'panelDiv';
	                				me.onRangObjectDeptEmpId = rangObjectDeptEmpId;
	                				me.results = data.result[i];
	                				if(data.data[i].split('--').length == 5){
	                					editConent = data.data[i].split('--')[4];
	                				}
                			    }
                			}
                			
                			if(riskName.length > 60){
                				riskNameShow = riskName.substring(0, 60) + '...';
                			}else{
                				riskNameShow = riskName;
                			}
                			
                			htmls +=  '<div style="border:1px solid #D0D0D0;border-top:0px solid #D0D0D0;height:30px;">' + 
                							otherHtml + '　　 '+ (i + 1) + ".　<a href=javascript:void(0) " +
    									"onclick=Ext.getCmp('" + me.id + "').shows('" + riskId + "','" + objectId + "') data-qtip="+ riskName +" data-qwidth=300>" 
            				 +  riskNameShow + "</a></div>" + 
            						"<div id='" + riskId + "panelDiv'></div>";
                		}
                	}
                	
                	//初始化没有渲染时
                	if(me.onId == ''){
                		for(var i = 0; i < data.totalCount; i++){
                			riskId = data.data[i].split('--')[1];
                			riskName = data.data[i].split('--')[0];
                			rangObjectDeptEmpId = data.data[i].split('--')[2];
                			evaluate = data.data[i].split('--')[3];
                			
                			if(me.onId == ''){
                				me.count = i;
                				me.onId = riskId + 'panelDiv';
                				me.onRangObjectDeptEmpId = rangObjectDeptEmpId;
                				me.results = data.result[i];
                				if(data.data[i].split('--').length == 5){
                					editConent = data.data[i].split('--')[4];
                				}
            			    }
                			break;
                		}
                	}
                	
                	me.htmlPanel = Ext.create('Ext.panel.Panel',{
			        	 border : false,
			        	 margin : '15 15 15 15',
			        	 html : htmls
			        });
                	
                	me.add(me.htmlPanel);
                	
                	if(me.onId != ''){
                		me.bba = Ext.create('Ext.panel.Panel',{
   				        	border : false,
   				        	renderTo : me.onId,
   				        	items : [me.operAssess.getFieldSetAssess(me.results, me, me.onId, me.onRangObjectDeptEmpId, editConent, me.onId + 'panel')]
            			});
            			me.operAssess.assessRender(me.results, me);
                	}
                	
                	if(data.totalCount != 0){
                		if(me.bba.items.items[0].items.items[0] != null){
                    		me.bba.items.items[0].items.items[0].items.items[0].setHeight(me.bba.items.items[0].items.items[0].items.items[0].getHeight() + 30);
                    	}
                	}
                	
                	if(Ext.getCmp('QuaAssessManId').winId != null){
                		Ext.getCmp('quaAssessCardId').setHeight(Ext.getCmp(Ext.getCmp('QuaAssessManId').winId).getHeight() - 155);
                	}else{
                		Ext.getCmp('quaAssessCardId').setHeight(Ext.getCmp('QuaAssessManId').getHeight() - 155);
                	}
                	me.body.unmask();
                }
            }
        });
	},
	
	assessAppLoad : function(count, operAsses){
		var me = this;
		
		me.body.mask("读取中...","x-mask-loading");
		FHD.ajax({
            url: __ctxPath + '/assess/quaAssess/assessFindRiskInfoByIds.f',
            params: {
            	params : Ext.JSON.encode(me.riskDatas),
            	assessPlanId : Ext.getCmp('QuaAssessManId').businessId,
            	executionId : Ext.getCmp('QuaAssessManId').executionId
            },
            callback: function (data) {
                if (data && data.success) {
                	
                	var editConent = '';
                	if(count == me.totalCount){
                		count--;
                	}
                	for(var i = count; i < me.totalCount; i++){
                		if(data.data[i] != null){
                			if(i == count){
                				me.onId = data.data[i].split('--')[1] + 'panelDiv';
                				me.onRangObjectDeptEmpId = data.data[i].split('--')[2];
                				me.results = data.result[i];
                				if(data.data[i].split('--').length == 5){
                					editConent = data.data[i].split('--')[4];
                				}
                				
                				break;
                			}
                		}
                	}
                	
                	if(count != me.totalCount){
                		if(me.bba != undefined){
                    		me.bba.removeAll();
                    	}
                		
                    	me.bba = Ext.create('Ext.panel.Panel',{
                        	 border : false,
                        	 renderTo : me.onId,
                        	 items : [me.operAssess.getFieldSetAssess(me.results, me, me.onId, me.onRangObjectDeptEmpId, editConent, me.onId + 'panel')]
                        });
                    	
                    	me.operAssess.assessRender(me.results, me);
                    	if(me.bba.items.items[0].items.items[0] != null){
                    		me.bba.items.items[0].items.items[0].items.items[0].setHeight(me.bba.items.items[0].items.items[0].items.items[0].getHeight() + 30);
                    	}
                    	me.infoNav = Ext.widget('infoNav',{height : 10, executionId : Ext.getCmp('QuaAssessManId').executionId, 
                			businessId : Ext.getCmp('QuaAssessManId').businessId});
                    	me.infoNav.load(me.riskDatas);
                    	me.count = count;
                	}
                	
                	
                	me.body.unmask();
                }
            }
        });
//    	me.pxs = me.body.dom.scrollTop + me.pxs - 10;
//    	me.body.dom.scrollTop = me.pxs;
	},
	
	//加载评价
	evaluateLoad : function(rangObjectDeptEmpId, riskId){
		var me = this;
		
		riskId = riskId.replace('panelDiv', '');
		
		FHD.ajax({
            url: __ctxPath + '/assess/quaAssess/assessEvaluate.f',
            params: {
            	rangObjectDeptEmpId : rangObjectDeptEmpId,
            	assessPlanId : Ext.getCmp('QuaAssessManId').businessId,
            	executionId : Ext.getCmp('QuaAssessManId').executionId,
            	riskId : riskId
            },
            callback: function (data) {
                if (data && data.success) {
                	if(data.evaluate){
                		document.getElementById(riskId + 'panelSpan').innerHTML = '评价';
                		document.getElementById(riskId + 'panelImg').src =  __ctxPath + '/images/icons/state_ok.gif';
                		me.evaluateCount++;
                	}else{
                		document.getElementById(riskId + 'panelSpan').innerHTML = '评价';
                		document.getElementById(riskId + 'panelImg').src =  __ctxPath + '/images/icons/state_error.gif';
                	}
                }
            }
        });
	},
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.map = new me.Map();
        
        me.operAssess = Ext.widget('operAssess',{meMap : me.map});
        
        Ext.apply(me, {
        	margin : '5 5 5 5',
        	border : false,
        	layout:{
                align: 'stretch',
                type: 'vbox'
    		},
    		//height : 100,
    		autoScroll:true
        });
        
        me.callParent(arguments);
    }
});