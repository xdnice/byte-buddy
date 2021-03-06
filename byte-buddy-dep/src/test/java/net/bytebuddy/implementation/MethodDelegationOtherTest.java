package net.bytebuddy.implementation;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import org.junit.Test;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.isToString;
import static net.bytebuddy.test.utility.FieldByFieldComparison.hasPrototype;
import static org.hamcrest.MatcherAssert.assertThat;

public class MethodDelegationOtherTest {

    @Test(expected = IllegalStateException.class)
    public void testDelegationToInvisibleInstanceThrowsException() throws Exception {
        new ByteBuddy()
                .subclass(Object.class)
                .method(isToString())
                .intercept(MethodDelegation.to(new Foo()))
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testDelegationToInvisibleFieldTypeThrowsException() throws Exception {
        new ByteBuddy()
                .with(TypeValidation.DISABLED)
                .subclass(Object.class)
                .defineField("foo", Foo.class)
                .method(isToString())
                .intercept(MethodDelegation.toField("foo"))
                .make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelegationWithIllegalType() throws Exception {
        MethodDelegation.to(new Object(), String.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldDoesNotExist() throws Exception {
        new ByteBuddy()
                .subclass(Object.class)
                .method(any())
                .intercept(MethodDelegation.toField("foo"))
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotDelegateToInstanceFieldFromStaticMethod() throws Exception {
        new ByteBuddy()
                .subclass(Object.class)
                .defineField("foo", Object.class)
                .defineMethod("bar", void.class, Ownership.STATIC)
                .intercept(MethodDelegation.toField("foo"))
                .make();
    }

    @Test
    public void testEmptyConfiguration() throws Exception {
        assertThat(MethodDelegation.withEmptyConfiguration()
                .withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS)
                .withResolvers(MethodDelegationBinder.AmbiguityResolver.DEFAULT), hasPrototype(MethodDelegation.withDefaultConfiguration()));
    }

    static class Foo {
        /* empty */
    }
}
