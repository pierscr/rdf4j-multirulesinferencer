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

public class QueryAIS2 {
	public static void main(String[] args) {
	File baseDir = new File("/home/piero/Development/rdf4j/repotest1");
	LocalRepositoryManager manager = new LocalRepositoryManager(baseDir);
	String repositoryId = "spin-test1";
	Repository repository = manager.getRepository(repositoryId);
	RepositoryConnection con=repository.getConnection();
	   String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
	   		+ "PREFIX sp: <http://spinrdf.org/sp#>"
	   		+ "PREFIX spin: <http://spinrdf.org/spin#>"
	   		+ "PREFIX effector: <http://effector.com/>"
	   		+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
	   		+ "PREFIX time: <http://www.w3.org/2006/time#>"
	   		+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
	   		+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
	   		+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
	   		+ "select ?this ?g2 "
	   		+ "WHERE "
	   		+ "{"
	   		+ "  ?v geo:hasGeometry ?g2."
	   		+ "  ?g2 time:inXSDDateTimeStamp ?pippo."
	   		+ "  {"
	   		+ "    SELECT ?v ?this (min(?t2) as ?pippo)"
	   		+ "      WHERE"
	   		+ "      {"
	   		+ "        FILTER ( !EXISTS {"
	   		+ "         ?this effector:next ?x."
	   		+ "        })"
	   		+ "        FILTER ( !EXISTS {"
	   		+ "         ?g2 effector:previus ?x."
	   		+ "        })"
	   		+ "        ?v geo:hasGeometry ?this ."
	   		+ "        ?this  time:inXSDDateTimeStamp ?t1 ."
	   		+ "        ?v geo:hasGeometry ?g2."	   		
	   		+ "        ?g2 time:inXSDDateTimeStamp ?t2 ."
	   		+ "        filter(?this !=?g2)"
	   		+ "        filter(?t1<?t2)"
	   		+ "      }"
	   		+ "      GROUP BY ?v ?this "
	   		+ "  }"
	   		+ "}";
	   TupleQuery tupleQuery = con.prepareTupleQuery(queryString);
	   try (TupleQueryResult result = tupleQuery.evaluate()) {
	      while (result.hasNext()) {  // iterate over the result
	         BindingSet bindingSet = result.next();
	         Value valueOfX = bindingSet.getValue("this");
	         Value valueOfY = bindingSet.getValue("g2");
	         System.out.println(valueOfX.stringValue()+"/"+valueOfY.stringValue());
	      }
	   }
//	   String explanation = tupleQuery.explain(Level.Executed).toString();
//	   System.out.println(explanation);
	}
}
