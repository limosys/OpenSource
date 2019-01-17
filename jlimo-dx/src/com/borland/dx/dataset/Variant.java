//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/Variant.java,v 7.6 2003/08/08 20:27:27 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.math.*;
import com.borland.dx.dataset.cons.TimeConst;

/**
 * The Variant class is a type of storage class whose value can be one of many data types. It can hold data of these types:
 * <p>
 * <ul>
 * <li>byte</li>
 * <li>short</li>
 * <li>int</li>
 * <li>long</li>
 * <li>float</li>
 * <li>double</li>
 * <li>BigDecimal</li>
 * <li>boolean</li>
 * <li>input</li>
 * <li>stream</li>
 * <li>Date</li>
 * <li>Time</li>
 * <li>Timestamp</li>
 * <li>String</li>
 * <li>Object</li>
 * <li>byte</li>
 * <li>array</li>
 * </ul>
 * <p>
 * Variant contains constants used to identify all of these data types. It also contains the methods to get and set data values and to perform operations on
 * Variant data, such as addition, subtraction, and comparing one value to another. The dataset package uses the Variant data type frequently because it can
 * handle all types of data.
 *
 */
public class Variant implements Cloneable, Serializable {
	private static final long serialVersionUID = 200L;

	// UNASSIGNED_NULL - ASSIGNED_NULL expected to belong to a contiguous range
	//

	/**
	 * An integer constant used to identify an unassigned null value. An unassigned null value is a data value that was never assigned. This is in contrast to an
	 * assigned null value that is explicitly assigned.
	 * 
	 * @see #ASSIGNED_NULL
	 */
	public static final int UNASSIGNED_NULL = 0;

	/**
	 * Constant that identifies a data type for values that are explicitly set to null. This is in contrast to data that is never assigned.
	 * 
	 * @see #UNASSIGNED_NULL
	 */
	public static final int ASSIGNED_NULL = 1;

	/**
	 * An integer constant used to identify null data. Null data can be either assigned or unassigned.
	 *
	 * @see #ASSIGNED_NULL
	 * @see #UNASSIGNED_NULL
	 */
	public static final int NULL_TYPES = 1;

	// BYTE - LONG expected to belong to a contiguous range
	//
	/**
	 * An integer constant used to identify data of type <b>byte</b>.
	 */
	public static final int BYTE = 2;

	/**
	 * An integer constant used to identify the <b>short</b> data type.
	 */
	public static final int SHORT = 3;

	/**
	 * An integer constant used to identify the <b>int</b> data type.
	 */
	public static final int INT = 4;

	/**
	 * An integer constant used to identify the <b>long</b> data type.
	 */
	public static final int LONG = 5;

	// FLOAT - DOUBLE expected to belong to a contiguous range
	//
	/**
	 * An integer constant used to identify the <b>float</b> data type.
	 */
	public static final int FLOAT = 6;

	/**
	 * An integer constant used to identify the <b>double</b> data type.
	 */
	public static final int DOUBLE = 7;

	/**
	 * An integer constant used to identify the BigDecimal data type. BigDecimal values have an unlimited precision integer value and an integer scale factor.
	 */
	public static final int BIGDECIMAL = 10;

	/**
	 * An integer constant used to identify data of type <b>boolean</b>.
	 */
	public static final int BOOLEAN = 11;

	/**
	 * @deprecated use INPUTSTREAM.
	 */
	public static final int BINARY_STREAM = 12;

	/**
	 * An integer constant used to identify data of a input stream.
	 */
	public static final int INPUTSTREAM = 12;

	/**
	 * An integer constant used to identify the Date data type.
	 */
	public static final int DATE = 13;

	/**
	 * An integer constant used to identify the Time data type.
	 */
	public static final int TIME = 14;

	/**
	 * An integer constant used to identify the TimeStamp data type.
	 */
	public static final int TIMESTAMP = 15;

	/**
	 * An integer constant used to identify the String data type.
	 */
	public static final int STRING = 16;

	/**
	 * An integer constant used to identify the Object data type.
	 */
	public static final int OBJECT = 17;

	/**
	 * An integer constant used to identify data in a <b>byte</b> array.
	 */
	public static final int BYTE_ARRAY = 18;

	/**
	 * Type names
	 */

	/**
	 * A constant that displays an assigned <b>null</b> value as the string "ASSIGNED_NULL". An assigned null is a value explicitly set to <b>null</b> in contrast
	 * to one that is simply not assigned.
	 */
	public static final String AssignedNull_S = "ASSIGNED_NULL"; // NORES

	/**
	 * A constant that represents an unassigned null as the string "UNASSIGNED_NULL".
	 */
	public static final String UnassignedNull_S = "UNASSIGNED_NULL"; // NORES

	/**
	 * A constant that represents the <b>byte</b> data type as the string "BYTE".
	 */
	public static final String ByteType_S = "BYTE"; // NORES

	/**
	 * A constant that represents the <b>short</b> data type as the string "SHORT".
	 */
	public static final String ShortType_S = "SHORT"; // NORES

	/**
	 * A constant that represents the <b>int</b> data type as the string "INT".
	 */
	public static final String IntType_S = "INT"; // NORES

	/**
	 * A constant that represents the <b>long</b> data type as the string "LONG".
	 */
	public static final String LongType_S = "LONG"; // NORES

	/**
	 * A constant that represents the <b>float</b> data type as the string "FLOAT".
	 */
	public static final String FloatType_S = "FLOAT"; // NORES

	/**
	 * A constant that represents the <b>double</b> date type as the string "DOUBLE".
	 */
	public static final String DoubleType_S = "DOUBLE"; // NORES

	/**
	 * A constant that represents the BigDecimal data type as the string "BIGDECIMAL".
	 */
	public static final String BigDecimalType_S = "BIGDECIMAL"; // NORES

	/**
	 * A constant that represents the <b>boolean</b> data type as the string "BOOLEAN".
	 */
	public static final String BooleanType_S = "BOOLEAN"; // NORES

	/**
	 * A constant that represents the INPUTSTREAM data type as the string "INPUTSTREAM".
	 */
	public static final String InputStreamType_S = "INPUTSTREAM"; // NORES

	/**
	 * @deprecated Use {@link #InputStreamType_S} instead.
	 */
	public static final String BinaryStreamType_S = "BINARY_STREAM"; // NORES

	/**
	 * A constant that represents the Date data type as the string "DATE".
	 */
	public static final String DateType_S = "DATE"; // NORES

	/**
	 * A constant that represents the Time data type as the string "TIME".
	 */
	public static final String TimeType_S = "TIME"; // NORES

	/**
	 * A constant that represents the TimeStamp data type as the string "TIMESTAMP".
	 */
	public static final String TimestampType_S = "TIMESTAMP"; // NORES

	/**
	 * A constant that displays a <b>byte</b> array as the string "BYTE_ARRAY".
	 */
	public static final String ByteArrayType_S = "BYTE_ARRAY"; // NORES

	/**
	 * A constant that represents the String data type as the string "STRING".
	 */
	public static final String StringType_S = "STRING"; // NORES

	/**
	 * A constant that represents the Object data type as the string "OBJECT".
	 */
	public static final String ObjectType_S = "OBJECT"; // NORES

	/**
	 * A constant that represents an unknown data type as the string "UNKNOWN".
	 */
	public static final String UnknownType_S = "UNKNOWN"; // NORES

	/**
	 * An integer constant used to identify a Variant data type with an unassigned null data value.
	 */
	public static final Variant nullVariant = new Variant(UNASSIGNED_NULL);

	private static final int SET_AS_INPUTSTREAM = -2;
	private static final int SET_AS_OBJECT = -3;
	private static final int SET_AS_LONG = -4;
	private static final int SET_AS_DATE = -5;
	private static final int SET_AS_TIME = -6;
	private static final int SET_AS_BOTH = -7;

	// ! NOTE! NOTE! NOTE!
	// ! If you add anything here or change the order, be sure to update the String[]
	// ! for the DataTypeEditor in jbcl\editors! It must be perfectly parallel
	// ! to the order and size of these types.

