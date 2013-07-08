/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.preferences;

import java.util.Map;
import java.util.prefs.BackingStoreException;

/**
 * Yet another preferences abstraction.
 * <p>
 * This is very close to the native Java preferences with the additional feature
 * of "scoping" (and some other minor deviations).
 */
public interface IPreferences {
	/**
	 * The non - persistent default settings
	 */
	public static final String SCOPE_DEFAULT = "DEFAULT"; //$NON-NLS-1$

	/**
	 * The preferences provided with the instrument installation
	 */
	public static final String SCOPE_INSTALLATION = "INSTALLATION"; //$NON-NLS-1$

	/**
	 * Preferences for all users
	 */
	public static final String SCOPE_GLOBAL = "GLOBAL"; //$NON-NLS-1$

	/**
	 * Preferences for a dedicated team of users
	 */
	public static final String SCOPE_TEAM = "TEAM"; //$NON-NLS-1$

	/**
	 * Preferences for a specific user
	 */
	public static final String SCOPE_USER = "USER"; //$NON-NLS-1$

	/**
	 * Returns this preference node's absolute path name.
	 * 
	 * @return this preference node's absolute path name.
	 */
	public String absolutePath();

	/**
	 * The array of all child preferences.
	 * 
	 * @return The array of all child preferences.
	 */
	public IPreferences[] children();

	/**
	 * Returns the names of the children of this preference node, relative to
	 * this node. (The returned array will be of size zero if this node has no
	 * children.)
	 * 
	 * @return the names of the children of this preference node.
	 * @throws BackingStoreException
	 *             if this operation cannot be completed due to a failure in the
	 *             backing store, or inability to communicate with it.
	 */
	public String[] childrenNames() throws BackingStoreException;

	/**
	 * Removes all of the preferences (key-value associations) in this
	 * preference node. This call has no effect on any descendants of this node.
	 * 
	 * <p>
	 * If this implementation supports <i>stored defaults</i>, and this node in
	 * the preferences hierarchy contains any such defaults, the stored defaults
	 * will be "exposed" by this call, in the sense that they will be returned
	 * by succeeding calls to <tt>get</tt>.
	 * 
	 * @throws BackingStoreException
	 *             if this operation cannot be completed due to a failure in the
	 *             backing store, or inability to communicate with it.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @see #removeNode()
	 */
	public void clear() throws BackingStoreException;

	/**
	 * Forces any changes in the contents of this preference node and its
	 * descendants to the persistent store. Once this method returns
	 * successfully, it is safe to assume that all changes made in the subtree
	 * rooted at this node prior to the method invocation have become permanent.
	 * 
	 * <p>
	 * Implementations are free to flush changes into the persistent store at
	 * any time. They do not need to wait for this method to be called.
	 * 
	 * <p>
	 * When a flush occurs on a newly created node, it is made persistent, as
	 * are any ancestors (and descendants) that have yet to be made persistent.
	 * Note however that any preference value changes in ancestors are
	 * <i>not</i> guaranteed to be made persistent.
	 * 
	 * <p>
	 * If this method is invoked on a node that has been removed with the
	 * {@link #removeNode()} method, flushSpi() is invoked on this node, but not
	 * on others.
	 * 
	 * @throws BackingStoreException
	 *             if this operation cannot be completed due to a failure in the
	 *             backing store, or inability to communicate with it.
	 * @see #sync()
	 */
	public void flush() throws BackingStoreException;

	/**
	 * Returns the current value of the string-valued preference with the given
	 * key. Returns the default-default value (the empty string <code>""</code>
	 * ) if there is no preference with the given key, or if the current value
	 * cannot be treated as a string.
	 * 
	 * @param key
	 *            the key of the preference
	 * 
	 * @return the string-valued preference
	 */
	public String get(String key);

	/**
	 * Returns the value associated with the specified key in this preference
	 * node. Returns the specified default if there is no value associated with
	 * the key, or the backing store is inaccessible.
	 * 
	 * <p>
	 * Some implementations may store default values in their backing stores. If
	 * there is no value associated with the specified key but there is such a
	 * <i>stored default</i>, it is returned in preference to the specified
	 * default.
	 * 
	 * @param key
	 *            key whose associated value is to be returned.
	 * @param def
	 *            the value to be returned in the event that this preference
	 *            node has no value associated with <tt>key</tt>.
	 * @return the value associated with <tt>key</tt>, or <tt>def</tt> if no
	 *         value is associated with <tt>key</tt>, or the backing store is
	 *         inaccessible.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @throws NullPointerException
	 *             if <tt>key</tt> is <tt>null</tt>. (A <tt>null</tt> value for
	 *             <tt>def</tt> <i>is</i> permitted.)
	 */
	public String get(String key, String def);

