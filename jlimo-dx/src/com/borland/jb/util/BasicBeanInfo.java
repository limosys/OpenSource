//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/BasicBeanInfo.java,v 7.0 2002/08/08 18:40:49 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.jb.util;

//import com.borland.jb.util.Trace;

import java.beans.*;
import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.*;
import java.net.*;

/**
 * A convenient implementation of the java.beans.BeanInfo interface,
 * designed to be subclassed to fill in appropriate properties, methods,
 * and events for a JavaBean. Extend BasicBeanInfo when you want to
 * provide explicit information about your component rather than have
 * JBuilder and other such tools derive the information through introspection.
 */
public abstract class BasicBeanInfo implements BeanInfo
{
  // Basic Info

  /**
   * The JavaBean component class. A subclassed bean information class
   * must specify a component class, which is the only required field.
   */
  protected Class beanClass;

  /**
   * The customizer class for this JavaBean, if one exists.
   */
  protected Class customizerClass;

  // Property Info

  /*
   * !doc Possibly newer. Has significantly different text.
   *
   * The property information for the @link java.beans.BeanInfo. By default will
   * introspect for properties, override to customize.  Null
   * parameters are ignored, with the exception of "GetterMethodName"
   * and "SetterMethodName" for which a null value (not "") indicates
   * the property should be write-only or read-only, respectively.
   * For the "GetterMethodName" and "SetterMethodName", setting BOTH
   * to the empty string ("") indicates that the standard Java
   * property naming rules should be applied to determine the getter
   * and setter names from the property name.  The first four entries
   * are required; use null as a placeholder to ignore properties.
   * If the "SetterMethodParamType" entry is non-null, it will be
   * used to find the actual setter method with that parameter type,
   * rather than letting java.beans.PropertyDescriptor find it.
   * To specify a primitive type as the parameter type, append ".class"
   * to the end of the primitive type name.
   *
   * Format:  {{"PropertyName", "Short Description", "GetterMethodName", "SetterMethodName", "PropertyEditor", "Expert", "Hidden", "SetterMethodParamType"}, ...}
   * Example: {{"text", "Control Text", "getText", "setText", "mypackage.MyPropertyEditor", "true", "false", "java.lang.String"}, ...}
   * Example: {{"eigenvalue", "Complex property", "", "", null, "true", "true", "int.class"}, ...}
   */

  /**
   * The property information for your JavaBean.  Null
   * parameters are ignored, with the exception of <CODE>
   * GetterMethodName</CODE> and <CODE>SetterMethodName</CODE>
   * for which a null value (not "") indicates the property
   * should be write-only or read-only, respectively.
   *
   * <P>Setting both the <CODE>GetterMethodName</CODE> and
   * <CODE>SetterMethodName</CODE> to the empty string ("")
   * indicates that the standard Java property naming rules
   * should be applied to determine the getter and setter names
   * from the property name.
   *
   * <P>The first four entries are required; use <STRONG>
   * null</STRONG> as a placeholder to ignore properties. If the
   * <CODE>SetterMethodParamType</CODE> entry is non-null, it
   * will be used to find the actual setter method with that
   * parameter type, rather than letting <CODE>
   * java.beans.PropertyDescriptor</CODE> find it. To specify a
   * primitive type as the parameter type, append ".class" to
   * the end of the primitive type name.
   *
   * Specify the property information using this format:
   *
   * <pre>{{"PropertyName", "Short Description", "GetterMethodName", SetterMethodName",
   *      "PropertyEditor", "Expert", "Hidden", "SetterMethodParamType"}, ...}</pre>
   *
   * <P><STRONG>Examples: </STRONG>
   * <PRE>{{"eigenvalue", "Complex property", "", "", null,
   * "true", "true", "int.class"}, ...}</PRE>
   * <p>
   *  <pre>{{"text", "Control Text", "getText", "setText",
   *      "mypackage.MyPropertyEditor", "true", "false", "java.lang.String"}, ...} </PRE>
   */
  protected String[][] propertyDescriptors;


  /*
   * !doc possibly newer example
   * Example:
   * <pre>propertyDescritporAttributes = new Object[][] {
   *      {"preferred","true},
   *      {"enumerationValues", new Object[]
   *            {"LEFT", new Integer(2), "SwingConstants.LEFT", "CENTER",
   *             new Integer(0), "SwingConstants.CENTER"}},}</PRE>
   */

