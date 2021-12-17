package DTree;

class UsageError extends Exception
	{
    /**
     * Constructs an <code>Exception</code> with no specified detail message.
     *
     * @since   JDK1.0
     */
    public UsageError() {
	super();
    }

    /**
     * Constructs an <code>Exception</code> with the specified detail message.
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public UsageError(String s) {
	super(s);
    }
}

