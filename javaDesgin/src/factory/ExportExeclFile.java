package factory;

/**
 * 导出Execl数据
 * @author pangpeijie
 *
 */
public class ExportExeclFile implements ExportFileApi {

	@Override
	public boolean export(String data) {
		// TODO Auto-generated method stub
		System.out.println("导出Execl数据:"+data);
		return true;
	}

}
