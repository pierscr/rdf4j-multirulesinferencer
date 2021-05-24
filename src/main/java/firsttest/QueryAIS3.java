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
	String repositoryId = "test-custom-inference-nospin21";
	SailRegistry.getInstance().add(new MultipleRulesInferenceFactory());
	Repository repository = manager.getRepository(repositoryId);
	RepositoryConnection con=repository.getConnection();
	String queryString="PREFIX effector: <http://effector.com/>\n"
			+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
			+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
			+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
			+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
			+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
			+ "PREFIX cfn: <http://eng.it/rdf4j/custom-function/>\n"
			+ "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n"
			+ "\n"
			+ "select ?puntiveri\n"
			+ "where\n"
			+ "{\n"
			+ "    {\n"
			+ "    select ?g1 (geof:envelope(STRDT(concat(\"MULTIPOINT (\",(GROUP_CONCAT(replace(str(?wkt2),\"POINT\",\"\");separator=\",\")),\")\"),geo:wktLiteral)) as ?puntiveri) \n"
			+ "      where{  \n"
			+ "      ?g1 ^geo:hasGeometry ?v1.\n"
			+ "      ?g1 a effector:Turnpoint.\n"
			+ "      ?g1 geo:asWKT ?wkt1.\n"
			+ "      ?g2 ^geo:hasGeometry ?v2.\n"
			+ "      ?g2 a effector:Turnpoint.\n"
			+ "      ?g2 geo:asWKT ?wkt2.\n"
			+ "      filter(!sameterm(?v1, ?v2))\n"
			+ "      bind(geof:distance(?wkt1,?wkt2,<http://www.opengis.net/def/uom/OGC/1.0/metre>) as ?test)\n"
			+ "      filter(?test<5000)\n"
			+ "      }\n"
			+ "      group by ?g1\n"
			+ "    }\n"
			+ "}group by ?puntiveri";
	   TupleQuery tupleQuery = con.prepareTupleQuery(queryString);
	   try (TupleQueryResult result = tupleQuery.evaluate()) {
	      while (result.hasNext()) {  // iterate over the result
	         BindingSet bindingSet = result.next();
	         Value valueOfX = bindingSet.getValue("puntiveri");
	         System.out.println(valueOfX.stringValue());
	      }
	   }
//	   String explanation = tupleQuery.explain(Level.Executed).toString();
//	   System.out.println(explanation);
	}
}