	/**
	 * The maximum number of data types Variant can handle.
	 */
	public static final int MaxTypes = 18;
	// !RC TODO <ron> Made this public to know range of legal data types

	/**
	 * Constructs a Variant object that can contain data of the type specified with the dataType parameter. Variants instantiated with this constructor must have
	 * the results of all get or set operations be of the type specified.
	 *
	 * @param dataType
	 *          If this constructor is used, all set operations must be of the data type from which the Variant was constructed.
	 */
	public Variant(int dataType) {
		setType = dataType;
	}

	/**
	 * Constructs a Variant object without specifying the explicit data type.
	 */
	public Variant() {}

	/**
	 * Returns the name of a data type as a string. For example, the string representation of a BOOLEAN data type is "BOOLEAN".
	 *
	 * @param type
	 *          The data type. Specify the type using one of the data type constants of Variant. For example, BOOLEAN is the name of the constant for a boolean
	 *          data type.
	 * @return The name of a data type as a string.
	 */
	public static String typeName(int type) {
		switch (type) {
			case ASSIGNED_NULL:
				return AssignedNull_S;
			case UNASSIGNED_NULL:
				return UnassignedNull_S;

			case BYTE:
				return ByteType_S;
			case SHORT:
				return ShortType_S;
			case INT:
				return IntType_S;
			case LONG:
				return LongType_S;

			case FLOAT:
				return FloatType_S;
			case DOUBLE:
				return DoubleType_S;

			case BIGDECIMAL:
				return BigDecimalType_S;

			case BOOLEAN:
				return BooleanType_S;

			case INPUTSTREAM:
				return InputStreamType_S;

			case DATE:
				return DateType_S;
			case TIME:
				return TimeType_S;
			case TIMESTAMP:
				return TimestampType_S;

			case STRING:
				return StringType_S;
			case BYTE_ARRAY:
				return ByteArrayType_S;
			case OBJECT:
				return ObjectType_S;
			default:
				return UnknownType_S;
		}
	}

	/**
	 * Returns an integer that identifies the data type specified in the typeName parameter.
	 *
	 * @param typeName
	 *          The name of a data type as a string. For example, the string "BOOLEAN" results in an integer value of 11, which is the value of the BOOLEAN
	 *          constant.
	 * @return An integer that identifies the data type specified in the typeName parameter.
	 */
	public static int typeOf(String typeName) {
		// Put more common ones first.
		//
		if (typeName.equals(StringType_S)) return STRING;
		if (typeName.equals(DateType_S)) return DATE;
		if (typeName.equals(TimeType_S)) return TIME;
		if (typeName.equals(TimestampType_S)) return TIMESTAMP;
		if (typeName.equals(IntType_S)) return INT;
		if (typeName.equals(BigDecimalType_S)) return BIGDECIMAL;

		if (typeName.equals(AssignedNull_S)) return ASSIGNED_NULL;
		if (typeName.equals(UnassignedNull_S)) return UNASSIGNED_NULL;

		if (typeName.equals(ByteType_S)) return BYTE;
		if (typeName.equals(ShortType_S)) return SHORT;
		if (typeName.equals(LongType_S)) return LONG;

		if (typeName.equals(DoubleType_S)) return DOUBLE;
		if (typeName.equals(FloatType_S)) return FLOAT;

		if (typeName.equals(BooleanType_S)) return BOOLEAN;

		if (typeName.equals(BinaryStreamType_S)) return INPUTSTREAM;
		if (typeName.equals(InputStreamType_S)) return INPUTSTREAM;
		if (typeName.equals(ByteArrayType_S)) return BYTE_ARRAY;

		if (typeName.equals(ObjectType_S)) return OBJECT;

		VariantException.fire(Res.bundle.format(ResIndex.InvalidVariantName, new String[] { typeName }));
		return 0;
	}

	/**
	 * Returns the integer value that represents the specified type name. For example, a name value of "" returns an integer of 11.
	 *
	 * @param name
	 *          The name of a data type. Specify the name using one of the data type constants of Variant. For example, BOOLEAN is the name of the BOOLEAN
	 *          constant for a boolean data type.
	 * @return The integer value that represents the specified type name. For example, a name value of "" returns an integer of 11.
	 */
	public static int typeId(String name) {
		for (int i = NULL_TYPES + 1; i <= MaxTypes; i++)
			if (name.equals(typeName(i)))
				return i;
		return UNASSIGNED_NULL;
	}

	/**
	 * Returns the time zone offset, in milliseconds, of the current time zone. Used internally.
	 *
	 * @return The time zone offset, in milliseconds, of the current time zone.
	 */
	public static long getTimeZoneOffset() {
		if (!offsetsKnown) {
			java.sql.Date date = new java.sql.Date(70, 0, 1);
			timeZoneOffset = date.getTime();
			offsetsKnown = true;
		}
		return timeZoneOffset;
	}

	// setType is used to enforce type safe set operations.
	// setType never changes. If setType is set to non zero, type can only be changed
	// to the same type as setType or to one of the null states.
	//
	private int setType;
	private int type;

	private boolean booleanVal;
	private int intVal;
	private long longVal;
	private float floatVal;
	private double doubleVal;

	private String stringVal;
	private byte[] byteArrayVal;
	private BigDecimal bigDecimalVal;
	private java.sql.Date dateVal;
	private Time timeVal;
	private Timestamp timestampVal;

	private transient Object objectVal;

	private static String zeroString;
	private static BigDecimal zeroBIGDECIMAL;
	private static ByteArrayInputStream zeroBinary;
	private static byte[] zeroByteArray;
	private static boolean offsetsKnown;
	private static long timeZoneOffset;
	// ! private static long milliSecsPerDay = 24*60*60*1000;

	// !TODO. Get this functionality out of Variant!
	// !
	public final Object getDisplayValue() {
		switch (type) {
			case ASSIGNED_NULL:
			case UNASSIGNED_NULL:
				return "";
			case OBJECT:
				return getObject();
			case INPUTSTREAM:
				return getInputStream();
			default:
				break; // to make compiler happy
		}
		return toString();
	}

	private boolean setZeroValue(int unexpectedType, int expectedType) {
		if (zeroString == null) {
			zeroString = "";
			zeroBIGDECIMAL = new BigDecimal(0);
			zeroByteArray = new byte[0];
			zeroBinary = new ByteArrayInputStream(zeroByteArray);
		}

		switch (expectedType) {
			case INT:
			case SHORT:
			case BYTE:
				intVal = 0;
				break;
			case TIMESTAMP:
				setTimestamp(0, 0);
				break;
			case TIME:
				setTime(0L);
				break;
			case DATE:
				setDate(0L);
				break;
			case LONG:
				longVal = 0;
				break;
			case BOOLEAN:
				booleanVal = false;
				break;
			case FLOAT:
				floatVal = 0;
				break;
			case DOUBLE:
				doubleVal = 0;
				break;
			case STRING:
				stringVal = zeroString;
				break;
			case BIGDECIMAL:
				bigDecimalVal = zeroBIGDECIMAL;
				booleanVal = false;
				break;
			case OBJECT:
			case INPUTSTREAM:
				intVal = 0;
				objectVal = zeroBinary;
				break;
			case BYTE_ARRAY:
				byteArrayVal = zeroByteArray;
				break;
			default:
				return false;
		}
		DiagnosticJLimo.check(unexpectedType <= NULL_TYPES);
		// Preserve null type.
		//
		type = unexpectedType;
		return true;
	}

	private void typeProblem(int unexpectedType, int expectedType, boolean getter) {
		if (unexpectedType <= NULL_TYPES && setZeroValue(unexpectedType, expectedType))
			return;
		int str = getter ? ResIndex.UnexpectedTypeGet : ResIndex.UnexpectedType;

		VariantException.fire(Res.bundle.format(str,
																						new String[] { typeName(unexpectedType),
																								typeName(expectedType) }));
	}