	/**
	 * Returns the current value of the boolean-valued preference with the given
	 * key. Returns the default-default value (<code>false</code>) if there is
	 * no preference with the given key, or if the current value cannot be
	 * treated as a boolean.
	 * 
	 * @param key
	 *            the key of the preference
	 * 
	 * @return the boolean-valued preference
	 */
	public boolean getBoolean(String key);

	/**
	 * Returns the boolean value represented by the string associated with the
	 * specified key in this preference node. Valid strings are <tt>"true"</tt>,
	 * which represents true, and <tt>"false"</tt>, which represents false. Case
	 * is ignored, so, for example, <tt>"TRUE"</tt> and <tt>"False"</tt> are
	 * also valid. This method is intended for use in conjunction with
	 * {@link #putBoolean}.
	 * 
	 * <p>
	 * Returns the specified default if there is no value associated with the
	 * key, the backing store is inaccessible, or if the associated value is
	 * something other than <tt>"true"</tt> or <tt>"false"</tt>, ignoring case.
	 * 
	 * <p>
	 * If the implementation supports <i>stored defaults</i> and such a default
	 * exists and is accessible, it is used in preference to the specified
	 * default, unless the stored default is something other than
	 * <tt>"true"</tt> or <tt>"false"</tt>, ignoring case, in which case the
	 * specified default is used.
	 * 
	 * @param key
	 *            key whose associated value is to be returned as a boolean.
	 * @param def
	 *            the value to be returned in the event that this preference
	 *            node has no value associated with <tt>key</tt> or the
	 *            associated value cannot be interpreted as a boolean, or the
	 *            backing store is inaccessible.
	 * @return the boolean value represented by the string associated with
	 *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
	 *         associated value does not exist or cannot be interpreted as a
	 *         boolean.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @throws NullPointerException
	 *             if <tt>key</tt> is <tt>null</tt>.
	 * @see #get(String,String)
	 * @see #putBoolean(String,boolean)
	 */
	public boolean getBoolean(String key, boolean def);

	/**
	 * Returns the byte array value represented by the string associated with
	 * the specified key in this preference node. Valid strings are
	 * <i>Base64</i> encoded binary data, as defined in <a
	 * href=http://www.ietf.org/rfc/rfc2045.txt>RFC 2045</a>, Section 6.8, with
	 * one minor change: the string must consist solely of characters from the
	 * <i>Base64 Alphabet</i>; no newline characters or extraneous characters
	 * are permitted. This method is intended for use in conjunction with
	 * {@link #putByteArray}.
	 * 
	 * <p>
	 * Returns the specified default if there is no value associated with the
	 * key, the backing store is inaccessible, or if the associated value is not
	 * a valid Base64 encoded byte array (as defined above).
	 * 
	 * <p>
	 * If the implementation supports <i>stored defaults</i> and such a default
	 * exists and is accessible, it is used in preference to the specified
	 * default, unless the stored default is not a valid Base64 encoded byte
	 * array (as defined above), in which case the specified default is used.
	 * 
	 * @param key
	 *            key whose associated value is to be returned as a byte array.
	 * @param def
	 *            the value to be returned in the event that this preference
	 *            node has no value associated with <tt>key</tt> or the
	 *            associated value cannot be interpreted as a byte array, or the
	 *            backing store is inaccessible.
	 * @return the byte array value represented by the string associated with
	 *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
	 *         associated value does not exist or cannot be interpreted as a
	 *         byte array.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @throws NullPointerException
	 *             if <tt>key</tt> is <tt>null</tt>. (A <tt>null</tt> value for
	 *             <tt>def</tt> <i>is</i> permitted.)
	 * @see #get(String,String)
	 * @see #putByteArray(String,byte[])
	 */
	public byte[] getByteArray(String key, byte[] def);

	/**
	 * Returns the current value of the double-valued preference with the given
	 * key. Returns the default-default value (<code>0.0</code>) if there is no
	 * preference with the given key, or if the current value cannot be treated
	 * as a double.
	 * 
	 * @param key
	 *            the key of the preference
	 * 
	 * @return the double-valued preference
	 */
	public double getDouble(String key);

