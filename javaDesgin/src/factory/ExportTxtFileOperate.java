package factory;

/**
 * 导出文本的具体实现
 * @author pangpeijie
 *
 */
public class ExportTxtFileOperate extends ExportFileOperate {

	@Override
	protected ExportFileApi getFactoryMethod() {
		// TODO Auto-generated method stub
		return new ExportTxtFile();
	}

}
