package eng.rs.rdf4j.inference;

/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.config.SailConfigException;
import org.eclipse.rdf4j.sail.config.SailFactory;
import org.eclipse.rdf4j.sail.config.SailImplConfig;

/**
 * A {@link SailFactory} that creates a {@link CustomGraphQueryInferencer} based on RDF configuration data.
 *
 * @author Dale Visser
 */
public class MultipleRulesInferenceFactory implements SailFactory {

	/**
	 * The type of repositories that are created by this factory.
	 *
	 * @see SailFactory#getSailType()
	 */
	public static final String SAIL_TYPE = "openrdf:MultipleRulesInference";

	/**
	 * Returns the Sail's type: <tt>openrdf:MultipleRulesInference</tt>.
	 */
	@Override
	public String getSailType() {
		return SAIL_TYPE;
	}

	@Override
	public SailImplConfig getConfig() {
		return new MultipleRulesInferenceConfig();
	}

	@Override
	public Sail getSail(SailImplConfig config) throws SailConfigException {
		if (!SAIL_TYPE.equals(config.getType())) {
			throw new SailConfigException("Invalid Sail type: " + config.getType());
		}
		MultipleRulesInference sail = new MultipleRulesInference();
		if (config instanceof MultipleRulesInferenceConfig) {
			MultipleRulesInferenceConfig customConfig = (MultipleRulesInferenceConfig) config;
			try {
				sail.setFields(customConfig.getQueryLanguage(), customConfig.getRuleQuery(),
						customConfig.getMatcherQuery());
			} catch (RDF4JException e) {
				throw new SailConfigException("Problem occured parsing rule or matcher query text.", e);
			}
		}
		return sail;
	}
}
