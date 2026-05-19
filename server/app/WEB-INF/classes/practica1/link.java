package practica1;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;

public class link {
	private URL url;
	private String rel;
	
	@XmlAttribute(name="href")
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	
	@XmlAttribute
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	public link(String url, String rel) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.rel = rel;
	}
	
	public link() {

	}

}
