package it.eng.rslab.rdf4j.inference;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/

import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.UnsupportedQueryLanguageException;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.Var;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.impl.EmptyBindingSet;
import org.eclipse.rdf4j.query.parser.ParsedGraphQuery;
import org.eclipse.rdf4j.query.parser.QueryParserUtil;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.sail.NotifyingSail;
import org.eclipse.rdf4j.sail.SailConnectionListener;
import org.eclipse.rdf4j.sail.SailException;
import org.eclipse.rdf4j.sail.helpers.NotifyingSailWrapper;
import org.eclipse.rdf4j.sail.inferencer.InferencerConnection;
import org.eclipse.rdf4j.sail.inferencer.InferencerConnectionWrapper;
import org.eclipse.rdf4j.sail.inferencer.fc.config.CustomGraphQueryInferencerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A forward-chaining inferencer that infers new statements using a SPARQL or SeRQL graph query.
 *
 * @author Dale Visser
 */
public class MultipleRulesInference extends NotifyingSailWrapper {

	private static final Logger logger = LoggerFactory.getLogger(MultipleRulesInference.class);

	private ArrayList<ParsedGraphQuery> customQuerys = new ArrayList<ParsedGraphQuery>();

	private ArrayList<ParsedGraphQuery> customMatchers = new ArrayList<ParsedGraphQuery>();

	private final ArrayList<Collection<Value>> watchPredicatesArray = new  ArrayList<Collection<Value>>();//new HashSet<>();

	private final ArrayList<Collection<Value>> watchSubjectsArray  = new  ArrayList<Collection<Value>>();

	private final ArrayList<Collection<Value>>watchObjectsArray  = new  ArrayList<Collection<Value>>();

	private ArrayList<Boolean> hasWatchValuesArray = new ArrayList<Boolean>();
	
	ListIterator <ParsedGraphQuery> itercustomQuerys;
	ListIterator <ParsedGraphQuery>itercustomMatchers;

	boolean addStatemet=false;

	public MultipleRulesInference() {
		super();
	}

	/**
	 * Create a new custom inferencer.
	 *
	 * @param language    language that <tt>queryText</tt> and <tt>matcherText</tt> are expressed in
	 * @param queryText   a query that returns an RDF graph of inferred statements to be added to the underlying Sail
	 * @throws MalformedQueryException           if there is a problem parsing either of the given queries
	 * @throws UnsupportedQueryLanguageException if an unsupported query language is specified
	 * @throws SailException                     if a problem occurs interpreting the rule pattern
	 */
	public MultipleRulesInference(QueryLanguage language, ArrayList<String> queryText)
			throws MalformedQueryException, UnsupportedQueryLanguageException, SailException {
		super();
		setFields(language, queryText);
		logger.debug("MultipleRulesInference V0.0.8-SNAPSHOT");
	}

	/**
	 * Create a new custom inferencer.
	 *
	 * @param baseSail    an underlying Sail, such as another inferencer or a SailRepository
	 * @param language    language that <tt>queryText</tt> and <tt>matcherText</tt> are expressed in
	 * @param queryText   a query that returns an RDF graph of inferred statements to be added to the underlying Sail
	 * @param matcherText a query that returns an RDF graph of existing inferred statements already added previously
	 * @throws MalformedQueryException           if there is a problem parsing either of the given queries
	 * @throws UnsupportedQueryLanguageException
	 * @throws SailException                     if a problem occurs interpreting the rule pattern
	 */
	public MultipleRulesInference(NotifyingSail baseSail, QueryLanguage language, ArrayList<String> queryText,
			ArrayList<String> matcherText) throws MalformedQueryException, UnsupportedQueryLanguageException, SailException {
		super(baseSail);
		logger.debug("MultipleRulesInference V0.0.8-SNAPSHOT");
		setFields(language, queryText);
	}

