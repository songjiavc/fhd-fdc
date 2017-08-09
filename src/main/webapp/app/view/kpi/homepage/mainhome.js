Ext.define('FHD.view.kpi.homepage.mainhome', {

	extend : 'Ext.panel.Panel',

	title : '',
	

	border : false,

	layout : {
		align : 'stretch',
		type : 'hbox'
	},
	
	initScript:function(){
		var me = this;
		
		$('#navigation a').stop().animate({'marginLeft':'-85px'},1000);

        $('#navigation > li').hover(
            function () {
                $('a',$(this)).stop().animate({'marginLeft':'-2px'},200);
            },
            function () {
                $('a',$(this)).stop().animate({'marginLeft':'-85px'},200);
            }
        );
        
        $("#nav_explore").click(function(){
        	me.centerpanel.explore(me.centerpanel);
        });
        
        $("#nav_focuseAdd").click(function(){
        	me.centerpanel.focusAdd();
        });
        
        $("#nav_myfocuse").click(function(){
        	//me.centerpanel.body.mask("加载中...","x-mask-loading");
        	me.centerpanel.reload(me.centerpanel);
        	//me.centerpanel.body.unmask();
        	me.centerpanel.setComponentBtnVisable();
        });
        
	},
	
	
	// 初始化方法
	initComponent : function() {
		var me = this;
		
		var ulhtml = '<ul id="navigation" >';
		ulhtml+='<li class="myfocus"><a id="nav_myfocuse" href="javascript:void(0)" title="我的关注"></a></li>';
		ulhtml+='<li class="focus"><a id="nav_focuseAdd" href="javascript:void(0)" title="关注"></a></li>';
		ulhtml+='<li class="explore"><a id="nav_explore" href="javascript:void(0)" title="搜索"></a></li>';
		ulhtml+='<li class="search"><a href="javascript:void(0)" title=""></a></li>';
		ulhtml+='<li class="photos"><a href="javascript:void(0)" title=""></a></li>';
		ulhtml+='<li class="rssfeed"><a href="javascript:void(0)" title=""></a></li>';
		ulhtml+='<li class="podcasts"><a href="javascript:void(0)" title=""></a></li>';
		ulhtml+='<li class="contact"><a href="javascript:void(0)" title=""></a></li>';
		ulhtml+='</ul>';
			
		
		me.leftpanel = Ext.create('Ext.panel.Panel',{
			html:ulhtml,
			flex:0.5,
			border:false
		});
		
		me.centerpanel = Ext.create('FHD.view.kpi.homepage.home',{
			flex:9.5
		});
		
		Ext.applyIf(me, {

			items : [ 
			         me.leftpanel,
			         me.centerpanel
			        ],
			autoScroll : true,
			listeners: {
    			afterrender: function (c, opts) {
    				me.initScript();
    				me.centerpanel.setComponentBtnVisable();
    			}
    		}
		});

		me.callParent(arguments);
		
	}

});