/**
 * Copyright (c) 1996-2004 Borland Software Corp. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */
//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation.  All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dbswing;

import java.util.Hashtable;
import java.util.Vector;

/**
 * <p>Used by <code>JdbTable</code> to cache images stored in
 * <code>com.borland.dx.dataset.Variant.INPUTSTREAM</code> fields.
 * Don't deploy this class unless your <code>DataSet</code> contains
 * images and displays them using a dbSwing component.</p>
 *
 * <p><code>LRUCache</code> is a resizable,"least-recently-used" (LRU)
 * general-purpose cache. An LRUCache holds a fixed number of items (25
 * by default). When the cache is full, adding a new item causes the
 * "oldest" item in the cache to be removed and the new item to become
 * the "newest" item. If an item already in the cache is added to a
 * full cache or if an item is fetched from the cache, that item's
 * "age" is updated so that it becomes the "newest" item in the cache.</p>
 *
 * @see JdbTable
 */
public class LRUCache implements java.io.Serializable
{
  private transient Hashtable map = new Hashtable(); // do not Serialize
  private transient Vector queue = new Vector(); // do not Serialize
  private int maxItems = 25;

  /**
   * <p>Constructs an <code>LRUCache</code> compnent with space for a maximum of 25 cached items.</p>
   */
  public LRUCache() {
  }

  /**
   * <p>Constructs an <code>LRUCache</code> component with space for the specified maximum number of cached items.</p>
   * @param maxItems The maximum number of cached items.
   */
  public LRUCache(int maxItems) {
    if (maxItems <= 0) {
      throw new IllegalArgumentException();
    }
    this.maxItems = maxItems;
  }

  /**
   * <p>Empties the cache.</p>
   */
  public void emptyCache() {
    map.clear();
    queue.removeAllElements();
  }

  /**
   * <p>Sets the maximum number of items to hold in the cache.</p>
   *
   * @param newMaxItems The maximum number of items to hold in the cache.
   * @see #getMaxItems
   */
  public void setMaxItems(int newMaxItems) {
    if (newMaxItems <= 0) {
      throw new IllegalArgumentException();
    }
    int size;
    while ((size = queue.size()) > newMaxItems) {
      Object lastKey = queue.elementAt(size-1);
      queue.removeElementAt(size-1);
      map.remove(lastKey);
    }
    this.maxItems = newMaxItems;
  }

  /**
   * <p>Returns the maximum number of items held in the cache.</p>
   *
   * @return The maximum number of items to hold in the cache.
   * @see #setMaxItems
   */
  public int getMaxItems() {
    return maxItems;
  }

  /**
   * <p>Puts an item, retrievable by the specified key, into the cache.
   * Keys and items should not be <code>null</code>.</p>
   *
   * @param key The key by which to retrieve the item.
   * @param item The item to put into the cache.
   */
  public void put(Object key, Object item) {
    // throw away tail key if cache is too full
    int size;
    if ((size = queue.size()) >= maxItems) {
      Object lastKey = queue.elementAt(size-1);
      queue.removeElementAt(size-1);
      if (lastKey != null) {
        map.remove(lastKey);
      }
    }
    map.put(key, item);
    queue.insertElementAt(key, 0);
  }

  /**
   * <p>Returns the item in the cache identified by the specified
   * (non-null) key. Returns <code>null</code> if the key cannot be
   * found in the cache.</p>
   *
   * @param key The key by which to retrieve the item.
   * @return The item in the cache.
   */
  public Object get(Object key) {
    Object item = map.get(key);
    if (item != null) {
      // every access moves key to head to maintain LRU
      queue.removeElement(key);
      queue.insertElementAt(key, 0);
    }
    return item;
  }
}
