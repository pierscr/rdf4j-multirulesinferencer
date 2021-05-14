package firsttest;
import java.io.File;
import java.util.function.Consumer;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.util.ModelException;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryImplConfig;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.sail.config.SailConfigException;
import org.eclipse.rdf4j.sail.config.SailImplConfig;
import org.eclipse.rdf4j.sail.inferencer.fc.config.DedupingInferencerConfig;
import org.eclipse.rdf4j.sail.inferencer.fc.config.SchemaCachingRDFSInferencerConfig;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;
import org.eclipse.rdf4j.sail.spin.SpinSail;
import org.eclipse.rdf4j.sail.spin.config.SpinSailConfig;
import org.eclipse.rdf4j.sail.spin.config.SpinSailSchema;

public class Test {
	@SuppressWarnings("deprecation")
	public static void main(String[] args){

		
		// create a basic Sail Stack with a simple Memory Store and SPIN inferencing support
		@SuppressWarnings("deprecation")
		File baseDir = new File("/opt/testrepo2");
		LocalRepositoryManager manager = new LocalRepositoryManager(baseDir);
		manager.init();
		File dataDir = new File("/opt/testrepo");
		MemoryStore memStore = new MemoryStore(dataDir);
//		Repository repo = new SailRepository(memStore);

		SpinSail spinSail = new SpinSail();
		spinSail.setBaseSail(memStore);
		spinSail.setValidateConstraints(false);
		Repository rep = new SailRepository(spinSail);
		rep.init();
		// create the config for the sail stack
		SailImplConfig spinSailConfig = new SpinSailConfig(
		           new SchemaCachingRDFSInferencerConfig(
		                 new DedupingInferencerConfig(new MemoryStoreConfig())
		           ),false
		);
		RepositoryImplConfig repositoryTypeSpec = new SailRepositoryConfig(spinSailConfig);
		// create the config for the actual repository
		String repositoryId = "spin-test";
		RepositoryConfig repConfig = new RepositoryConfig(repositoryId, repositoryTypeSpec);
		manager.addRepositoryConfig(repConfig);

		// get the Repository from the manager
		Repository repository = manager.getRepository(repositoryId);
		System.out.println("ultima versione2");
	}
}
