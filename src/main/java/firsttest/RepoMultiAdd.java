package firsttest;

import java.io.File;
import java.io.IOException;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryImplConfig;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.config.SailImplConfig;
import org.eclipse.rdf4j.sail.inferencer.fc.config.DedupingInferencerConfig;
import org.eclipse.rdf4j.sail.inferencer.fc.config.SchemaCachingRDFSInferencerConfig;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;
import org.eclipse.rdf4j.sail.nativerdf.config.NativeStoreConfig;
import org.eclipse.rdf4j.sail.spin.config.SpinSailConfig;

public class RepoMultiAdd {

	public static void main(String[] args) {
		//File baseDir = new File("/home/piero/.RDF4J/server");
		File baseDir = new File("/home/piero/Development/rdf4j/repotest1");
		LocalRepositoryManager manager = new LocalRepositoryManager(baseDir);
		File file = new File("/home/piero/Development/sparql/test-temp/2.ttl");
		String repositoryId = "aaaaaaaa2";
		if(!manager.hasRepositoryConfig(repositoryId)) {
			SailImplConfig sailConfig = new NativeStoreConfig();
			RepositoryImplConfig repositoryTypeSpec = new SailRepositoryConfig(sailConfig);
			RepositoryConfig repConfig = new RepositoryConfig(repositoryId, repositoryTypeSpec);
			manager.addRepositoryConfig(repConfig);
			manager.init();
		}
        Repository repository = manager.getRepository(repositoryId);
        RepositoryConnection con = repository.getConnection();
		System.out.println("reading rdf file");
		try {
			con.add(file);
		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
		    con.close();
		}
		System.out.println("QUERY!");
		con = repository.getConnection();	
		   String queryString = "PREFIX effector: <http://effector.com/>\n"
			   		+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
			   		+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
			   		+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			   		+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			   		+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
			   		+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
			   		+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
			   		+ "select (count(*)as ?test)\n"
			   		+ "where {{\n"
			   		+ "	?a a geo:Geometry.\n"
			   		+ "}}";
			   TupleQuery tupleQuery = con.prepareTupleQuery(queryString);
			   try (TupleQueryResult result = tupleQuery.evaluate()) {
			      while (result.hasNext()) {  // iterate over the result
			         BindingSet bindingSet = result.next();
			         Value valueOfX = bindingSet.getValue("test");
			         System.out.println(valueOfX.stringValue());
			      }
			   }

		System.out.println("DONE!");
	}

}
