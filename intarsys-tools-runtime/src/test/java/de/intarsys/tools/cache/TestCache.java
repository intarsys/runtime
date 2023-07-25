/*
 * Copyright (c) 2008, intarsys GmbH
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
/*
 * Copyright (c) 2003, intarsys GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of intarsys nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
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
package de.intarsys.tools.cache;

import de.intarsys.tools.component.ISynchronizable;
import junit.framework.TestCase;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public class TestCache extends TestCase {

	protected static class SynchedObject implements ISynchronizable {
		private boolean outOfSynch = true;

		public SynchedObject(boolean outOfSynch) {
			this.outOfSynch = outOfSynch;
		}

		@Override
		public boolean isOutOfSynch() {
			return outOfSynch;
		}

		public boolean isSynchSynchronous() {
			return true;
		}

		@Override
		public void synch() {
			//
		}
	}

	public TestCache() {
		super();
	}

	public TestCache(String name) {
		super(name);
	}

	public void testRemove() {
		Cache cache;
		Object result;

		// empty remove
		cache = new Cache(3);
		cache.remove("test"); //$NON-NLS-1$
		assertEquals(0, cache.size());
		// element reads
		cache = new Cache(3);
		cache.put("1", "test1"); //$NON-NLS-1$ //$NON-NLS-2$
		result = cache.get("1"); //$NON-NLS-1$
		assertEquals("test1", result); //$NON-NLS-1$
		cache.remove("1"); //$NON-NLS-1$
		result = cache.get("1"); //$NON-NLS-1$
		assertEquals(null, result);
		assertEquals(0, cache.size());
	}

	public void testSimple() {
		Cache cache;
		Object result;

		// empty read
		cache = new Cache(3);
		assertEquals(0, cache.size());
		result = cache.get("1"); //$NON-NLS-1$
		assertEquals(null, result);
		// element reads
		cache = new Cache(3);
		cache.put("1", "test1"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(1, cache.size());
		result = cache.get("1"); //$NON-NLS-1$
		assertEquals("test1", result); //$NON-NLS-1$
		// full read
		cache = new Cache(3);
		cache.put("1", "test1"); //$NON-NLS-1$ //$NON-NLS-2$
		cache.put("2", "test2"); //$NON-NLS-1$ //$NON-NLS-2$
		cache.put("3", "test3"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(3, cache.size());
		result = cache.get("1"); //$NON-NLS-1$
		assertEquals("test1", result); //$NON-NLS-1$
		result = cache.get("2"); //$NON-NLS-1$
		assertEquals("test2", result); //$NON-NLS-1$
		result = cache.get("3"); //$NON-NLS-1$
		assertEquals("test3", result); //$NON-NLS-1$
		// overflow read
		cache = new Cache(3);
		cache.put("1", "test1"); //$NON-NLS-1$ //$NON-NLS-2$
		cache.put("2", "test2"); //$NON-NLS-1$ //$NON-NLS-2$
		cache.put("3", "test3"); //$NON-NLS-1$ //$NON-NLS-2$
		cache.put("4", "test4"); //$NON-NLS-1$ //$NON-NLS-2$
		cache.put("5", "test5"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(3, cache.size());
		result = cache.get("1"); //$NON-NLS-1$
		assertEquals(null, result);
		result = cache.get("2"); //$NON-NLS-1$
		assertEquals(null, result);
		result = cache.get("3"); //$NON-NLS-1$
		assertEquals("test3", result); //$NON-NLS-1$
		result = cache.get("4"); //$NON-NLS-1$
		assertEquals("test4", result); //$NON-NLS-1$
		result = cache.get("5"); //$NON-NLS-1$
		assertEquals("test5", result); //$NON-NLS-1$
		// overflow read
		cache = new Cache(3);
		cache.put("1", "test1"); //$NON-NLS-1$ //$NON-NLS-2$
		cache.put("2", "test2"); //$NON-NLS-1$ //$NON-NLS-2$
		cache.put("3", "test3"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(3, cache.size());
		result = cache.get("1"); //$NON-NLS-1$
		assertEquals("test1", result); //$NON-NLS-1$
		cache.put("4", "test4"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(3, cache.size());
		result = cache.get("3"); //$NON-NLS-1$
		assertEquals("test3", result); //$NON-NLS-1$
		cache.put("5", "test5"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(3, cache.size());
		result = cache.get("1"); //$NON-NLS-1$
		assertEquals(null, result);
		result = cache.get("2"); //$NON-NLS-1$
		assertEquals(null, result);
		result = cache.get("3"); //$NON-NLS-1$
		assertEquals("test3", result); //$NON-NLS-1$
		result = cache.get("4"); //$NON-NLS-1$
		assertEquals("test4", result); //$NON-NLS-1$
		result = cache.get("5"); //$NON-NLS-1$
		assertEquals("test5", result); //$NON-NLS-1$
		//
		cache.clear();
	}

	public void testSynchronizable() {
		Cache cache;
		Object result;
		Object value1 = new SynchedObject(false);
		Object value2 = new SynchedObject(true);

		// read synched
		cache = new Cache(3);
		cache.put("1", value1); //$NON-NLS-1$
		assertEquals(1, cache.size());
		result = cache.get("1"); //$NON-NLS-1$
		assertSame(value1, result);
		// read out of synch
		cache = new Cache(3);
		cache.put("1", value1); //$NON-NLS-1$
		assertEquals(1, cache.size());
		result = cache.get("1"); //$NON-NLS-1$
		assertSame(value1, result);
		cache.put("2", value2); //$NON-NLS-1$
		assertEquals(2, cache.size());
		result = cache.get("2"); //$NON-NLS-1$
		assertNull(result);
		assertEquals(1, cache.size());
	}
}
