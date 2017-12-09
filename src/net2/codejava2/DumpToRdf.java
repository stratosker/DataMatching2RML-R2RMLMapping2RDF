package net2.codejava2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Collection;
import java.io.PrintWriter;
import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;

import org.apache.commons.io.IOUtils;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import com.google.gson.Gson;

import be.ugent.mmlab.rml.core.NodeRMLPerformer;
import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.function.Config;
import be.ugent.mmlab.rml.function.FunctionArea;
import be.ugent.mmlab.rml.function.FunctionAsGML;
import be.ugent.mmlab.rml.function.FunctionAsWKT;
import be.ugent.mmlab.rml.function.FunctionCentroidX;
import be.ugent.mmlab.rml.function.FunctionCentroidY;
import be.ugent.mmlab.rml.function.FunctionCoordinateDimension;
import be.ugent.mmlab.rml.function.FunctionDimension;
import be.ugent.mmlab.rml.function.FunctionEQUI;
import be.ugent.mmlab.rml.function.FunctionFactory;
import be.ugent.mmlab.rml.function.FunctionHasSerialization;
import be.ugent.mmlab.rml.function.FunctionIs3D;
import be.ugent.mmlab.rml.function.FunctionIsEmpty;
import be.ugent.mmlab.rml.function.FunctionIsSimple;
import be.ugent.mmlab.rml.function.FunctionLength;
import be.ugent.mmlab.rml.function.FunctionSpatialDimension;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;


//
import eu.linkedeodata.geotriples.dump_rdf;
//import d2rq.dump_rdf;


/**
 * Servlet implementation class DumpToRdf
 */
