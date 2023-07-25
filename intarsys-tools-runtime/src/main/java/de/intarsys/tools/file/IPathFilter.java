package de.intarsys.tools.file;

public interface IPathFilter {

	IPathFilter ANY_FILTER = new IPathFilter() {
		@Override
		public boolean accept(String path) {
			return true;
		}
	};

	public boolean accept(String path);

}
