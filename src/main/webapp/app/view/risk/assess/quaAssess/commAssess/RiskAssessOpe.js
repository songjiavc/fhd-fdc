/**
 * 
 * 定性评估操作面板
 */

Ext.define('FHD.view.risk.assess.quaAssess.commAssess.RiskAssessOpe', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.riskAssessOpe',
    
    requires: [
				'FHD.view.risk.assess.quaAssess.commAssess.RiskOperAssess'
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
    
    load : function(riskId, templateId) {
    	var me = this;
		var riskDatas = [];
		var value = {};
    	value['riskId'] = riskId;
    	value['templateId'] = templateId;
    	value['rangObjectDeptEmpId'] = Math.random();
    	value['riskName'] = '';
    	riskDatas.push(value);
		
		me.riskDatas = riskDatas;
		
		//me.body.mask("读取中...","x-mask-loading");
		me.onId = '';
    	
		FHD.ajax({
            url: __ctxPath + '/assess/quaassess/assessRiskFindRiskInfoByIds.f',
            params: {
            	params : Ext.JSON.encode(riskDatas)
            },
            callback: function (data) {
                if (data && data.success) {
                	
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
                	
                	for(var i = 0; i < data.totalCount; i++){
                		if(data.data[i] != null){
                			riskId = data.data[i].split('--')[1];
                			riskName = data.data[i].split('--')[0];
                			rangObjectDeptEmpId = data.data[i].split('--')[2];
                			evaluate = data.data[i].split('--')[3];
                			
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
                			htmls +=  "<div id='" + riskId + "panelDiv'></div>";
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
			        	 //margin : '5 5 5 5',
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
                	me.htmlPanel.doLayout();
                	//me.body.unmask();
                }
            }
        });
	},
	
	loadInit : function(templateId) {
    	var me = this;
		var riskDatas = [];
		var value = {};
    	value['templateId'] = templateId;
    	value['rangObjectDeptEmpId'] = Math.random();
    	riskDatas.push(value);
		
		me.riskDatas = riskDatas;
		
		//me.body.mask("读取中...","x-mask-loading");
		me.onId = '';
    	
		FHD.ajax({
            url: __ctxPath + '/assess/quaassess/assessRiskFindRiskInfo.f',
            params: {
            	params : Ext.JSON.encode(riskDatas)
            },
            callback: function (data) {
                if (data && data.success) {
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
                	
                	for(var i = 0; i < data.totalCount; i++){
                		if(data.data[i] != null){
                			riskId = data.data[i].split('--')[1];
                			riskName = data.data[i].split('--')[0];
                			rangObjectDeptEmpId = data.data[i].split('--')[2];
                			evaluate = data.data[i].split('--')[3];
                			
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
                			htmls +=  "<div id='" + riskId + "panelDiv'></div>";
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
			        	 //margin : '5 5 5 5',
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
                	me.htmlPanel.doLayout();
                	//me.body.unmask();
                }
            }
        });
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
        
        me.operAssess = Ext.widget('riskOperAssess',{meMap : me.map});
        
        Ext.apply(me, {
        	//margin : '5 5 5 5',
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