	public final int getInt() {
		if (type != INT)
			typeProblem(type, INT, true);
		return intVal;
	}

	public final short getShort() {
		if (type != BYTE && type != SHORT)
			typeProblem(type, SHORT, true);
		return (short) intVal;
	}

	public final byte getByte() {
		if (type != BYTE)
			typeProblem(type, BYTE, true);
		return (byte) intVal;
	}

	public final long getLong() {
		if (type != LONG)
			typeProblem(type, LONG, true);
		return longVal;
	}

	public final boolean getBoolean() {
		if (type != BOOLEAN)
			typeProblem(type, BOOLEAN, true);
		return booleanVal;
	}

	public final double getDouble() {
		if (type != DOUBLE)
			typeProblem(type, DOUBLE, true);
		return doubleVal;
	}

	public final float getFloat() {
		if (type != FLOAT)
			typeProblem(type, FLOAT, true);
		return floatVal;
	}

	public final String getString() {
		if (type != STRING)
			typeProblem(type, STRING, true);
		return stringVal;
	}

	public final BigDecimal getBigDecimal() {
		if (type != BIGDECIMAL)
			typeProblem(type, BIGDECIMAL, true);
		if (booleanVal) {
			bigDecimalVal = new BigDecimal(stringVal);
			booleanVal = false;
		}
		return bigDecimalVal;
	}

	public final java.sql.Date getDate() {
		if (type != DATE)
			typeProblem(type, DATE, true);
		if (intVal == SET_AS_LONG) {
			LocalDateUtil.setAsLocalDate(this, longVal, null);
			intVal = SET_AS_BOTH;
		}
		return dateVal;
	}

	public final Time getTime() {
		if (type != TIME)
			typeProblem(type, TIME, true);
		if (intVal == SET_AS_LONG) {
			LocalDateUtil.setAsLocalTime(this, longVal, null);
			intVal = SET_AS_BOTH;
		}
		return timeVal;
	}

	public final Timestamp getTimestamp() {
		if (type != TIMESTAMP)
			typeProblem(type, TIMESTAMP, true);
		return timestampVal;
	}

	public final byte[] getByteArray() {
		if (type != BYTE_ARRAY && type != INPUTSTREAM && type != OBJECT)
			typeProblem(type, BYTE_ARRAY, true);

		if (byteArrayVal == null && intVal == SET_AS_INPUTSTREAM) {
			InputStream stream = (InputStream) objectVal;
			try {
				intVal = stream.available();
				byteArrayVal = new byte[intVal];
				stream.read(byteArrayVal);
				setByteArray(byteArrayVal, intVal);
			} catch (IOException ex) {
				return null;
			}
			objectVal = null;
		}

		return byteArrayVal;
	}

	/**
	 * Retrieves the length of an array.
	 *
	 * @return Sets the length of an array.
	 */
	public final int getArrayLength() {
		return intVal;
	}

	/**
	 * @deprecated. Use getInputStream().
	 */
	public final InputStream getBinaryStream() {
		return getInputStream();
	}

	public final InputStream getInputStream() {
		if (type != INPUTSTREAM && type != OBJECT && type != BYTE_ARRAY)
			typeProblem(type, INPUTSTREAM, true);

		if (intVal != SET_AS_INPUTSTREAM) {
			if (objectVal == null && byteArrayVal != null) {
				setInputStream(new ByteArrayInputStream(byteArrayVal, 0, intVal));
				byteArrayVal = null;
			}
		}
		DiagnosticJLimo.check(objectVal == null || objectVal instanceof InputStream);
		return (InputStream) objectVal;
	}

	public final void setInt(int val) {
		DiagnosticJLimo.check(this != nullVariant);
		if (setType != 0 && setType != INT)
			typeProblem(setType, INT, false);
		type = INT;
		intVal = val;
	}

	public final void setShort(short val) {
		if (setType != 0 && setType != SHORT)
			typeProblem(setType, SHORT, false);
		type = SHORT;
		intVal = val;
	}

	public final void setByte(byte val) {
		if (setType != 0 && setType < BYTE || setType > LONG)
			typeProblem(setType, BYTE, false);
		type = BYTE;
		intVal = val;
	}
	// !/*
	// ! public final void setAsInt(int val) {
	// ! switch (type) {
	// ! case BYTE: setByte((byte)val); return;
	// ! case SHORT: setShort((short)val); return;
	// ! default: setInt(val); return;
	// ! }
	// ! }
	// !
	// !*/

	public final void setLong(long val) {
		if (setType != 0 && (setType < BYTE || setType > LONG))
			typeProblem(setType, LONG, false);
		type = LONG;
		longVal = val;
	}

	public final void setBoolean(boolean val) {
		if (setType != 0 && setType != BOOLEAN)
			typeProblem(setType, BOOLEAN, false);
		type = BOOLEAN;
		booleanVal = val;
	}

	public final void setDouble(double val) {
		if (setType != 0 && setType != DOUBLE)
			typeProblem(setType, DOUBLE, false);
		type = DOUBLE;
		doubleVal = val;
	}

	public final void setFloat(float val) {
		if (setType != 0 && setType != FLOAT)
			typeProblem(setType, FLOAT, false);
		type = FLOAT;
		floatVal = val;
	}

	// ! /*
	// ! public final void setAsDouble(double val) {
	// ! switch(setType) {
	// ! case FLOAT: setFloat((float)val); return;
	// ! case DOUBLE: setDouble(val); return;
	// ! default: break; // to make compiler happy
	// ! }
	// ! typeProblem(setType, DOUBLE, false);
	// ! }
	// !*/

	public final void setString(String val) {
		if (setType != STRING && setType != 0)
			typeProblem(setType, STRING, false);
		if (val == null)
			type = ASSIGNED_NULL;
		else
			type = STRING;
		stringVal = val;
	}

	public final void setBigDecimal(BigDecimal val) {
		if (setType != BIGDECIMAL && setType != 0)
			typeProblem(setType, BIGDECIMAL, false);
		type = val == null ? ASSIGNED_NULL : BIGDECIMAL;
		bigDecimalVal = val;
		booleanVal = false;
	}
	// !/*
	// ! public final void setBigDecimal(String val) {
	// ! if (setType != BIGDECIMAL && setType != 0)
	// ! typeProblem(setType, BIGDECIMAL, false);
	// ! type = val == null ? ASSIGNED_NULL : BIGDECIMAL;
	// ! bigDecimalVal = null;
	// ! stringVal = val;
	// ! booleanVal = true;
	// ! }
	// !*/

	public final void setDate(java.sql.Date val) {
		if (setType != DATE && setType != 0)
			typeProblem(setType, DATE, false);
		if (val == null) {
			type = ASSIGNED_NULL;
			dateVal = null;
		} else {
			type = DATE;
			intVal = SET_AS_DATE;
			if (dateVal == null)
				// ! dateVal = new Date(val.getTime() /*System.currentTimeMillis()*/);
				dateVal = new Date(val.getTime());
			else
				dateVal.setTime(val.getTime());
		}
	}

	public final void setTime(Time val) {
		if (setType != TIME && setType != 0)
			typeProblem(setType, TIME, false);
		if (val == null) {
			type = ASSIGNED_NULL;
			timeVal = null;
		} else {
			type = TIME;
			intVal = SET_AS_TIME;
			if (timeVal == null)
				// ! timeVal = new Time(val.getTime() /*System.currentTimeMillis()*/);
				timeVal = new Time(val.getTime());
			else
				timeVal.setTime(val.getTime());
		}
	}

	public final void setTimestamp(Timestamp val) {
		if (setType != TIMESTAMP && setType != 0)
			typeProblem(setType, TIMESTAMP, false);
		if (val == null) {
			type = ASSIGNED_NULL;
			timestampVal = null;
		} else {
			type = TIMESTAMP;
			// Must create dummy timeStamp. val.getTime() cannot be set directly on
			// timeStamp value with constructor since the Timestamp() constructor
			// massages the value passed into the constructor.
			//
			if (timestampVal == null)
				timestampVal = new Timestamp(System.currentTimeMillis()); // Do not pass val.getTime()!

			timestampVal.setTime(val.getTime());
			timestampVal.setNanos(val.getNanos());
		}
	}

