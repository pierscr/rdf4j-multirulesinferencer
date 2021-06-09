package eng.rs.rdf4j.test;

import it.eng.rslab.rdf4j.inference.MultipleRulesInference;
import it.eng.rslab.rdf4j.inference.MultipleRulesInferenceFactory;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.explanation.Explanation;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.config.SailRegistry;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MultiReasonerTest {

    public static void main(String[] args) throws IOException {
        File dataDir = new File("/tmp/myTmpRepository");
        SailRegistry.getInstance().add(new MultipleRulesInferenceFactory());
        Repository repo = new SailRepository(new MultipleRulesInference(new MemoryStore(), QueryLanguage.SPARQL, new File("/home/piero/Development/sparql/rulefolder")));
        RepositoryConnection con=repo.getConnection();
        File file = new File("/home/piero/Development/sparql/test-ais/ais5.ttl");
        FileOutputStream fos = new FileOutputStream("/home/piero/Development/sparql/results/output3.ttl");
        try {
            con.add(file);
            String queryString = "PREFIX effector: <http://effector.com/>\n" +
                    "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                    "PREFIX time: <http://www.w3.org/2006/time#>\n" +
                    "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "prefix owl: <http://www.w3.org/2002/07/owl#>\n" +
                    "prefix dc: <http://purl.org/dc/elements/1.1/>\n" +
                    "\n" +
                    "select  (count(*) as ?c)\n" +
                    "where \n" +
                    "{\n" +
                    " ?g1 effector:next ?g2\n" +
                    "}";
            TupleQuery tupleQuery = con.prepareTupleQuery(queryString);

               try (TupleQueryResult result = tupleQuery.evaluate()) {
                  while (result.hasNext()) {  // iterate over the result
                     BindingSet bindingSet = result.next();
                     Value valueOfv1 = bindingSet.getValue("c");
                     System.out.println(valueOfv1.stringValue());
                  }
               };

            RDFHandler writer = Rio.createWriter(RDFFormat.TURTLE, fos);
            Resource[] contexts = new Resource[] { null };
            con.exportStatements(null,null,null,true, writer,contexts);
        }finally {
            con.close();
        }
}}
