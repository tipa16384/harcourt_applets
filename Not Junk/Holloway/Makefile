CLASSFILES= \
			Holloway.class \
			Record.class

JARFILE= index.html
DATAFILES= holloway.db

.SUFFIXES: .java .class

$(JARFILE): $(CLASSFILES) $(DATAFILES)
	java Holloway

.java.class :
	javac $<

#	set CLASSPATH=.

clean:                
    -del *.class

