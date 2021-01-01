package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DyDisTest {
    interface I {
    }

    class X implements I {
    }

    class Y implements I {
    }

    abstract class A {
        abstract String called();

        abstract void x(I i);
    }

    class C extends A {
        String methodCalled = null;

        @Override
        public String called() {
            return methodCalled;
        }

        @Override
        void x(I i) {
            methodCalled = "x(I)";
        }

        void x(X x) {
            methodCalled = "x(X)";
        }

        void x(Y x) {
            methodCalled = "x(Y)";
        }
    }

    @Test
    public void which_I_with_A() {
        I x = new X();
        A a = new C();

        a.x(x);

        assertThat(a.called(), is(equalTo("x(I)")));
    }

    @Test
    public void which_X_with_A() {
        X x = new X();
        A a = new C();

        a.x(x);

        assertThat(a.called(), is(equalTo("x(I)")));
    }

    @Test
    public void which_X_with_C() {
        X x = new X();
        C a = new C();

        a.x(x);

        assertThat(a.called(), is(equalTo("x(X)")));
    }

    @Test
    public void which_I_with_C() {
        I x = new X();
        C a = new C();

        a.x(x);

        assertThat(a.called(), is(equalTo("x(I)")));
    }
}