	public final void setDate(long val) {
		if (setType != DATE && setType != 0)
			typeProblem(setType, DATE, false);
		type = DATE;
		intVal = SET_AS_DATE;
		if (dateVal == null)
			dateVal = new Date(System.currentTimeMillis());
		dateVal.setTime(val);
	}

	public final void setEncodedDate(int encodedValue) {
		if (setType != DATE && setType != 0)
			typeProblem(setType, DATE, false);
		type = DATE;
		intVal = SET_AS_LONG;
		longVal = encodedValue;
	}

	public final void setTime(long val) {
		if (setType != TIME && setType != 0)
			typeProblem(setType, TIME, false);
		type = TIME;
		intVal = SET_AS_TIME;
		if (timeVal == null)
			timeVal = new Time(System.currentTimeMillis());
		timeVal.setTime(val);
	}

	public final void setEncodedTime(int encodedValue) {
		DiagnosticJLimo.check(encodedValue >= 0 && encodedValue < 86400000);
		if (setType != TIME && setType != 0)
			typeProblem(setType, TIME, false);
		type = TIME;
		intVal = SET_AS_LONG;
		longVal = encodedValue;
	}

	public final void setTimestamp(long val) {
		setTimestamp(val, 0);
	}

	/**
	 * Sets the value of the Variant as a Timestamp value.
	 *
	 * @param val
	 *          The new value as a <b>long</b> value.
	 * @param nanos
	 *          The number of nanoseconds in the Timestamp value.
	 */
	public final void setTimestamp(long val, int nanos) {
		if (setType != TIMESTAMP && setType != 0)
			typeProblem(setType, TIMESTAMP, false);
		type = Variant.TIMESTAMP;
		if (timestampVal == null)
			timestampVal = new Timestamp(System.currentTimeMillis());
		long secs = val / TimeConst.MILLIS_PER_SECOND;
		nanos += (int) ((val % TimeConst.MILLIS_PER_SECOND) * (TimeConst.NANOS_PER_SECOND / TimeConst.MILLIS_PER_SECOND));
		while (nanos < 0) {
			nanos += TimeConst.NANOS_PER_SECOND;
			--secs;
		}
		while (nanos > TimeConst.NANOS_PER_SECOND) {
			nanos -= TimeConst.NANOS_PER_SECOND;
			++secs;
		}
		timestampVal.setTime(secs * TimeConst.MILLIS_PER_SECOND);
		timestampVal.setNanos(nanos);
	}

	/**
	 * Sets the value of the Variant to a new array of bytes.
	 *
	 * @param val
	 *          The new array of bytes that becomes the new value of this Variant.
	 * @param length
	 *          The length of the new byte array.
	 */
	public final void setByteArray(byte[] val, int length) {
		setByteArray(BYTE_ARRAY, val, length);
	}

	/**
	 * Sets the value of the Variant to a new array of bytes.
	 *
	 * @param valType
	 *          Variant data type for val param.
	 * @param val
	 *          The new array of bytes that becomes the new value of this Variant.
	 * @param length
	 *          The length of the new byte array.
	 */
	public final void setByteArray(int valType, byte[] val, int length) {
		if (setType != BYTE_ARRAY && setType != INPUTSTREAM && setType != OBJECT && setType != 0)
			typeProblem(setType, BYTE_ARRAY, false);
		byteArrayVal = val;
		if (val == null)
			type = ASSIGNED_NULL;
		else if (setType != 0)
			type = setType;
		else
			type = valType;
		objectVal = null;
		intVal = length;
	}

	/**
	 * Sets the length of an array.
	 * 
	 * @param length
	 *          The length of an array.
	 */
	public final void setArrayLength(int length) {
		intVal = length;
	}

	/**
	 * @deprecated. Use setInputStream().
	 */
	public final void setBinaryStream(InputStream val) {
		setInputStream(val);
	}

	public final void setInputStream(InputStream val) {
		setInputStream(INPUTSTREAM, val);
	}

	public final void setInputStream(int valType, InputStream val) {
		if (setType != INPUTSTREAM && setType != BYTE_ARRAY && setType != OBJECT && setType != 0)
			typeProblem(setType, INPUTSTREAM, false);

		if (val == null)
			type = ASSIGNED_NULL;
		else if (setType != 0)
			type = setType;
		else
			type = INPUTSTREAM;

		intVal = SET_AS_INPUTSTREAM;
		byteArrayVal = null;
		objectVal = (Object) val;
	}

	private final void setBlob(Variant value) {
		DiagnosticJLimo.check(setType == 0 || setType == value.type);
		type = value.type;
		intVal = value.intVal;
		byteArrayVal = value.byteArrayVal;
		objectVal = value.objectVal;
	}

	public final void setVariant(Variant value) {
		switch (value.type) {
			case STRING:
				setString(value.stringVal);
				break;

			case BYTE:
				setByte((byte) value.intVal);
				break;
			case SHORT:
				setShort((short) value.intVal);
				break;
			case INT:
				setInt(value.intVal);
				break;
			case BOOLEAN:
				setBoolean(value.booleanVal);
				break;

			case TIMESTAMP:
				setTimestamp(value.getTimestamp());
				break;
			case DATE:
				if (value.intVal != SET_AS_LONG)
					setDate(value.dateVal);
				type = value.type;
				intVal = value.intVal;
				longVal = value.longVal;
				break;
			case TIME:
				if (value.intVal != SET_AS_LONG)
					setTime(value.timeVal);
				type = value.type;
				intVal = value.intVal;
				longVal = value.longVal;
				break;

			case LONG:
				setLong(value.longVal);
				break;
			case FLOAT:
				setFloat(value.floatVal);
				break;
			case DOUBLE:
				setDouble(value.doubleVal);
				break;
			case BIGDECIMAL:
				// ! if (value.booleanVal)
				// ! setBigDecimal(value.stringVal);
				// ! else
				setBigDecimal(value.bigDecimalVal);
				break;
			case INPUTSTREAM:
				if (setType == 0 || setType == value.type)
					setBlob(value);
				else
					setInputStream(value.getInputStream());
				break;
			case BYTE_ARRAY:
				if (setType == 0 || setType == value.type)
					setBlob(value);
				else {
					byte[] buf = value.getByteArray();
					setByteArray(buf, value.intVal);
				}
				break;
			case OBJECT:
				if (setType == 0 || setType == value.type)
					setBlob(value);
				else
					setObject(value.getObject());
				break;

			case ASSIGNED_NULL:
			case UNASSIGNED_NULL:
				if (setType != value.type && setType != 0)
					typeProblem(value.type, setType, false);
				type = value.type;
				break;
			default:
				invalidVariantType(value.type);
				// ! Diagnostic.println("putVariant() Invalid type: "+value.type);
				break;
		}
	}

	/**
	 * @since 2.01 Set this variant to value. If value is not the same setType, then an attempt is made to convert to the data type of this variant.
	 */

	public final void setAsVariant(Variant value) {
		switch (setType) {
			case STRING:
				setString(value.toString());
				break;

			case BYTE:
				setByte((byte) value.getAsInt());
				break;
			case SHORT:
				setShort((short) value.getAsInt());
				break;
			case INT:
				setInt(value.getAsInt());
				break;
			case BOOLEAN:
				setBoolean(value.getAsBoolean());
				break;

			case TIMESTAMP:
				setAsTimestamp(value);
				break;
			case DATE:
				setAsDate(value);
				break;
			case TIME:
				setAsTime(value);
				break;
			case LONG:
				setLong(value.getAsLong());
				break;

			case FLOAT:
				setFloat(value.getAsFloat());
				break;
			case DOUBLE:
				setDouble(value.getAsDouble());
				break;
			case BIGDECIMAL:
				setBigDecimal(value.getAsBigDecimal());
				break;
			// ! case UNASSIGNED_NULL: setUnassignedNull(); break; //A Variant without a setType has setType==0
			case ASSIGNED_NULL:
				setAssignedNull();
				break;
			case OBJECT:
				setObject(value.getAsObject());
				break;
			// ! case INPUTSTREAM: missing implementation!!!
			// ! case BYTE_ARRAY: missing implementation!!!
			default:
				if (setType == 0 || setType == value.type || value.isNull())
					setVariant(value);
				else
					invalidVariantType(setType);
				break;
		}
	}

