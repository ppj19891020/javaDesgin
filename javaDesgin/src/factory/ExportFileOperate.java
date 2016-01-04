package factory;

/**
 * 实现导出业务的核心对象
 * @author pangpeijie
 *
 */
public abstract class ExportFileOperate {

	/**
	 * 创建导出的对象
	 * @return
	 */
	protected abstract ExportFileApi getFactoryMethod();
	
	/**
	 * 导出数据
	 * @param data
	 * @return
	 */
	protected boolean export(String data){
		ExportFileApi exportFileApi = getFactoryMethod();
		return exportFileApi.export(data);
	}
}
