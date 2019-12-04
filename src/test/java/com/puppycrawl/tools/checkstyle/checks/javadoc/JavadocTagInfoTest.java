////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2019 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.javadoc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class JavadocTagInfoTest {

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testJavadocTagInfoValueOf() {
        final JavadocTagInfo tag = JavadocTagInfo.valueOf("AUTHOR");
        assertEquals(JavadocTagInfo.AUTHOR, tag, "Invalid valueOf result");
    }

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testTypeValueOf() {
        final JavadocTagInfo.Type type = JavadocTagInfo.Type.valueOf("BLOCK");
        assertEquals(JavadocTagInfo.Type.BLOCK, type, "Invalid valueOf result");
    }

    /* Additional test for jacoco, since values()
     * is generated by javac and jacoco reports that
     * values() is uncovered.
     */
    @Test
    public void testTypeValues() {
        final JavadocTagInfo.Type[] expected = {
            JavadocTagInfo.Type.BLOCK,
            JavadocTagInfo.Type.INLINE,
        };
        final JavadocTagInfo.Type[] actual = JavadocTagInfo.Type.values();
        assertArrayEquals(expected, actual, "Invalid Type values");
    }

    @Test
    public void testAuthor() {
        final DetailAstImpl ast = new DetailAstImpl();

        final int[] validTypes = {
            TokenTypes.PACKAGE_DEF,
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ANNOTATION_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue(JavadocTagInfo.AUTHOR.isValidOn(ast),
                    "Invalid ast type for current tag: " + ast.getType());
        }

        ast.setType(TokenTypes.LAMBDA);
        assertFalse(JavadocTagInfo.AUTHOR.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");
    }

    @Test
    public void testOthers() throws ReflectiveOperationException {
        final JavadocTagInfo[] tags = {
            JavadocTagInfo.CODE,
            JavadocTagInfo.DOC_ROOT,
            JavadocTagInfo.LINK,
            JavadocTagInfo.LINKPLAIN,
            JavadocTagInfo.LITERAL,
            JavadocTagInfo.SEE,
            JavadocTagInfo.SINCE,
            JavadocTagInfo.VALUE,
        };
        for (JavadocTagInfo tagInfo : tags) {
            final DetailAstImpl astParent = new DetailAstImpl();
            astParent.setType(TokenTypes.LITERAL_CATCH);

            final DetailAstImpl ast = new DetailAstImpl();
            final Method setParent = ast.getClass().getDeclaredMethod("setParent",
                    DetailAstImpl.class);
            setParent.setAccessible(true);
            setParent.invoke(ast, astParent);

            final int[] validTypes = {
                TokenTypes.PACKAGE_DEF,
                TokenTypes.CLASS_DEF,
                TokenTypes.INTERFACE_DEF,
                TokenTypes.ENUM_DEF,
                TokenTypes.ANNOTATION_DEF,
                TokenTypes.METHOD_DEF,
                TokenTypes.CTOR_DEF,
                TokenTypes.VARIABLE_DEF,
            };
            for (int type: validTypes) {
                ast.setType(type);
                assertTrue(tagInfo.isValidOn(ast),
                        "Invalid ast type for current tag: " + ast.getType());
            }

            astParent.setType(TokenTypes.SLIST);
            ast.setType(TokenTypes.VARIABLE_DEF);
            assertFalse(tagInfo.isValidOn(ast),
                    "Should return false when ast type is invalid for current tag");

            ast.setType(TokenTypes.PARAMETER_DEF);
            assertFalse(tagInfo.isValidOn(ast),
                    "Should return false when ast type is invalid for current tag");
        }
    }

    @Test
    public void testDeprecated() throws ReflectiveOperationException {
        final DetailAstImpl ast = new DetailAstImpl();
        final DetailAstImpl astParent = new DetailAstImpl();
        astParent.setType(TokenTypes.LITERAL_CATCH);
        final Method setParent = ast.getClass().getDeclaredMethod("setParent", DetailAstImpl.class);
        setParent.setAccessible(true);
        setParent.invoke(ast, astParent);

        final int[] validTypes = {
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ANNOTATION_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.ENUM_CONSTANT_DEF,
            TokenTypes.ANNOTATION_FIELD_DEF,
            TokenTypes.VARIABLE_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue(JavadocTagInfo.DEPRECATED.isValidOn(ast),
                    "Invalid ast type for current tag: " + ast.getType());
        }

        astParent.setType(TokenTypes.SLIST);
        ast.setType(TokenTypes.VARIABLE_DEF);
        assertFalse(JavadocTagInfo.DEPRECATED.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");

        ast.setType(TokenTypes.PARAMETER_DEF);
        assertFalse(JavadocTagInfo.DEPRECATED.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");
    }

    @Test
    public void testSerial() throws ReflectiveOperationException {
        final DetailAstImpl ast = new DetailAstImpl();
        final DetailAstImpl astParent = new DetailAstImpl();
        astParent.setType(TokenTypes.LITERAL_CATCH);
        final Method setParent = ast.getClass().getDeclaredMethod("setParent", DetailAstImpl.class);
        setParent.setAccessible(true);
        setParent.invoke(ast, astParent);

        final int[] validTypes = {
            TokenTypes.VARIABLE_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue(JavadocTagInfo.SERIAL.isValidOn(ast),
                    "Invalid ast type for current tag: " + ast.getType());
        }

        astParent.setType(TokenTypes.SLIST);
        ast.setType(TokenTypes.VARIABLE_DEF);
        assertFalse(JavadocTagInfo.SERIAL.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");

        ast.setType(TokenTypes.PARAMETER_DEF);
        assertFalse(JavadocTagInfo.SERIAL.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");
    }

    @Test
    public void testException() {
        final DetailAstImpl ast = new DetailAstImpl();

        final int[] validTypes = {
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue(JavadocTagInfo.EXCEPTION.isValidOn(ast),
                    "Invalid ast type for current tag: " + ast.getType());
        }

        ast.setType(TokenTypes.LAMBDA);
        assertFalse(JavadocTagInfo.EXCEPTION.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");
    }

    @Test
    public void testThrows() {
        final DetailAstImpl ast = new DetailAstImpl();

        final int[] validTypes = {
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue(JavadocTagInfo.THROWS.isValidOn(ast),
                    "Invalid ast type for current tag: " + ast.getType());
        }

        ast.setType(TokenTypes.LAMBDA);
        assertFalse(JavadocTagInfo.THROWS.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");
    }

    @Test
    public void testVersions() {
        final DetailAstImpl ast = new DetailAstImpl();

        final int[] validTypes = {
            TokenTypes.PACKAGE_DEF,
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ANNOTATION_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue(JavadocTagInfo.VERSION.isValidOn(ast),
                    "Invalid ast type for current tag: " + ast.getType());
        }

        ast.setType(TokenTypes.LAMBDA);
        assertFalse(JavadocTagInfo.VERSION.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");
    }

    @Test
    public void testParam() {
        final DetailAstImpl ast = new DetailAstImpl();

        final int[] validTypes = {
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue(JavadocTagInfo.PARAM.isValidOn(ast),
                    "Invalid ast type for current tag: " + ast.getType());
        }

        ast.setType(TokenTypes.LAMBDA);
        assertFalse(JavadocTagInfo.PARAM.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");
    }

    @Test
    public void testReturn() {
        final DetailAstImpl ast = new DetailAstImpl();
        final DetailAstImpl astChild = new DetailAstImpl();
        astChild.setType(TokenTypes.TYPE);
        ast.setFirstChild(astChild);
        final DetailAstImpl astChild2 = new DetailAstImpl();
        astChild2.setType(TokenTypes.LITERAL_INT);
        astChild.setFirstChild(astChild2);

        final int[] validTypes = {
            TokenTypes.METHOD_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue(JavadocTagInfo.RETURN.isValidOn(ast),
                    "Invalid ast type for current tag: " + ast.getType());
        }

        astChild2.setType(TokenTypes.LITERAL_VOID);
        assertFalse(JavadocTagInfo.RETURN.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");

        ast.setType(TokenTypes.LAMBDA);
        assertFalse(JavadocTagInfo.RETURN.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");
    }

    @Test
    public void testSerialField() {
        final DetailAstImpl ast = new DetailAstImpl();
        final DetailAstImpl astChild = new DetailAstImpl();
        astChild.setType(TokenTypes.TYPE);
        ast.setFirstChild(astChild);
        final DetailAstImpl astChild2 = new DetailAstImpl();
        astChild2.setType(TokenTypes.ARRAY_DECLARATOR);
        astChild2.setText("ObjectStreamField");
        astChild.setFirstChild(astChild2);

        final int[] validTypes = {
            TokenTypes.VARIABLE_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue(JavadocTagInfo.SERIAL_FIELD.isValidOn(ast),
                    "Invalid ast type for current tag: " + ast.getType());
        }

        astChild2.setText("1111");
        assertFalse(JavadocTagInfo.SERIAL_FIELD.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");

        astChild2.setType(TokenTypes.LITERAL_VOID);
        assertFalse(JavadocTagInfo.SERIAL_FIELD.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");

        ast.setType(TokenTypes.LAMBDA);
        assertFalse(JavadocTagInfo.SERIAL_FIELD.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");
    }

    @Test
    public void testSerialData() {
        final DetailAstImpl ast = new DetailAstImpl();
        ast.setType(TokenTypes.METHOD_DEF);
        final DetailAstImpl astChild = new DetailAstImpl();
        astChild.setType(TokenTypes.IDENT);
        astChild.setText("writeObject");
        ast.setFirstChild(astChild);

        final String[] validNames = {
            "writeObject",
            "readObject",
            "writeExternal",
            "readExternal",
            "writeReplace",
            "readResolve",
        };
        for (String name: validNames) {
            astChild.setText(name);
            assertTrue(JavadocTagInfo.SERIAL_DATA.isValidOn(ast),
                    "Invalid ast type for current tag: " + ast.getType());
        }

        astChild.setText("1111");
        assertFalse(JavadocTagInfo.SERIAL_DATA.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");

        ast.setType(TokenTypes.LAMBDA);
        assertFalse(JavadocTagInfo.SERIAL_DATA.isValidOn(ast),
                "Should return false when ast type is invalid for current tag");
    }

    @Test
    public void testCoverage() {
        assertEquals(JavadocTagInfo.Type.BLOCK, JavadocTagInfo.VERSION.getType(), "Invalid type");

        assertEquals("text [@version] name [version] type [BLOCK]",
            JavadocTagInfo.VERSION.toString(), "Invalid toString result");

        try {
            JavadocTagInfo.fromName(null);
            fail("IllegalArgumentException is expected");
        }
        catch (IllegalArgumentException ex) {
            assertEquals("the name is null", ex.getMessage(),
                    "Invalid exception message");
        }

        try {
            JavadocTagInfo.fromName("myname");
            fail("IllegalArgumentException is expected");
        }
        catch (IllegalArgumentException ex) {
            assertEquals("the name [myname] is not a valid Javadoc tag name", ex.getMessage(),
                    "Invalid exception message");
        }

        try {
            JavadocTagInfo.fromText(null);
            fail("IllegalArgumentException is expected");
        }
        catch (IllegalArgumentException ex) {
            assertEquals("the text is null", ex.getMessage(), "Invalid exception message");
        }

        try {
            JavadocTagInfo.fromText("myname");
            fail("IllegalArgumentException is expected");
        }
        catch (IllegalArgumentException ex) {
            assertEquals(
                    "the text [myname] is not a valid Javadoc tag text", ex.getMessage(),
                    "Invalid exception message");
        }

        assertEquals(JavadocTagInfo.VERSION, JavadocTagInfo.fromText("@version"),
                "Invalid fromText result");
    }

}
