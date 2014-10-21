/*
 * Copyright 2014 Higher Frequency Trading http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.map.jrs166.map;

import net.openhft.chronicle.common.ClosableHolder;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.openhft.chronicle.map.TcpReplicationConfig;
import net.openhft.chronicle.map.jrs166.JSR166TestCase;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

import static net.openhft.chronicle.common.StatelessBuilder.remoteAddress;
import static org.junit.Assert.*;

/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 * Other contributors include Andrew Wright, Jeffrey Hayes,
 * Pat Fisher, Mike Judd.
 */


public class StatelessChronicleMapTest extends JSR166TestCase {

    static int i = 8076;

    static ChronicleMap<Integer, String> newShmIntString(int size) throws IOException {
        i++;

        final ChronicleMap<Integer, String> serverMap = ChronicleMapBuilder.of(Integer.class, String.class)
                .replicators((byte) 1, TcpReplicationConfig.of(i)).create();

        final ChronicleMap<Integer, String> statelessMap = ChronicleMapBuilder.of(Integer
                .class, String.class)
                .stateless(remoteAddress(new InetSocketAddress("localhost", i))).create();

        ((ClosableHolder) statelessMap).addCloseable(serverMap);

        return statelessMap;

    }


    static ChronicleMap<CharSequence, CharSequence> newShmStringString(int size) throws IOException {
        i++;
        System.out.println(i);
        final ChronicleMap<CharSequence, CharSequence> serverMap = ChronicleMapBuilder.of(CharSequence.class, CharSequence.class)
                .replicators((byte) 2, TcpReplicationConfig.of(i)).create();
/*

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/

        final ChronicleMap<CharSequence, CharSequence> statelessMap = ChronicleMapBuilder.of(CharSequence
                .class, CharSequence.class)
                .stateless(remoteAddress(new InetSocketAddress("localhost", i))).create();

        ((ClosableHolder) statelessMap).addCloseable(serverMap);

        return statelessMap;
    }


    static ChronicleMap<Integer, CharSequence> newShmIntString() throws IOException {
        i++;
        System.out.println(i);
        final ChronicleMap<Integer, CharSequence> serverMap = ChronicleMapBuilder.of(Integer.class, CharSequence.class)
                .replicators((byte) 3, TcpReplicationConfig.of(i)).create();

        final ChronicleMap<Integer, CharSequence> statelessMap = ChronicleMapBuilder.of(Integer
                .class, CharSequence.class)
                .stateless(remoteAddress(new InetSocketAddress("localhost", i))).create();

        ((ClosableHolder) statelessMap).addCloseable(serverMap);

        return statelessMap;

    }


    /**
     * Returns a new map from Integers 1-5 to Strings "A"-"E".
     */
    private static ChronicleMap map5() throws IOException {
        ChronicleMap<Integer, String> map = newShmIntString(5);

        assertTrue(map.isEmpty());
        map.put(one, "A");
        map.put(two, "B");
        map.put(three, "C");
        map.put(four, "D");
        map.put(five, "E");
        assertFalse(map.isEmpty());
        assertEquals(5, map.size());
        return map;
    }

    /**
     * clear removes all pairs
     */
    @Test
    public void testClear() throws IOException {
        ChronicleMap map = map5();
        map.clear();
        assertEquals(0, map.size());
        map.close();
    }


    /**
     * contains returns true for contained value
     */
    @Test
    public void testContains() throws IOException {
        ChronicleMap map = map5();
        assertTrue(map.containsValue("A"));
        assertFalse(map.containsValue("Z"));
        map.close();
    }

    /**
     * containsKey returns true for contained key
     */
    @Test
    public void testContainsKey() throws IOException {
        ChronicleMap map = map5();
        assertTrue(map.containsKey(one));
        assertFalse(map.containsKey(zero));
        map.close();
    }

    /**
     * containsValue returns true for held values
     */
    @Test
    public void testContainsValue() throws IOException {
        ChronicleMap map = map5();
        assertTrue(map.containsValue("A"));
        assertFalse(map.containsValue("Z"));
        map.close();
    }


    /**
     * Inserted elements that are subclasses of the same Comparable
     * class are found.
     */
   /* @Test public void testComparableFamily() throws IOException {
        ChronicleMap<BI, Boolean> m =
                newShmBiBoolean();
        for (int i = 0; i < 1000; i++) {
            assertTrue(m.put(new CI(i), true) == null);
        }
        for (int i = 0; i < 1000; i++) {
            assertTrue(m.containsKey(new CI(i)));
            assertTrue(m.containsKey(new DI(i)));
        }
    }*/

    /**
     * TODO :     fix test
     * Elements of classes with erased generic type parameters based
     * on Comparable can be inserted and found.
     */
   /* @Test public void testGenericComparable() throws IOException {
        ChronicleMap<Object, Boolean> m =
                newShmBiBoolean();
        for (int i = 0; i < 1000; i++) {
            BI bi = new BI(i);
            BS bs = new BS(String.valueOf(i));
            LexicographicList<BI> bis = new LexicographicList<BI>(bi);
            LexicographicList<BS> bss = new LexicographicList<BS>(bs);
            assertTrue(m.putIfAbsent(bis, true) == null);
            assertTrue(m.containsKey(bis));
            if (m.putIfAbsent(bss, true) == null)
                assertTrue(m.containsKey(bss));
            assertTrue(m.containsKey(bis));
        }
        for (int i = 0; i < 1000; i++) {
            assertTrue(m.containsKey(new ArrayList(Collections.singleton(new BI(i)))));
        }
    }
*/

    /**
     * Elements of non-comparable classes equal to those of classes
     * with erased generic type parameters based on Comparable can be
     * inserted and found.
     */
  /*  @Test public void testGenericComparable2() throws IOException {
        ChronicleMap<Object, Boolean> m =
                newShmListBoolean();
        for (int i = 0; i < 1000; i++) {
            m.put(new ArrayList(Collections.singleton(new BI(i))), true);
        }

        for (int i = 0; i < 1000; i++) {
            LexicographicList<BI> bis = new LexicographicList<BI>(new BI(i));
            assertTrue(m.containsKey(bis));
        }
    }
*/

    /**
     * get returns the correct element at the given key, or null if not present
     */
    @Test
    public void testGet() throws IOException {
        ChronicleMap map = map5();
        assertEquals("A", (String) map.get(one));
        ChronicleMap empty = newShmIntString();
        assertNull(map.get(notPresent));
        map.close();
    }

    /**
     * isEmpty is true of empty map and false for non-empty
     */
    @Test
    public void testIsEmpty() throws IOException {
        ChronicleMap empty = newShmIntString();
        ChronicleMap map = map5();
        assertTrue(empty.isEmpty());
        assertFalse(map.isEmpty());
        map.close();
        empty.close();
    }

    /**
     * keySet returns a Set containing all the keys
     */
    @Test
    public void testKeySet() throws IOException {
        ChronicleMap map = map5();
        Set s = map.keySet();
        assertEquals(5, s.size());
        assertTrue(s.contains(one));
        assertTrue(s.contains(two));
        assertTrue(s.contains(three));
        assertTrue(s.contains(four));
        assertTrue(s.contains(five));
        map.close();
    }

    /**
     * keySet.toArray returns contains all keys
     */
    @Test
    public void testKeySetToArray() throws IOException {
        ChronicleMap map = map5();
        Set s = map.keySet();
        Object[] ar = s.toArray();
        assertTrue(s.containsAll(Arrays.asList(ar)));
        assertEquals(5, ar.length);
        ar[0] = m10;
        assertFalse(s.containsAll(Arrays.asList(ar)));
        map.close();
    }

    /**
     * Values.toArray contains all values
     */
    @Test
    public void testValuesToArray() throws IOException {
        ChronicleMap map = map5();
        Collection v = map.values();
        Object[] ar = v.toArray();
        ArrayList s = new ArrayList(Arrays.asList(ar));
        assertEquals(5, ar.length);
        assertTrue(s.contains("A"));
        assertTrue(s.contains("B"));
        assertTrue(s.contains("C"));
        assertTrue(s.contains("D"));
        assertTrue(s.contains("E"));
        map.close();
    }

    /**
     * TODO : enumeration returns an enumeration containing the correct
     * elements
     */
  /*  @Test public void testEnumeration() throws IOException {
        ChronicleMap map = map5();
        Enumeration e = map.elements();
        int count = 0;
        while (e.hasMoreElements()) {
            count++;
            e.nextElement();
        }
        assertEquals(5, count);
    }*/

    /**
     * entrySet.toArray contains all entries
     */
    @Test
    public void testEntrySetToArray() throws IOException {
        ChronicleMap map = map5();
        Set s = map.entrySet();
        Object[] ar = s.toArray();
        assertEquals(5, ar.length);
        for (int i = 0; i < 5; ++i) {
            assertTrue(map.containsKey(((Map.Entry) (ar[i])).getKey()));
            assertTrue(map.containsValue(((Map.Entry) (ar[i])).getValue()));
        }
        map.close();
    }

    /**
     * values collection contains all values
     */
    @Test
    public void testValues() throws IOException {
        ChronicleMap map = map5();
        Collection s = map.values();
        assertEquals(5, s.size());
        assertTrue(s.contains("A"));
        assertTrue(s.contains("B"));
        assertTrue(s.contains("C"));
        assertTrue(s.contains("D"));
        assertTrue(s.contains("E"));
        map.close();
    }

    /**
     * TODO : keys returns an enumeration containing all the keys from the map
     */
   /* @Test public void testKeys() throws IOException {
        ChronicleMap map = map5();
        Enumeration e = map.keys();
        int count = 0;
        while (e.hasMoreElements()) {
            count++;
            e.nextElement();
        }
        assertEquals(5, count);
    }*/

    /**
     * entrySet contains all pairs
     */
    @Test
    public void testEntrySet() throws IOException {
        ChronicleMap map = map5();
        Set s = map.entrySet();
        assertEquals(5, s.size());
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            assertTrue(
                    (e.getKey().equals(one) && e.getValue().equals("A")) ||
                            (e.getKey().equals(two) && e.getValue().equals("B")) ||
                            (e.getKey().equals(three) && e.getValue().equals("C")) ||
                            (e.getKey().equals(four) && e.getValue().equals("D")) ||
                            (e.getKey().equals(five) && e.getValue().equals("E"))
            );
        }
        map.close();
    }