	/**
	 * Attempts to parse the passed string s to the type indicated by wantedType. Date values must be of the format "yyyy-mm-dd". Time values must be of the
	 * format "hh:mm:ss". Timestamp values must be of the format "yyyy-mm-dd hh:mm:ss.fffffffff", where f indicates a digit of the fractions of seconds. Boolean
	 * values are true for true, anything else is false.
	 *
	 * @param wantedType
	 * @param s
	 */
	public final void setFromString(int wantedType, String s) {
		if (setType != wantedType && setType != 0)
			typeProblem(setType, wantedType, false);

		if (s == null)
			type = STRING; // Force use of setString method

		switch (wantedType) {
			case BYTE:
				setByte(Byte.parseByte(s));
				break;
			case SHORT:
				setShort(Short.parseShort(s));
				break;
			case INT:
				setInt(Integer.parseInt(s));
				break;
			case LONG:
				setLong(Long.parseLong(s));
				break;

			case FLOAT:
				setFloat(Float.valueOf(s).floatValue());
				break;
			case DOUBLE:
				setDouble(Double.valueOf(s).doubleValue());
				break;
			case BIGDECIMAL:
				setBigDecimal(new BigDecimal(s));
				break;

			case BOOLEAN:
				setBoolean(Boolean.valueOf(s).booleanValue());
				break;

			case TIMESTAMP: type = TIMESTAMP; timestampVal = java.sql.Timestamp.valueOf(s); break;

			case DATE: LocalDateUtil.setLocalDateAsLong(this, s); break;
			case TIME: LocalDateUtil.setLocalTimeAsLong(this, s); break;

			case STRING:
				setString(s);
				break;

			default:
				invalidVariantType(type);
				break;
		}
	}

	private final void invalidVariantType(int type) {
		VariantException.fire(Res.bundle.format(ResIndex.InvalidVariantType, new String[] { typeName(type) }));
	}

	public final void setObject(Object val) {
		if (setType != OBJECT && setType != 0)
			typeProblem(setType, OBJECT, false);
		if (val == null)
			type = ASSIGNED_NULL;
		else
			type = OBJECT;
		intVal = SET_AS_OBJECT;
		objectVal = val;
	}

	public final boolean isSetAsObject() {
		return intVal == SET_AS_OBJECT;
	}

	public final Object getObject() {
		if (type != OBJECT && type != BYTE_ARRAY && type != INPUTSTREAM)
			typeProblem(type, OBJECT, true);
		if (intVal != SET_AS_OBJECT) {
			if ((intVal == SET_AS_INPUTSTREAM && objectVal != null) || byteArrayVal != null) {
				try {
					InputStream stream = getInputStream();
					try {
						stream.reset();
					} catch (IOException ex) {}
					setObject((new ObjectInputStream(stream)).readObject());
				} catch (Exception ex) {
					VariantException.fire(com.borland.jb.util.ExceptionChain.getOriginalMessage(ex));
				}
			}
		}
		return objectVal;
	}

	public final short getAsShort() {
		switch (type) {
			case BYTE:
			case SHORT:
			case INT:
				return (short) intVal;
			case BOOLEAN:
				return (short) (booleanVal ? 1 : 0);
			case TIME:
			case DATE:
			case TIMESTAMP:
				return (short) getAsLong();
			case LONG:
				return (short) longVal;
			case FLOAT:
				return (short) floatVal;
			case DOUBLE:
				return (short) doubleVal;
			case BIGDECIMAL:
				return (short) getBigDecimal().intValue();
			case Variant.UNASSIGNED_NULL:
			case Variant.ASSIGNED_NULL:
				return 0;
			default:
				break; // to make compiler happy
		}
		typeProblem(type, SHORT, true);
		return 0;
	}

	public final int getAsInt() {
		switch (type) {
			case BYTE:
			case SHORT:
			case INT:
				return intVal;
			case BOOLEAN:
				return booleanVal ? 1 : 0;
			case TIME:
				if (intVal == SET_AS_TIME) {
					longVal = LocalDateUtil.getLocalTimeAsLong(timeVal, null);
					intVal = SET_AS_BOTH;
				}
				return (int) longVal;
			case DATE:
				if (intVal == SET_AS_DATE) {
					longVal = LocalDateUtil.getLocalDateAsLong(dateVal, null);
					intVal = SET_AS_BOTH;
				}
				return (int) longVal;
			case TIMESTAMP:
				return (int) getAsLong();
			case LONG:
				return (int) longVal;
			case FLOAT:
				return (int) floatVal;
			case DOUBLE:
				return (int) doubleVal;
			case BIGDECIMAL:
				return getBigDecimal().intValue();
			case Variant.UNASSIGNED_NULL:
			case Variant.ASSIGNED_NULL:
				return 0;
			default:
				break; // to make compiler happy
		}
		typeProblem(type, INT, true);
		return 0;
	}

	public final long getAsLong() {
		switch (type) {
			case BOOLEAN:
				return booleanVal ? 1 : 0;
			case BYTE:
			case SHORT:
			case INT:
				return intVal;
			case LONG:
				return longVal;
			case FLOAT:
				return (long) floatVal;
			case DOUBLE:
				return (long) doubleVal;
			case BIGDECIMAL:
				return getBigDecimal().longValue();
			case TIME:
				return getTime().getTime();
			case DATE:
				return getDate().getTime();
			case TIMESTAMP: {
				// UGLY workaround for JDK1.4 (JOAL)
				// Under JDK1.4 timestampVal.getTime() will include the msecs of the nanos, but not in JDK1.3.
				// GetAsLong has always returned the long value without the msecs of the nanos, and JDataStore
				// depends on that in TimestampKeyElement for storage of timestamp values.
				//
				// This strange code makes sure, that the nanos will not be included in the returned value under JDK1.4:
				// For JDK's before JDK1.4 the setting and resetting of nanos has no effect.
				//
				int nanos = timestampVal.getNanos();
				timestampVal.setNanos(0);
				long time = timestampVal.getTime();
				timestampVal.setNanos(nanos);
				return time;
			}
			case Variant.UNASSIGNED_NULL:
			case Variant.ASSIGNED_NULL:
				return 0;
			default:
				break; // to make compiler happy
		}
		typeProblem(type, LONG, true);
		return 0;
	}

	public final double getAsDouble() {
		switch (type) {
			case BYTE:
			case SHORT:
			case INT:
				return intVal;
			case LONG:
				return longVal;
			case FLOAT:
				return (double) floatVal;
			case DOUBLE:
				return doubleVal;
			case BIGDECIMAL:
				return getBigDecimal().doubleValue();
			case TIME:
			case DATE:
			case TIMESTAMP:
				return (double) getAsLong();
			case Variant.UNASSIGNED_NULL:
			case Variant.ASSIGNED_NULL:
				return 0;
			default:
				break; // to make compiler happy
		}
		typeProblem(type, DOUBLE, true);
		return 0;
	}

	public final float getAsFloat() {
		switch (type) {
			case BYTE:
			case SHORT:
			case INT:
				return intVal;
			case LONG:
				return longVal;
			case FLOAT:
				return floatVal;
			case DOUBLE:
				return (float) doubleVal;
			case BIGDECIMAL:
				return getBigDecimal().floatValue();
			case TIME:
			case DATE:
			case TIMESTAMP:
				return (float) getAsLong();
			case Variant.UNASSIGNED_NULL:
			case Variant.ASSIGNED_NULL:
				return 0;
			default:
				break; // to make compiler happy
		}
		typeProblem(type, FLOAT, true);
		return 0;
	}