	/**
	 * Returns the double value represented by the string associated with the
	 * specified key in this preference node. The string is converted to an
	 * integer as by {@link Double#parseDouble(String)}. Returns the specified
	 * default if there is no value associated with the key, the backing store
	 * is inaccessible, or if <tt>Double.parseDouble(String)</tt> would throw a
	 * {@link NumberFormatException} if the associated value were passed. This
	 * method is intended for use in conjunction with {@link #putDouble}.
	 * 
	 * <p>
	 * If the implementation supports <i>stored defaults</i> and such a default
	 * exists, is accessible, and could be converted to a double with
	 * <tt>Double.parseDouble</tt>, this double is returned in preference to the
	 * specified default.
	 * 
	 * @param key
	 *            key whose associated value is to be returned as a double.
	 * @param def
	 *            the value to be returned in the event that this preference
	 *            node has no value associated with <tt>key</tt> or the
	 *            associated value cannot be interpreted as a double, or the
	 *            backing store is inaccessible.
	 * @return the double value represented by the string associated with
	 *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
	 *         associated value does not exist or cannot be interpreted as a
	 *         double.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @throws NullPointerException
	 *             if <tt>key</tt> is <tt>null</tt>.
	 * @see #putDouble(String,double)
	 * @see #get(String,String)
	 */
	public double getDouble(String key, double def);

	/**
	 * Returns the current value of the float-valued preference with the given
	 * key. Returns the default-default value (<code>0.0f</code>) if there is no
	 * preference with the given key, or if the current value cannot be treated
	 * as a float.
	 * 
	 * @param key
	 *            the key of the preference
	 * 
	 * @return the float-valued preference
	 */
	public float getFloat(String key);

	/**
	 * Returns the float value represented by the string associated with the
	 * specified key in this preference node. The string is converted to an
	 * integer as by {@link Float#parseFloat(String)}. Returns the specified
	 * default if there is no value associated with the key, the backing store
	 * is inaccessible, or if <tt>Float.parseFloat(String)</tt> would throw a
	 * {@link NumberFormatException} if the associated value were passed. This
	 * method is intended for use in conjunction with {@link #putFloat}.
	 * 
	 * <p>
	 * If the implementation supports <i>stored defaults</i> and such a default
	 * exists, is accessible, and could be converted to a float with
	 * <tt>Float.parseFloat</tt>, this float is returned in preference to the
	 * specified default.
	 * 
	 * @param key
	 *            key whose associated value is to be returned as a float.
	 * @param def
	 *            the value to be returned in the event that this preference
	 *            node has no value associated with <tt>key</tt> or the
	 *            associated value cannot be interpreted as a float, or the
	 *            backing store is inaccessible.
	 * @return the float value represented by the string associated with
	 *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
	 *         associated value does not exist or cannot be interpreted as a
	 *         float.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @throws NullPointerException
	 *             if <tt>key</tt> is <tt>null</tt>.
	 * @see #putFloat(String,float)
	 * @see #get(String,String)
	 */
	public float getFloat(String key, float def);

	/**
	 * Returns the current value of the integer-valued preference with the given
	 * key. Returns the default-default value (<code>0</code>) if there is no
	 * preference with the given key, or if the current value cannot be treated
	 * as an integer.
	 * 
	 * @param key
	 *            the key of the preference
	 * 
	 * @return the int-valued preference
	 */
	public int getInt(String key);

	/**
	 * Returns the int value represented by the string associated with the
	 * specified key in this preference node. The string is converted to an
	 * integer as by {@link Integer#parseInt(String)}. Returns the specified
	 * default if there is no value associated with the key, the backing store
	 * is inaccessible, or if <tt>Integer.parseInt(String)</tt> would throw a
	 * {@link NumberFormatException} if the associated value were passed. This
	 * method is intended for use in conjunction with {@link #putInt}.
	 * 
	 * <p>
	 * If the implementation supports <i>stored defaults</i> and such a default
	 * exists, is accessible, and could be converted to an int with
	 * <tt>Integer.parseInt</tt>, this int is returned in preference to the
	 * specified default.
	 * 
	 * @param key
	 *            key whose associated value is to be returned as an int.
	 * @param def
	 *            the value to be returned in the event that this preference
	 *            node has no value associated with <tt>key</tt> or the
	 *            associated value cannot be interpreted as an int, or the
	 *            backing store is inaccessible.
	 * @return the int value represented by the string associated with
	 *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
	 *         associated value does not exist or cannot be interpreted as an
	 *         int.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @throws NullPointerException
	 *             if <tt>key</tt> is <tt>null</tt>.
	 * @see #putInt(String,int)
	 * @see #get(String,String)
	 */
	public int getInt(String key, int def);

