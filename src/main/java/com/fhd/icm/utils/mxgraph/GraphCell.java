package com.fhd.icm.utils.mxgraph;

import java.util.HashSet;
import java.util.Set;



/**
 * 流程图对象
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-9-11		上午9:26:38
 *
 * @see 	 
 */
public class GraphCell {

	/**
	 * cell的ID
	 */
	private String id;
	
	/**
	 * cell的显示值
	 */
	private String value;
	
	/**
	 * cell的图形样式
	 */
	private String style;
	/**
	 * cell的所属图形
	 */
	private String parent;
	/**
	 * ENDO
	 */
	private String vertex;
	/**
	 * ENDO
	 */
	private String edge;
	/**
	 * 源
	 */
	private String source;
	/**
	 * 目标
	 */
	private String target;
	/**
	 * ENDO
	 */
	private Double x;
	/**
	 * ENDO
	 */
	private Double y; 
	/**
	 * ENDO
	 */
	private Double width;
	/**
	 * ENDO
	 */
	private Double height;
	/**
	 * ENDO
	 */
	private String relative;
	/**
	 * ENDO
	 */

	private Set<GraphCellPoint> graphCellPoints = new HashSet<GraphCellPoint>();
	
	private Set<GraphCellPoint> arrayPoints = new HashSet<GraphCellPoint>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getVertex() {
		return vertex;
	}
	public void setVertex(String vertex) {
		this.vertex = vertex;
	}
	public String getEdge() {
		return edge;
	}
	public void setEdge(String edge) {
		this.edge = edge;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Double getX() {
		return x;
	}
	public void setX(Double x) {
		this.x = x;
	}
	public Double getY() {
		return y;
	}
	public void setY(Double y) {
		this.y = y;
	}
	public Double getWidth() {
		return width;
	}
	public void setWidth(Double width) {
		this.width = width;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	
	public String getRelative() {
		return relative;
	}
	public void setRelative(String relative) {
		this.relative = relative;
	}
	public Set<GraphCellPoint> getGraphCellPoints() {
		return graphCellPoints;
	}
	public void setGraphCellPoints(Set<GraphCellPoint> graphCellPoints) {
		this.graphCellPoints = graphCellPoints;
	}
	public Set<GraphCellPoint> getArrayPoints() {
		return arrayPoints;
	}
	public void setArrayPoints(Set<GraphCellPoint> arrayPoints) {
		this.arrayPoints = arrayPoints;
	}
	
}

