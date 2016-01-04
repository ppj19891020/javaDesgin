package factory;

/**
 * 导出Execl的具体实现
 * @author pangpeijie
 *
 */
public class ExportExeclFileOperate extends ExportFileOperate{

	@Override
	protected ExportFileApi getFactoryMethod() {
		// TODO Auto-generated method stub
		return new ExportExeclFile();
	}

}