  /**
   * Additional attributes for each property described in the <CODE>
   * propertyDescriptors</CODE> array. Entries in the <CODE>
   * propertyDescriptorAttributes</CODE> array and the <CODE>
   * propertyDescriptors</CODE> array are matched by an index
   * position in each array. Use a <STRONG>null</STRONG> value as a
   * placeholder for property entries without attributes. The <CODE>
   * propertyDescriptorAttributes</CODE> array need not be the same
   * size as the <CODE>propertyDescriptors</CODE> array.
   *
   * <P>Specify the attributes using this format:
   * <PRE>{{"AttributeName", "AttributeValueAsString"}, ...} </PRE>
   *
   * <P><STRONG>Example: </STRONG>
   * <pre>{null, {"enumerationValues", "LEFT, 2, SwingConstants.LEFT,
   *         CENTER, 0, SwingConstants.CENTER"},}</pre>
   */
  protected Object[][] propertyDescriptorAttributes;

  /**
   * The index of the default property for your JavaBean. The index
   * identifies the property in the set of property descriptors held
   * in the array of the the <CODE>propertyDescriptors</CODE> property.
   * An index value of -1 means there is no default property.
   * <p>A default property has no meaning to JBuilder.
   */
  protected int defaultPropertyIndex = -1;

  // Method Info

  /*
   * !doc some of this may be newer
   *
   * The method names (non-properties) for your JavaBean. By default
   * will introspect for methods, override to customize.
   * Format: {"method1", "method2", "method3", ...}
   * Example: {"fillRect", "eraseRect", "close", "open"}
   */

  /**
   * The method names for your JavaBean. Don't include the access
   * methods for properties. Specify the method names
   * using this format:
   *
   * <PRE>{"method1", "method2", "method3", ...}</PRE>
   *
   * <P><STRONG>Example: </STRONG>
   * <PRE>{"fillRect", "eraseRect", "close", "open"}</PRE>
   */
    protected String[] methodNames;

  /*
   * !doc some of this may be newer
   * The method parameters for each of your JavaBean's methods. Required if methodNames is filled in.
   *
   * Format:  {{"method1Parameter1", "method1Parameter2", ...}, ...}
   * Example: {{"java.awt.Graphics", "java.awt.Rectangle", ...}, ...}
   */

   /**
    * The method parameters for each of your JavaBean's methods. Specify
    * the parameters using this format:
    *
    * <PRE>{{"method1Parameter1", "method1Parameter2", ...}, ...} </PRE>
    *
    * <P><STRONG>Example: </STRONG>
    * <PRE>{{"java.awt.Graphics", "java.awt.Rectangle", ...}, ...}</PRE>
    */
  protected String[][] methodParameters;

  // Event Info

  /*
   * !doc This material may be newer
   *
   * The event information for the JavaBean. By default will introspect
   * for events, override to customize.
   * The first four entries are required; use null as a placeholder to ignore properties.
   * If "AddMethod" and "RemoveMethod" are empty strings (""), "add" and "remove" will be prepended
   * to the base "EventListenerClass" name to set the names of the add and remove listener methods.
   *
   * Format:  {{"EventSetName", "EventListenerClass", "AddMethod", "RemoveMethod", "Expert", "Hidden",}, ...}
   * Example: {{"ActionListener", "java.awt.event.ActionListener", "addActionListener", "removeActionListener"}, ...}
   */

   /**
    * The event information for your JavaBean. Specify the event information using this format:
    *
    * <PRE>
    * {{"EventSetName", "EventListenerClass", "AddMethod",
    *     "RemoveMethod"}, ...}
    * </PRE>
    *
    * <P><STRONG>Example: </STRONG></P>
    * <pre>{{"ActionListener", "java.awt.event.ActionListener",
    *         "addActionListener", "removeActionListener"}, ...}
    * </pre>
    *
    */
  protected String[][] eventSetDescriptors;

