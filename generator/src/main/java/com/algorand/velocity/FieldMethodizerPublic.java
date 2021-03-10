package com.algorand.velocity;

import org.apache.velocity.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 *  <p>
 *  This is a small utility class allow easy access to public fields in a class,
 *  such as string constants.  Velocity will not introspect for class
 *  fields (and won't in the future :), but writing setter/getter methods to do
 *  this really is a pain,  so use this if you really have
 *  to access fields.
 *
 *  <p>
 *  The idea it so enable access to the fields just like you would in Java.
 *  For example, in Java, you would access a public field like
 *  <blockquote><pre>
 *  MyClass.STRING_CONSTANT
 *  </pre></blockquote>
 *  and that is the same thing we are trying to allow here.
 *
 *  <p>
 *  So to use in your Java code, do something like this :
 *  <blockquote><pre>
 *   context.put("runtime", new FieldMethodizer( "org.apache.velocity.runtime.Runtime" ));
 *  </pre></blockquote>
 *  and then in your template, you can access any of your public fields in this way :
 *  <blockquote><pre>
 *   $runtime.COUNTER_NAME
 *  </pre></blockquote>
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: FieldMethodizer.java 652755 2008-05-02 04:00:58Z nbubna $
 */
public class FieldMethodizerPublic
{
    /** Hold the field objects by field name */
    private HashMap fieldHash = new HashMap();

    /**
     * Allow object to be initialized without any data. You would use
     * addObject() to add data later.
     */
    public FieldMethodizerPublic()
    {
    }

    /**
     *  Constructor that takes as it's arg the name of the class
     *  to methodize.
     *
     *  @param s Name of class to methodize.
     */
    public FieldMethodizerPublic( String s )
    {
        try
        {
            addObject(s);
        }
        catch( Exception e )
        {
            System.err.println("Could not add " + s
                    + " for field methodizing: "
                    + e.getMessage());
        }
    }

    /**
     *  Constructor that takes as it's arg a living
     *  object to methodize.  Note that it will still
     *  only methodized the public fields of
     *  the class.
     *
     *  @param o Name of class to methodize.
     */
    public FieldMethodizerPublic( Object o )
    {
        try
        {
            addObject(o);
        }
        catch( Exception e )
        {
            System.err.println("Could not add " + o
                    + " for field methodizing: "
                    + e.getMessage());
        }
    }

    /**
     * Add the Name of the class to methodize
     * @param s
     * @throws Exception
     */
    public void addObject ( String s )
            throws Exception
    {
        inspect(ClassUtils.getClass(s));
    }

    /**
     * Add an Object to methodize
     * @param o
     * @throws Exception
     */
    public void addObject ( Object o )
            throws Exception
    {
        inspect(o.getClass());
    }

    /**
     *  Accessor method to get the fields by name.
     *
     *  @param fieldName Name of field to retrieve
     *
     *  @return The value of the given field.
     */
    public Object get( String fieldName )
    {
        Object value = null;
        try
        {
            Field f = (Field) fieldHash.get( fieldName );
            if (f != null)
            {
                value = f.get(null);
            }
        }
        catch( IllegalAccessException e )
        {
            System.err.println("IllegalAccessException while trying to access " + fieldName
                    + ": " + e.getMessage());
        }
        return value;
    }

    /**
     *  Method that retrieves all public fields
     *  in the class we are methodizing.
     */
    private void inspect(Class clas)
    {
        Field[] fields = clas.getFields();
        for( int i = 0; i < fields.length; i++)
        {
            /*
             *  only if public
             */
            int mod = fields[i].getModifiers();
            //if ( Modifier.isStatic(mod) && Modifier.isPublic(mod) )
            if ( Modifier.isPublic(mod) )
            {
                fieldHash.put(fields[i].getName(), fields[i]);
            }
        }
    }
}
