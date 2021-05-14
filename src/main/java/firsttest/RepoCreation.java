package firsttest;

import java.io.File;

import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryImplConfig;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.sail.config.SailImplConfig;
import org.eclipse.rdf4j.sail.inferencer.fc.config.DedupingInferencerConfig;
import org.eclipse.rdf4j.sail.inferencer.fc.config.SchemaCachingRDFSInferencerConfig;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;
import org.eclipse.rdf4j.sail.spin.config.SpinSailConfig;

public class RepoCreation {

	public static void main(String[] args) {
		File baseDir = new File("/home/piero/Development/rdf4j/repotest1");
		LocalRepositoryManager manager = new LocalRepositoryManager(baseDir);
		SailImplConfig spinSailConfig = new SpinSailConfig(
		           new SchemaCachingRDFSInferencerConfig(
		                 new DedupingInferencerConfig(new MemoryStoreConfig())
		           ),false
		);
		RepositoryImplConfig repositoryTypeSpec = new SailRepositoryConfig(spinSailConfig);
		String repositoryId = "spin-test-spin";
		RepositoryConfig repConfig = new RepositoryConfig(repositoryId, repositoryTypeSpec);
		manager.addRepositoryConfig(repConfig);
		manager.init();
	}

}
