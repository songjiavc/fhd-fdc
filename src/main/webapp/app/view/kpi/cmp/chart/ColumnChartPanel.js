Ext.define('FHD.view.kpi.cmp.chart.ColumnChartPanel',{
	       extend: 'Ext.panel.Panel',
	       initComponent : function() {
	    	   var me = this;
	    	   me.store = Ext.create('Ext.data.JsonStore',{
	   			 	fields:['timePeriod','targetValue','finishValue']
	   			  });
	    	   
	    	   Ext.apply(me,{	    		   
	    		   layout : 'fit',
	    		   items: [me.chart],
	    		   border: false
	    	   });

	   		   me.callParent(arguments);
	       },
	       loadData:function(data,maxi,mini) {
	    	   var me = this;
	    	   var numAxe =  {
	     			 		type: 'Numeric',
	     			 		position: 'left',
	     			 		fields: ['targetValue','finishValue'],
	     			 		gird: true
	     			 	};
	    	   if(mini <= 0) {
	    		   numAxe.minimum = mini;
	    	   }
	    	   if(maxi <= 0) {
	    		   numAxe.maximum = 0;
	    	   }
	    	   me.removeAll();
	    	   if(data) {
		        me.chart = Ext.create('Ext.chart.Chart',{
	        			xtype:'chart',
	     			 	style: 'background: #fff',
	     			 	animate: {
	     			 		easing: 'ease',
	     			 		duration: 500
	     			 	},
	     			 	shadow: false,
	     			 	store: me.store,     			 	  			 	
	     			 	axes:[numAxe,
	     			 	{   type: 'Category',
	     			 		position: 'bottom',
	     			 		fields: ['timePeriod']
	     			 	}],
	     			 	legend: {
	     			 		position: 'right',
	     			 	    createLegendItem: function(series, yFieldIndex) {
	     			 	        var me = this;
	     			 	        return new Ext.chart.LegendItem({
	     			 	            legend: me,
	     			 	            series: series,
	     			 	            surface: me.chart.surface,
	     			 	            yFieldIndex: yFieldIndex,
		      			 	        getLabelText: function() {
		      			 	          var me = this,
		      			 	          series = me.series,
		      			 	          idx = me.yFieldIndex;	
		      			 	          function getSeriesProp(name) {
		      			 	              var val = series[name];
		      			 	              var ret =  (Ext.isArray(val) ? val[idx] : val);
		      			 	              if('targetValue' == ret) {
		      			 	            	  ret = "目标值";
		      			 	              } else if('finishValue' == ret) {
		      			 	            	  ret = "完成值";
		      			 	              }
		      			 	              return ret;
		      			 	          }		      			 	          
		      			 	          return getSeriesProp('title') || getSeriesProp('yField');
		      			 	      }
	     			 	        });
	     			 	    }
	     			 	},
	     			 	series: [{
	     			    type:'column',
	     			    showInlegend: true,
	     			 	xField: ['timePeriod'],
	     			 	yField: ['targetValue','finishValue'],
	     			 	axis: 'left',
	     			 	highlight: false
	     			 	}]
	     			 }); 
		           me.add(me.chart);
	    		   me.store.loadData(Ext.decode(data));	  
	    		   
	    	   } 
	       }
})