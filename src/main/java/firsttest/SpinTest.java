package firsttest;

import java.io.File;
import java.io.IOException;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.rio.RDFParseException;

public class SpinTest {
	public static void main(String[] args) {
	File baseDir = new File("/home/piero/Development/rdf4j/repotest1");
	LocalRepositoryManager manager = new LocalRepositoryManager(baseDir);
	String repositoryId = "spin-test-spin";
	Repository repository = manager.getRepository(repositoryId);
	RepositoryConnection con=repository.getConnection();
	File rulefile = new File("/home/piero/Development/sparql/effector-rules/next2.ttl");
	System.out.println("rule read");
	try {
		con.add(rulefile);
	} catch (RDFParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (RepositoryException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	File file = new File("/home/piero/Development/sparql/test-ais/ais5.ttl");
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
	}
	System.out.println("DONE!");
	}
}
