Ext.define('FHD.view.kpi.alarm.AlarmEditPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.alarmeditpanel',
    border: false,
    bodyPadding: 5,
    requires: ['FHD.ux.dict.DictSelectForEditGrid'],

    validate: function () {
        var me = this;
        var alarmname = Ext.getCmp('alarm_name' + main_rndNum).value;
        var validateItems = {
            "name": alarmname
        };
        var flag = false;
        if(alarmname!=null&&alarmname.length>255){
        	FHD.alert("方案名称过长，名称长度为255字符，请重新设置。");
        	return false;
        }
        FHD.ajax({
            url: __ctxPath + "/kpi/alarm/validate.f",
            async: false, // 这一项必须加上，否则后台返回true,flag的值也为false
            params: {
                mode: me.isAdd,
                id: me.param.id,
                name: alarmname,
                validateItems: Ext.JSON.encode(validateItems)
            },
            callback: function (data) {
                if (data && data.success) {
                    flag = true;
                    FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
                } else {
                    if (data.error == "nameRepeat") {
                        Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarmplan.prompt.nameRepeat'));
                    }
                }
            }
        })
        return flag;
    },
    getLevel: function (id) {
        var me = this;
        var level = "";
        var storeItems = me.rangelist.store.data.items;
        Ext.Array.each(storeItems, function (object) {
            if (object.data.id == id) {
                level = object.data.level;
            }
        });
        return level;
    },
    bubbleSort: function (arr) {
        var me = this;
        var temp;
        var exchange;
        for (var i = 0; i < arr.length; i++) {
            exchange = false;
            for (var j = arr.length - 2; j >= i; j--) {
                if (parseInt(arr[j + 1].formulaMax) < parseInt(arr[j].formulaMax)) {
                    temp = arr[j + 1];
                    arr[j + 1] = arr[j];
                    arr[j] = temp;
                    exchange = true;
                }
            }
            if (!exchange) break;
        }
        return arr;
    },
    validateRangeValue: function () {
        var me = this;
        if (me.rangeObjectList) {
            var rangeArray = [];
            var hlevel = {};
            hlevel.level = '';
            hlevel.formulaMax = '';
            hlevel.formulaMin = '';
            hlevel.maxSign = '';
            hlevel.minSign = '';
            var mlevel = {};
            mlevel.level = '';
            mlevel.formulaMax = '';
            mlevel.formulaMin = '';
            mlevel.maxSign = '';
            mlevel.minSign = '';
            var llevel = {};
            llevel.level = '';
            llevel.formulaMax = '';
            llevel.formulaMin = '';
            llevel.maxSign = '';
            llevel.minSign = '';
            var notequalSign = false;
            var hflag = false;
            var lflag = false;
            var mflag = false;
            for (var i = 0; i < me.rangeObjectList.length; i++) {
                var obj = me.rangeObjectList[i];
                var maxSign = obj.signTwoId;
                var minSign = obj.signOneId;
                if ('0_compare_symbol=' == maxSign || '0_compare_symbol=' == minSign || '0_compare_symbol!=' == maxSign || '0_compare_symbol!=' == minSign) {
                    notequalSign = true;
                    break;
                }
            }
            if (!notequalSign) {
                //不包含'='和'!='继续校验
                for (var i = 0; i < me.rangeObjectList.length; i++) {
                    var rangeObj = {};
                    var obj = me.rangeObjectList[i];
                    rangeObj.level = me.getLevel(obj.id);
                    rangeObj.formulaMax = obj.formulaTwo;
                    rangeObj.formulaMin = obj.formulaOne;
                    rangeObj.maxSign = obj.signTwoId;
                    rangeObj.minSign = obj.signOneId;
                    if ('0alarm_startus_h' == rangeObj.level) {
                        hlevel = rangeObj;
                    } else if ('0alarm_startus_l' == rangeObj.level) {
                        llevel = rangeObj;
                    } else if ('0alarm_startus_m' == rangeObj.level) {
                        mlevel = rangeObj;
                    }
                    rangeArray.push(rangeObj);
                }
                if (me.rangeObjectList.length == 3) {
                    if (me.rangeObjectList.length == 3) {
                        for (var i = 0; i < me.rangeObjectList.length; i++) {
                            var obj = me.rangeObjectList[i];
                            if ('0alarm_startus_h' == me.getLevel(obj.id)) {
                                hflag = true;
                            } else if ('0alarm_startus_l' == me.getLevel(obj.id)) {
                                lflag = true;
                            } else if ('0alarm_startus_m' == me.getLevel(obj.id)) {
                                mflag = true;
                            }
                        }
                        if (!(hflag && lflag && mflag)) {
                            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.alarm.level.h.m.l'));
                            return false;
                        }
                    } else {
                        Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.alarm.level.h.m.l'));
                        return false;
                    }
                    if(hlevel.formulaMin!=''&&hlevel.formulaMax!=''){
                    	if(parseInt(hlevel.formulaMin)>=parseInt(hlevel.formulaMax)){
                    		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                            return false;
                    	}
                    }
                    if(mlevel.formulaMin!=''&&mlevel.formulaMax!=''){
                    	if(parseInt(mlevel.formulaMin)>=parseInt(mlevel.formulaMax)){
                    		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                            return false;
                    	}
                    }
                    if(llevel.formulaMin!=''&&llevel.formulaMax!=''){
                    	if(parseInt(llevel.formulaMin)>=parseInt(llevel.formulaMax)){
                    		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                            return false;
                    	}
                    }
                    //排序
                    var sortArray = me.bubbleSort(rangeArray);
                    llevel = sortArray[0];
                    mlevel = sortArray[1];
                    hlevel = sortArray[2];
                    //校验区间值
                    if (mlevel.formulaMax != '' && hlevel.formulaMin != '' && parseInt(mlevel.formulaMax) == parseInt(hlevel.formulaMin)) {
                        if (mlevel.maxSign != '' && hlevel.minSign != '' && mlevel.maxSign == hlevel.minSign&&mlevel.maxSign=='0_compare_symbol<=') {
                            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                            return false;
                        }
                    } else {
                        if (mlevel.formulaMax != '' && hlevel.formulaMin != '' && parseInt(mlevel.formulaMax )> parseInt(hlevel.formulaMin)) {
                            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                            return false;
                        }
                    }

                    if (mlevel.formulaMin != '' && llevel.formulaMax != '' && parseInt(mlevel.formulaMin) == parseInt(llevel.formulaMax)) {
                        if (mlevel.minSign != '' && llevel.maxSign != '' && mlevel.minSign == llevel.maxSign&&mlevel.minSign=='0_compare_symbol<=') {
                            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                            return false;
                        }
                    } else {
                        if (mlevel.formulaMin != '' && llevel.formulaMax != '' && parseInt(mlevel.formulaMin) < parseInt(llevel.formulaMax)) {
                            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                            return false;
                        }
                    }

                } else if (me.rangeObjectList.length == 2) {
                	var sortArray = me.bubbleSort(rangeArray);
                    for (var i = 0; i < me.rangeObjectList.length; i++) {
                        var obj = me.rangeObjectList[i];
                        if ('0alarm_startus_h' == me.getLevel(obj.id)) {
                            hflag = true;
                        } else if ('0alarm_startus_l' == me.getLevel(obj.id)) {
                            lflag = true;
                        } else if ('0alarm_startus_m' == me.getLevel(obj.id)) {
                            mflag = true;
                        }
                    }
                    if (!((hflag && lflag) || (hflag && mflag) || (mflag && lflag))) {
                        Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.alarm.level.err'));// '等级设置错误.'
                        return false
                    }
                    if (hflag && mflag) {
                    	mlevel =  sortArray[0];
                    	hlevel =  sortArray[1];
                    	if(hlevel.formulaMin!=''&&hlevel.formulaMax!=''){
                        	if(parseInt(hlevel.formulaMin)>=parseInt(hlevel.formulaMax)){
                        		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                        	}
                        }
                        if(mlevel.formulaMin!=''&&mlevel.formulaMax!=''){
                        	if(parseInt(mlevel.formulaMin)>=parseInt(mlevel.formulaMax)){
                        		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                        	}
                        }
                        //校验区间值
                        if (mlevel.formulaMax != '' && hlevel.formulaMin != '' && parseInt(mlevel.formulaMax) == parseInt(hlevel.formulaMin)) {
                            if (mlevel.maxSign != '' && hlevel.minSign != '' && mlevel.maxSign ==hlevel.minSign&&mlevel.maxSign=='0_compare_symbol<=') {
                                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                            }
                        } else {
                            if (mlevel.formulaMax != '' && hlevel.formulaMin != '' && parseInt(mlevel.formulaMax) > parseInt(hlevel.formulaMin)) {
                                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                            }
                        }
                    } else if (hflag && lflag) {
                    	llevel =  sortArray[0];
                    	hlevel =  sortArray[1];
                    	if(hlevel.formulaMin!=''&&hlevel.formulaMax!=''){
                        	if(parseInt(hlevel.formulaMin)>=parseInt(hlevel.formulaMax)){
                        		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                        	}
                        }
                        if(llevel.formulaMin!=''&&llevel.formulaMax!=''){
                        	if(parseInt(llevel.formulaMin)>=parseInt(llevel.formulaMax)){
                        		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                        	}
                        }
                        //校验区间值
                        if (llevel.formulaMax != '' && hlevel.formulaMin != '' && parseInt(llevel.formulaMax) == parseInt(hlevel.formulaMin)) {
                            if (llevel.maxSign != '' && hlevel.minSign != '' && llevel.maxSign == hlevel.minSign&&llevel.maxSign=='0_compare_symbol<=') {
                                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                            }
                        } else {
                            if (llevel.formulaMax != '' && hlevel.formulaMin != '' && parseInt(llevel.formulaMax) > parseInt(hlevel.formulaMin)) {
                                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                            }
                        }
                    } else if (mflag && lflag) {
                    	llevel =  sortArray[0];
                    	mlevel =  sortArray[1];
                        if(mlevel.formulaMin!=''&&mlevel.formulaMax!=''){
                        	if(parseInt(mlevel.formulaMin)>=parseInt(mlevel.formulaMax)){
                        		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                        	}
                        }
                        if(llevel.formulaMin!=''&&llevel.formulaMax!=''){
                        	if(parseInt(llevel.formulaMin)>=parseInt(llevel.formulaMax)){
                        		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                        	}
                        }
                        //校验区间值
                        if (llevel.formulaMax != '' && mlevel.formulaMin != '' && parseInt(llevel.formulaMax) == parseInt(mlevel.formulaMin)) {
                            if (llevel.maxSign != '' && mlevel.minSign != '' && llevel.maxSign == mlevel.minSign&&llevel.maxSign=='0_compare_symbol<=') {
                                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                            }
                        } else {
                            if (llevel.formulaMax != '' && mlevel.formulaMin != '' && parseInt(llevel.formulaMax) > parseInt(mlevel.formulaMin)) {
                                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                                return false;
                            }
                        }
                    }

                }else if(me.rangeObjectList.length == 1){
                	 var obj = me.rangeObjectList[0];
                	 rangeObj.level = me.getLevel(obj.id);
                     rangeObj.formulaMax = obj.formulaTwo;
                     rangeObj.formulaMin = obj.formulaOne;
                     rangeObj.maxSign = obj.signTwoId;
                     rangeObj.minSign = obj.signOneId;
                     if(rangeObj.formulaMin!=''&&rangeObj.formulaMax!=''){
                     	if(parseInt(rangeObj.formulaMin)>=parseInt(rangeObj.formulaMax)){
                     		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.err'));
                            return false;
                     	}
                     }
                }

            } else {
                //校验高中低-3
            	 if (me.rangeObjectList.length == 3) {
                     for (var i = 0; i < me.rangeObjectList.length; i++) {
                         var obj = me.rangeObjectList[i];
                         if ('0alarm_startus_h' == me.getLevel(obj.id)) {
                             hflag = true;
                         } else if ('0alarm_startus_l' == me.getLevel(obj.id)) {
                             lflag = true;
                         } else if ('0alarm_startus_m' == me.getLevel(obj.id)) {
                             mflag = true;
                         }
                     }
                     if (!(hflag && lflag && mflag)) {
                         Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.alarm.level.h.m.l'));
                         return false;
                     }
                 } 
            	 //校验高中-2
                 //校验高低-2
            	 //校验中低-2
            	  if (me.rangeObjectList.length == 2) {
                     for (var i = 0; i < me.rangeObjectList.length; i++) {
                         var obj = me.rangeObjectList[i];
                         if ('0alarm_startus_h' == me.getLevel(obj.id)) {
                             hflag = true;
                         } else if ('0alarm_startus_l' == me.getLevel(obj.id)) {
                             lflag = true;
                         } else if ('0alarm_startus_m' == me.getLevel(obj.id)) {
                             mflag = true;
                         }
                     }
                     if (!((hflag && lflag) || (hflag && mflag) || (mflag && lflag))) {
                         Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.alarm.level.err'));// '等级设置错误.'
                         return false
                     }
                 }

            }
            return true;
        }
    },
    getStoreValue: function () {
    	 var me = this;
    	 var rangeArray = [];
    	 if (me.rangeObjectList) {
    		 for (var i = 0; i < me.rangeObjectList.length; i++) {
                 var rangeObj = {};
                 var obj = me.rangeObjectList[i];
                 rangeObj.level = me.getLevel(obj.id);
                 rangeObj.formulaMax = obj.formulaTwo;
                 rangeObj.formulaMin = obj.formulaOne;
                 rangeObj.maxSign = obj.signTwoId;
                 rangeObj.minSign = obj.signOneId;
                 if ('0alarm_startus_h' == rangeObj.level) {
                     hlevel = rangeObj;
                 } else if ('0alarm_startus_l' == rangeObj.level) {
                     llevel = rangeObj;
                 } else if ('0alarm_startus_m' == rangeObj.level) {
                     mlevel = rangeObj;
                 }
                 rangeArray.push(rangeObj);
             }
    	 }
        return rangeArray;
    },

    rangeSave: function () {
        var me = this;
        var formulaTwoValue = Ext.getCmp('formulaTwo' + range_rndNum).value;
        if (formulaTwoValue == null || formulaTwoValue == undefined) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),  FHD.locale.get('fhd.alarm.range.value.notnul'));
            return;
        }
        var formulaOneValue = Ext.getCmp('formulaOne' + range_rndNum).value;
        if (formulaOneValue == null || formulaOneValue == undefined) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.value.notnul'));
            return;
        }
        
        if(me.validateValue(formulaOneValue)&&me.validateValue(formulaTwoValue)){//验证数值格式是否正确
        	
        	var signOneId = Ext.getCmp('signOne' + range_rndNum).value;
	        if (signOneId == null || signOneId == "" || signOneId == undefined) {
	            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.sign.notnul'));
	            return;
	        }
	        var signOneValue = signOneId.substr(16);
	
	        //if (signOneValue == "<") {
	        //    signOneValue = "&lt;";
	        //}
	        var signTwoId = Ext.getCmp('signTwo' + range_rndNum).value;
	        if (signTwoId == null || signTwoId == "" || signTwoId == undefined) {
	            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.alarm.range.sign.notnul'));
	            return;
	        }
	        var signTwoValue = signTwoId.substr(16);
	
			if(me.compareValue(formulaOneValue,signOneValue,signTwoValue,formulaTwoValue)){//验证公式是否正确
				
				var selection = me.rangelist.getSelectionModel().getSelection();
		        for (var i = 0; i < selection.length; i++) {
		            var id = selection[i].get('id');
		            for (var j = 0; j < me.rangeObjectList.length; j++) {
		                if (id == me.rangeObjectList[j].id) {
		                    me.rangeObjectList[j].formulaOne = formulaOneValue;
		                    me.rangeObjectList[j].formulaTwo = formulaTwoValue;
		                    me.rangeObjectList[j].signOneId = signOneId;
		                    me.rangeObjectList[j].signOneValue = signOneValue;
		                    me.rangeObjectList[j].signTwoId = signTwoId;
		                    me.rangeObjectList[j].signTwoValue = signTwoValue;
		                }
		            }
		        }
		        var exp = formulaOneValue + signOneValue + "X" + signTwoValue + formulaTwoValue;
		        for (var k = 0; k < selection.length; k++) {
		            selection[k].set({
		                'range': exp
		            });
		        }
		        me.winsub.close();
			}
			
        }
        
      
    },

    setValues: function () {
        var me = this;
        var selection = me.rangelist.getSelectionModel().getSelection();
        for (var i = 0; i < selection.length; i++) {
            var id = selection[i].get('id');
            for (var j = 0; j < me.rangeObjectList.length; j++) {
                if (id == me.rangeObjectList[j].id) {
                    Ext.getCmp('formulaOne' + range_rndNum).setValue(me.rangeObjectList[j].formulaOne);
                    Ext.getCmp('formulaTwo' + range_rndNum).setValue(me.rangeObjectList[j].formulaTwo);
                    Ext.getCmp('signOne' + range_rndNum).setValue(me.rangeObjectList[j].signOneId);
                    Ext.getCmp('signTwo' + range_rndNum).setValue(me.rangeObjectList[j].signTwoId);
                }
            }
        }
    },

    createRndNum: function (n) {
        var rnd = "";
        for (var i = 0; i < n; i++)
        rnd += Math.floor(Math.random() * 10);
        return rnd;
    },
    rangeDel: function () {
        var me = this;
        var k = 0;
        var tempList = [];
        var selection = me.rangelist.getSelectionModel().getSelection();
        for (var i = 0; i < selection.length; i++) {
            me.rangelist.store.remove(selection[i]);
        }
        var rangestore = me.rangelist.store;
        rangestore.each(function (r) {
            for (var j = 0; j < me.rangeObjectList.length; j++) {
                if (me.rangeObjectList[j].id == r.data.id) {
                    tempList[k++] = me.rangeObjectList[j];
                }
            }
        });
        me.rangeObjectList = tempList;
    },



    rangeAdd: function () { // 新增方法
        var me = this;
        var r = Ext.create('rangeModel');
        var randNum = me.createRndNum(6);
        r.set({
            id: randNum
        });
        me.rangelist.store.add(r);
        me.rangelist.editingPlugin.startEditByPosition({
            row: 0,
            column: 0
        });

        var rangeObj = {};
        rangeObj.id = randNum;
        me.rangeObjectList.push(rangeObj);
    },
    onchange: function () { // 设置你按钮可用状态
        var me = this;
        me.rangelist.down('#delId').setDisabled(me.rangelist.getSelectionModel().getSelection().length === 0);
    },
    initComponent: function () {
        var me = this;
        me.tempValue = null;
        main_rndNum = me.createRndNum(10);
        Ext.define('rangeModel', {
            extend: 'Ext.data.Model',
            fields: [{
                name: 'id',
                type: 'string'
            }, {
                name: 'level',
                type: 'string'
            }, {
                name: 'range',
                type: 'string'
            }]
        });
        me.rangeObjectList = [];
        me.param = me.formwindow.initialConfig;
        var url = __ctxPath + "/kpi/alarm/findalarmplanregions.f";

        var rangeTrigger = Ext.create('Ext.form.field.Trigger', {
            xtype: 'trigger',
            editable: false,
            onTriggerClick: function () {
                range_rndNum = me.createRndNum(10);
                var mytrigger = this;
                me.winsub = new Ext.Window({
                    constrain: true,
                    width: 550,
                    height: 180,
                    layoutConfig: {
                        pack: 'center',
                        align: 'middle'
                    },
                    defaults: {
                        margins: '0 5 0 0'
                    },
                    modal: true,
                    title: FHD.locale.get('fhd.alarmplan.form.rangeformula'),
                    items: [
	                    {	 
	                     xtype : 'fieldset',
						 margin: '5 5 5 5',
						 layout: 'vbox',
						 title:FHD.locale.get('fhd.formula.commonlyUsed'),
						 defaults:{
							 width:30,margin: '3 5 2 5'
						 },
						 items:[
	
	 				     {
							xtype: 'container',
							layout: 'hbox',
		                	margin: '5 5 5 20',
		               		defaults: {
		               		 width:30,
		                   	 margins: '0 5 0 0'
		            	    },
		            	    width: 500,
							items:[
							 {xtype : 'button',text:'0',
		 				    	 handler:function(){
		 				    	 	me.btnClick('0');
		 				    	 }
		 				     },
		 				     {xtype : 'button',text:'1',
		 				    	 handler:function(){
		 				    		me.btnClick('1');
		 				    	 }
		 				     },{xtype : 'button',text:'2',
		 				    	 handler:function(){
		 				    		me.btnClick('2');
		 				    	 }
		 				     },
		 				     {xtype : 'button',text:'3',
		 				    	 handler:function(){
		 				    		me.btnClick('3');
		 				    	 }
		 				     },{xtype : 'button',text:'4',
		 				    	 handler:function(){
		 				    		me.btnClick('4');
		 				    	 }
		 				     },
		 				     {xtype : 'button',text:'5',
		 				    	 handler:function(){
		 				    		me.btnClick('5');
		 				    	 }
		 				     },{xtype : 'button',text:'6',
		 				    	 handler:function(){
		 				    		me.btnClick('6');
		 				    	 }
		 				     },
		 				     {xtype : 'button',text:'7',
		 				    	 handler:function(){
		 				    		me.btnClick('7');
		 				    	 }
		 				     },{xtype : 'button',text:'8',
		 				    	 handler:function(){
		 				    		me.btnClick('8');
		 				    	 }
		 				     },
		 				     {xtype : 'button',text:'9',
		 				    	 handler:function(){
		 				    		me.btnClick('9');
		 				    	 }
		 				     },
		 				     {xtype : 'button',text:'.',
		 				    	 handler:function(){
		 				    		me.btnClick('.');
		 				    	 }
		 				     },{xtype : 'button',text:'-∞',
		 				    	 handler:function(){
		 				    	 	me.btnClick('-∞');
		 				    	 }
		 				     },
		 				     {xtype : 'button',text:'+∞',
		 				    	 handler:function(){
		 				    		me.btnClick('+∞');
		 				    	 }
		 				     }
							]},
						{
							xtype: 'container',
							layout: 'hbox',
		                	margin: '5 5 5 20',
		                	width: 500,
		               		defaults: {
		                   	 margins: '0 5 0 0'
		            	    },
							items:[ {
		                        //xtype: 'numberfield',
		                    	xtype: 'textfield',
		                        //step: 1,
		                        id: 'formulaOne' + range_rndNum,
		                        minValue:-999999999,
		                        width: 150,
								listeners: {
								    render: function(p) {
								    p.getEl().on('click', function(p){
								      me.tempValue = 'min'; 
								    });
								}}
		                    },
		                    {
		                    	xtype:'dictselectforeditgrid',
		                        dictTypeId: '0_compare_symbol',
		                        fieldLabel: '',
		                        width: 55,
		                        id: 'signOne' + range_rndNum,
		                        multiSelect: false
		                    }, {
		                        xtype: 'label',
		                        text: 'X'
		                    },
		                    {
		                    	xtype:'dictselectforeditgrid',
		                        dictTypeId: '0_compare_symbol',
		                        fieldLabel: '',
		                        width: 55,
		                        id: 'signTwo' + range_rndNum,
		                        multiSelect: false
		                    }, {
		                    	xtype: 'textfield',
		                        //xtype: 'numberfield',
		                        //step: 1,
		                        id: 'formulaTwo' + range_rndNum,
		                        width: 150,
								listeners: {
								    render: function(p) {
								    p.getEl().on('click', function(p){
								      me.tempValue = 'max'; 
								    });
								}}
		                    }]
						}
					]}	
                   ],
                    buttonAlign: 'center',
                    buttons: [{
                        text: FHD.locale.get('fhd.common.save'),
                        handler: function () {
                            me.rangeSave();

                        }
                    }, {
                        text: FHD.locale.get('fhd.common.cancel'),
                        handler: function () {
                            me.winsub.close();
                        }
                    }]

                });
                me.winsub.show(mytrigger);
                // 给公式赋值
                me.setValues();
            }
        });

        /*rangeTrigger.on('focus', function (c, e) {
            rangeTrigger.onTriggerClick();
        });*/

        var gridColums = [{
            header: FHD.locale.get("fhd.alarmplan.form.level"),
            dataIndex: 'level',
            sortable: true,
            width: 60,
            flex: 1,
            editor: {
	            dictTypeId: '0alarm_startus',
	            xtype:'dictselectforeditgrid',
	            fieldLabel: ''
        	},
            renderer: function (v) {
                var color = "";
                var text = "";
                var icon = "";
                switch (v) {
                    case '0alarm_startus_h':
                        /* 对应数据字典中的主键 */
                        color = 'symbol_high_sm';
                        //icon = "symbol-4-sm";
                        //text = FHD.locale.get("fhd.alarmplan.form.hight");
                        break;
                    case '0alarm_startus_m':
                        color = 'symbol_mid_sm';
                        //icon = "symbol-5-sm";
                        //text = FHD.locale.get("fhd.alarmplan.form.min");
                        break;
                    case '0alarm_startus_l':
                        color = 'symbol_low_sm';
                        //icon = "symbol-6-sm";
                        //text = FHD.locale.get("fhd.alarmplan.form.low");
                        break;
                    case '0alarm_startus_safe':
                    	color = 'symbol_jrj_g_sm';
                    	break;
                    		
                }
                return color != "" ? '<img src="' + __ctxPath + '/images/icons/' + color + '.gif">' : "";
            }

        }, {
            header: FHD.locale.get("fhd.alarmplan.form.range"),
            dataIndex: 'range',
            sortable: true,
            width: 60,
            flex: 1,
            renderer: function (v) {
                var rangestr = v;
                if (v.indexOf("<") != -1) {
                    rangestr = v.replace("<", "&lt;");
                }
                return rangestr;
            },
            editor: rangeTrigger
        }];
        if (me.isAdd) {
           url = "";
        }
        me.rangelist = Ext.create('FHD.ux.EditorGridPanel', {
            url: url,
            searchable: false,
            cols: gridColums,
            extraParams: {
                id:me.param.id
            },
            height: 180,
            pagable: false,
            tbarItems: [{
                iconCls: 'icon-add',
                handler: function () {
                    me.rangeAdd();
                }
            }, '-', {
                iconCls: 'icon-del',
                handler: function () {
                    me.rangeDel();
                },
                id: 'delId'
            }]
        });

        me.rangelist.store.on('load', function () {
            me.onchange();
        }); // 执行store.load()时改变按钮可用状态
        me.rangelist.on('selectionchange', function () {
            me.onchange();
        }); // 选择记录发生改变时改变按钮可用状态
        me.rangelist.store.on('update', function () {
            me.onchange();
        }); // 修改时改变按钮可用状态

        Ext.apply(me, {
            items: [{
                xtype: 'hidden',
                hidden: true,
                name: 'memo',
                id: 'memo'
            }, {
                xtype: 'fieldset',
                flex: 1,
                defaults: {
                    columnWidth: 1 / 1,
                    labelWidth: 76,
                    margin: '3 3 3 3'
                }, // 每行显示一列，可设置多列
                layout: {
                    type: 'column'
                },
                title: FHD.locale.get('fhd.common.baseInfo'),
                items: [{
                    xtype: 'hidden',
                    name: 'id',
                    value: me.param.id
                }, {
                    xtype: 'hidden',
                    name: 'range'
                }, // 提交表单时拼接成json串,赋值给range属性,传递给后台[{level:'',formulaMax:'',formulaMin:'',maxSign:'',minSign:''}.....]
                {
                    xtype: 'textfield',
                    labelAlign: 'left',
                    fieldLabel: FHD.locale.get('fhd.alarmplan.form.name') + '<font color=red>*</font>',
                    id: 'alarm_name' + main_rndNum,
                    name: 'name',
                    allowBlank: false,
                    margin: '3 3 3 3'
                },
                 {
                	xtype:'dictselectforeditgrid',
                    fieldLabel: FHD.locale.get('fhd.alarmplan.form.types') + '<font color=red>*</font>',
                    margin: '3 3 3 3',
                    labelWidth: 76,
                    name: 'types',
                    labelAlign: 'left',
                    allowBlank: false,
                    editable: false,
                    multiSelect: false,
                    dictTypeId: '0alarm_type'
                }, {
                    xtype: 'textareafield',
                    rows: 5,
                    margin: '3 3 3 3',
                    labelAlign: 'left',
                    fieldLabel: FHD.locale.get('fhd.sys.dic.desc'),
//                    id: 'descs' + main_rndNum,
                    name: 'descs',
                    allowBlank: true
                }]
            }, {
                xtype: 'fieldset',
                flex: 1,
                collapsed: false,
                overflow: 'auto',
                title: FHD.locale.get('fhd.alarmplan.form.rangeset'),
                items: [
                me.rangelist]
            }

            ],
            buttons: [{
                text: FHD.locale.get('fhd.common.save'),
                handler: function () {
                    if (me) {
                    	var rangeFlag = true;
                        var form = me.getForm();
                        if (form.isValid() && me.validate()) {
                            if(!me.validateRangeValue()){
                            	return;
                            }
                            var rangeArray = me.getStoreValue();
                            for(var i=0;i<rangeArray.length;i++){
                            	var rangeObj = rangeArray[i];
                            	if(!rangeObj['formulaMax']||!rangeObj['formulaMin']||!rangeObj['maxSign']||!rangeObj['minSign']){
                            		FHD.alert('区间设置错误，请重新设置。');
                            		rangeFlag = false;
                            		break;
                            	}
                            }
                            if(!rangeFlag){
                            	return ;
                            }
                            //form.setValues({
                            //    range: rangeArray,
                            //    memo: Ext.getCmp('descs' + main_rndNum).value
                            //});
                            var objValues = form.getValues();
                            objValues.range = rangeArray;
//                            objValues.memo = Ext.getCmp('descs' + main_rndNum).value;
                            if (me.isAdd) { // 新增
                                // 参数依次为form，url，callback
                            	FHD.ajax({
                                    url: __ctxPath + "/kpi/alarm/savealarmplan.f",
                                    params: {
                                        values: Ext.JSON.encode( objValues)
                                    },
                                    callback: function (data) {
                                    	me.grid.store.load();
                                    }
                                });
                            	
                                /*FHD.submit({
                                    form: form,
                                    url: __ctxPath + "/kpi/alarm/savealarmplan.f",
                                    callback: function (data) {
                                        me.grid.store.load();
                                    }
                                });*/
                                
                            } else { // 更新
                            	
                            	FHD.ajax({
                                    url: __ctxPath + "/kpi/alarm/mergealarmplan.f",
                                    params: {
                                        values: Ext.JSON.encode( objValues)
                                    },
                                    callback: function (data) {
                                    	me.grid.store.load();
                                    	var selections = me.grid.getSelectionModel().getSelection();
                                    	if(selections.length>0){
                                    		var selected = selections[0];
                                    		me.grid.getSelectionModel().deselect(selected);
                                    		me.grid.getSelectionModel().select(selected);
                                    	}
                                    }
                                });
                            	
                                /*FHD.submit({
                                    form: form,
                                    url: __ctxPath + "/kpi/alarm/mergealarmplan.f",
                                    callback: function (data) {
                                        me.grid.store.load();
                                    }
                                });*/
                            }
                            me.formwindow.close();
                        }
                    }
                }
            }, {
                text: FHD.locale.get('fhd.common.cancel'),
                handler: function () {
                    me.formwindow.close();
                }
            }]
        });

        me.callParent(arguments);

        if (typeof (me.param.id) != "undefined") {
            me.form.load({
                url: __ctxPath + "/kpi/alarm/findalarmplanbyid.f",
                params: {
                    id: me.param.id
                },
                success: function (form, action) {
                    me.rangeObjectList = Ext.JSON.decode(action.result.regions);
                }
            });
        }
    
    },
    btnClick:function(newValue){
    	var me = this;
    	me.mixValue = me.items.items[2].items.items[0];
	 	if(me.tempValue!=null&&me.tempValue=='min'){
	 		var min = me.winsub.items.items[0].items.items[1].items.items[0];
	 		var oldValue = min.getValue();
	 		var newValue = oldValue + newValue;
	 		min.setValue(newValue);
	 	}
	 	if(me.tempValue!=null&&me.tempValue=='max'){
	 		var max = me.winsub.items.items[0].items.items[1].items.items[4];
	 		var oldValue = max.getValue();
	 		var newValue = oldValue + newValue;
	 		max.setValue(newValue);
	 	}
    },
    
    //校验数值格式是否正确
    validateValue:function(val){
    	var result= null;
    	result = val.match(/^-?([-∞]|^[\d+|\+][∞]|[1-9]\d*|[1-9]\d*\.[0-9]\d*|0\.\d*[1-9]\d*|0?\.0+|0)$/); 
		if(result==null){
			Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请输入正确的数值格式');
			return false; 
		}
		return true;
    },
    //校验区间公式是否正确
    compareValue:function(min,signOne,signTwo,max){
    	
    	if(!isNaN(min)&&!isNaN(max)){

    		if((signOne=='<'&&signTwo=='<')||(signOne=='<'&&signTwo=='<=')||(signOne=='<='&&signTwo=='<')||(signOne=='<'&&signTwo=='=')||(signOne=='='&&signTwo=='<')){
    			if(eval(min)>=eval(max)){
    				Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '最小值必须小于最大值');
					return false; 
    			}
    		}
    		if((signOne=='<'&&signTwo=='!=')||(signOne=='<='&&signTwo=='!=')||(signOne=='!='&&signTwo=='<')||(signOne=='!='&&signTwo=='<=')||(signOne=='='&&signTwo=='<')){
    			if(eval(min)>=eval(max)){
    				Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '最小值必须小于最大值');
					return false; 
    			}
    		}
    		if((signOne=='<='&&signTwo=='<=')||(signOne=='<='&&signTwo=='=')||(signOne=='='&&signTwo=='<=')){
    			if(eval(min)>eval(max)){
    				Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '最小值必须小于等于最大值');
					return false; 
    			}
    		}
    		if((signOne=='='&&signTwo=='!=')||(signOne=='!='&&signTwo=='=')){
    			if(eval(min)>eval(max)){
    				Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '区间公式不成立');
					return false; 
    			}
    		}
    		if((signOne=='='&&signTwo=='=')||(signOne=='!='&&signTwo=='=')){
    			if(eval(min)!=eval(max)){
    				Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '区间公式不成立');
					return false; 
    			}
    		}
    	}
    	
		if(min=='+∞'&&min=='-∞'||min=='+∞'||max=='-∞'){
    		Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '区间公式不成立');
			return false; 
	    }	
		return true;
    }
});