  /**
   * The names of each event set's listener methods. Specify
   * the names using this format:
   * <br>
   * <pre>{{"listener1Method1", "listener1Method2",
   *      "listener1Method3", ...}, ...}
   * </pre>
   * <strong>Example:</strong>  <code>{{"actionPerformed"}, ...}</code>
   */
  protected String[][] eventListenerMethods;

  /**
   * The index of the default event for your JavaBean. The index
   * identifies the event in the set of event descriptors described
   * held in the array of the the eventSetDescriptors property.
   * An index value of -1 means there is no default event.
   */
  protected int defaultEventIndex = -1;

  // Icon Info

  /**
   * A 16x16 pixel color icon for your JavaBean.  If none is
   * specified, one will be searched
   * for using a default resource name.
   */
  protected Image iconColor16x16;

  /**
   * A 32x32 pixel color icon for your JavaBean.  If none is
   * specified, one will be searched
   * for using a default resource name.
   */
  protected Image iconColor32x32;

  /**
   * A 16x16 pixel monochromatic icon for your JavaBean.  If
   * none is specified, one will be
   * searched for using a default resource name.
   */
  protected Image iconMono16x16;

  /**
   * A 32x32 pixel monochromatic icon for your JavaBean.
   * If none is specified, one will be
   * searched for using a default resource name.
   */
  protected Image iconMono32x32;

  // Additional Info

  /**
   * An array of other bean information objects.
   */
  protected BeanInfo[] additionalBeanInfo;

  // Named attributes


   /** Any additional named attributes for the JavaBean. Specify
    * the attributes using this format:
    *
    * <PRE>{{"AttributeName", AttributeSetting}, ...}</PRE>
    *
    * <P><STRONG>Example: </STRONG>
    * <PRE>{{"isContainer", Boolean.TRUE}, {"containerDelegate",
    * "getContentPane"}, ...}</PRE>
    */
  protected Object[][] namedAttributes;


  // BeanInfo Implementation
  /**
   * Returns the bean descriptor associated with this JavaBean.
   */
  public BeanDescriptor getBeanDescriptor() {
    BeanDescriptor bd = new BeanDescriptor(beanClass, customizerClass);
    if (namedAttributes != null && namedAttributes.length > 0) {
      for (int i = 0; i < namedAttributes.length; i++) {
        if (namedAttributes[i].length >= 2 && namedAttributes[i][0] != null)
          bd.setValue(namedAttributes[i][0].toString(), namedAttributes[i][1]);
      }
    }
    return bd;
  }

  /**
   * Returns the array of bean information objects for this JavaBean.
   */
  public BeanInfo[] getAdditionalBeanInfo() {
    return additionalBeanInfo;
  }

