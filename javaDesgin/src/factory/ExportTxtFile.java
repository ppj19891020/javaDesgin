package factory;

/**
 * 导出文本文件
 * @author pangpeijie
 *
 */
public class ExportTxtFile implements ExportFileApi{

	@Override
	public boolean export(String data) {
		// TODO Auto-generated method stub
		System.out.println("导出文本文件:"+data);
		return true;
	}
	
}
