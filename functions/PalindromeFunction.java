package eng.rslab.rdf4j.functions;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.algebra.evaluation.ValueExprEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.function.Function;

public class PalindromeFunction implements Function  {
	
	public static final String NAMESPACE = "http://example.org/custom-function/";

	
	public String getURI() {
	    return NAMESPACE + "palindrome";
	}
	
	 /**
     * Executes the palindrome function.
     *
     * @return A boolean literal representing true if the input argument is a
     *         palindrome, false otherwise.
     * @throws ValueExprEvaluationException
     *         if more than one argument is supplied or if the supplied argument
     *         is not a literal.
     */
    public Value evaluate(ValueFactory valueFactory, Value... args)
	throws ValueExprEvaluationException
	{
	    // our palindrome function expects only a single argument, so throw an error
	    // if there's more than one
	    if (args.length != 1) {
		throw new ValueExprEvaluationException(
			"palindrome function requires"
			+ "exactly 1 argument, got "
			+ args.length);
	    }
	    Value arg = args[0];
	    // check if the argument is a literal, if not, we throw an error
	    if (!(arg instanceof Literal)) {
		throw new ValueExprEvaluationException(
			"invalid argument (literal expected): " + arg);
	    }

	    // get the actual string value that we want to check for palindrome-ness.
	    String label = ((Literal)arg).getLabel();
	    // we invert our string
	    String inverted = "";
	    for (int i = label.length() - 1; i >= 0; i--) {
		inverted += label.charAt(i);
	    }
	    // a string is a palindrome if it is equal to its own inverse
	    boolean palindrome = inverted.equalsIgnoreCase(label);

	    // a function is always expected to return a Value object, so we
	    // return our boolean result as a Literal
	    return valueFactory.createLiteral(palindrome);
	}
}