    /**
     * putAll adds all key-value pairs from the given map
     */
    @Test
    public void testPutAll() throws IOException {
        ChronicleMap empty = newShmIntString();
        ChronicleMap map = map5();
        empty.putAll(map);
        assertEquals(5, empty.size());
        assertTrue(empty.containsKey(one));
        assertTrue(empty.containsKey(two));
        assertTrue(empty.containsKey(three));
        assertTrue(empty.containsKey(four));
        assertTrue(empty.containsKey(five));
        map.close();
        empty.close();
    }

    /**
     * putIfAbsent works when the given key is not present
     */
    @Test
    public void testPutIfAbsent() throws IOException {
        ChronicleMap map = map5();
        map.putIfAbsent(six, "Z");
        assertTrue(map.containsKey(six));
        map.close();
    }

    /**
     * putIfAbsent does not add the pair if the key is already present
     */
    @Test
    public void testPutIfAbsent2() throws IOException {
        ChronicleMap map = map5();
        assertEquals("A", map.putIfAbsent(one, "Z"));
        map.close();
    }

    /**
     * replace fails when the given key is not present
     */
    @Test
    public void testReplace() throws IOException {
        ChronicleMap map = map5();
        assertNull(map.replace(six, "Z"));
        assertFalse(map.containsKey(six));
        map.close();
    }

