package firsttest;

import java.io.File;
import java.io.IOException;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.rio.RDFParseException;

public class ImportRules {

	public static void main(String[] args) {
		File baseDir = new File("/home/piero/Development/rdf4j/repotest1");
		LocalRepositoryManager manager = new LocalRepositoryManager(baseDir);
		String repositoryId = "spin-test1";
		Repository repository = manager.getRepository(repositoryId);
		System.out.println(repository.getDataDir());
		RepositoryConnection con=repository.getConnection();
		File file = new File("/home/piero/Development/sparql/test-ais/ais5.ttl");
		
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
		
	}

}