	public final BigDecimal getAsBigDecimal() {
		switch (type) {
			case BYTE:
			case SHORT:
			case INT:
				return new BigDecimal(intVal);
			case LONG:
				return BigDecimal.valueOf(longVal, 0); // !JOAL BugFix, otherwise the double constructor is used, which does not have enough precision to hold a long.
			case FLOAT:
				return new BigDecimal(floatVal);
			// ! case DOUBLE: return new BigDecimal(doubleVal, 4); //! JDK beta 3.2
			case DOUBLE:
				return new BigDecimal(doubleVal);
			case BIGDECIMAL:
				return getBigDecimal();
			case TIME:
			case DATE:
			case TIMESTAMP:
				return BigDecimal.valueOf(getAsLong());
			case Variant.UNASSIGNED_NULL:
			case Variant.ASSIGNED_NULL:
				return new BigDecimal(0);
			default:
				break; // to make compiler happy
		}
		typeProblem(type, BIGDECIMAL, true);
		return null;
	}

	public final boolean getAsBoolean() {
		switch (type) {
			case BOOLEAN:
				return booleanVal;
			case STRING:
				return Boolean.valueOf(stringVal).booleanValue();
			case BYTE:
			case SHORT:
			case INT:
				return intVal != 0;
			case LONG:
				return longVal != 0;
			case FLOAT:
				return floatVal != 0;
			case DOUBLE:
				return doubleVal != 0;
			case BIGDECIMAL:
				return bigDecimalVal.doubleValue() != 0;
			case TIME:
			case DATE:
			case TIMESTAMP:
				return getAsLong() != 0;
		}
		return false;
	}

	/**
	 * Sets Time to value.
	 * 
	 * @param value
	 *          if value is of type TIME, value is copied directly. if value is of type BOOLEAN, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BIGDECIMAL, DATE,
	 *          TIMESTAMP, setTimeStamp() is called with the return value from value.getAsLong() If value is of type ASSIGNED_NULL or UNASSIGNED_NULL, this is set
	 *          to the same *_NULL value.
	 */
	public final void setAsTime(Variant value) {
		switch (value.type) {
			case TIME:
				setTime(value.getTime());
				break;
			case BOOLEAN:
			case BYTE:
			case SHORT:
			case INT:
			case LONG:
			case FLOAT:
			case DOUBLE:
			case BIGDECIMAL:
			case DATE:
			case TIMESTAMP:
				setTime(value.getAsLong());
				return;
			case Variant.UNASSIGNED_NULL:
				setUnassignedNull();
				return;
			case Variant.ASSIGNED_NULL:
				setAssignedNull();
				return;
			default:
				break; // to make compiler happy
		}
		typeProblem(type, TIME, false);
	}

	/**
	 * Sets Timestamp to value.
	 * 
	 * @param value
	 *          if value is of type TIME, value is copied directly. if value is of type BOOLEAN, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BIGDECIMAL, DATE, TIME,
	 *          setTimeStamp() is called with the return value from value.getAsLong() If value is of type ASSIGNED_NULL or UNASSIGNED_NULL, this is set to the
	 *          same *_NULL value.
	 */

	public final void setAsTimestamp(Variant value) {
		switch (value.type) {
			case TIMESTAMP:
				setTimestamp(value.getTimestamp());
				break;
			case BOOLEAN:
			case BYTE:
			case SHORT:
			case INT:
			case LONG:
			case FLOAT:
			case DOUBLE:
			case BIGDECIMAL:
				setTimestamp(value.getAsLong());
				return;
			case DATE: // RAID139953
				setTimestamp(value.getAsLong());
				timestampVal.setHours(0);
				timestampVal.setMinutes(0);
				timestampVal.setSeconds(0);
				return;
			case TIME: // RAID139953
				long lvalue = value.getAsLong();
				lvalue = lvalue % 86400000;
				if (lvalue < 0)
					lvalue += 86400000;
				setTimestamp(lvalue);
				return;
			case Variant.UNASSIGNED_NULL:
				setUnassignedNull();
				return;
			case Variant.ASSIGNED_NULL:
				setAssignedNull();
				return;
			default:
				break; // to make compiler happy
		}
		typeProblem(type, TIMESTAMP, false);
	}

	public final void setAsDate(Variant value) {
		switch (value.type) {
			case DATE:
				setDate(value.getDate());
				break;
			case BOOLEAN:
			case BYTE:
			case SHORT:
			case INT:
			case LONG:
			case FLOAT:
			case DOUBLE:
			case BIGDECIMAL:
			case TIME:
			case TIMESTAMP:
				setDate(value.getAsLong());
				return;
			case Variant.UNASSIGNED_NULL:
				setUnassignedNull();
				return;
			case Variant.ASSIGNED_NULL:
				setAssignedNull();
				return;
			default:
				break; // to make compiler happy
		}
		typeProblem(type, DATE, false);
	}

	public final void setNull(int nullType) {
		// Test makes sure we set it to something reasonable.
		//
		if (nullType == UNASSIGNED_NULL)
			this.type = UNASSIGNED_NULL;
		else {
			DiagnosticJLimo.check(nullType == ASSIGNED_NULL);
			this.type = ASSIGNED_NULL;
		}
	}

	/**
	 * Sets the value of the Variant as an assigned <b>null</b>. An assigned <b>null</b> is a value that has been explicitly set to <b>null</b> in contrast to one
	 * that is simply unassigned.
	 */
	public final void setAssignedNull() {
		this.type = ASSIGNED_NULL;
	}

	public final void setUnassignedNull() {
		this.type = UNASSIGNED_NULL;
	}

	public final boolean isAssignedNull() {
		return type == ASSIGNED_NULL;
	}

	public final boolean isUnassignedNull() {
		return type == UNASSIGNED_NULL;
	}

	public final boolean isNull() {
		return type <= NULL_TYPES;
	}

	public final int getType() {
		return type;
	}

	public final int getSetType() {
		return setType;
	}

	public final int getStoreType() {
		switch (type) {
			case INPUTSTREAM:
			default:
				if (objectVal == null && byteArrayVal != null)
					return BYTE_ARRAY;
				break;
			case BYTE_ARRAY:
				if (byteArrayVal == null && objectVal != null && intVal == SET_AS_INPUTSTREAM)
					return INPUTSTREAM;
				break;
		}
		return type;
	}

	// used by dbSwing components (dcy)
	public Object getAsObject() {
		switch (type) {
			case ASSIGNED_NULL:
			case UNASSIGNED_NULL:
				return null;
			case INT:
				return new Integer(intVal);
			case BYTE:
				return new Byte((byte) intVal);
			case SHORT:
				return new Short((short) intVal);
			case FLOAT:
				return new Float(floatVal);
			case DOUBLE:
				return new Double(doubleVal);
			case LONG:
				return new Long(longVal);
			case BIGDECIMAL:
				if (booleanVal)
					return new BigDecimal(stringVal);
				if (bigDecimalVal == null)
					return new BigDecimal("0");
				return bigDecimalVal;
			case BOOLEAN:
				return new Boolean(booleanVal);
			case STRING:
				if (stringVal == null)
					return "";
				return stringVal;
			case DATE:
				return new java.sql.Date(getDate().getTime());
			case TIME:
				return new Time(getTime().getTime());
			case TIMESTAMP:
				return new Timestamp(timestampVal.getTime());
			case BYTE_ARRAY:
				if (byteArrayVal == null)
					return null;
				// could be huge, so we don't clone it
				return byteArrayVal;
			case OBJECT:
				getObject();
				if (objectVal == null)
					return null;
				// could be huge, so we don't clone it
				return objectVal;
			case INPUTSTREAM:
				getInputStream();
				if (objectVal == null)
					return null;
				// could be huge, so we don't clone it
				return objectVal;
			default:
				break; // to make compiler happy
		}
		DiagnosticJLimo.fail();
		return null;
	}

	// ! used by dbSwing components (dcy)

