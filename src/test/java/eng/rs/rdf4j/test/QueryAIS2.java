package eng.rs.rdf4j.test;

import java.io.File;
import java.io.IOException;


import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.explanation.Explanation.Level;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class QueryAIS2 {
	public static void main(String[] args) throws RDFParseException, RepositoryException, IOException {
	 Repository repo = new SailRepository(new MemoryStore());
	 RepositoryConnection con=repo.getConnection();
	 File file = new File("/home/piero/Development/sparql/test-ais/ais5.ttl");
	 con.add(file);
	 String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
	 		+ "PREFIX cfn: <http://eng.it/rdf4j/custom-function/>PREFIX sp: <http://spinrdf.org/sp#>"
	   		+ "PREFIX spin: <http://spinrdf.org/spin#>"
	   		+ "PREFIX effector: <http://effector.com/>"
	   		+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
	   		+ "PREFIX time: <http://www.w3.org/2006/time#>"
	   		+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
	   		+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
	   		+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
	   		+ "SELECT ?v1 ?v2 ?g1 ?g2 ?d\n"
	   		+ "WHERE {"
	   		+ "filter(?v1!=?v2)\n"
	   		+ "   ?v1 a geo:Feature.\n"
	   		+ "   ?v1 geo:hasGeometry ?g1.\n"
	   		+ "   ?g1 time:inXSDDateTimeStamp ?t1.    \n"
	   		+ "   ?v2 a geo:Feature.\n"
	   		+ "   ?v2 geo:hasGeometry ?g2.\n"
	   		+ "   ?g2 time:inXSDDateTimeStamp ?t2.\n"
	   		+ "   bind(cfn:minustime(?t1,?t2) as ?d)\n"
	   		+ "   filter(?d < 100000)"
	   		+ "}\n"
	   		+ "order by desc(?d)";
	   TupleQuery tupleQuery = con.prepareTupleQuery(queryString);
//	   try (TupleQueryResult result = tupleQuery.evaluate()) {
//	      while (result.hasNext()) {  // iterate over the result
//	         BindingSet bindingSet = result.next();
//	         Value valueOfv1 = bindingSet.getValue("v1");
//	         Value valueOfv2 = bindingSet.getValue("v2");
//	         Value valueOft1 = bindingSet.getValue("t1");
//	         Value valueOft2 = bindingSet.getValue("t2");
//	         Value valueOfresult = bindingSet.getValue("result");	         
//	         System.out.println(valueOfv1.stringValue()+"/"+valueOfv2.stringValue()+"/"+valueOft1+"/"+valueOft2+"/"+valueOfresult);
//	      }
//	   }
	   tupleQuery.setMaxExecutionTime(3600);
	   String explanation = tupleQuery.explain(Level.Executed).toString();
	   System.out.println(explanation);
	}
}
