package firsttest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.rdf4j.common.lang.service.ServiceRegistry;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.config.SailFactory;
import org.eclipse.rdf4j.sail.config.SailImplConfig;
import org.eclipse.rdf4j.sail.config.SailRegistry;
import org.eclipse.rdf4j.sail.inferencer.fc.CustomGraphQueryInferencer;
import org.eclipse.rdf4j.sail.inferencer.fc.config.CustomGraphQueryInferencerConfig;
import org.eclipse.rdf4j.sail.inferencer.fc.config.DedupingInferencerConfig;
import org.eclipse.rdf4j.sail.inferencer.fc.config.SchemaCachingRDFSInferencerConfig;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;
import org.eclipse.rdf4j.sail.spin.config.SpinSailConfig;

import eng.rs.rdf4j.inference.MultipleRulesInference;
import eng.rs.rdf4j.inference.MultipleRulesInferenceConfig;
import eng.rs.rdf4j.inference.MultipleRulesInferenceFactory;

public class MyCustomReasoner {

	public static void main(String[] args) {
		File rulefile = new File("/home/piero/Development/sparql/effector-rules/next2.ttl");
		String myRules="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX sp: <http://spinrdf.org/sp#>"
				+ "PREFIX spin: <http://spinrdf.org/spin#>"
				+ "PREFIX effector: <http://effector.com/>"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "\n"
				+ "CONSTRUCT { ?this  effector:next ?g2. ?g2 effector:previus ?this } \n"
				+ "WHERE \n"
				+ "{\n"
				+ "  ?v geo:hasGeometry ?g2.\n"
				+ "  ?g2 time:inXSDDateTimeStamp ?pippo.\n"
				+ "  {\n"
				+ "    SELECT ?v ?this (min(?t2) as ?pippo)\n"
				+ "      WHERE \n"
				+ "      {\n"
				+ "        FILTER ( !EXISTS {\n"
				+ "         ?this effector:next ?x.\n"
				+ "        })  \n"
				+ "        FILTER ( !EXISTS {\n"
				+ "         ?g2 effector:previus ?x.\n"
				+ "        })  \n"
				+ "        ?v geo:hasGeometry ?this . \n"
				+ "        ?this  time:inXSDDateTimeStamp ?t1 .\n"
				+ "        ?v geo:hasGeometry ?g2.\n"
				+ "        ?g2 time:inXSDDateTimeStamp ?t2 .\n"
				+ "        filter(?this !=?g2)\n"
				+ "        filter(?t1<?t2)\n"
				+ "      }\n"
				+ "      GROUP BY ?v ?this \n"
				+ "  }  \n"
				+ "}";
		String myMatchs="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX sp: <http://spinrdf.org/sp#>\n"
				+ "PREFIX spin: <http://spinrdf.org/spin#>\n"
				+ "PREFIX effector: <http://effector.com/>\n"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "\n"
				+ "CONSTRUCT { ?this  effector:nexts ?g2. ?g2 effector:previuss ?this } \n"
				+ "WHERE \n"
				+ "{\n"
				+ "    ?this  effector:nexts ?g2.\n"
				+ "}";
		String myRule2="PREFIX effector: <http://effector.com/>\n"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
				+ "construct{?this effector:hasSegment _:s. _:s a effector:Segment. _:s a geo:Geometry . _:s geo:asWKT ?multi5. ?g1 effector:beginSegment _:s. ?g2 effector:endSegment _:s}\n"
				+ "where {\n"
				+ "        {\n"
				+ "        select ?this (GROUP_CONCAT(?coords;separator=',') as ?seg)(count(?g) as ?con) \n"
				+ "        where {\n"
				+ "                {\n"
				+ "                select ?this ?g ?coords ?tot1 \n"
				+ "                    where{\n"
				+ "                      FILTER ( !EXISTS {\n"
				+ "                         ?this effector:hasSegment ?x.\n"
				+ "                      })  \n"
				+ "                      ?this geo:hasGeometry ?g.\n"
				+ "                      ?g geo:asWKT ?coords.\n"
				+ "                      ?g time:inXSDDateTimeStamp ?tot1.\n"
				+ "                      ?g w3cGeo:long ?long.\n"
				+ "                      ?g w3cGeo:long ?lat\n"
				+ "                      filter(?long!=181.0)\n"
				+ "                      filter(?long!=91.0)\n"
				+ "                    }\n"
				+ "                order by asc(?tot1)\n"
				+ "                }\n"
				+ "            }\n"
				+ "        group by ?this\n"
				+ "        }\n"
				+ "filter(?con>1)\n"
				+ "bind(replace(?seg,'POINT','') as ?tot2)\n"
				+ "bind(replace(?tot2,'\\\\(','') as ?multi2)\n"
				+ "bind(replace(?multi2,'\\\\)','') as ?multi3)\n"
				+ "bind(concat('LINESTRING(',?multi3,')') as ?multi4)\n"
				+ "bind(STRDT(?multi4,geo:wktLiteral) as ?multi5)\n"
				+ "}";
		String myMatch2="PREFIX effector: <http://effector.com/>\n"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
				+ "\n"
				+ "construct{?g2 a effector:Segments. }\n"
				+ "where{ \n"
				+ "    ?g2 a effector:Segments.\n"
				+ "}";
		
	String rule3="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "PREFIX sp: <http://spinrdf.org/sp#>\n"
			+ "PREFIX spin: <http://spinrdf.org/spin#>\n"
			+ "PREFIX effector: <http://effector.com/>\n"
			+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
			+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
			+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
			+ "CONSTRUCT  {?this  a effector:Start}\n"
			+ "WHERE \n"
			+ "{\n"
			+ "    FILTER ( !EXISTS {\n"
			+ "     ?this a effector:Start.\n"
			+ "    })  \n"
			+ "  ?this  effector:next ?g2.\n"
			+ "  ?g2 effector:next ?g3.\n"
			+ "  ?this effector:hasSog ?s1.\n"
			+ "  ?g2 effector:hasSog ?s2 .\n"
			+ "  ?g3 effector:hasSog ?s3 . \n"
			+ "  filter(?s1!=?s2)\n"
			+ "  filter(?s1=0)\n"
			+ "  filter(?s3!=0)\n"
			+ "}";
	String match3="PREFIX effector: <http://effector.com/>\n"
			+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
			+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
			+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
			+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
			+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
			+ "construct{ ?this effector:next ?t}\n"
			+ "where {\n"
			+ "?this effector:next ?t}limit 10";
		String stopRule="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX sp: <http://spinrdf.org/sp#>\n"
				+ "PREFIX spin: <http://spinrdf.org/spin#>\n"
				+ "PREFIX effector: <http://effector.com/>\n"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "CONSTRUCT  {?this  a effector:Stop}\n"
				+ "WHERE \n"
				+ "{\n"
				+ "    FILTER ( !EXISTS {\n"
				+ "     ?this a effector:Stop.\n"
				+ "    })  \n"
				+ "  ?this  effector:next ?g2.\n"
				+ "  ?g2 effector:next ?g3.\n"
				+ "  ?this effector:hasSog ?s1.\n"
				+ "  ?g2 effector:hasSog ?s2 .\n"
				+ "  ?g3 effector:hasSog ?s3 . \n"
				+ "  filter(?s1!=?s2)\n"
				+ "  filter(?s2=0)\n"
				+ "  filter(?s3=0)\n"
				+ "}";
		String stopMatch="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX sp: <http://spinrdf.org/sp#>\n"
				+ "PREFIX spin: <http://spinrdf.org/spin#>\n"
				+ "PREFIX effector: <http://effector.com/>\n"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "CONSTRUCT  {?this a effector:Stop}\n"
				+ "WHERE \n"
				+ "{\n"
				+ "    ?this a effector:Stop"
	
				+ "}";
		String changeDirRule="PREFIX effector: <http://effector.com/>\n"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
				+ "\n"
				+ "construct{?g2 a effector:Turnpoint. ?g2 effector:turnof ?diff2 }\n"
				+ "where{ \n"
				+ "    FILTER ( !EXISTS {\n"
				+ "     ?this a effector:Turnpoint.\n"
				+ "    })  \n"
				+ "  ?this effector:hasCog ?h1.\n"
				+ "  ?this effector:next ?g2.\n"
				+ "  ?g2 effector:hasCog ?h2.\n"
				+ "  bind (if(?h1>?h2,?h1-?h2,?h2-?h1) as ?diff)\n"
				+ "  bind (if(?diff>180,360-?diff,?diff) as ?diff2)\n"
				+ "  filter(?diff2>45)\n"
				+ "  ?g2 w3cGeo:long ?long.\n"
				+ "  ?g2 w3cGeo:long ?lat\n"
				+ "  filter(?long!=181.0)\n"
				+ "  filter(?long!=91.0)\n"
				+ "}\n";
		String changeDirMatch="PREFIX effector: <http://effector.com/>\n"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
				+ "\n"
				+ "construct{?g2 a effector:Turnpoint. ?g2 effector:turnof ?diff2 }\n"
				+ "where{ \n"
				+ "   ?g2 a effector:Turnpoint."
				+ "   ?g2 effector:turnof ?diff2."
				+ "  filter(?long!=91.0)\n"
				+ "}\n";
		ArrayList<String> rules=new ArrayList<String>();
		ArrayList<String> match=new ArrayList<String>();
		rules.add(myRules);
		rules.add(myRule2);
		rules.add(rule3);
		rules.add(stopRule);
		rules.add(changeDirRule);
		match.add(myMatchs);
		match.add(myMatch2);
		match.add(match3);
		match.add(stopMatch);
		match.add(changeDirMatch);
		File baseDir = new File("/home/piero/.RDF4J/server");
		LocalRepositoryManager manager = new LocalRepositoryManager(baseDir);
		File file = new File("/home/piero/Development/sparql/test-ais/ais5.ttl");
		SailRegistry.getInstance().add(new MultipleRulesInferenceFactory());
		boolean persist = true;
		//SailImplConfig sailConfig = new SpinSailConfig(new DedupingInferencerConfig(new MemoryStoreConfig(persist)),false);
		SailImplConfig sailConfig = new DedupingInferencerConfig(new MemoryStoreConfig(persist));
		MultipleRulesInferenceConfig myneconf=new MultipleRulesInferenceConfig(sailConfig);
		myneconf.setMatcherQuery(match);
		myneconf.setRuleQuery(rules);
		myneconf.setQueryLanguage(QueryLanguage.SPARQL);
//		SailImplConfig sailConfig2 = new DedupingInferencerConfig(new MemoryStoreConfig(persist));
//		CustomGraphQueryInferencerConfig myneconf2=new CustomGraphQueryInferencerConfig(sailConfig2);
//		myneconf2.setRuleQuery(myRule2);		
//		myneconf2.setMatcherQuery(myMatch2);
//		myneconf2.setQueryLanguage(QueryLanguage.SPARQL);
		String repositoryId = "test-custom-inference-nospin21";
		SailRepositoryConfig repositoryTypeSpec = new SailRepositoryConfig(myneconf);
		RepositoryConfig repConfig = new RepositoryConfig(repositoryId, repositoryTypeSpec);
		manager.addRepositoryConfig(repConfig);
		manager.init();
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
		}
		con = repository.getConnection();
		System.out.println("DONE!");
		   String queryString = "PREFIX effector: <http://effector.com/>\n"
			   		+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
			   		+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
			   		+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			   		+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			   		+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
			   		+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
			   		+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
			   		+ "select ?a\n"
			   		+ "where {\n"
			   		+ "	?a a effector:Start.\n"
			   		+ "}limit 10";
			   TupleQuery tupleQuery = con.prepareTupleQuery(queryString);
			   try (TupleQueryResult result = tupleQuery.evaluate()) {
			      while (result.hasNext()) {  // iterate over the result
			         BindingSet bindingSet = result.next();
			         Value valueOfX = bindingSet.getValue("a");
			         System.out.println(valueOfX.stringValue());
			      }
			   }
		}		
	}


