/**
 * 风险评价准则主面板
 * 
 * @author 
 */
Ext.define('FHD.view.risk.baseConfig.baseConfigTree', {
    extend: 'Ext.container.Container',
    alias: 'widget.baseConfigTree',
    
    requires: [
               'FHD.view.risk.assess.quaAssess.FormulateCalculateEdit'
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	
    	me.isCompanyEmp;
    	me.baseConfigTree = Ext.create('Ext.tree.Panel', {
		    region: 'west',
		    title:FHD.locale.get('fhd.risk.baseconfig.option'),
		    useArrows: true,
		    rowLines:true,
		    split: true,
	        collapsible : true,
	        border:true,
	        rootVisible : false,
	        maxWidth:300,
		    width:220,
		    root: {
		        text: '',
		        iconCls:'icon-help',
		        expanded: true,
		        autoLoad:true
//		        children:[
//					{
//					    text: FHD.locale.get('fhd.risk.baseconfig.dimensionConfig'),
//					  	iconCls:'icon-asterisk-orange',
//					    leaf: true
//					},
//					{
//					    text: FHD.locale.get('fhd.risk.baseconfig.templateConfig'),
//					  	iconCls:'icon-xhtml',
//					    leaf: true
//					},
//					{
//					    text: FHD.locale.get('fhd.risk.baseconfig.riskMappingConfig'),
//					  	iconCls:'icon-tupu',
//					    leaf: true
//					},
//					{
//					    text: FHD.locale.get('fhd.risk.baseconfig.weightConfig'),
//					  	iconCls:'icon-chart-pie',
//					    leaf: true
//					},
//					{
//					    text: '公式计算',
//					  	iconCls:'icon-chart-pie',
//					    leaf: true
//					},{
//					    text: '缺陷认定',
//					  	iconCls:'icon-chart-pie',
//					    leaf: true
//					}
//		        ]
		        
		    },
		    listeners : {
		    	itemclick:function(view,node){
		    		me.remove(me.demopanel);
		    		switch (node.data.text){
			    		case FHD.locale.get('fhd.risk.baseconfig.dimensionConfig'):
			    			initDemoPanel('FHD.view.risk.baseConfig.Dimension');
		    				break;
			    		case FHD.locale.get('fhd.risk.baseconfig.templateConfig'):
			    			initDemoPanel('FHD.view.risk.baseConfig.Template');
		    				break;
			    		case FHD.locale.get('fhd.risk.baseconfig.weightConfig'):
			    			initDemoPanel('FHD.view.risk.assess.WeightingSetMain');
		    				break;
			    		case '公式计算':
			    			initDemoPanel('FHD.view.risk.assess.quaAssess.FormulateCalculateEdit');
		    				break;
			    		case '缺陷认定':
			    			initDemoPanel('pages.risk.baseConfig.DefectChangeRiskList');
		    				break;
		    			default:
		    				initDemoPanel('暂无');
		    		}
		    	}
		    }
		});
    	
    	Ext.define('demopanel',{
    		extend:'Ext.panel.Panel',
    		border:false,
    		autoScroll:false,
    		region: 'center'
    	});
    	function initDemoPanel(url){
    		var panelDemopanel;
    		if(url!='暂无'){
    			//panelDemopanel=Ext.create('demopanel',{autoLoad :{ url: url,scripts: true}});
    			panelDemopanel=Ext.create(url,{region: 'center'});
    		}else{
    			panelDemopanel=Ext.create('demopanel',{title:'暂无'});
    		}
    		me.demopanel=panelDemopanel;
    		me.add(panelDemopanel);
    	}
    	
        if($ifAllGranted('ROLE_ALL_ASSESS_RISKSTANDARD_TEM')){
        	me.baseConfigTree.getRootNode().appendChild({iconCls:'icon-xhtml', 
            	text: FHD.locale.get('fhd.risk.baseconfig.templateConfig'), leaf: true}); 
        	
        	me.panelDemopanel = Ext.create('FHD.view.risk.baseConfig.Template',{
	    		border:false,
	    		autoScroll:false,
	    		region: 'center'
	    	});
        	
        	Ext.apply(me, {
        		autoScroll:false,
    		    layout: {
    		        type: 'border'
    		    },
    		    defaults: {
    	            border:true
    	        },
    		    demopanel: me.panelDemopanel,
    		    items:[me.baseConfigTree, me.panelDemopanel]
            });
        }else{
        	Ext.apply(me, {
        		autoScroll:false,
    		    layout: {
    		        type: 'border'
    		    },
    		    defaults: {
    	            border:true
    	        },
    		    demopanel: me.panelDemopanel,
    		    items:[me.baseConfigTree, me.panelDemopanel]
            });
        }
        
        FHD.ajax({
			url : __ctxPath + '/sys/organization/findempiscompany.f',
			callback : function(data){
				me.isCompanyEmp = data.isCompanyPerson;//集团的人登录，隐藏权重设置和公式计算
//				if(!me.isCompanyEmp){
					if($ifAllGranted('ROLE_ALL_ASSESS_RISKSTANDARD_DIM')){
			        	me.baseConfigTree.getRootNode().appendChild({iconCls:'icon-asterisk-orange', 
			            	text: FHD.locale.get('fhd.risk.baseconfig.dimensionConfig'), leaf: true});
			        }
			        
			        if($ifAllGranted('ROLE_ALL_ASSESS_RISKSTANDARD_WEIGHT')){
			        	me.baseConfigTree.getRootNode().appendChild({iconCls:'icon-chart-pie', 
							text: FHD.locale.get('fhd.risk.baseconfig.weightConfig'), leaf: true});
			        }
			
					if($ifAllGranted('ROLE_ALL_ASSESS_RISKSTANDARD_FORMULA')){
						me.baseConfigTree.getRootNode().appendChild({iconCls:'icon-chart-pie', text: '公式计算', leaf: true}); 
					}	
//				}else{
//					if($ifAllGranted('ROLE_ALL_ASSESS_RISKSTANDARD_DIM')){
//			        	me.baseConfigTree.getRootNode().appendChild({iconCls:'icon-asterisk-orange', 
//			            	text: FHD.locale.get('fhd.risk.baseconfig.dimensionConfig'), leaf: true});
//			        }
//				}
			}
		});
        
		me.callParent(arguments);
//        me.baseConfigTree.getRootNode().appendChild({iconCls:'icon-tupu', 
//    		text: FHD.locale.get('fhd.risk.baseconfig.riskMappingConfig'), leaf: true}); 
//        me.baseConfigTree.getRootNode().appendChild({iconCls:'icon-chart-pie', text: '缺陷认定', leaf: true}); 
    }
        
});