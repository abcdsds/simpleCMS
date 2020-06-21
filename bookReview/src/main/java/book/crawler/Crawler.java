package book.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler{

	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		// TODO Auto-generated method stub
		return super.shouldVisit(referringPage, url);
	}
	
	@Override
	public void visit(Page page) {
		// TODO Auto-generated method stub
		super.visit(page);
	}
	
	
}
