package pms.util.func;

import org.apache.commons.fileupload.FileItem;

public interface Handler {
	public void handle(FileItem item,int i);
}