	public MultipleRulesInference(NotifyingSail baseSail, QueryLanguage language, File rulesFolder) throws MalformedQueryException, UnsupportedQueryLanguageException, SailException {
		super(baseSail);
		logger.debug("MultipleRulesInference V0.0.8-SNAPSHOT");
		File[] listOfFiles = rulesFolder.listFiles();
		Iterator<File> fileIterator=Arrays.stream(listOfFiles).sorted(new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
			}
		}).iterator();
		ArrayList<String> queryTexts=new ArrayList<String>();
		while(fileIterator.hasNext()){
			try {
				queryTexts.add(FileUtils.readFileToString(fileIterator.next(), StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new SailException(e);
			}
		}
		setFields(language, queryTexts);
	}

	/**
	 * Called in order to set all the fields needed for the inferencer to function.
	 *
	 * @param language    language that <tt>queryText</tt> and <tt>matcherText</tt> are expressed in
	 * @param queryTextCollection   a query that returns an RDF graph of inferred statements to be added to the underlying Sail
	 * @throws MalformedQueryException if there is a problem parsing either of the given queries
	 * @throws SailException           if a problem occurs interpreting the rule pattern
	 */
	public final void setFields(QueryLanguage language, ArrayList<String> queryTextCollection)
			throws MalformedQueryException, SailException {
	    ListIterator<String> iter=queryTextCollection.listIterator();
		while(iter.hasNext()){
			String queryText=iter.next();

			ParsedGraphQuery customQuery = QueryParserUtil.parseGraphQuery(language, queryText, null);
			Collection<Value> watchSubjects = new HashSet<>();
			Collection<Value> watchPredicates = new HashSet<>();
			Collection<Value> watchObjects = new HashSet<>();
/*			customQuery.getTupleExpr().visit(new AbstractQueryModelVisitor<SailException>() {

				@Override
				public void meet(StatementPattern statement) throws SailException {
					Var var = statement.getSubjectVar();
					if (var.hasValue()) {
						
						watchSubjects.add(var.getValue());
					}
					var = statement.getPredicateVar();
					if (var.hasValue()) {
						watchPredicates.add(var.getValue());
					}
					var = statement.getObjectVar();
					if (var.hasValue()) {
						watchObjects.add(var.getValue());
					}
				}
			});
			hasWatchValuesArray.add(!(watchSubjects.isEmpty() && watchPredicates.isEmpty() && watchObjects.isEmpty()));*/
			customQuerys.add(customQuery);
/*			watchSubjectsArray.add(watchSubjects);
			watchPredicatesArray.add(watchPredicates);
			watchObjectsArray.add(watchObjects);*/
		}
		itercustomQuerys=customQuerys.listIterator();
		//itercustomMatchers=customMatchers.listIterator();
	}

	@Override
	public InferencerConnection getConnection() throws SailException {
		try {
			InferencerConnection con = (InferencerConnection) super.getConnection();
			return new Connection(con);
		} catch (ClassCastException e) {
			throw new SailException(e.getMessage(), e);
		}
	}

	@Override
	public void initialize() throws SailException {
		super.initialize();
		try (InferencerConnection con = getConnection()) {
			con.begin();
			con.flushUpdates();
			con.commit();
		}
	}

	/**
	 * Exposed for test purposes.
	 *
	 * @return a computed collection of the statement subjects that, when added or removed, trigger an update of
	 *         inferred statements
	 */
	public ArrayList<Collection<Value>> getWatchSubjects() {
		return watchSubjectsArray;
	}

	/**
	 * Exposed for test purposes.
	 *
	 * @return a computed collection of the statement predicates that, when added or removed, trigger an update of
	 *         inferred statements
	 */
	public ArrayList<Collection<Value>>  getWatchPredicates() {
		return watchPredicatesArray;
	}

	/**
	 * Exposed for test purposes.
	 *
	 * @return a computed collection of the statement objects that, when added or removed, trigger an update of inferred
	 *         statements
	 */
	public ArrayList<Collection<Value>>  getWatchObjects() {
		return watchObjectsArray;
	}

	private class Connection extends InferencerConnectionWrapper implements SailConnectionListener {

		/**
		 * Flag indicating whether an update of the inferred statements is needed.
		 */
		private ArrayList<Boolean> updateNeededArray = new ArrayList<Boolean>();

		private Connection(InferencerConnection con) {
			super(con);
			con.addConnectionListener(this);
		}

		@Override
		public void statementAdded(Statement statement) {
			addStatemet=true;
		}

		@Override
		public void statementRemoved(Statement statement) {
			//setUpdateNeededIfMatching(statement);
		}

		private void setUpdateNeededIfMatching(Statement statement) {
			ListIterator<Boolean> iterWatchValues=hasWatchValuesArray.listIterator();
			ListIterator<Collection<Value>> iterPredicates=watchPredicatesArray.listIterator();
			ListIterator<Collection<Value>> iterSubjects=watchSubjectsArray.listIterator();
			ListIterator<Collection<Value>> iterObjects=watchObjectsArray.listIterator();
			updateNeededArray=new ArrayList<Boolean>();
			while(iterWatchValues.hasNext()) {
			
				boolean updateNeeded = iterWatchValues.next() ? iterPredicates.next().contains(statement.getPredicate())
						|| iterSubjects.next().contains(statement.getSubject()) || iterObjects.next().contains(statement.getObject())
						: true;
				updateNeededArray.add(updateNeeded);
			}

		}

		@Override
		public void rollback() throws SailException {
			super.rollback();
			updateNeededArray=new ArrayList<Boolean>();
		}

		@Override
		public void flushUpdates() throws SailException {
			super.flushUpdates();
			logger.debug("MultipleRulesInference V0.0.8-SNAPSHOT");
			ListIterator <Boolean>iterUpdateNeeded=updateNeededArray.listIterator();
			while(addStatemet && itercustomQuerys.hasNext()) {
				Collection<Statement> forAddition = new HashSet<>(256);
				Resource[] contexts = new Resource[] { null };

					try {
						// Determine which statements should be added and which should be
						// removed
						buildDeltaSets(forAddition,itercustomQuerys.next());
						logger.debug("new check on new object and new virtual properties: {}", forAddition.size());
						for (Statement st : forAddition) {
							addInferredStatement(st.getSubject(), st.getPredicate(), st.getObject(), contexts);
						}
					} catch (RDFHandlerException e) {
						Throwable cause = e.getCause();
						if (cause instanceof SailException) {
							throw (SailException) cause;
						} else {
							throw new SailException(cause);
						}
					} catch (QueryEvaluationException e) {
						throw new SailException(e);
					}
					super.flushUpdates();

			}
		}

		private void buildDeltaSets(Collection<Statement> forAddition,ParsedGraphQuery customQuery)
				throws SailException, RDFHandlerException, QueryEvaluationException {
			//evaluateIntoStatements(customMatcher, forRemoval);
			evaluateIntoStatements(customQuery, forAddition);
			//logger.debug("existing virtual properties: {}", forRemoval.size());
			logger.debug("new test virtual properties: {}", forAddition.size());
			//Collection<Statement> inCommon = new HashSet<>(forRemoval);
			//inCommon.retainAll(forAddition);
			//forRemoval.removeAll(inCommon);
			//forAddition.removeAll(inCommon);
			//logger.debug("virtual properties to remove: {}", forRemoval.size());
			//logger.debug("virtual properties to add: {}", forAddition.size());
		}

		private void evaluateIntoStatements(ParsedGraphQuery query, Collection<Statement> statements)
				throws SailException, RDFHandlerException, QueryEvaluationException {

			try (CloseableIteration<? extends BindingSet, QueryEvaluationException> bindingsIter = getWrappedConnection()
					.evaluate(query.getTupleExpr(), null, EmptyBindingSet.getInstance(), true)) {
				ValueFactory factory = getValueFactory();
				while (bindingsIter.hasNext()) {
					BindingSet bindings = bindingsIter.next();
					Value subj = bindings.getValue("subject");
					Value pred = bindings.getValue("predicate");
					Value obj = bindings.getValue("object");
					if (subj instanceof Resource && pred instanceof IRI && obj != null) {
						statements.add(factory.createStatement((Resource) subj, (IRI) pred, obj));
					}
				}
			}
		}
	}
}
