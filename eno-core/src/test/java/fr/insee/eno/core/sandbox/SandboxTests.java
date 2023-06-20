package fr.insee.eno.core.sandbox;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.navigation.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import reusable33.AbstractIdentifiableType;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SandboxTests {

    static abstract class FooAbstract {
        @DDI(contextType = AbstractIdentifiableType.class, field = "hello")
        int a;
        public void setA(int a) {
            this.a = a;
        }
    }

    static class FooChild extends FooAbstract {
        int c;
        public void setC(int c) {
            this.c = c;
        }
    }

    @Test
    void propertyInheritanceWithAbstractClass() {
        //
        FooChild foo = new FooChild();
        foo.setA(1);
        foo.setC(7);

        //
        BeanWrapper beanWrapper = new BeanWrapperImpl(foo);

        // Count properties in the class
        long propertyCount = Arrays.stream(beanWrapper.getPropertyDescriptors())
                .filter(propertyDescriptor -> !propertyDescriptor.getName().equals("class"))
                .count();
        assertEquals(2L, propertyCount);

        // Get superclass
        assertEquals(FooAbstract.class, foo.getClass().getSuperclass());

        // Get property inherited from parent
        TypeDescriptor aTypeDescriptor = beanWrapper.getPropertyTypeDescriptor("a");
        assertNotNull(aTypeDescriptor);
        DDI ddiAnnotation = aTypeDescriptor.getAnnotation(DDI.class);
        assertNotNull(ddiAnnotation);
        assertEquals("hello", ddiAnnotation.field());
    }


    @Test
    void testIfListsHaveIntersection() {
        List<Integer> list = List.of(1,2);
        assertFalse(list.stream().noneMatch(List.of(2,3,4)::contains));
    }

    @Test
    void addElementWithIndexInArrayList() {
        List<String> fooList = new ArrayList<>(); // this creates a list with a capacity of 10.
        assertNotNull(fooList);
        assertThrows(IndexOutOfBoundsException.class, () -> fooList.add(12, "foo"));
        assertDoesNotThrow(() -> {
            for (int i=0; i<12; i++) {
                fooList.add("foo");
            }
        });
        /*
        Here in Eno: Lunatic-Model creates array lists of size 10 and doesn't allow a setter te replace these lists.
        So adding elements in a precise position in Lunatic lists is not safe if the index might be >= 10.
         */
    }

    @Test
    void toStringOverride() {
        Filter filter = new Filter();
        filter.setId("foo");
        assertEquals("Filter[id=foo]", filter.toString());
    }

    private double d1;
    private Double d2;
    @Test
    void doubleDefaultValue() {
        assertEquals(0d, d1);
        assertNull(d2);
    }

    @Test
    void continueKeyword() {
        List<Integer> result = new ArrayList<>();
        for(int i : List.of(1,2,3,4)) {
            if (i == 2) {
                continue;
            }
            result.add(i);
        }
        assertEquals(List.of(1,3,4), result);
    }

    @Test
    void hashMapRemoveNull(){
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 2);
        Integer a = map.remove("a");
        Integer b = map.remove("b");
        assertEquals(2, a);
        assertNull(b);
    }

}