	/**
	 * Returns the current value of the long-valued preference with the given
	 * key. Returns the default-default value (<code>0L</code>) if there is no
	 * preference with the given key, or if the current value cannot be treated
	 * as a long.
	 * 
	 * @param key
	 *            the key of the preference
	 * 
	 * @return the long-valued preference
	 */
	public long getLong(String key);

	/**
	 * Returns the long value represented by the string associated with the
	 * specified key in this preference node. The string is converted to a long
	 * as by {@link Long#parseLong(String)}. Returns the specified default if
	 * there is no value associated with the key, the backing store is
	 * inaccessible, or if <tt>Long.parseLong(String)</tt> would throw a
	 * {@link NumberFormatException} if the associated value were passed. This
	 * method is intended for use in conjunction with {@link #putLong}.
	 * 
	 * <p>
	 * If the implementation supports <i>stored defaults</i> and such a default
	 * exists, is accessible, and could be converted to a long with
	 * <tt>Long.parseLong</tt>, this long is returned in preference to the
	 * specified default.
	 * 
	 * @param key
	 *            key whose associated value is to be returned as a long.
	 * @param def
	 *            the value to be returned in the event that this preference
	 *            node has no value associated with <tt>key</tt> or the
	 *            associated value cannot be interpreted as a long, or the
	 *            backing store is inaccessible.
	 * @return the long value represented by the string associated with
	 *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
	 *         associated value does not exist or cannot be interpreted as a
	 *         long.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @throws NullPointerException
	 *             if <tt>key</tt> is <tt>null</tt>.
	 * @see #putLong(String,long)
	 * @see #get(String,String)
	 */
	public long getLong(String key, long def);

	public String getModifierString(String key);

	/**
	 * <code>true</code> if the preference has the requested modifier.
	 * <p>
	 * An implementation is free to support modifiers and define their
	 * respective semantics.
	 * <p>
	 * An example for a modifier may be "secret" to indicate that the preference
	 * value is encrypted.
	 * 
	 * @param modifier
	 *            The modifier.
	 * 
	 * @return <code>true</code> if the preference has the requested modifier.
	 * 
	 */
	public boolean hasModifier(String key, String modifier);

	/**
	 * Returns all of the keys that have an associated value in this preference
	 * node. (The returned array will be of size zero if this node has no
	 * preferences.)
	 * 
	 * <p>
	 * If the implementation supports <i>stored defaults</i> and there are any
	 * such defaults at this node that have not been overridden, by explicit
	 * preferences, the defaults are returned in the array in addition to any
	 * explicit preferences.
	 * 
	 * @return an array of the keys that have an associated value in this
	 *         preference node.
	 * @throws BackingStoreException
	 *             if this operation cannot be completed due to a failure in the
	 *             backing store, or inability to communicate with it.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 */
	public String[] keys() throws BackingStoreException;

	/**
	 * Returns this preference node's name, relative to its parent.
	 * 
	 * @return this preference node's name, relative to its parent.
	 */
	public abstract String name();

	/**
	 * Returns the named preference node in the same tree as this node, creating
	 * it and any of its ancestors if they do not already exist. Accepts a
	 * relative or absolute path name. Relative path names (which do not begin
	 * with the slash character <tt>('/')</tt>) are interpreted relative to this
	 * preference node.
	 * 
	 * <p>
	 * If the returned node did not exist prior to this call, this node and any
	 * ancestors that were created by this call are not guaranteed to become
	 * permanent until the <tt>flush</tt> method is called on the returned node
	 * (or one of its ancestors or descendants).
	 * 
	 * @param pathName
	 *            the path name of the preference node to return.
	 * @return the specified preference node.
	 * @throws IllegalArgumentException
	 *             if the path name is invalid (i.e., it contains multiple
	 *             consecutive slash characters, or ends with a slash character
	 *             and is more than one character long).
	 * @throws NullPointerException
	 *             if path name is <tt>null</tt>.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @see #flush()
	 */
	public abstract IPreferences node(String pathName);