    /**
     * replace succeeds if the key is already present
     */
    @Test
    public void testReplace2() throws IOException {
        ChronicleMap map = map5();
        assertNotNull(map.replace(one, "Z"));
        assertEquals("Z", map.get(one));
        map.close();
    }

    /**
     * replace value fails when the given key not mapped to expected value
     */
    @Test
    public void testReplaceValue() throws IOException {
        ChronicleMap map = map5();
        assertEquals("A", map.get(one));
        assertFalse(map.replace(one, "Z", "Z"));
        assertEquals("A", map.get(one));
        map.close();
    }

    /**
     * replace value succeeds when the given key mapped to expected value
     */
    @Test
    public void testReplaceValue2() throws IOException {
        ChronicleMap map = map5();
        assertEquals("A", map.get(one));
        assertTrue(map.replace(one, "A", "Z"));
        assertEquals("Z", map.get(one));
        map.close();
    }

    /**
     * remove removes the correct key-value pair from the map
     */
    @Test
    public void testRemove() throws IOException {
        ChronicleMap map = map5();
        map.remove(five);
        assertEquals(4, map.size());
        assertFalse(map.containsKey(five));
        map.close();
    }

    /**
     * remove(key,value) removes only if pair present
     */
    @Test
    public void testRemove2() throws IOException {
        ChronicleMap map = map5();
//        map.remove(five, "E");
     /*   assertEquals(4, map.size());
        assertFalse(map.containsKey(five));
        map.remove(four, "A");
        assertEquals(4, map.size());
        assertTrue(map.containsKey(four));*/
        map.close();
    }

