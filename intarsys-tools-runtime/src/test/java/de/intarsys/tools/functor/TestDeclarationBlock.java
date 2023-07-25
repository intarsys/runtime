package de.intarsys.tools.functor;

import junit.framework.TestCase;

public class TestDeclarationBlock extends TestCase {

	public void testRemove() {
		DeclarationBlock block = new DeclarationBlock(null);
		IDeclarationElement decl0 = new ArgumentDeclaration(null, "0", null, (IFunctor) null, null);
		block.addDeclarationElement(decl0);
		assertTrue(block.size() == 1);
		IDeclarationElement decl1 = new ArgumentDeclaration(null, "1", null, (IFunctor) null, null);
		block.addDeclarationElement(decl1);
		assertTrue(block.size() == 2);
		block.removeDeclarationElement(decl0);
		assertTrue(block.size() == 1);
		assertTrue(block.getDeclarationElements()[0] == decl1);
		block.removeDeclarationElement(decl0);
		assertTrue(block.size() == 1);
		assertTrue(block.getDeclarationElements()[0] == decl1);
		block.removeDeclarationElement(decl1);
		assertTrue(block.size() == 0);
	}
}