	/**
	 * Returns true if the named preference node exists in the same tree as this
	 * node. Relative path names (which do not begin with the slash character
	 * <tt>('/')</tt>) are interpreted relative to this preference node.
	 * 
	 * <p>
	 * If this node (or an ancestor) has already been removed with the
	 * {@link #removeNode()} method, it <i>is</i> legal to invoke this method,
	 * but only with the path name <tt>""</tt>; the invocation will return
	 * <tt>false</tt>. Thus, the idiom <tt>p.nodeExists("")</tt> may be used to
	 * test whether <tt>p</tt> has been removed.
	 * 
	 * @param pathName
	 *            the path name of the node whose existence is to be checked.
	 * @return true if the specified node exists.
	 * @throws BackingStoreException
	 *             if this operation cannot be completed due to a failure in the
	 *             backing store, or inability to communicate with it.
	 * @throws IllegalArgumentException
	 *             if the path name is invalid (i.e., it contains multiple
	 *             consecutive slash characters, or ends with a slash character
	 *             and is more than one character long).
	 * @throws NullPointerException
	 *             if path name is <tt>null</tt>. s *
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method and <tt>pathName</tt> is not the
	 *             empty string (<tt>""</tt>).
	 */
	public abstract boolean nodeExists(String pathName)
			throws BackingStoreException;

	/**
	 * Returns the parent of this preference node, or <tt>null</tt> if this is
	 * the root.
	 * 
	 * @return the parent of this preference node.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 */
	public abstract IPreferences parent();

	/**
	 * A map of all property mappings in this {@link IPreferences} node.
	 * 
	 * @return A map of all property mappings in this {@link IPreferences} node.
	 */
	public Map<String, String> properties();

	/**
	 * Sets the current value of the boolean-valued preference with the given
	 * key.
	 * 
	 * @param key
	 *            the key of the preference
	 * @param value
	 *            the new current value of the preference
	 */
	public void put(String key, boolean value);

	/**
	 * Associates a string representing the specified byte array with the
	 * specified key in this preference node. The associated string is the
	 * <i>Base64</i> encoding of the byte array, as defined in <a
	 * href=http://www.ietf.org/rfc/rfc2045.txt>RFC 2045</a>, Section 6.8, with
	 * one minor change: the string will consist solely of characters from the
	 * <i>Base64 Alphabet</i>; it will not contain any newline characters. Note
	 * that the maximum length of the byte array is limited to three quarters of
	 * <tt>MAX_VALUE_LENGTH</tt> so that the length of the Base64 encoded String
	 * does not exceed <tt>MAX_VALUE_LENGTH</tt>. This method is intended for
	 * use in conjunction with {@link #getByteArray}.
	 * 
	 * @param key
	 *            key with which the string form of value is to be associated.
	 * @param value
	 *            value whose string form is to be associated with key.
	 * @throws NullPointerException
	 *             if key or value is <tt>null</tt>.
	 * @throws IllegalArgumentException
	 *             if key.length() exceeds MAX_KEY_LENGTH or if value.length
	 *             exceeds MAX_VALUE_LENGTH*3/4.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @see #getByteArray(String,byte[])
	 * @see #get(String,String)
	 */
	public void put(String key, byte[] value);

	/**
	 * Sets the current value of the double-valued preference with the given
	 * key.
	 * 
	 * @param key
	 *            the key of the preference
	 * @param value
	 *            the new current value of the preference
	 */
	public void put(String key, double value);

	/**
	 * Sets the current value of the float-valued preference with the given key.
	 * 
	 * @param key
	 *            the key of the preference
	 * @param value
	 *            the new current value of the preference
	 */
	public void put(String key, float value);

	/**
	 * Sets the current value of the integer-valued preference with the given
	 * key.
	 * 
	 * @param key
	 *            the key of the preference
	 * @param value
	 *            the new current value of the preference
	 */
	public void put(String key, int value);

	/**
	 * Sets the current value of the long-valued preference with the given key.
	 * 
	 * @param key
	 *            the key of the preference
	 * @param value
	 *            the new current value of the preference
	 */
	public void put(String key, long value);

