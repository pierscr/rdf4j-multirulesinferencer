package firsttest;

import java.io.File;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.explanation.Explanation.Level;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.sail.config.SailRegistry;

import eng.rs.rdf4j.inference.MultipleRulesInferenceFactory;

public class QueryAIS3 {
	public static void main(String[] args) {
	File baseDir = new File("/home/piero/.RDF4J/server");
	LocalRepositoryManager manager = new LocalRepositoryManager(baseDir);
	String repositoryId = "test-custom-inference-nospin19";
	SailRegistry.getInstance().add(new MultipleRulesInferenceFactory());
	Repository repository = manager.getRepository(repositoryId);
	RepositoryConnection con=repository.getConnection();
	   String queryString = "PREFIX effector: <http://effector.com/>\n"
	   		+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
	   		+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
	   		+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
	   		+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
	   		+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
	   		+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
	   		+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
	   		+ "select ?a\n"
	   		+ "where {\n"
	   		+ "	?a a effector:Start.\n"
	   		+ "}";
	   TupleQuery tupleQuery = con.prepareTupleQuery(queryString);
	   try (TupleQueryResult result = tupleQuery.evaluate()) {
	      while (result.hasNext()) {  // iterate over the result
	         BindingSet bindingSet = result.next();
	         Value valueOfX = bindingSet.getValue("a");
	         System.out.println(valueOfX.stringValue());
	      }
	   }
//	   String explanation = tupleQuery.explain(Level.Executed).toString();
//	   System.out.println(explanation);
	}
}
