/**
 *    @description 内控标准数据主页面，框架包括了 tree 和 列表
 *    @author 宋佳
 *    @since 2013-3-5
 */
Ext.define('FHD.view.risk.measureedit.RiskClassBaseMainFieldSet',{
	extend : 'Ext.form.FieldContainer',
    alias: 'widget.riskclassbasemainfieldset',
	requires: [
      'FHD.view.risk.measureedit.ClassBaseFirstLevel',
      'FHD.view.risk.measureedit.QuantificationFirstLevel'
    ],
    border : false,
	layout : {
                type: 'vbox',
                align: 'stretch'
              },
    margin: '7 10',
    autoHeight : true,
    type : '',
	initComponent : function() {
		var me = this;
		
		me.label = Ext.widget('label',{
			margin: '7 10',
			text : '风险分级标准:'
		})
		
		me.count = Ext.widget('numberfield',{
			fieldLabel : '设定层次数量',
			maxValue : 20,
			minValue : 0,
			value : 0
		});
		me.setCountBtn = Ext.widget('button',{
			margin: '0 10 0 20',
			text : '确定层级',
			handler : function(){
				me.addLevelName();
			} 
		});
		me.saveCountBtn = Ext.widget('button',{
			margin: '0 10 0 10',
			disabled : true,
			text : '保存描述',
			handler : function(){
				me.saveLevelName();
			}
		});
		me.levelNameFieldContainer = Ext.widget('fieldcontainer');
		me.levelCountContainer = Ext.widget('fieldset',{
			layout : {
				type : 'vbox'
			},
			autoHeight : true,
			title:'定性设定',
			collapsible: true,
			margin: '7 10',
			items : [{
						layout : { type:'hbox' },
						border : false,
						items : [me.count,me.setCountBtn,me.saveCountBtn]
					},
				me.levelNameFieldContainer
			]
		});
		
        //定性fieldset
        me.quantitativeFieldset = Ext.widget('fieldset', {
        	hidden : true,
            collapsible: true,
        	autoHeight : true,
            title : '定性分级',
            items : [
            ]
        });
        me.addQuantificationFirstLevelBtn = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").addQuantificationFirstLevel()'>增加同级</a>"
        });
        me.quantificationfirstlevel = Ext.widget('quantificationfirstlevel',{btnName : me.btnName});
        me.quantificationFieldset = Ext.widget('fieldset', {
            collapsible: true,
            autoHeight : true,
            title : '定量分级',
            items : [me.quantificationfirstlevel,me.addQuantificationFirstLevelBtn]
        });
		Ext.applyIf(me,{
			items:[me.label,me.levelCountContainer,me.quantitativeFieldset,me.quantificationFieldset]
		});
	 	me.callParent(arguments);
	},
	
	saveLevelName : function(){
		var me = this;
		var levelNameFieldContainer = me.levelNameFieldContainer;
		var flag = Ext.each(levelNameFieldContainer.items.items,function(item){
			if(item.getValue() == ''){
				alert('请将所有分级设定描述！');
				return false;
			}
			item.setReadOnly(true);
		});
		if(flag != false){
			me.hiddenFieldshow(true);
		}
	},
	
	addLevelName : function(){
		var me = this;
		var levelNameFieldContainer = me.levelNameFieldContainer;
		levelNameFieldContainer.removeAll();
		if(me.count.value != 0){
			for(var i = 1;i<=me.count.value;i++){
				levelNameFieldContainer.insert(i,Ext.widget('textfield',{
					margin: '7 10',
					fieldLabel : '分级'+ i + '描述',
					maxWidth : 300
				}));
			}
			me.saveCountBtn.setDisabled(false);
			me.hiddenFieldshow(false);//更改层次的时候，隐藏风险分级标准
		}else{
			me.saveCountBtn.setDisabled(false);
			me.hiddenFieldshow(false);//更改层次的时候，隐藏风险分级标准
			me.saveLevelName();
		}
		
	},
	addFirstLevel : function(){
		var me = this;
		var quantitativeFieldset = me.quantitativeFieldset;
		var levelNameFieldContainer = me.levelNameFieldContainer;
		var firstlevel = Ext.widget('classbasefirstlevel',{btnName : me.btnName,type : me.type});
		Ext.each(levelNameFieldContainer.items.items,function(item){
			firstlevel.add(Ext.widget('classbasesecondlevel',{btnName : me.btnName,secondnamefield:item.getValue(),type : me.type}));
		});
		quantitativeFieldset.insert(0,firstlevel);
	},
	hiddenFieldshow : function(flag){
		var me = this;
		me.quantitativeFieldset.setVisible(flag);
		
		me.quantitativeFieldset.removeAll();
		var addFirstLevelBtn = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").addFirstLevel()'>增加标准</a>"
    	});
		me.quantitativeFieldset.insert(0,addFirstLevelBtn);
	},
	
	modifyLoad : function(data){
		/**
		 * data参数：
		 * count : 层数
		 * LQSuccess : true为有定性分级内容
		 * fatherLQDESC : 父分级描述
		 * fatherLQRelaPre : 父分级绑定预案
		 * childLQNAME : 子分级名称
		 * childLQDESC : 子分级描述
		 * childLQRelaPre : 子分级绑定预案
		 * 
		 * **/
		var me = this;
		var LQSuccess = data.LQSuccess;
		var fatherLQDESC = Ext.decode(data.fatherLQDESC);
		var fatherLQRelaPre = Ext.decode(data.fatherLQRelaPre);
		var childLQNAME = Ext.decode(data.childLQNAME);
		var childLQDESC = Ext.decode(data.childLQDESC);
		var childLQRelaPre = Ext.decode(data.childLQRelaPre);
		
		if(LQSuccess == true){
			var levelNameFieldContainer = me.levelNameFieldContainer;
			var count = data.count;//层数
			me.count.setValue(count);
			me.addLevelName();
			if(count != 0){
				var num = 0;
				Ext.each(levelNameFieldContainer.items.items,function(item){
					item.setValue(childLQNAME[num].name);
					num++;
				});
			}
			me.saveLevelName();
			var quantitativeFieldset = me.quantitativeFieldset;
			for(var i=0;i<fatherLQDESC.length;i++){
				var firstlevel = Ext.widget('classbasefirstlevel',{
					btnName : me.btnName,
					type : me.type,
					firsttextfiled : fatherLQDESC[i].desc,
					firsthidfield : childLQRelaPre[i].preid
				});
				for(var j=0;j<count;j++){
					firstlevel.add(
						Ext.widget('classbasesecondlevel',{
							btnName : me.btnName,
							type : me.type,
							secondnamefield : childLQNAME[count*i+j].name,
							secondtextfield : childLQDESC[count*i+j].desc,
							secondhidfield : childLQRelaPre[count*i+j].preid
						})
					);
				
				}
				quantitativeFieldset.insert(0,firstlevel);
			}
		}
	}
});
