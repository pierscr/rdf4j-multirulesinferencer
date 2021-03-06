package it.eng.rslab.rdf4j.inference;

/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;


/**
 * Configuration schema URI's for {@link MultipleRulesInferenca}.
 *
 * @author Dale Visser
 */
public class MultipleRulesInferenceSchema {

	/**
	 * The CustomGraphQueryInferencer schema namespace (
	 * <tt>http://www.openrdf.org/config/sail/customGraphQueryInferencer#</tt>).
	 */
	public static final String NAMESPACE = "http://www.openrdf.org/config/sail/multipleRulesInference#";

	/** <tt>http://www.openrdf.org/config/sail/multipleRulesInference#queryLanguage</tt> */
	public final static IRI QUERY_LANGUAGE;

	/** <tt>http://www.openrdf.org/config/sail/multipleRulesInference#ruleQuery</tt> */
	public final static IRI RULE_QUERY;

	/** <tt>http://www.openrdf.org/config/sail/multipleRulesInference#matcherQuery</tt> */
	public final static IRI MATCHER_QUERY;

	static {
		ValueFactory factory = SimpleValueFactory.getInstance();
		QUERY_LANGUAGE = factory.createIRI(NAMESPACE, "queryLanguage");
		RULE_QUERY = factory.createIRI(NAMESPACE, "ruleQuery");
		MATCHER_QUERY = factory.createIRI(NAMESPACE, "matcherQuery");
	}
}
