package firsttest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.algebra.evaluation.function.FunctionRegistry;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.config.SailImplConfig;
import org.eclipse.rdf4j.sail.inferencer.fc.config.DedupingInferencerConfig;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;

import it.eng.rslab.rdf4j.inference.MultipleRulesInferenceConfig;
import eng.rslab.rdf4j.functions.PalindromeFunction;

public class MyCustomReasoner {

	public static void main(String[] args) {
		File rulefile = new File("/home/piero/Development/sparql/effector-rules/next2.ttl");
		String myRules="#a\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
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
		String myMatchs="#a\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX sp: <http://spinrdf.org/sp#>\n"
				+ "PREFIX spin: <http://spinrdf.org/spin#>\n"
				+ "PREFIX effector: <http://effector.com/>\n"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "\n"
				+ "CONSTRUCT { ?this  effector:next ?g2. ?g2 effector:previus ?this } \n"
				+ "WHERE \n"
				+ "{\n"
				+ "    ?this  effector:nexts ?g2.\n"
				+ "}";
		String myRule2="#b\n"
				+ "PREFIX effector: <http://effector.com/>\n"
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
		String myMatch2="#b\n"
				+ "PREFIX effector: <http://effector.com/>\n"
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
		
	String rule3="#c\n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
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
	String match3="#c\n"
			+ "PREFIX effector: <http://effector.com/>\n"
			+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
			+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
			+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
			+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
			+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
			+ "construct{ ?this a effector:Start}\n"
			+ "where {\n"
			+ "?this a effector:Start}";
		String stopRule="#e\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
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
		String stopMatch="#e\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
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
		String changeDirRule="#f\n"
				+ "PREFIX effector: <http://effector.com/>\n"
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
		String changeDirMatch="#f\n"
				+ "PREFIX effector: <http://effector.com/>\n"
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
		

		String changeAreaRule="#g\n"
				+ "PREFIX effector: <http://effector.com/>\n"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
				+ "PREFIX cfn: <http://eng.it/rdf4j/custom-function/>\n"
				+ "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n"
				+ "\n"
				+ "construct{ _:s geo:asWKT ?puntiveri. _:s a effector:Area}\n"
				+ "where\n"
				+ "{\n"
				+ "    {\n"
				+ "        select ?g1 (geof:envelope(STRDT(concat(\"MULTIPOINT (\",replace(str(?wkt1),\"POINT\",\"\"),\",\",(GROUP_CONCAT(replace(str(?wkt2),\"POINT\",\"\");separator=\",\")),\")\"),geo:wktLiteral)) as ?puntiveri)\n"
				+ "        where{  \n"
				+ "          ?g1 a effector:Turnpoint.\n"
				+ "          ?g1 geo:asWKT ?wkt1.\n"
				+ "          ?g2 a effector:Turnpoint.\n"
				+ "          ?g2 geo:asWKT ?wkt2.\n"
				+ "          ?g1 ^geo:hasGeometry ?v1.\n"
				+ "          ?g2 ^geo:hasGeometry ?v2.\n"
				+ "          filter(!sameterm(?v1, ?v2))\n"
				+ "          bind(geof:distance(?wkt1,?wkt2,<http://www.opengis.net/def/uom/OGC/1.0/metre>) as ?test)\n"
				+ "          filter(?test<20000)\n"
				+ "        }\n"
				+ "        group by ?g1 ?wkt1\n"
				+ "    }\n"
				+ "}group by ?puntiveri";
		String changeAreaMatch="#g\n"
				+ "PREFIX effector: <http://effector.com/>\n"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
				+ "PREFIX cfn: <http://eng.it/rdf4j/custom-function/>\n"
				+ "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n"
				+ "\n"
				+ "construct{ ?a effector:Area ?b}\n"
				+ "where\n"
				+ "{\n"
				+ " ?a effector:Area ?b\n"
				+ "}";
	    String ruleAreaTurnpoint="#h\n"
				+ "PREFIX effector: <http://effector.com/>\n"
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
				+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
				+ "PREFIX cfn: <http://eng.it/rdf4j/custom-function/>\n"
				+ "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n"
				+ "\n"
				+ "construct{ _:s geo:asWKT ?puntiveri. _:s a effector:AreaTurnpoint}\n"
				+ "where\n"
				+ "{\n"
				+ "    {\n"
				+ "        select ?g1 (geof:envelope(STRDT(concat(\"MULTIPOINT (\",replace(str(?wkt1),\"POINT\",\"\"),\",\",(GROUP_CONCAT(replace(str(?wkt2),\"POINT\",\"\");separator=\",\")),\")\"),geo:wktLiteral)) as ?puntiveri) (count(?v2) as ?nvassels)\n"
				+ "        where{  \n"
				+ "          ?g1 a effector:Turnpoint.\n"
				+ "          ?g1 geo:asWKT ?wkt1.\n"
				+ "          ?g2 a effector:Turnpoint.\n"
				+ "          ?g2 geo:asWKT ?wkt2.\n"
				+ "          ?g1 ^geo:hasGeometry ?v1.\n"
				+ "          ?g2 ^geo:hasGeometry ?v2.\n"
				+ "          filter(!sameterm(?v1, ?v2))\n"
				+ "          bind(geof:distance(?wkt1,?wkt2,<http://www.opengis.net/def/uom/OGC/1.0/metre>) as ?test)\n"
				+ "          filter(?test<5000)\n"
				+ "        }\n"
				+ "        group by ?g1 ?wkt1\n"
				+ "        HAVING (?nvassels > 2)\n"
				+ "    }\n"
				+ "}group by ?puntiveri";
	    String matchAreaTurnpoint="#h\n"
				+ "PREFIX effector: <http://effector.com/>\n"
	    		+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
	    		+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
	    		+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
	    		+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
	    		+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
	    		+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
	    		+ "PREFIX w3cGeo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
	    		+ "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n"
	    		+ "construct { ?a a effector:AreaTurnpoint. ?a geo:asWKT ?wkt}\n"
	    		+ "where\n"
	    		+ "  {\n"
	    		+ "	?a a effector:AreaTurnpoint.\n"
	    		+ "  }";
	    String ruleIntersection="#i\n"
	    		+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
	    		+ "PREFIX cfn: <http://eng.it/rdf4j/custom-function/>\n"
	    		+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
	    		+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
	    		+ "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n"
	    		+ "PREFIX effector: <http://effector.com/>\n"
	    		+ "construct {_:a geo:asWKT ?puntiveri. _:a a effector:Intersection}\n"
	    		+ "where{\n"
	    		+ "        {\n"
	    		+ "        SELECT distinct ?v1 ?v2 ?w1 ?w2 ?d \n"
	    		+ "        WHERE {\n"
	    		+ "          filter(?v1!=?v2)\n"
	    		+ "          ?v1 a geo:Feature.\n"
	    		+ "          ?v1 effector:hasSegment ?g1.\n"
	    		+ "          ?g1 geo:asWKT ?w1.  \n"
	    		+ "          ?v2 a geo:Feature.\n"
	    		+ "          ?v2 effector:hasSegment ?g2.\n"
	    		+ "          ?g2 geo:asWKT ?w2.\n"
	    		+ "          bind(geof:sfIntersects(?w1,?w2) as ?d)\n"
	    		+ "          filter(?d!=false)\n"
	    		+ "            }\n"
	    		+ "        }  \n"
	    		+ "    bind(geof:intersection(?w1,?w2) as ?points)\n"
	    		+ "    bind(geof:envelope(?points) as ?puntiveri)\n"
	    		+ "}";
	    String matchIntersection="#i/n"
	    		+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
	    		+ "PREFIX cfn: <http://eng.it/rdf4j/custom-function/>\n"
	    		+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
	    		+ "PREFIX time: <http://www.w3.org/2006/time#>\n"
	    		+ "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n"
	    		+ "PREFIX effector: <http://effector.com/>\n"
	    		+ "construct {?x geo:asWKT ?puntiveri. ?x a effector:Intersection}\n"
	    		+ "where{\n"
	    		+ "	?x a effector:Intersection\n"
	    		+ "}";
		ArrayList<String> rules=new ArrayList<String>();
		ArrayList<String> match=new ArrayList<String>();
		rules.add(myRules);
		rules.add(myRule2);
		rules.add(rule3);
		rules.add(stopRule);
		rules.add(changeDirRule);
		rules.add(changeAreaRule);
		rules.add(ruleAreaTurnpoint);
		rules.add(ruleIntersection);
		match.add(myMatchs);
		match.add(myMatch2);
		match.add(match3);
		match.add(stopMatch);
		match.add(changeDirMatch);
		match.add(changeAreaMatch);
		match.add(matchAreaTurnpoint);
		match.add(matchIntersection);
		File baseDir = new File("/home/piero/.RDF4J/server");
		LocalRepositoryManager manager = new LocalRepositoryManager(baseDir);
		File file = new File("/home/piero/Development/sparql/test-ais/ais-26mega2.ttl");
//		SailRegistry.getInstance().add(new MultipleRulesInferenceFactory());
		FunctionRegistry.getInstance().add(new PalindromeFunction());
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
		String repositoryId = "test-custom-inference-nospin38";
//		String repositoryId = "trytoupdate";
		if(!manager.hasRepositoryConfig(repositoryId)) {
			SailRepositoryConfig repositoryTypeSpec = new SailRepositoryConfig(myneconf);
			RepositoryConfig repConfig = new RepositoryConfig(repositoryId, repositoryTypeSpec);
			manager.addRepositoryConfig(repConfig);
		}
        Repository repository = manager.getRepository(repositoryId);
		manager.init();
        RepositoryConnection con = repository.getConnection();
		System.out.println("reading rdf file");
		try {
			con.add(file
					);
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
		System.out.println("DONE!");
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
			   		+ "select (count(*)as ?n)\n"
			   		+ "where {{\n"
			   		+ "	?test effector:next ?b.\n"
			   		+ "}}";
			   TupleQuery tupleQuery = con.prepareTupleQuery(queryString);
			   try (TupleQueryResult result = tupleQuery.evaluate()) {
			      while (result.hasNext()) {  // iterate over the result
			         BindingSet bindingSet = result.next();
			         Value valueOfX = bindingSet.getValue("n");
			         System.out.println(valueOfX.stringValue());
			      }
			   }
		}		
	}


