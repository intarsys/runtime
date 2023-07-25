package de.intarsys.tools.authenticate;

import junit.framework.TestCase;

public class TestPasswordTools extends TestCase {

	public TestPasswordTools() {
	}

	public void testPasswordTool() {
		char[] passwd;
		int LEN = 120;
		String ADDED = "-.\""; //$NON-NLS-1$

		// test the secure random password generation
		String CHARS = PasswordTools.CHARS_UPPERCASE + ADDED;
		passwd = PasswordTools.createPassword(LEN, CHARS);
		System.out.println(passwd);
		assertTrue(passwd.length == LEN);

		// should not contain deselected characters
		for (int i = 0; i < PasswordTools.CHARS_LOWERCASE.length(); i++) {
			char x = PasswordTools.CHARS_LOWERCASE.charAt(i);
			for (int j = 0; j < passwd.length; j++) {
				assertFalse(passwd[j] == x); // $NON-NLS-1$
			}
		}
		for (int i = 0; i < PasswordTools.CHARS_DIGITS.length(); i++) {
			char x = PasswordTools.CHARS_DIGITS.charAt(i);
			for (int j = 0; j < passwd.length; j++) {
				assertFalse(passwd[j] == x); // $NON-NLS-1$
			}
		}

		// have to contain only selected characters.
		String allowed = PasswordTools.CHARS_UPPERCASE + ADDED;
		for (int i = 0; i < passwd.length; i++) {
			char x = passwd[i];
			for (int j = 0; j <= allowed.length(); j++) {
				if (j == allowed.length()) {
					fail("");
				}
				if (allowed.charAt(j) == x) {
					break;
				}
			}
		}
	}
}