	/**
	 * Currently used by dbSwing components to set a Variant from an Object.
	 *
	 * @param object
	 *          The value to set.
	 * @param variantType
	 *          Variant data type that the object maps to. For example, if the object is of type Integer, then variantType should be Variant.INT.
	 */
	public void setAsObject(Object object, int variantType) {
		switch (variantType) {
			case Variant.ASSIGNED_NULL:
				setAssignedNull();
				break;
			case Variant.UNASSIGNED_NULL:
				setUnassignedNull();
				break;
			case Variant.INT:
				setInt(((Integer) object).intValue());
				break;
			case Variant.BYTE:
				setByte(((Byte) object).byteValue());
				break;
			case Variant.SHORT:
				setShort(((Short) object).shortValue());
				break;
			case Variant.FLOAT:
				setFloat(((Float) object).floatValue());
				break;
			case Variant.DOUBLE:
				setDouble(((Double) object).doubleValue());
				break;
			case Variant.LONG:
				setLong(((Long) object).longValue());
				break;
			case Variant.BIGDECIMAL:
				setBigDecimal((BigDecimal) object);
				break;
			case Variant.BOOLEAN:
				setBoolean(((Boolean) object).booleanValue());
				break;
			case Variant.STRING:
				// ! use toString() instead of (String) cast per
				// ! raid 144293
				// !
				setString(object.toString());
				break;
			case Variant.DATE:
				setDate((java.sql.Date) object);
				break;
			case Variant.TIME:
				setTime((Time) object);
				break;
			case Variant.TIMESTAMP:
				setTimestamp((Timestamp) object);
				break;
			case Variant.BYTE_ARRAY:
				setByteArray((byte[]) object, ((byte[]) object).length);
				break;
			case Variant.OBJECT:
				setObject(object);
				break;
			case Variant.INPUTSTREAM:
				setInputStream((InputStream) object);
				break;
			default:
				break; // to make compiler happy
		}
	}

	public final String toString() {
		switch (type) {
			case ASSIGNED_NULL:
			case UNASSIGNED_NULL:
				return "";
			case INT:
			case BYTE:
			case SHORT:
				return Integer.toString(intVal, 10);
			case FLOAT:
				return Float.toString(floatVal);
			case DOUBLE:
				return Double.toString(doubleVal);
			case LONG:
				return Long.toString(longVal, 10);
			case BIGDECIMAL:
				if (booleanVal)
					return stringVal;
				if (bigDecimalVal == null)
					return "";
				return bigDecimalVal.toString();
			case BOOLEAN:
				return booleanVal ? "true" : "false"; // NORES
			case STRING:
				if (stringVal == null)
					return "";
				return stringVal;
			case DATE:
				return (intVal == SET_AS_DATE ? dateVal.toString() : LocalDateUtil.getLocalDateAsString(this));
			case TIME:
				return (intVal == SET_AS_TIME ? timeVal.toString() : LocalDateUtil.getLocalTimeAsString(this));
			case TIMESTAMP:
				return timestampVal.toString();
			case BYTE_ARRAY:
				if (byteArrayVal == null)
					return "";
				return new String(byteArrayVal, 0, intVal);
			case OBJECT:
				getObject();
				if (objectVal == null)
					return "";
				return objectVal.toString();
			case INPUTSTREAM:
				getInputStream();
				if (objectVal == null)
					return "";
				return objectVal.toString();
			default:
				break; // to make compiler happy
		}
		DiagnosticJLimo.fail();
		return "";
	}

	/**
	 * @since JB2.0 Returns true if value or value instance changed. Note that will return false for Variants storing different Object reference values that may
	 *        be equal. Provides high speed test that indicates two variants may not be equal. If true is returned they are equal. If false is returned, they
	 *        might still be equal.
	 */
	/**
	 * Returns <b>true</b> if the value or value instance changed. Returns <b>false</b> for Variants storing different object reference values that may be equal.
	 * Provides a high speed test that indicates if two variants are equal. If <b>true</b> is returned, they are equal. If false is returned, they might still be
	 * equal.
	 *
	 * @param value2
	 * @return <b>true</b> if the value or value instance changed.
	 */
	public boolean equalsInstance(Variant value2) {
		if (type == value2.type) {
			switch (type) {
				case Variant.INPUTSTREAM:
					if (intVal == SET_AS_INPUTSTREAM && value2.intVal == SET_AS_INPUTSTREAM)
						return getInputStream() == value2.getInputStream();
					break;
				case Variant.OBJECT:
					if (intVal == SET_AS_OBJECT && value2.intVal == SET_AS_OBJECT)
						return getObject() == value2.getObject();
					break;
			}
		}
		return equals(value2);
	}

	private final boolean equalsByteArray(Variant value) {
		if (intVal == value.intVal) {
			if (byteArrayVal == value.byteArrayVal)
				return true;
			int off = 0;
			while (off < intVal && byteArrayVal[off] == value.byteArrayVal[off])
				++off;
			return off == intVal;
		}
		return false;
	}

	/**
	 * Determines whether a Variant value is equal to this Variant value. If equals() returns <b>true</b>, the two Variant values are of the same type and are
	 * equal in value. A returned value of <b>false</b> indicates that the two values differ in value or type.
	 *
	 * @param value
	 *          The Variant value being compared to the data type and value of this Variant.
	 * @return <b>true</b> if the two Variant values are of the same type and are equal in value. A returned value of <b>false</b> indicates that the two values
	 *         differ in value or type.
	 */
	public final boolean equals(Variant value) {

		if (type != value.type) {
			if (type <= NULL_TYPES || value.type <= NULL_TYPES)
				return false;
			typeProblem(value.type, type, true);
		}

		switch (type) {
			case ASSIGNED_NULL:
			case UNASSIGNED_NULL:
				return value.type == type;
			case INT:
			case BYTE:
			case SHORT:
				return intVal == value.intVal;
			case BOOLEAN:
				return booleanVal == value.booleanVal;
			case FLOAT:
				return floatVal == value.floatVal;
			case DOUBLE:
				return doubleVal == value.doubleVal;
			case TIMESTAMP:
				if (timestampVal.getNanos() != value.getTimestamp().getNanos())
					return false;
				return timestampVal.getTime() == value.getTimestamp().getTime();
			case DATE:
				return compareDates(this, value) == 0;
			case TIME:
				return compareTimes(this, value) == 0;
			case LONG:
				return longVal == value.longVal;
			case BIGDECIMAL:
				// ! if (booleanVal && value.booleanVal && stringVal == value.stringVal)
				// ! return true;
				if (getBigDecimal() == value.getBigDecimal())
					return true;
				// return bigDecimalVal.equals(value.bigDecimalVal);
				// !RD TODO 5/6/97 Only compareTo() allows for different scales
				return bigDecimalVal.compareTo(value.bigDecimalVal) == 0;
			case STRING:
				if (stringVal == value.stringVal)
					return true;
				return stringVal.equals(value.stringVal);
			case BYTE_ARRAY:
				if (intVal == SET_AS_INPUTSTREAM)
					getByteArray();
				value.getByteArray();
				return equalsByteArray(value);
			case INPUTSTREAM:
				if (intVal >= 0 && intVal == value.intVal)
					return equalsByteArray(value);
				return equals(getInputStream(), value.getInputStream());
			case OBJECT:
				// These optimizations are not valid because bitwise compares of two objects
				// does not mean they are equal. For example (new byte[5]).equals(new byte[5)
				// evaluates to false. -Steve
				// if (intVal >= 0 && intVal == value.intVal)
				// return equalsByteArray(value);

				// Optimization for raid 136031.
				// Avoid having to instantiate the object since the class may not
				// be on the classpath.
				//
				if (intVal != SET_AS_OBJECT && value.intVal != SET_AS_OBJECT)
					return equals(getInputStream(), value.getInputStream());

				if (getObject() == value.getObject())
					return true;

				return getObject().equals(value.getObject());
			default:
				break; // to make compiler happy
		}
		DiagnosticJLimo.fail();
		return false;
	}

