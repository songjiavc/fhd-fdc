package com.fhd.icm.web.controller.process;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessGraph;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.icm.business.process.ProcessGraphBO;
import com.fhd.icm.utils.mxgraph.Constants;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.mxgraph.canvas.mxGraphicsCanvas2D;
import com.mxgraph.canvas.mxICanvas2D;
import com.mxgraph.reader.mxSaxOutputHandler;
import com.mxgraph.util.mxUtils;

/**
 * 流程设计视图Control类
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-9-11		上午11:10:44
 *
 * @see 	 
 */
@Controller
public class ProcessGraphControl {
	@Autowired
	private ProcessGraphBO o_processGraphBO;
	@Autowired
	private ProcessBO o_processBO;
	
	@RequestMapping(value = "/graph/findprocessgraph.f")
	public String findProcessGraph(String processId,String viewType,HttpServletRequest request) throws IOException{
		if(processId!=null && processId.length()>0){
			request.setAttribute("processId", processId);
			Process process = o_processBO.findProcessById(processId);
			if(process!=null && process.getId()!=null){
				request.setAttribute("graphName", process.getName());
			}
			ProcessGraph processGraph = o_processGraphBO.getProcessGraphByProcessId(processId);
			if(processGraph!=null && processGraph.getId()!=null && processGraph.getId().length()>0){
				String graphContext = IOUtils.toString(new ByteArrayInputStream(processGraph.getGraphContext()),"UTF-8");
				request.setAttribute("graphContext", graphContext.replaceAll("\\n"," ").replaceAll("\"", "\'"));
			}
		}
		if(StringUtils.isNotBlank(viewType) && viewType.startsWith("graph")){
			return "graph/"+StringUtils.trim(viewType);
		}else{
			return "graph/graphview";
		}
	}
	
