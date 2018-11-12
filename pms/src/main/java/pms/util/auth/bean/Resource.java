package pms.util.auth.bean;

import java.io.Serializable;

import pms.util.reflect.anno.Skip;

public class Resource implements Serializable {
	@Skip(skip = true)
	private static final long serialVersionUID = -2322021871498652528L;
	private String id;
	private String name;
	private String fid;
	private String url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		Resource resource = (Resource) o;
		return this.getId().equals(resource.getId());
	}
}
