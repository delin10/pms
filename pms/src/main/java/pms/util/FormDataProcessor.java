package pms.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import pms.util.func.Handler;

public class FormDataProcessor {
	private String tempPath;
	private String filePath;
	private List<FileItem> items;
	/** 总上传文件最大为10M */
	private Long TOTAL_FILE_MAXSIZE = 10000000L;
	/** 单个上传文件最大为10M */
	private int SINGLE_FILE_MAXSIZE = 2 * 1024 * 1024;

	public FormDataProcessor(String temp) {
		FileUtil.notExistCreate(temp);
		this.tempPath=temp;
	}
	
	public FormDataProcessor process(HttpServletRequest request) {
		if (items!=null) {
			return this;
		}
		items = getFormFields(request, tempPath,SINGLE_FILE_MAXSIZE,TOTAL_FILE_MAXSIZE);
		return this;
	}
	
	public String mapToJSON() {
		Map<String, String> map=getFormTextFields("UTF-8");
		return JsonUtil.mapToJSON(map);
	}
	
	public List<FileItem> getFormFields(HttpServletRequest request,String tempPath,
														int SINGLE_FILE_MAXSIZE,long TOTAL_FILE_MAXSIZE) {
		File tempPathFile = new File(tempPath);
		// 文件对象的工厂类
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 设置最大上传大小
		factory.setSizeThreshold(SINGLE_FILE_MAXSIZE);
		// 将临时文件夹交给文件对象的工厂类
		factory.setRepository(tempPathFile);
		// 创建一个上传文件的处理者
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 设置所有请求的总大小
		upload.setSizeMax(TOTAL_FILE_MAXSIZE);
		// 解析request
		List<FileItem> items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
		return items;
	}
	
	public Map<String,String> getFormTextFields(String charset){
		Map<String,String> kv=new HashMap<>();
		System.out.println(items.get(0));
		items.stream().filter(FileItem::isFormField).forEach(item->{
			try {
				System.out.println(item.getName());
				kv.put(item.getFieldName(), item.getString(charset));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return kv;
	}
	
	public List<FileItem> getFileFields(){
		return items.stream()
				.filter(item->!item.isFormField())
					.collect(Collectors.toList());
	}
	
	public void handleFileFields(Handler handler){
		List<FileItem> items=getFileFields();
		int size=items.size();
		for (int i=0;i<size;++i) {
			handler.handle(items.get(i), i);
		}
	}

	public String getTempPath() {
		return tempPath;
	}

	public String getFilePath() {
		return filePath;
	}

	public FormDataProcessor setFilePath(String filePath) {
		this.filePath = filePath;
		return this;
	}

	public List<FileItem> getItems() {
		return items;
	}

	public Long getTOTAL_FILE_MAXSIZE() {
		return TOTAL_FILE_MAXSIZE;
	}


	public int getSINGLE_FILE_MAXSIZE() {
		return SINGLE_FILE_MAXSIZE;
	}

	
	public FormDataProcessor setTotalFileMaxsize(long TOTAL_FILE_MAXSIZE) {
		this.TOTAL_FILE_MAXSIZE=TOTAL_FILE_MAXSIZE;
		return this;
	}

	public FormDataProcessor setSingleFileMaxsize(int SINGLE_FILE_MAXSIZE) {
		this.SINGLE_FILE_MAXSIZE=SINGLE_FILE_MAXSIZE;
		return this;
	}
}
