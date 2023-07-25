package de.intarsys.tools.objectmodel;

import org.junit.Assert;
import org.junit.Test;

public class TestObjectModelAlias {

	@Test
	public void test() throws Exception {
		IClassSelector selector;
		IField fieldRegister;
		IField fieldLookup;
		//
		selector = ObjectModelTools.createSelector("de.intarsys.tools.objectmodel.Foo");
		fieldRegister = new FunctorField("gnu", null, null);
		ObjectModelTools.registerField(selector, fieldRegister);
		fieldLookup = ObjectModelTools.lookupField(Bar.class, "gnu");
		Assert.assertTrue(fieldRegister == fieldLookup);
	}

}