	@RequestMapping(value = "/graph/mergeprocessgraph.f")
	public void mergeProcessGraph(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        String processId = null;
        String graphName = null;
        String graphContext = null;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
            ProcessGraph processGraph = new ProcessGraph();
            processId = request.getParameter("processId");
            graphName = request.getParameter("graphName");
            graphContext = request.getParameter("graphContext");
			processGraph.setProcessId(processId);
			processGraph.setGraphName(graphName);
			processGraph.setGraphContext(java.net.URLDecoder.decode(graphContext.replaceAll("\\n"," ").replaceAll("\"", "\'"),"UTF-8").getBytes("UTF-8"));
			o_processGraphBO.updateProcessGraph(processGraph);
			if(StringUtils.isNotBlank(processId)){
				String format = request.getParameter("format");
				int w = Integer.parseInt(request.getParameter("w"));
				int h = Integer.parseInt(request.getParameter("h"));
				String tmp = request.getParameter("bg");
				String xml = getRequestXml(request);
				Process process = o_processBO.findProcessById(processId);
				process.setFlowbg(tmp);
				process.setFlowFormat(format);
				process.setFlowHeight(String.valueOf(h));
				process.setFlowWidth(String.valueOf(w));
				process.setFlowXML(xml);
				o_processBO.mergeProcess(process);
			}
			flag = true;
	       	out.print(flag);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if(out!=null){
        		out.close();
        	}
        }
	}
	
	@RequestMapping(value="/graph/refreshprocessgraph.f")
	public void refreshProcessGraph(String processId,HttpServletRequest request,HttpServletResponse response) throws IOException{
		PrintWriter out=response.getWriter();
		boolean result=false;
		try {
			ProcessGraph processGraph = o_processGraphBO.getProcessGraphByProcessId(processId);
			String url = "http://"+request.getHeader("host")+request.getContextPath();
			if(null != processGraph){
//				String flowchartContext = new String(flowchart.getFlowchartContext());
//				List<BPCell> list_source = BPCellXMLUtils.renderBPCell(flowchartContext);//原来的流程图xml
//				List<BPCell> list_target = BPCellXMLUtils.renderBPCell(o_flowchartBO.renderXML(processureId,url));//要生成的流程图xml
//				boolean remove = true;
//				boolean add = true;
//				//源xml里有但目标xml里没有的mxcell删掉
//				for (BPCell source : list_source) {
//					for (BPCell target : list_target) {
//						if(target.getId().equals(source.getId())){
//							remove = false;
//						}
//					}
//					if(remove){
//						list_source.remove(source);
//					}
//				}
//				for (BPCell target : list_target) {
//					for (BPCell source : list_source) {
//						if(target.getId().equals(source.getId())){//源xml里有且目标xml里也有的用目标xml更新源xml
//							source.setValue(target.getValue());
//							source.setParent(target.getParent());
//							source.setSource(target.getSource());
//							source.setTarget(target.getTarget());
//							add = false;
//						}
//					}
//					//源xml里没有但目标xml里有的，添加到源xml里
//					if(add){
//						list_source.add(target);
//					}
//				}
//				flowchart.setFlowchartContext(BPCellXMLUtils.render(list_source).getBytes());
			}else{
				processGraph = new ProcessGraph();
				processGraph.setProcessId(processId);
				processGraph.setGraphName(o_processBO.findProcessById(processId).getName());
				
			}
			processGraph.setGraphContext(o_processGraphBO.renderXML(processId,url).getBytes("UTF-8"));
			o_processGraphBO.updateProcessGraph(processGraph);
			result = true;
		} catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	out.print(result);
            out.close();
        }
	}
	
	/**
	 * 
	 */
	private transient SAXParserFactory parserFactory = SAXParserFactory
			.newInstance();

	/**
	 * Cache for all images.
	 */
	private transient Map<String, Image> imageCache = new Hashtable<String, Image>();
	
	@RequestMapping(value = "/graph/imageExport.f")
	public void imageExport(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			if (request.getContentLength() < Constants.MAX_REQUEST_SIZE)
			{
//				long t0 = System.currentTimeMillis();

				handleRequest(request, response);

//				long mem = Runtime.getRuntime().totalMemory()
//						- Runtime.getRuntime().freeMemory();
//				long dt = System.currentTimeMillis() - t0;

//				System.out.println("Export: ip=" + request.getRemoteAddr()
//						+ " ref=\"" + request.getHeader("Referer")
//						+ "\" length=" + request.getContentLength() + " mem="
//						+ mem + " dt=" + dt);
			}
			else
			{
				response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
			}
		}
		catch (OutOfMemoryError e)
		{
			e.printStackTrace();
//			final Runtime r = Runtime.getRuntime();
//			System.out.println("r.freeMemory() = " + r.freeMemory() / 1024.0
//					/ 1024);
//			System.out.println("r.totalMemory() = " + r.totalMemory() / 1024.0
//					/ 1024);
//			System.out.println("r.maxMemory() = " + r.maxMemory() / 1024.0
//					/ 1024);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		finally
		{
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}

	/**
	 * Gets the parameters and logs the request.
	 * 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws DocumentException 
	 */
	protected void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		// Parses parameters
		String format = request.getParameter("format");
		String fname = request.getParameter("filename");
		int w = Integer.parseInt(request.getParameter("w"));
		int h = Integer.parseInt(request.getParameter("h"));
		String tmp = request.getParameter("bg");
		String xml = getRequestXml(request);
		
		Color bg = (tmp != null) ? mxUtils.parseColor(tmp) : Color.WHITE;

		// Checks parameters
		if (w > 0 && w <= Constants.MAX_WIDTH && h > 0
				&& h <= Constants.MAX_HEIGHT && format != null && xml != null
				&& xml.length() > 0)
		{
			if (fname != null && fname.toLowerCase().endsWith(".xml"))
			{
				fname = fname.substring(0, fname.length() - 4) + format;
			}

			String url = request.getRequestURL().toString();

			// Writes response
			if (format.equals("pdf"))
			{
				writePdf(url, fname, w, h, bg, xml, response);
			}
			else
			{
				writeImage(url, format, fname, w, h, bg, xml, response);
			}

			response.setStatus(HttpServletResponse.SC_OK);
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	/**
	 * Gets the XML request parameter.
	 * @throws UnsupportedEncodingException 
	 */
	protected String getRequestXml(HttpServletRequest request) throws UnsupportedEncodingException{
		return URLDecoder.decode(request.getParameter("plain"), "UTF-8");
	}

	/**
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * 
	 */
	protected void writeImage(String url, String format, String fname, int w, int h,
			Color bg, String xml, HttpServletResponse response)
			throws IOException, SAXException, ParserConfigurationException
	{
		BufferedImage image = mxUtils.createBufferedImage(w, h, bg);
		response.setCharacterEncoding("utf-8");
		if (image != null)
		{
			Graphics2D g2 = image.createGraphics();
			mxUtils.setAntiAlias(g2, true, true);
			renderXml(xml, createCanvas(url, g2));

			if (fname != null)
			{
				response.setContentType("application/x-unknown");
				response.setHeader("Content-Disposition", "attachment;filename="
						+ URLEncoder.encode(fname, "UTF-8"));
			}
			else if (format != null)
			{
				response.setContentType("image/" + format.toLowerCase());
			}

			// Optional: For faster PNG encoding
			//			if (format.equalsIgnoreCase("png"))
			//			{
			//				PngEncoder encoder = new PngEncoder();
			//				encoder.encode(image, response.getOutputStream());
			//			}
			//			else
			//			{
			ImageIO.write(image, format, response.getOutputStream());
			//			}
		}
	}

	/**
	 * Creates and returns the canvas for rendering.
	 * @throws IOException 
	 * @throws DocumentException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	protected void writePdf(String url, String fname, int w, int h, Color bg, String xml,
			HttpServletResponse response) throws DocumentException,
			IOException, SAXException, ParserConfigurationException
	{
		response.setContentType("application/pdf");

		if (fname != null)
		{
			response.setHeader("Content-Disposition", "attachment; filename="+ 
				URLEncoder.encode(fname, "UTF-8"));
		}

		// Fixes PDF offset
		w += 1;
		h += 1;

		Document document = new Document(new Rectangle(w, h));
		PdfWriter writer = PdfWriter.getInstance(document,
				response.getOutputStream());
		document.open();

		mxGraphicsCanvas2D gc = createCanvas(url, writer.getDirectContent()
				.createGraphics(w, h));

		// Fixes PDF offset
		gc.translate(1, 1);

		renderXml(xml, gc);
		gc.getGraphics().dispose();
		document.close();
	}

	/**
	 * Renders the XML to the given canvas.
	 */
	protected void renderXml(String xml, mxICanvas2D canvas)
			throws SAXException, ParserConfigurationException, IOException
	{
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		reader.setContentHandler(new mxSaxOutputHandler(canvas));
		reader.parse(new InputSource(new StringReader(xml)));
	}

	/**
	 * Creates a graphics canvas with an image cache.
	 */
	protected mxGraphicsCanvas2D createCanvas(String url, Graphics2D g2)
	{
		// Caches custom images for the time of the request
		final Map<String, Image> shortCache = new Hashtable<String, Image>();
		final String domain = url.substring(0, url.lastIndexOf('/'));

		mxGraphicsCanvas2D g2c = new mxGraphicsCanvas2D(g2)
		{
			public Image loadImage(String src)
			{
				// Uses local image cache by default
				Map<String, Image> cache = shortCache;

				// Uses global image cache for local images
				if (src.startsWith(domain))
				{
					cache = imageCache;
				}

				Image image = cache.get(src);

				if (image == null)
				{
					image = super.loadImage(src);

					if (image != null)
					{
						cache.put(src, image);
					}
					else
					{
						cache.put(src, Constants.EMPTY_IMAGE);
					}
				}
				else if (image == Constants.EMPTY_IMAGE)
				{
					image = null;
				}

				return image;
			}
		};

		g2c.setAutoAntiAlias(true);
		
		return g2c;
	}
		
}
