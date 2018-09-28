package pms.util.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pms.util.PatternUtil;
import pms.util.comm.lambda.exception.Handler;
import pms.util.comm.lambda.exception.SimpleExec;
import pms.util.comm.lambda.param.ParamWrapper;
import pms.util.excel.anno.Excel;
import pms.util.excel.anno.ExcelColumn;
import pms.util.file.FileUtil;
import pms.util.reflect.Reflector;

public class ExcelTranslator {
	private final static String Excel_2003 = "xls"; // 2003 版本的excel
	private final static String Excel_2007 = "xlsx"; // 2007 版本的excel
	private HSSFWorkbook wb = null;
	private Map<Class<?>, Map<String, String>> map;

	@SuppressWarnings("unchecked")
	public Map<String, String> register(Class<?> clazz) {
		if (map == null) {
			map = new HashMap<>();
		}

		Map<String, String> mappers = map.get(clazz);
		if (mappers != null) {
			return mappers;
		}
		mappers = new HashMap<>();
		Excel excel_anno = clazz.getDeclaredAnnotation(Excel.class);
		if (excel_anno != null) {
			// Excel annotation to do something
		}
		ParamWrapper wrapper = ParamWrapper.instance().set(mappers);

		HashMap<String, Field> fields = Reflector.getFields(clazz);
		fields.values().forEach(field -> {
			ExcelColumn col = field.getAnnotation(ExcelColumn.class);
			//System.out.println(col);
			if (col != null && !col.Column().isEmpty()) {
				((Map<String, String>) wrapper.get()).put(field.getName(), col.Column());
			}
		});
		return mappers;
	}

	public ExcelTranslator transToExcel(String sheetName, ArrayList<Object> list, Class<?> clazz) {
		Map<String, String> mappers = register(clazz);
		ArrayList<String> attrs=new ArrayList<>();
		ArrayList<String> columns=new ArrayList<>();
		mappers.entrySet().stream().forEach(entry->{
			attrs.add(entry.getKey());
			columns.add(entry.getValue());
		});
		//System.out.println("attr-size:"+attrs.size());
		ArrayList<String[]> rows=new ArrayList<>();
		list.forEach(e -> {
			ArrayList<String> ls = new ArrayList<>();
			attrs.stream().forEach(attr -> {
				SimpleExec.exec((data) -> {
					ls.add(""+Reflector.get(e, attr));
					return null;
				}, Handler.PRINTTRACE);
			});
			rows.add(ls.toArray(new String[0]));
		});
		
		return transToExcel(sheetName,columns.toArray(new String[0]),rows.toArray(new String[0][]));
	}

