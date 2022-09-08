package fr.insee.eno.core.sandbox;

import fr.insee.eno.core.annotations.DDI;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import reusable33.AbstractIdentifiableType;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SandboxTests {

    @Getter
    @Setter
    public static abstract class FooAbstract {
        @DDI(contextType = AbstractIdentifiableType.class, field = "hello")
        int a;
    }

    @Getter
    @Setter
    public class FooChild extends FooAbstract {
        int c;
    }

    @Test
    public void propertyInheritanceWithAbstractClass() {
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
    public void testIfListsHaveIntersection() {
        List<Integer> list = List.of(1,2);
        assertFalse(list.stream().noneMatch(List.of(2,3,4)::contains));
    }

}