public class DumpToRdf extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DumpToRdf() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		
		
		if ((request.getParameter("inputFileType").equals("shp"))|| (request.getParameter("inputFileType").equals("csv"))){
			System.out.println("csv Or shp");
			
			
			registerFunctions(); // register custom functions (e.g.,
			// dimension(geometry) )

			RMLMapping mapping;
			RDFFormat format;
			try {

				String source = request.getParameter("rml");
				if (request.getParameter("inputFileType").equals("shp")) {
					//System.out.println("it is a shp");

					// source=source.replaceAll("rml:source.*\n","rml:source \"" +
					// "/opt/tomcat/webapps/data/gr_100km.shp"+"\"" );
				} else if (request.getParameter("inputFileType").equals("csv")) {
					//System.out.println("it is a csv");

				}

				//System.out.println(source);
				InputStream in = IOUtils.toInputStream(source, "UTF-8");

				mapping = RMLMappingFactory.extractRMLMapping(in);

				if (request.getParameter("epsgCode").equals("")) {
					Config.EPSG_CODE = "4326";
				} else {
					Config.EPSG_CODE = request.getParameter("epsgCode");
				}


				//

				if (request.getParameter("outputFormat").equals("turtle")) {
					format = RDFFormat.TURTLE;
				} else if (request.getParameter("outputFormat").equals("ntriples")) {
					format = RDFFormat.NTRIPLES;
				} else if (request.getParameter("outputFormat").equals("n3")) {
					format = RDFFormat.N3;
				} else if (request.getParameter("outputFormat").equals("rdfXml")) {
					format = RDFFormat.RDFXML;
				} else {
					format = RDFFormat.NTRIPLES;
				}
				//

				SesameDataSet outputDataSet = new SesameDataSet();
				// String fileName = "/Users/admin/Downloads/test.csv"; // afto
				// einai to shapefile

				RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();

				for (TriplesMap m : mapping.getTriplesMaps()) {
					RMLProcessor processor = factory.create(m.getLogicalSource()
							.getReferenceFormulation());
					Collection<Statement> statements = null;
					if (request.getParameter("inputFileType").equals("shp")) {
						System.out.println("shp2");
						processor.setInMemoryInput(false);
						statements = processor.execute(outputDataSet, m,
								new NodeRMLPerformer(processor), m
										.getLogicalSource().getIdentifier());
					} else if (request.getParameter("inputFileType").equals("csv")) {
						processor.setInMemoryInput(true);
						processor.setMemoryInput(request
								.getParameter("inputFileCont"));
						statements = processor.execute(outputDataSet, m,
								new NodeRMLPerformer(processor), null);
					} 
					else{
						System.out.println("not csv not shp not db");
					}
					//System.out.println(statements);


					System.out.println("The triples generated by one iteration:");
					

				}
				//System.out.println(outputDataSet.getSize());

				String outputFilename;
				if (request.getParameter("outputFilename").equals("")) {
					outputFilename = "output.nt";
				} else {
					outputFilename = request.getParameter("outputFilename");
				}
				outputDataSet.dumpRDF(outputFilename, format);

				byte[] buffer = new byte[4096];
				int read;
				FileInputStream in2 = new FileInputStream(outputFilename);
				ServletOutputStream out = response.getOutputStream();
				while ((read = in2.read(buffer, 0, 4096)) != -1) {
					out.write(buffer, 0, read);
					//System.out.println(new String(buffer, StandardCharsets.UTF_8));
				}
				in2.close();
				out.flush();
			} catch (RepositoryException | RDFParseException
					| InvalidR2RMLStructureException | InvalidR2RMLSyntaxException
					| R2RMLDataError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			
		}
		else if( request.getParameter("inputFileType").equals("db")){
			String connectionUrl = "jdbc:"+request.getParameter("dbEngine")+"://"+
					request.getParameter("dbHost")+":"+request.getParameter("dbPort")+
					"/"+request.getParameter("dbName");
			
			
			String r2rml = request.getParameter("rml");
			String format;
			
			if (request.getParameter("outputFormat").equals("turtle")) {
				format = "TURTLE";
			} else if (request.getParameter("outputFormat").equals("ntriples")) {
				format = "N-TRIPLE";
			} else if (request.getParameter("outputFormat").equals("n3")) {
				format = "TURTLE";
			} else if (request.getParameter("outputFormat").equals("rdfXml")) {
				format = "RDF/XML";
			} else {
				format = "TURTLE";
			}
			
			String epsgCode;
			if (request.getParameter("epsgCode").equals("")) {
				epsgCode = "4326";
			} else {
				epsgCode = request.getParameter("epsgCode");
			}
			
			try(  PrintWriter out = new PrintWriter("mapping.ttl")  ){
			    out.println(r2rml);
			}
			
			//File f1 = null;
			//f1 = File.createTempFile("mapping", ".ttl");
			//f1.deleteOnExit();

			
			 String[] argsd = {"-o", "result.ttl", 
					 "-b", "http://example.org", "-u", request.getParameter("dbUserName"), "-p", request.getParameter("dbPassword"),"-f", format,
					                  "-s",epsgCode,"-jdbc", connectionUrl, 
					 "mapping.ttl"};
			try {
				System.out.println("IN");
				dump_rdf.main(argsd);
				System.out.println("OUT");
				
				//
				//File f2 = null;
				//f2 = File.createTempFile("result", ".ttl");
				//f2.deleteOnExit();
				
				byte[] buffer = new byte[4096];
				int read;
				FileInputStream in2 = new FileInputStream("result.ttl");
				ServletOutputStream out = response.getOutputStream();
				while ((read = in2.read(buffer, 0, 4096)) != -1) {
					out.write(buffer, 0, read);
					//System.out.println(new String(buffer, StandardCharsets.UTF_8));
				}
				in2.close();
				out.flush();
				//
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		

	}

	public void registerFunctions() {
		FunctionFactory.registerFunction(new URIImpl(
				"http://www.w3.org/ns/r2rml-ext/functions/def/equi"),
				new FunctionEQUI()); // don't remove or change this line, it
										// replaces the equi join functionality
										// of R2RML

		FunctionFactory.registerFunction(new URIImpl(
				"http://www.w3.org/ns/r2rml-ext/functions/def/asWKT"),
				new FunctionAsWKT());
		FunctionFactory
				.registerFunction(
						new URIImpl(
								"http://www.w3.org/ns/r2rml-ext/functions/def/hasSerialization"),
						new FunctionHasSerialization());
		FunctionFactory.registerFunction(new URIImpl(
				"http://www.w3.org/ns/r2rml-ext/functions/def/asGML"),
				new FunctionAsGML());
		FunctionFactory.registerFunction(new URIImpl(
				"http://www.w3.org/ns/r2rml-ext/functions/def/isSimple"),
				new FunctionIsSimple());
		FunctionFactory.registerFunction(new URIImpl(
				"http://www.w3.org/ns/r2rml-ext/functions/def/isEmpty"),
				new FunctionIsEmpty());
		FunctionFactory.registerFunction(new URIImpl(
				"http://www.w3.org/ns/r2rml-ext/functions/def/is3D"),
				new FunctionIs3D());
		FunctionFactory
				.registerFunction(
						new URIImpl(
								"http://www.w3.org/ns/r2rml-ext/functions/def/spatialDimension"),
						new FunctionSpatialDimension());
		FunctionFactory.registerFunction(new URIImpl(
				"http://www.w3.org/ns/r2rml-ext/functions/def/dimension"),
				new FunctionDimension());
		FunctionFactory
				.registerFunction(
						new URIImpl(
								"http://www.w3.org/ns/r2rml-ext/functions/def/coordinateDimension"),
						new FunctionCoordinateDimension());
		FunctionFactory.registerFunction(new URIImpl(
				"http://www.w3.org/ns/r2rml-ext/functions/def/area"),
				new FunctionArea());
		FunctionFactory.registerFunction(new URIImpl(
				"http://www.w3.org/ns/r2rml-ext/functions/def/length"),
				new FunctionLength());
		FunctionFactory.registerFunction(new URIImpl(
				"http://www.w3.org/ns/r2rml-ext/functions/def/centroidx"),
				new FunctionCentroidX());
		FunctionFactory.registerFunction(new URIImpl(
				"http://www.w3.org/ns/r2rml-ext/functions/def/centroidy"),
				new FunctionCentroidY());

	}

	public void convertToRDF() throws RepositoryException, RDFParseException,
			InvalidR2RMLStructureException, InvalidR2RMLSyntaxException,
			R2RMLDataError, IOException, SQLException {

	}
}