	public ExcelTranslator transToExcel(String sheetName, String[] title, String[][] values) {
		wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		HSSFRow row = null;
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		int index = 0;
		if (title != null) {
			HSSFCell cell = null;
			row = sheet.createRow(index++);
			for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
			}
		}
		for (int i = 0; i < values.length; i++) {
			row = sheet.createRow(index++);
			for (int j = 0; j < values[i].length; j++) {
				row.createCell(j).setCellValue(values[i][j]);
			}
		}
		return this;
	}

	/**
	 * 描述：根据文件后缀，自动适应上传文件的版本
	 * 
	 * @param inStr,fileName
	 * @return
	 * @throws Exception
	 */
	public Workbook getWorkbook(InputStream inStr, String ext) throws Exception {
		Workbook work = null;
		if (Excel_2003.equals(ext)) {
			work = new HSSFWorkbook(inStr);// 2003 版本的excel
		} else if (Excel_2007.equals(ext)) {
			work = new XSSFWorkbook(inStr);// 2007 版本的excel
		} else {
			throw new Exception("解析文件格式有误！");
		}
		return work;
	}

	/**
	 * 描述：获取IO流中的数据，组装成List<List<Object>>对象
	 * 
	 * @param in,fileName
	 * @return
	 * @throws IOException
	 */
	public ArrayList<TableObject> importExcelFile(InputStream in, String fileName) throws Exception {
		ArrayList<TableObject> sheets = new ArrayList<>();
		Workbook work = this.getWorkbook(in, FileUtil.ext(fileName));
		if (null == work) {
			throw new Exception("创建Excel工作薄为空！");
		}
		work.forEach(sheet -> {
			if (sheet != null) {
				TableObject table = new TableObject(sheet.getRow(0).getPhysicalNumberOfCells());
				System.out.println(sheet.getNumMergedRegions());
				sheet.forEach(row -> {

					if (row != null) {
						ArrayList<String> cells = new ArrayList<>();
						row.forEach(cell -> {
							cells.add(this.getCellValue(cell).toString());
						});
						table.addRow(cells.toArray(new String[0]));
					}
				});
				sheets.add(table);
			}
		});
		work.close();
		return sheets;
	}

	public ArrayList<TableObject> importExcelFile(String path, String fileName) throws Exception {
		ArrayList<TableObject> sheets = new ArrayList<>();
		try (InputStream in = Files.newInputStream(Paths.get(path + File.separator + fileName))) {
			Workbook work = this.getWorkbook(in, FileUtil.ext(fileName));
			if (null == work) {
				throw new Exception("创建Excel工作薄为空！");
			}
			work.forEach(sheet -> {
				if (sheet != null) {
					TableObject table = new TableObject(sheet.getRow(0).getPhysicalNumberOfCells());
					// System.out.println(sheet.getNumMergedRegions());
					sheet.forEach(row -> {

						if (row != null) {
							ArrayList<String> cells = new ArrayList<>();
							row.forEach(cell -> {
								String value = this.getCellValue(cell);
								if (!value.isEmpty()) {
									cells.add(value);
								}
							});
							table.addRow(cells.toArray(new String[0]));
						}
					});
					sheets.add(table);
				}
			});
			work.close();
		}
		return sheets;
	}

	public String getCellValue(Cell cell) {
		String value = null;
		switch (cell.getCellType()) {
		case BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case BLANK:
			value = "";
			break;
		default:
			String cell_str = cell.toString();
			try {
				if (PatternUtil.isNumber(cell_str)) {
					value = "" + cell.getNumericCellValue();
				} else {
					value = "" + cell.getDateCellValue().toInstant().getEpochSecond();
				}
			} catch (IllegalStateException e) {
				value = String.valueOf(cell.getRichStringCellValue());
			}
			break;
		}
		// System.out.println(value);
		return value;
	}

	public void export(String path, String fn) throws IOException {
		FileUtil.notExistCreate(path);
		File file = new File(path + File.separator + (fn == null ? Instant.now().getEpochSecond() : fn) + ".xls");
		wb.write(file);
	}

	public void send(OutputStream out) throws IOException {
		wb.write(out);
	}

	public static class TableObject {
		private int col_num;
		private ArrayList<String[]> rows = new ArrayList<>();

		public TableObject(int num) {
			col_num = num;
		}

		public void addRow(String[] row) {
			rows.add(row);
		}

		public ArrayList<String[]> getRows() {
			return rows;
		}

		public ArrayList<String[]> getValidRows() {
			return rows.stream().filter(arr -> arr.length == col_num)
					.collect(Collectors.toCollection(ArrayList<String[]>::new));
		}

		public ArrayList<String[]> getRowOf(int cell_num) {
			return rows.stream().filter(arr -> arr.length == cell_num)
					.collect(Collectors.toCollection(ArrayList<String[]>::new));
		}

		public ArrayList<String[]> getRowOf(Set<Integer> cell_num) {
			return rows.stream().filter(arr -> cell_num.contains(arr.length))
					.collect(Collectors.toCollection(ArrayList<String[]>::new));
		}

		public String toString() {
			StringBuilder str = new StringBuilder();
			str.append("tbody:");
			str.append(getRows().size());
			str.append("\r\n");

			return str.toString();
		}
	}
}
