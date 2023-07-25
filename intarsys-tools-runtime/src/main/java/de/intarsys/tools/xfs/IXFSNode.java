package de.intarsys.tools.xfs;

import java.io.IOException;
import java.util.List;

public interface IXFSNode extends Comparable<IXFSNode> {

	public boolean exists();

	public IXFSNode getChild(String string);

	public List<IXFSNode> getChildren() throws IOException;

	public String getName();

	public String getPath();

}
