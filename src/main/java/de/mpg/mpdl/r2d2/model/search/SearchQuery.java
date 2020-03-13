package de.mpg.mpdl.r2d2.model.search;

import de.mpg.mpdl.r2d2.search.es.daoimpl.ElasticSearchGenericDAOImpl;

public class SearchQuery {
	
	private String query;
	
	private String sort;
	
	private int from = 0;
	
	private int size = -1;
	
	private String scrollId;
	
	private boolean scroll = false;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isScroll() {
		return scroll;
	}

	public void setScroll(boolean scroll) {
		this.scroll = scroll;
	}

	public String getScrollId() {
		return scrollId;
	}

	public void setScrollId(String scrollId) {
		this.scrollId = scrollId;
	}

}