  /**
   * Returns the array of property descriptors for this JavaBean.
   */
  public PropertyDescriptor[] getPropertyDescriptors() {
    if (propertyDescriptors == null)
      return null;
    try {
      PropertyDescriptor[] pds = new PropertyDescriptor[propertyDescriptors.length];
      ClassLoader cl = getClass().getClassLoader();
      Class editorClass = null;
      for (int i = 0; i < pds.length; i++) {
//!      System.err.println("BasicBeanInfo: Creating property descriptor for " + beanClass.getName() + "." + propertyDescriptors[i][0] + " property");
        if (propertyDescriptors[i].length > 7 && propertyDescriptors[i][7] != null) {
          // a setter method param was provided, use it to find the actual setter method, rather
          // than letting the PropertyDescriptor choose the wrong one.
          String normalizedName = normalizeName(propertyDescriptors[i][0]);
          Method setterMethod = null;
          Method getterMethod = null;
          try {
            Class paramClass = null;
            String className = propertyDescriptors[i][7];
            if (className.indexOf(".class") != -1) {
              //! not totally safe, but should be okay
              if (className.indexOf("int") == 0) {
                paramClass = int.class;
              }
              else if (className.indexOf("short") == 0) {
                paramClass = short.class;
              }
              else if (className.indexOf("long") == 0) {
                paramClass = long.class;
              }
              else if (className.indexOf("byte") == 0) {
                paramClass = byte.class;
              }
              else if (className.indexOf("float") == 0) {
                paramClass = float.class;
              }
              else if (className.indexOf("double") == 0) {
                paramClass = double.class;
              }
              else if (className.indexOf("char") == 0) {
                paramClass = char.class;
              }
              else if (className.indexOf("boolean") == 0) {
                paramClass = boolean.class;
              }
              else {
                paramClass = Class.forName(className);
              }
            }
            else if (className.indexOf("string[]") == 0) {
              String [] stringArray = new String[0];
              paramClass = stringArray.getClass();
            }
            else {
              paramClass = Class.forName(className);
            }
            setterMethod = beanClass.getMethod("set" + normalizedName, new Class [] { paramClass });
          }
          catch (Exception e) {
            // couldn't find the method, ignore the property
            System.err.println("BasicBeanInfo: unable to find setter for " + beanClass.getName() + " " + propertyDescriptors[i][0] + " property");
            continue;
          }
          if (setterMethod.getParameterTypes()[0] == Boolean.TYPE) {
            try {
              getterMethod = beanClass.getMethod("is" + normalizedName, null);
            }
            catch (Exception e) {
              // don't give up yet, getter may be prefixed by 'get' instead of 'is'
            }
          }
          if (getterMethod == null) {
            try {
              getterMethod = beanClass.getMethod("get" + normalizedName, null);
            }
            catch (Exception e) {
              // now give up and ignore the property, since we couldn't find a getter
              System.err.println("BasicBeanInfo: unable to find getter for " + beanClass.getName() + " " + propertyDescriptors[i][0] + " property");
              continue;
            }
          }
          pds[i] = new PropertyDescriptor(propertyDescriptors[i][0],
                                          getterMethod,
                                          setterMethod);
        }
        else if (propertyDescriptors[i][2] != null && propertyDescriptors[i][2].length() == 0 &&
                 propertyDescriptors[i][3] != null && propertyDescriptors[i][3].length() == 0) {
          pds[i] = new PropertyDescriptor(propertyDescriptors[i][0], beanClass);
        }
        else {
          pds[i] = new PropertyDescriptor(propertyDescriptors[i][0],
                                          beanClass,
                                          propertyDescriptors[i][2],
                                          propertyDescriptors[i][3]);
        }
        if (propertyDescriptors[i][1] != null)
          pds[i].setShortDescription(propertyDescriptors[i][1]);
        if (propertyDescriptors[i].length > 4 && propertyDescriptors[i][4] != null) {
          try {
            editorClass = Class.forName(propertyDescriptors[i][4]);
          }
          catch (ClassNotFoundException e) {
            editorClass = cl.loadClass(propertyDescriptors[i][4]);
          }
//!       System.err.println("BasicBeanInfo: setting " + beanClass.getName() + "." + propertyDescriptors[i][0] + " property's editor: " + editorClass);
          pds[i].setPropertyEditorClass(editorClass);
        }
        if (propertyDescriptors[i].length > 5 && propertyDescriptors[i][5] != null) {
//!       System.err.println("BasicBeanInfo: setting " + beanClass.getName() + "." + propertyDescriptors[i][0] + " as an EXPERT property");
          pds[i].setExpert(propertyDescriptors[i][5].toLowerCase().equals("true"));
        }
        if (propertyDescriptors[i].length > 6 && propertyDescriptors[i][6] != null) {
//!       System.err.println("BasicBeanInfo: setting " + beanClass.getName() + "." + propertyDescriptors[i][0] + " as a HIDDEN property");
          pds[i].setHidden(propertyDescriptors[i][6].toLowerCase().equals("true"));
        }
        if (propertyDescriptorAttributes != null &&
             i < propertyDescriptorAttributes.length && propertyDescriptorAttributes[i] != null) {
//!       System.err.println("BasicBeanInfo: setting " + beanClass.getName() + "." + propertyDescriptors[i][0] + " property attributes: " + propertyDescriptorAttributes[i][1]);

          pds[i].setValue(propertyDescriptorAttributes[i][0].toString(), propertyDescriptorAttributes[i][1]);
        }
      }
      return pds;
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    catch (IntrospectionException e) {
      e.printStackTrace();
    }
    return null;
  }

  static String normalizeName(String s) {
    if (s.length() == 0) {
      return s;
    }
    char chars[] = s.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  /**
   * Returns the default property index for this JavaBean. The
   * index returned locates the property in the <code>propertyDescriptors</code>
   * array. An index value of -1 means there is no default
   * property for this bean.
   */
  public int getDefaultPropertyIndex() {
    return defaultPropertyIndex;
  }

  /**
   * Returns the array of method descriptors for this JavaBean.
   */
  public MethodDescriptor[] getMethodDescriptors() {
    if (methodNames == null)
      return null;
    try {
      MethodDescriptor[] mds = new MethodDescriptor[methodNames.length];
      ClassLoader cl = getClass().getClassLoader();
      for (int i = 0; i < mds.length; i++) {
        Class[] params = new Class[methodParameters[i].length];
        for (int j = 0; j < params.length; j++) {
          try {
            params[j] = Class.forName(methodParameters[i][j]);
          }
          catch (ClassNotFoundException e) {
            params[j] =  cl.loadClass(methodParameters[i][j]);
          }
        }
        mds[i] = new MethodDescriptor(beanClass.getMethod(methodNames[i], params));
      }
      return mds;
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Returns the array of event set descriptors for this JavaBean.
   */
  public EventSetDescriptor[] getEventSetDescriptors() {
    if (eventSetDescriptors == null)
      return null;
    try {
      EventSetDescriptor[] esds = new EventSetDescriptor[eventSetDescriptors.length];
      Class eventSetClass = null;
      ClassLoader cl = getClass().getClassLoader();
      for (int i = 0; i < esds.length; i++)  {
//!     System.err.println("BasicBeanInfo: Creating event set descriptor for " + beanClass.getName() + "." + eventSetDescriptors[i][0] + " event");
        try {
          eventSetClass = Class.forName(eventSetDescriptors[i][1]);
        }
        catch (ClassNotFoundException e) {
          eventSetClass =  cl.loadClass(eventSetDescriptors[i][1]);
        }
        if (eventSetDescriptors[i][2] != null && eventSetDescriptors[i][2].length() == 0 &&
            eventSetDescriptors[i][3] != null && eventSetDescriptors[i][3].length() == 0) {
          String baseName = eventSetDescriptors[i][1].substring(eventSetDescriptors[i][1].lastIndexOf('.') + 1);
          esds[i] = new EventSetDescriptor(beanClass,
                                           eventSetDescriptors[i][0],
                                           eventSetClass,
                                           eventListenerMethods[i],
                                           "add" + baseName,
                                           "remove" + baseName);
        }
        else {
          esds[i] = new EventSetDescriptor(beanClass,
                                           eventSetDescriptors[i][0],
                                           eventSetClass,
                                           eventListenerMethods[i],
                                           eventSetDescriptors[i][2],
                                           eventSetDescriptors[i][3]);
        }
      }
      return esds;
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    catch (IntrospectionException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Returns the default event index for this JavaBean. The
   * index returned locates the event in the <code>eventSetDescriptors</code> array.
   * An index value of -1 means there is no default event for this bean.
   */
  public int getDefaultEventIndex() {
    return defaultEventIndex;
  }

  /**
   * Returns the default resource to use to find an icon
   * for a JavaBean.<P>
   *
   * If an icon is requested, and not explicitly set in the
   * subclass of <CODE>BasicBeanInfo</CODE>, an icon is searched
   * for using a simple look-up using the <CODE>beanClass's</CODE>
   * name and the requested icon type.
   * <P>Following is a simple chart describing the default resource
   * locations using an example of the JavaBean class
   * <CODE>package1.package2.MyBean</CODE>:<P>
   * <DL>
   * <DT>ICON_COLOR_16x16              <DD>image/MyBean_Color16.gif
   * <DT>ICON_COLOR_32x32              <DD>image/MyBean_Color32.gif
   * <DT>ICON_MONO_16x16               <DD>image/MyBean_Mono16.gif
   * <DT>ICON_MONO_32x32               <DD>image/MyBean_Mono32.gif
   * </DL>
   *
   * @param iconKind the <b>int</b> representing the icon type from <CODE>java.beans.BeanInfo</CODE>
   * @return A string representing the appropriate resource name.
   */
  protected String getDefaultIconResource(int iconKind) {
    // convert "package1.package2.MyBean" to "image/MyBean"
    String resource = "image/" + beanClass.getName().substring(beanClass.getName().lastIndexOf('.') + 1); // NORES
    switch (iconKind) {
      case ICON_COLOR_16x16: resource += "_Color16"; break; // NORES
      case ICON_COLOR_32x32: resource += "_Color32"; break; // NORES
      case ICON_MONO_16x16:  resource += "_Mono16"; break; // NORES
      case ICON_MONO_32x32:  resource += "_Mono32"; break; // NORES
    }
    return resource + ".gif";
  }

  public Image getIcon(int iconKind) {
    Image icon = null;
    switch (iconKind) {
      case ICON_COLOR_16x16:
        icon = iconColor16x16;
        break;
      case ICON_COLOR_32x32:
        icon = iconColor32x32;
        break;
      case ICON_MONO_16x16:
        icon = iconMono16x16;
        break;
      case ICON_MONO_32x32:
        icon = iconMono32x32;
        break;
    }

    if (icon == null && beanClass != null) {
      String resource = getDefaultIconResource(iconKind);
      icon = getImage(resource);
      // save the icon
      if (icon != null) switch (iconKind) {
        case ICON_COLOR_16x16:
          iconColor16x16 = icon;
          break;
        case ICON_COLOR_32x32:
          iconColor32x32 = icon;
          break;
        case ICON_MONO_16x16:
          iconMono16x16 = icon;
          break;
        case ICON_MONO_32x32:
          iconMono32x32 = icon;
          break;
      }
    }
    return icon;
  }

  /**
   * This is a simple utility function that retrieves an <code>Image</code>
   * object from a resource.  The resource must by specified as
   * a relative path to the <code>beanClass</code> resource.
   *
   * @param resource The resource name relative to the <code>beanClass</code> resource
   * @return A <code>java.awt.Image</code> object if the resource can be
   * resolved into one; otherwise, null.
   */
  public Image getImage(String resource) {
    try {
      if (beanClass != null) {
        URL url = beanClass.getResource(resource);
        if (url != null) {
          ImageProducer producer = (ImageProducer)url.getContent();
          Toolkit       toolkit  = Toolkit.getDefaultToolkit();
          return toolkit.createImage(producer);
        }
      }
    }
    catch (Exception x) {
      return null;
    }
    return null;
  }

  //known property descriptor attributes
  /**
   * A property descriptor key value that operates as
   * hint to the designer that this property setting
   * should come near the end (after the add calls)
   * Value should be set to <strong>true</strong>.
   * <p>When not present, assumed to be <strong>false</strong>.
   */
  static public final String LATE_SETTING = "lateSetting";
  /**
   * A property descriptor key value that will cause a tag list property
   * editor to be associated with this property.
   * <p>The value for this key should be an Object array with three
   * values for each entry desired in the tag list.
   * The first value in each set is the text that will
   * appear in the tag list, the next value is the live value and the
   * third value is  the java Initialization String.</p>
   */
  public static final String ENUMERATION = "enumerationValues" ;
  /**
   * A Bean Descriptor key value used to inform a designer that
   * although the bean extends <code>java.awt.Container</code>,
   * it should not be treated as one. The value should be
   * <strong>false</strong>.
   * <p>When not present, beans that extend
   * <code>java.awt.Container</code> are treated as a containers.
  */
  public static final String IS_CONTAINER = "isContainer";
  /**
   * A Bean descriptor key value used to inform a designer that  the add calls and
   * layout setting should not be applied directly to the component, but
   * should call this method first. The method is assumed to take no parameters
   * <P>The method is assumed to take no parameters.  <CODE>javax.swing.JFrame</CODE>
   * contains the following example:
   * <PRE> setValue("containerDelegate","getContentPane");</PRE>
  */
  public static final String CONTAINER_DELEGATE = "containerDelegate";

  public final void appendPropertyDescriptors(String[][] descriptors) {
    String[][] newDescriptors = new String[propertyDescriptors.length+descriptors.length][];
    System.arraycopy(propertyDescriptors, 0, newDescriptors, 0, propertyDescriptors.length);
    System.arraycopy(descriptors, 0, newDescriptors, propertyDescriptors.length, descriptors.length);
    propertyDescriptors = newDescriptors;
  }
}

