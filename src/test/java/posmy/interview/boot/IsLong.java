package posmy.interview.boot;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsLong extends TypeSafeMatcher<String> {

    @Override
    protected boolean matchesSafely(String s) {
        try {
            Long.parseLong(s); // Or the number type you need
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is long");
    }

    public static Matcher<String> isLong() {
        return new IsLong();
    }
}