    /**
     * size returns the correct values
     */
    @Test
    public void testSize() throws IOException {
        ChronicleMap map = map5();
        ChronicleMap empty = newShmIntString();
        assertEquals(0, empty.size());
        assertEquals(5, map.size());
        map.close();
    }

    /**
     * toString contains toString of elements
     */
    @Test
    public void testToString() throws IOException {
        ChronicleMap map = map5();
        String s = map.toString();
        for (int i = 1; i <= 5; ++i) {
            assertTrue(s.contains(String.valueOf(i)));
        }
        map.close();
    }

    /**
     * get(null) throws NPE
     */
    @Test
    public void testGet_NullPointerException() throws IOException {

        ChronicleMap c = null;
        try {
            c = newShmIntString(5);
            c.get(null);
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }

    }

    /**
     * containsKey(null) throws NPE
     */
    @Test
    public void testContainsKey_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmIntString(5);
            c.containsKey(null);
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }
    }

    /**
     * put(null,x) throws NPE
     */
    @Test
    public void testPut1_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmIntString(5);
            c.put(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }
    }

    /**
     * put(x, null) throws NPE
     */
    @Test
    public void testPut2_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmIntString(5);
            c.put(notPresent, null);
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }
    }

    /**
     * putIfAbsent(null, x) throws NPE
     */
    @Test
    public void testPutIfAbsent1_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmIntString(5);
            c.putIfAbsent(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }
    }

    // Exception tests

    /**
     * TODO : Cannot create with negative capacity
     */
   /* @Test public void testConstructor1() {
        try {
            newShmIntString(-1, 0, 1);
            shouldThrow();
        } catch (IllegalArgumentException success) {
        }
    }*/

    /**
     * TODO : Cannot create with negative concurrency level
     */
    /*@Test public void testConstructor2() {
        try {
            newShmIntString(1, 0, -1);
            shouldThrow();
        } catch (IllegalArgumentException success) {
        }
    }*/

    /**
     * TODO :Cannot create with only negative capacity
     */
   /* @Test public void testConstructor3() {
        try {
            newShmIntString(-1);
            shouldThrow();
        } catch (IllegalArgumentException success) {
        }
    }*/

    /**
     * replace(null, x) throws NPE
     */
    @Test
    public void testReplace_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmIntString(5);
            c.replace(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }
    }

    /**
     * replace(null, x, y) throws NPE
     */
    @Test
    public void testReplaceValue_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmIntString(5);
            c.replace(null, "A", "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }
    }

    /**
     * containsValue(null) throws NPE
     */
/*    @Test public void testContainsValue_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString(5);
            c.containsValue(null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }*/

    /**
     * todo  : contains(null) throws NPE
     */