	/**
	 * Sets the current value of the string-valued preference with the given
	 * key.
	 * 
	 * @param key
	 *            the key of the preference
	 * @param value
	 *            the new current value of the preference
	 */
	public void put(String key, String value);

	/**
	 * Set the current value. This is for drop in compatibility to Java platform
	 * preferences.
	 * 
	 * @param key
	 * @param value
	 */
	public void putBoolean(String key, boolean value);

	/**
	 * Set the current value. This is for drop in compatibility to Java platform
	 * preferences.
	 * 
	 * @param key
	 * @param value
	 */
	public void putByteArray(String key, byte[] value);

	/**
	 * Set the current value. This is for drop in compatibility to Java platform
	 * preferences.
	 * 
	 * @param key
	 * @param value
	 */
	public void putDouble(String key, double value);

	/**
	 * Set the current value. This is for drop in compatibility to Java platform
	 * preferences.
	 * 
	 * @param key
	 * @param value
	 */
	public void putFloat(String key, float value);

	/**
	 * Set the current value. This is for drop in compatibility to Java platform
	 * preferences.
	 * 
	 * @param key
	 * @param value
	 */
	public void putInt(String key, int value);

	/**
	 * Set the current value. This is for drop in compatibility to Java platform
	 * preferences.
	 * 
	 * @param key
	 * @param value
	 */
	public void putLong(String key, long value);

	/**
	 * Removes the value associated with the specified key in this preference
	 * node, if any.
	 * 
	 * <p>
	 * If this implementation supports <i>stored defaults</i>, and there is such
	 * a default for the specified preference, the stored default will be
	 * "exposed" by this call, in the sense that it will be returned by a
	 * succeeding call to <tt>get</tt>.
	 * 
	 * @param key
	 *            key whose mapping is to be removed from the preference node.
	 * @throws NullPointerException
	 *             if <tt>key</tt> is <tt>null</tt>.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 */
	public void remove(String key);

	/**
	 * Removes this preference node and all of its descendants, invalidating any
	 * preferences contained in the removed nodes. Once a node has been removed,
	 * attempting any method other than {@link #name()}, {@link #absolutePath()}
	 * , {@link #flush()} or {@link #node(String) nodeExists("")} on the
	 * corresponding <tt>Preferences</tt> instance will fail with an
	 * <tt>IllegalStateException</tt>. (The methods defined on {@link Object}
	 * can still be invoked on a node after it has been removed; they will not
	 * throw <tt>IllegalStateException</tt>.)
	 * 
	 * <p>
	 * The removal is not guaranteed to be persistent until the <tt>flush</tt>
	 * method is called on this node (or an ancestor).
	 * 
	 * <p>
	 * If this implementation supports <i>stored defaults</i>, removing a node
	 * exposes any stored defaults at or below this node. Thus, a subsequent
	 * call to <tt>nodeExists</tt> on this node's path name may return
	 * <tt>true</tt>, and a subsequent call to <tt>node</tt> on this path name
	 * may may return a (different) <tt>Preferences</tt> instance representing a
	 * non-empty collection of preferences and/or children.
	 * 
	 * @throws BackingStoreException
	 *             if this operation cannot be completed due to a failure in the
	 *             backing store, or inability to communicate with it.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has already been removed with
	 *             the {@link #removeNode()} method.
	 * @throws UnsupportedOperationException
	 *             if this method is invoked on the root node.
	 * @see #flush()
	 */
	public void removeNode() throws BackingStoreException;

	/**
	 * Create a new IPreferences object whose scopes include all scopes from the
	 * receiver before and including "scopeName".
	 * 
	 * @param scopeName
	 * @return a new IPreferences object whose scopes include all scopes from
	 *         the receiver before and including "scopeName".
	 */
	public IPreferences restrict(String scopeName);

	public void setModifierString(String key, String modifiers);

	/**
	 * Ensures that future reads from this preference node and its descendants
	 * reflect any changes that were committed to the persistent store (from any
	 * VM) prior to the <tt>sync</tt> invocation. As a side-effect, forces any
	 * changes in the contents of this preference node and its descendants to
	 * the persistent store, as if the <tt>flush</tt> method had been invoked on
	 * this node.
	 * 
	 * @throws BackingStoreException
	 *             if this operation cannot be completed due to a failure in the
	 *             backing store, or inability to communicate with it.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @see #flush()
	 */
	public void sync() throws BackingStoreException;

}
