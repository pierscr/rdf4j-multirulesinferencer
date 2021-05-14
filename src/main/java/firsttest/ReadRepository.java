package firsttest;

import java.io.File;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;

public class ReadRepository {

	public static void main(String[] args) {
		File baseDir = new File("/home/piero/Development/rdf4j/repotest1");
		LocalRepositoryManager manager = new LocalRepositoryManager(baseDir);
		String repositoryId = "spin-test1";
		Repository repository = manager.getRepository(repositoryId);
		System.out.println(repository.getDataDir());
	}

}