	private boolean equals(char[] val1, char[] val2) {
		int len = val1.length;
		if (len != val2.length)
			return false;
		for (int index = 0; index < len; ++index) {
			if (val1[index] != val2[index])
				return false;
		}
		return true;
	}

	// ! static int bugCount;

	private boolean equals(InputStream stream1, InputStream stream2) {
		if (stream1 == stream2)
			return true;
		if (stream1 == null || stream2 == null)
			return false;

		// Cannot compare, so assume not equal.
		//
		if (!stream1.markSupported() || !stream2.markSupported())
			return false;

		try {
			stream1.reset();
			stream2.reset();
			// ! int count = 0;
			// ! Diagnostic.println("stream1 length: " + stream1.available());
			// ! Diagnostic.println("stream2 length: " + stream2.available());

			int count = 1;
			int count2;
			byte[] buf = new byte[1024];
			byte[] buf2 = new byte[1024];
			while (count > 0) {

				count = stream1.read(buf);
				count2 = stream2.read(buf2);
				// ! ++count;
				if (count != count2) {
					// ! Diagnostic.println(count +" ch: "+ch+" ch2: "+ch2);
					// ! Diagnostic.println("mismatch: "+stream1+" "+stream2);
					return false;
				}
				for (int index = 0; index < count; ++index) {
					if (buf[index] != buf2[index])
						return false;
				}
			}
		} catch (IOException ex) {
			DiagnosticJLimo.println("IOException hit:");
			DiagnosticJLimo.printStackTrace(ex);
			return false;
		}
		return true;
	}

	private static final int compareInt(int value1, int value2) {
		if (value1 < value2)
			return -1;
		if (value1 > value2)
			return 1;
		return 0;
	}

	private static final int compareLong(long value1, long value2) {
		if (value1 < value2)
			return -1;
		if (value1 > value2)
			return 1;
		return 0;
	}

	private static final int compareDouble(double value1, double value2) {
		if (value1 < value2)
			return -1;
		if (value1 > value2)
			return 1;
		return 0;
	}

	private static final int compareFloat(float value1, float value2) {
		if (value1 < value2)
			return -1;
		else if (value1 > value2)
			return 1;
		else
			return 0;
	}

	// Remove the time portion when comparing dates:
	private static final int compareDates(Variant value1, Variant value2) {
		int ivalue1 = value1.getAsInt();
		int ivalue2 = value2.getAsInt();
		return compareInt(ivalue1, ivalue2);
	}

	// Remove the date portion when comparing times:
	private static final int compareTimes(Variant value1, Variant value2) {
		int ivalue1 = value1.getAsInt();
		int ivalue2 = value2.getAsInt();
		return compareInt(ivalue1, ivalue2);
	}

	private static int compareTimestamps(Variant value1, Variant value2) {
		int result = compareLong(value1.timestampVal.getTime(), value2.getTimestamp().getTime());
		if (result == 0)
			result = compareInt(value1.timestampVal.getNanos(), value2.getTimestamp().getNanos());
		return result;
	}

	private final int compareBoolean(boolean bool1, boolean bool2) {
		if (bool1 == bool2)
			return 0;
		if (bool1)
			return 1;
		return -1;
	}

	/**
	 * Compares a Variant value to the value of this Variant, returning the result. If the result is zero, the two Variants are equal. If the returned value is
	 * less than zero (a negative integer), the value of this Variant is less than value2. If the returned value is greater than zero (a positive integer), the
	 * value of this Variant is greater than value2.
	 *
	 * @param value2
	 *          The value this Variant is being compared to.
	 * @return Zero if the two Variants are equal. Less than zero (a negative integer) if this Variant is less than value2. Greater than zero (a positive integer)
	 *         if the value of this Variant is greater than value2.
	 */
	public int compareTo(Variant value2) {
		if (isNull())
			return value2.isNull() ? 0 : -1;
		if (value2.isNull())
			return 1;

		switch (type) {
			case Variant.BYTE:
			case Variant.SHORT:
			case Variant.INT:
				return compareInt(intVal, value2.getAsInt());

			case Variant.LONG:
				return compareLong(longVal, value2.getAsLong());
			case Variant.FLOAT:
				return compareFloat(floatVal, value2.getAsFloat());
			case Variant.DOUBLE:
				return compareDouble(doubleVal, value2.getAsDouble());
			case Variant.BIGDECIMAL:
				return getBigDecimal().compareTo(value2.getAsBigDecimal());

			case Variant.DATE:
				return compareDates(this, value2);
			case Variant.TIME:
				return compareTimes(this, value2);
			case Variant.TIMESTAMP:
				return compareTimestamps(this, value2);

			case Variant.BOOLEAN:
				return compareBoolean(booleanVal, value2.getBoolean());

			case Variant.STRING:
				return stringVal.compareTo(value2.getString());
			default:
				break; // to make compiler happy
		}

		DiagnosticJLimo.fail();
		return 0;
	}

	/**
	 * Adds a value to this Variant, storing the result in the result parameter.
	 *
	 * @param value2
	 *          The value added to this Variant.
	 * @param result
	 *          The result of the two Variant values added together.
	 */
	public void add(Variant value2, Variant result) {
		if (value2.isNull() && isNull())
			result.setVariant(this);
		else {
			switch (type) {
				case Variant.BYTE:
					result.setByte((byte) (intVal + value2.getAsInt()));
					break;
				case Variant.SHORT:
					result.setShort((short) (intVal + value2.getAsInt()));
					break;
				case Variant.INT:
					result.setInt(intVal + value2.getAsInt());
					break;
				case Variant.LONG:
					result.setLong(longVal + value2.getAsLong());
					break;
				case Variant.DATE:
					result.setDate(getDate().getTime() + value2.getDate().getTime());
					break;
				case Variant.TIME:
					result.setTime(getTime().getTime() + value2.getTime().getTime());
					break;
				case Variant.FLOAT:
					result.setFloat(floatVal + value2.getAsFloat());
					break;
				case Variant.DOUBLE:
					result.setDouble(doubleVal + value2.getAsDouble());
					break;
				case Variant.BIGDECIMAL:
					result.setBigDecimal(getBigDecimal().add(value2.getAsBigDecimal()));
					break;
				case Variant.UNASSIGNED_NULL:
				case Variant.ASSIGNED_NULL:
					result.setVariant(value2);
					break;
				default:
					DiagnosticJLimo.println("type:  " + type);
					DiagnosticJLimo.fail();
					break;
			}
		}
	}

	/**
	 * Subtracts a Variant value from the value of this Variant, storing the result in the result parameter.
	 *
	 * @param value2
	 *          The value being subtracted from this Variant.
	 * @param result
	 *          The value being subtracted from this Variant.
	 */
	public void subtract(Variant value2, Variant result) {
		if (value2.isNull() && isNull())
			result.setVariant(this);
		else {
			switch (type) {
				case Variant.BYTE:
				case Variant.SHORT:
				case Variant.INT:
					result.setInt(intVal - value2.getAsInt());
					break;
				case Variant.LONG:
					result.setLong(longVal - value2.getAsLong());
					break;
				case Variant.FLOAT:
					result.setFloat(floatVal - value2.getAsFloat());
					break;
				case Variant.DOUBLE:
					result.setDouble(doubleVal - value2.getAsDouble());
					break;
				case Variant.BIGDECIMAL:
					result.setBigDecimal(getBigDecimal().subtract(value2.getAsBigDecimal()));
					break;
				case Variant.UNASSIGNED_NULL:
				case Variant.ASSIGNED_NULL:
					result.setVariant(value2);
					break;
				default:
					DiagnosticJLimo.fail();
					break;
			}
		}
	}

	/**
	 * Creates a copy of this Variant, returning the copied object.
	 * 
	 * @return The copied object.
	 */
	public Object clone() {
		Variant value = new Variant(setType);
		value.setVariant(this);
		return value;
	}

	// Serialization support

	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		if (objectVal instanceof Serializable)
			s.writeObject(objectVal);
		else
			s.writeObject(null);
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		objectVal = s.readObject();
	}

}
