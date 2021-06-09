package eng.rs.rdf4j.test;

import org.eclipse.rdf4j.IsolationLevels;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.File;
import java.io.IOException;

public class TestLockRepository {
    public static void main(String[] args) {
        File baseDir = new File("/home/piero/.RDF4J/server");
        LocalRepositoryManager manager = new LocalRepositoryManager(baseDir);
        Repository repository = manager.getRepository("nifi-test4");
        repository.init();
        File file = new File("/home/piero/Development/sparql/test-ais/ais5.ttl");
        try(RepositoryConnection con=repository.getConnection()) {
            con.begin(IsolationLevels.SERIALIZABLE);
            con.add(file);
            con.commit();
            con.close();
            repository.shutDown();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}
