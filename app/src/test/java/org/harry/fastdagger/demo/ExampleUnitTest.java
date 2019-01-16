package org.harry.fastdagger.demo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testLetter(){
        char ch = '!';
        System.out.println("a is letter --> " + Character.isLetter(ch));

        String packageTitle = "package com.harry.demo;\n";
        StringBuilder sb = new StringBuilder(packageTitle);
        sb.append("import java.lang.String;\n")
                .append("import com.harry.mvp.view.IView;\n")
                .append("import com.harry.mvp.view.IMode;\n");

        System.out.println(sb.toString());

        sb.insert(sb.lastIndexOf("import"),"import test insert;\n");

        System.out.println(sb.toString());
    }
}