/*
    @Test public void testContains_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString(5);
            c.contains(null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }
*/

    /**
     * putIfAbsent(x, null) throws NPE
     */
    @Test
    public void testPutIfAbsent2_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmIntString(5);
            c.putIfAbsent(notPresent, null);
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }
    }

    /**
     * replace(x, null) throws NPE
     */
    @Test
    public void testReplace2_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmIntString(5);
            c.replace(notPresent, null);
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }
    }

    /**
     * replace(x, null, y) throws NPE
     */
    @Test
    public void testReplaceValue2_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmIntString(5);
            c.replace(notPresent, null, "A");
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }
    }

    /**
     * replace(x, y, null) throws NPE
     */
    @Test
    public void testReplaceValue3_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmIntString(5);
            c.replace(notPresent, "A", null);
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }
    }

    /**
     * remove(null) throws NPE
     */
    @Test
    public void testRemove1_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmStringString(5);
            c.put("sadsdf", "asdads");
            c.remove(null);
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            if (c != null)
                c.close();
        }
    }

    /**
     * remove(null, x) throws NPE
     */
    @Test
    public void testRemove2_NullPointerException() throws IOException {
        ChronicleMap c = null;
        try {
            c = newShmStringString(5);
            c.put("sadsdf", "asdads");
            c.remove(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        } finally {
            c.close();
        }
    }

    /**
     * remove(x, null) returns false
     */
    @Test
    public void testRemove3() throws IOException {
        ChronicleMap c = newShmStringString(5);
        c.put("sadsdf", "asdads");
        assertFalse(c.remove("sadsdf", null));
    }

    // classes for testing Comparable fallbacks
    static class BI implements Comparable<BI> {
        private final int value;

        BI(int value) {
            this.value = value;
        }

        public int compareTo(BI other) {
            return Integer.compare(value, other.value);
        }

        public boolean equals(Object x) {
            return (x instanceof BI) && ((BI) x).value == value;
        }

        public int hashCode() {
            return 42;
        }
    }

    static class CI extends BI {
        CI(int value) {
            super(value);
        }
    }

    static class DI extends BI {
        DI(int value) {
            super(value);
        }
    }

    static class BS implements Comparable<BS> {
        private final String value;

        BS(String value) {
            this.value = value;
        }

        public int compareTo(BS other) {
            return value.compareTo(other.value);
        }

        public boolean equals(Object x) {
            return (x instanceof BS) && value.equals(((BS) x).value);
        }

        public int hashCode() {
            return 42;
        }
    }

    static class LexicographicList<E extends Comparable<E>> extends ArrayList<E>
            implements Comparable<LexicographicList<E>> {
        private static final long serialVersionUID = 0;
        static long total;
        static long n;

        LexicographicList(Collection<E> c) {
            super(c);
        }

        LexicographicList(E e) {
            super(Collections.singleton(e));
        }

        public int compareTo(LexicographicList<E> other) {
            long start = System.currentTimeMillis();
            int common = Math.min(size(), other.size());
            int r = 0;
            for (int i = 0; i < common; i++) {
                if ((r = get(i).compareTo(other.get(i))) != 0)
                    break;
            }
            if (r == 0)
                r = Integer.compare(size(), other.size());
            total += System.currentTimeMillis() - start;
            n++;
            return r;
        }
    }

    /**
     * A deserialized map equals original
     */
  /*  @Test public void testSerialization() throws Exception {
        Map x = map5();
        Map y = serialClone(x);

        assertNotSame(x, y);
        assertEquals(x.size(), y.size());
        assertEquals(x, y);
        assertEquals(y, x);
    }*/

    /**
     * TODO : SetValue of an EntrySet entry sets value in the map.
     */
    /*@Test public void testSetValueWriteThrough() {
        // Adapted from a bug report by Eric Zoerner
        ChronicleMap map = newShmIntString(2, 5.0f, 1);
        assertTrue(map.isEmpty());
        for (int i = 0; i < 20; i++)
            map.put(new Integer(i), new Integer(i));
        assertFalse(map.isEmpty());
        Map.Entry entry1 = (Map.Entry) map.entrySet().iterator().next();
        // Unless it happens to be first (in which case remainder of
        // test is skipped), remove a possibly-colliding key from map
        // which, under some implementations, may cause entry1 to be
        // cloned in map
        if (!entry1.getKey().equals(new Integer(16))) {
            map.remove(new Integer(16));
            entry1.setValue("XYZ");
            assertTrue(map.containsValue("XYZ")); // fails if write-through broken
        }
    }*/

}

