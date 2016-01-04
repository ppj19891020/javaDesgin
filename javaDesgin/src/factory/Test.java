package factory;

/**
 * Test测试
 * @author pangpeijie
 *
 */
public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExportFileOperate exportFileOperate = new ExportExeclFileOperate();
		exportFileOperate.export("测试数据");
		
		ExportTxtFileOperate exportTxtFileOperate = new ExportTxtFileOperate();
		exportTxtFileOperate.export("测试数据");
	}

}
